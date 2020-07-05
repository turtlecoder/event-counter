package eventcounter
import java.time.{Duration, Instant}
import java.time.temporal.{ChronoUnit, TemporalAmount, TemporalUnit}

import utest._
import utest.test.Apply

import scala.util.Random
object EventCounterTests extends TestSuite {
  def tests = Tests {
    val start_timestamp = Instant.parse("2020-07-03T23:03:42Z")
    test("Add a single event") - {
      val e1_ts = start_timestamp
      EventCounter.signal(e1_ts)
      // generate a duration of 5 minutes
      val query_ts = e1_ts.plus(5, ChronoUnit.MINUTES)
      val result = EventCounter.query(Duration.of(5, ChronoUnit.MINUTES), query_ts)
      assert(result.contains(1))
    }

    test("Add multiple events over a few minutes") - {
      // Add some events
      val t1 = start_timestamp.plus(Random.between(10, 20), ChronoUnit.SECONDS)
      EventCounter.signal(t1)
      val t2 = t1.plus(Random.between(20,30), ChronoUnit.SECONDS)
      EventCounter.signal(t2)
      val t3 = t2
      EventCounter.signal(t3)
      val query_ts =  start_timestamp.plus(5, ChronoUnit.MINUTES)

      val r1 = EventCounter.query(Duration.of(5, ChronoUnit.MINUTES), query_ts)
      assert(r1.contains(4))
    }

    test("Query events after five minutes") - {
      // Add signals after an interval of 1 minutes
      val t1 = start_timestamp.plus(65, ChronoUnit.SECONDS)
      EventCounter.signal(t1)
      val t2 = t1.plus(Random.between(60, 120), ChronoUnit.SECONDS)
      EventCounter.signal(t2)
      val query_ts = start_timestamp.plus(6, ChronoUnit.MINUTES)
      val r2 = EventCounter.query(Duration.of(5, ChronoUnit.MINUTES), query_ts)
      assert(r2.contains(2))
    }

    test("Test illegal query") - {
      val r1 = EventCounter.query(Duration.of(6, ChronoUnit.MINUTES))
      assert(r1.isEmpty)
      val r2 = EventCounter.query(Duration.of(800, ChronoUnit.MILLIS))
      assert(r2.isEmpty)
    }
  }
}

