package eventcounter
import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}
import java.util.concurrent.locks.ReentrantReadWriteLock


object EventCounter {
  /**
   * Signal an event, and store the timestamp, and the hit count.
   * This is a thread safe method. Locks for writing
   * @param timestamp Current timestamp (in seconds granularity).
   */
  def signal(timestamp: Instant = Instant.now()): Unit = {
    val idx = math.toIntExact(timestamp.getEpochSecond % 300)
    lock.writeLock().lock()
    try {
      if (!timestamps(idx).contains(timestamp)) {
        events(idx) = 1
        timestamps(idx) = Some(timestamp)
      } else {
        events(idx) = events(idx) + 1
      }
    }finally {
      lock.writeLock().unlock()
    }
  }

  /**
   * Query for the hits
   * @param duration The length of the duration to query. Can query from 1 second to 5 minutes
   * @param currentTimestamp The current timestamp (to calculate) for the query.
   * @return An Optional value with either the number of events or None
   */
  def query(duration: Duration, currentTimestamp: Instant = Instant.now): Option[Long] = {
    if (duration.get(ChronoUnit.SECONDS)>300 || duration.get(ChronoUnit.SECONDS)<1 ){
      None
    }else {
      try {
        lock.readLock().lock()
        val epochSeconds = currentTimestamp.getEpochSecond
        Some((0 until 300).
          filter(i =>
            timestamps(i).exists(ts =>
              (epochSeconds - ts.getEpochSecond) <= duration.get(ChronoUnit.SECONDS))).
          map(i => events(i)).sum)
      } finally {
        lock.readLock().unlock()
      }
    }
  }

  // private val timestamps = new Array[Instant](300)
  private val timestamps = Array.fill[Option[Instant]](300)(None)
  private val events = new Array[Long](300)
  private val lock =  new ReentrantReadWriteLock()
}