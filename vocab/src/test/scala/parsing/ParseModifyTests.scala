import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._
import models._

class ParseModifyTests extends AnyFunSuite {
  test("Modify no part of speech") {
    val args = List("modify", "dog", "man's best friend")
    assertResult(Right(Modify("dog", "man's best friend", None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify missing definition") {
    val args = List("modify","trump")
    assertResult(Left(ParseErrorInvalidModifyCommand("missing definition!"))) {
      CommandLine.parseArgs(args)
    }
  }

  test ("Modify with part of speech valid") {
    val args = List("modify", "wow", "used to express strong feeling", "interjection")
    assertResult(Right(Modify("wow", "used to express strong feeling", Some(Interjection)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify with part of speech invalid") {
    val args = List("modify", "wow", "used to express strong feeling", "exclamation")
    assertResult(Left(ParseErrorInvalidModifyCommand("exclamation is not a valid part of speech!"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify missing word") {
    val args = List("modify")
    assertResult(Left(ParseErrorInvalidModifyCommand("missing word!"))) {
      CommandLine.parseArgs(args)
    }
  }
}
