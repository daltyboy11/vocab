import org.scalatest.FunSuite
import commandlineparser._

class CommandLineParserTests extends FunSuite {
  test("attempt to use unsupported command") {
    val args = "vocab drink a cup of tea"
    assertResult(Left(ParseErrorUnsupportedCommand("drink"))) {
      CommandLine.parseArgs(args)
    }
  }
}
