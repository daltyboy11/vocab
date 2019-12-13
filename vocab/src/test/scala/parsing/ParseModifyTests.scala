import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._
import models._

class ParseModifyTests extends AnyFunSuite {
  test("Modify no part of speech") {
    val args = "vocab modify cat the best friend of man"
    assertResult(Right(Modify("cat", "the best friend of man", None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify missing definition") {
    val args = "vocab modify trump"
    assertResult(Left(ParseErrorMissingDefinition("trump"))) {
      CommandLine.parseArgs(args)
    }
  }

  test ("Modify with part of speech valid") {
    val args = "vocab modify wow used to express strong feeling --interjection some excess words"
    assertResult(Right(Modify("wow", "used to express strong feeling", Some(Interjection)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify with part of speech invalid") {
    val args = "vocab modify wow used to express strong feeling --exclamation"
    assertResult(Left(ParseErrorInvalidPartOfSpeech(Invalid("exclamation")))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify with description invalid 1") {
    val args = "vocab modify hat $ometh1ng $0m30n3 w3ar$ 0n th31r h3ad --noun"
    assertResult(Left(ParseErrorUnexpectedNonAlphabeticalToken("$ometh1ng"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify with description invalid 2") {
    val args = "vocab modify hat something someone wears on their h4ad"
    assertResult(Left(ParseErrorUnexpectedNonAlphabeticalToken("h4ad"))) {
      CommandLine.parseArgs(args)
    }
  }

}
