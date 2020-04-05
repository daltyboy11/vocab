import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._
import models._

class ParseAddTests extends AnyFunSuite {
  test("Add no part of speech") {
    val args = List("add", "ardor", "enthusiasm or passion")
    assertResult(Right(Add("ardor", "enthusiasm or passion", None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add missing definition") {
    val args = List("add", "ardor")
    assertResult(Left(ParseErrorInvalidAddCommand())) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech valid") {
    val args = List("add", "ardor", "enthusiasm or passion", "noun")
    assertResult(Right(Add("ardor", "enthusiasm or passion", Some(Noun)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech invalid") {
    val args = List("add", "ardor", "enthusiasm or passion", "notAValidType")
    assertResult(Left(ParseErrorInvalidAddCommand())) {
      CommandLine.parseArgs(args)
    }
  }
}

