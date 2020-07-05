# Event Counter

## Prerequisites

* Java Version: 8
* Scala Version: 2.12

## Building eventcounter package

1. Clone the repository into a local directory
2. Run `./mill eventcounter.build` to build the projecct

## Running Tests

Run `./mill eventcounter.test` to run the unit tests

## Generating Idea project if using IntelliJ with Scala plugins

1. Goto the project directory
2. Run `./mill mill.scalalib.GenIdea/idea`
3. Open the project in IntelliJ

## Project Structure
```text
.
├── README.md
├── build.sc
├── eventcounter
│   ├── src
│   │   └── EventCounter.scala
│   └── test
│       └── src
│           └── EventCounterTests.scala
├── mill
└── out

```



