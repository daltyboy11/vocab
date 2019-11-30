import org.scalatest.FunSuite

import commandlineparser._

class CommandLineParserTests extends FunSuite {

  // General parse tests
  test("attempt to use unsupported command") {
    val args = "vocab drink a cup of tea"
    assertResult(Unknown(ParseErrorUnsupportedCommand("drink"))) {
      CommandLine.parseArgs(args)
    }
  }

  // Parse Add tests
  test("Add no part of speech") {
    val args = "vocab add ardor enthusiasm or passion"
    assertResult(Add("ardor", "enthusiasm or passion", None)) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech valid") {
    val args = "vocab add ardor enthusiasm or passion --noun some excess words"
    assertResult(Add("ardor", "enthusiasm or passion", Some(Noun))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech invalid") {
    val args = "vocab add ardor enthusiasm or passion --notAValidType"
    assertResult(Unknown(ParseErrorInvalidPartOfSpeech(Invalid("notAValidType")))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with description invalid 1") {
    val args = "vocab add ardor enthu$ia$m or pa$$ion --notValid"
    assertResult(Unknown(ParseErrorUnexpectedNonAlphabeticalToken("enthu$ia$m"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with description invalid 2") {
    val args = "vocab add ardor enthusiasm or pa$$ion"
    assertResult(Unknown(ParseErrorUnexpectedNonAlphabeticalToken("pa$$ion"))) {
      CommandLine.parseArgs(args)
    }
  }

}
