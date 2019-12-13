import org.scalatest.funsuite.AnyFunSuite
import commandlineparser._
import models._

class CommandLineParserTests extends AnyFunSuite {
  test("attempt to use unsupported command") {
    val args = "vocab drink a cup of tea"
    assertResult(Left(ParseErrorUnsupportedCommand("drink"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("vocab help") {
    val args = "vocab help bla bla bla"
    assertResult(Right(Help)) {
      CommandLine.parseArgs(args)
    }
  }

  test("vocab version") {
    val args = "vocab version"
    assertResult(Right(Version)) {
      CommandLine.parseArgs(args)
    }
  }

  test("vocab words") {
    val args = "vocab words"
    assertResult(Right(Words)) {
      CommandLine.parseArgs(args)
    }
  }
}
