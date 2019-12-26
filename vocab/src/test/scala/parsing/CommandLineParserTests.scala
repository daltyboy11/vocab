import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._
import models._

class CommandLineParserTests extends AnyFunSuite {
  test("attempt to use unsupported command") {
    val args = List("drink", "a", "cup", "of", "tea")
    assertResult(Left(ParseErrorUnsupportedCommand())) {
      CommandLine.parseArgs(args)
    }
  }

  test("vocab help") {
    val args = List("help", "blah", "bla", "bla")
    assertResult(Right(Help)) {
      CommandLine.parseArgs(args)
    }
  }

  test("vocab version") {
    val args = List("version")
    assertResult(Right(Version)) {
      CommandLine.parseArgs(args)
    }
  }

  test("vocab words") {
    val args = List("words")
    assertResult(Right(Words)) {
      CommandLine.parseArgs(args)
    }
  }
}
