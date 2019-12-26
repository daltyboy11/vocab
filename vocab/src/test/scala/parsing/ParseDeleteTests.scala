import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._
import models._

class ParseDeleteTests extends AnyFunSuite {
  test("Parse delete invalid word") {
    val args = List("delete", "not_a_word")
    assertResult(Left(ParseErrorInvalidWord("not_a_word"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse delete valid word no part of speech") {
    val args = List("delete", "hat")
    assertResult(Right(Delete("hat", None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse delete valid word with valid part of speech") {
    val args = List("delete", "hat","--type", "noun")
    assertResult(Right(Delete("hat", Some(Noun)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse delete valid word with invalid part of speech") {
    val args = List("delete", "hat", "hehehehe")
    assertResult(Left(ParseErrorInvalidDeleteCommand())) {
      CommandLine.parseArgs(args)
    }
  }
}
