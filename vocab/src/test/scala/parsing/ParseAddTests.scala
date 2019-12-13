import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._
import models._

class ParseAddTests extends AnyFunSuite {
  test("Add no part of speech") {
    val args = "vocab add ardor enthusiasm or passion"
    assertResult(Right(Add("ardor", "enthusiasm or passion", None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add missing definition 1") {
    val args = "vocab add ardor"
    assertResult(Left(ParseErrorMissingDefinition("ardor"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech valid") {
    val args = "vocab add ardor enthusiasm or passion --noun some excess words"
    assertResult(Right(Add("ardor", "enthusiasm or passion", Some(Noun)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech invalid") {
    val args = "vocab add ardor enthusiasm or passion --notAValidType"
    assertResult(Left(ParseErrorInvalidPartOfSpeech(Invalid("notAValidType")))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with description invalid 1") {
    val args = "vocab add ardor enthu$ia$m or pa$$ion --notValid"
    assertResult(Left(ParseErrorUnexpectedNonAlphabeticalToken("enthu$ia$m"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with description invalid 2") {
    val args = "vocab add ardor enthusiasm or pa$$ion"
    assertResult(Left(ParseErrorUnexpectedNonAlphabeticalToken("pa$$ion"))) {
      CommandLine.parseArgs(args)
    }
  }
}

