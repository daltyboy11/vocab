import org.scalatest.FunSuite
import commandlineparser._
import models._

class ParsePracticeTests extends FunSuite {
  test("Parse practice invalid argument") {
    val args = "vocab practice I think therefore I am"
    assertResult(Left(ParseErrorInvalidPracticeArg("I"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice no args supplied") {
    val args = "vocab practice"
    assertResult(Right(Practice(None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse `practice all`") {
    val args = "vocab practice all"
    assertResult(Right(Practice(Some(All)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse `practice half`") {
    val args = "vocab practice half"
    assertResult(Right(Practice(Some(Half)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice numeric valid") {
    val args = "vocab practice 120"
    assertResult(Right(Practice(Some(ExplicitNumeric(120))))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice numeric invalid") {
    val args = "vocab practice -120"
    assertResult(Left(ParseErrorInvalidExplicitNumeric(-120))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice percentage valid") {
    val args = "vocab practice 0.4"
    assertResult(Right(Practice(Some(PercentageNumeric(0.4f))))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice percentage invalid") {
    val args = "vocab practice 69.69"
    assertResult(Left(ParseErrorInvalidPercentageNumeric(69.69f))) {
      CommandLine.parseArgs(args)
    }
  }
}
