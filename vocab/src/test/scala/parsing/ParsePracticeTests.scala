import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._

import models._

class ParsePracticeTests extends AnyFunSuite {
  test("Parse practice invalid argument") {
    val args = List("practice", "I", "think", "therefore", "I", "am")
    assertResult(Left(ParseErrorInvalidPracticeCommand("I is not an acceptable practice session type!"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice no args supplied") {
    val args = List("practice")
    assertResult(Right(Practice(None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse `practice all`") {
    val args = List("practice", "all")
    assertResult(Right(Practice(Some(All)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse `practice half`") {
    val args = List("practice", "half")
    assertResult(Right(Practice(Some(Half)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice numeric valid") {
    val args = List("practice", "120")
    assertResult(Right(Practice(Some(ExplicitNumeric(120))))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice numeric invalid") {
    val args = List("practice", "-120")
    assertResult(Left(ParseErrorInvalidPracticeCommand("-120 is not an acceptable practice session type!"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice percentage valid") {
    val args = List("practice", "0.4")
    assertResult(Right(Practice(Some(PercentageNumeric(0.4f))))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice percentage invalid") {
    val args = List("practice", "69.69")
    assertResult(Left(ParseErrorInvalidPracticeCommand("69.69 is not an acceptable practice session type!"))) {
      CommandLine.parseArgs(args)
    }
  }
}
