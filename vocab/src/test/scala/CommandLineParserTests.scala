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

  // Parse modify tests
  test("Modify no part of speech") {
    val args = "vocab modify cat the best friend of man"
    assertResult(Modify("cat", "the best friend of man", None)) {
      CommandLine.parseArgs(args)
    }
  }

  test ("Modify with part of speech valid") {
    val args = "vocab modify wow used to express strong feeling --interjection some excess words"
    assertResult(Modify("wow", "used to express strong feeling", Some(Interjection))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify with part of speech invalid") {
    val args = "vocab modify wow used to express strong feeling --exclamation"
    assertResult(Unknown(ParseErrorInvalidPartOfSpeech(Invalid("exclamation")))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify with description invalid 1") {
    val args = "vocab modify hat $ometh1ng $0m30n3 w3ar$ 0n th31r h3ad --noun"
    assertResult(Unknown(ParseErrorUnexpectedNonAlphabeticalToken("$ometh1ng"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Modify with description invalid 2") {
    val args = "vocab modify hat something someone wears on their h4ad"
    assertResult(Unknown(ParseErrorUnexpectedNonAlphabeticalToken("h4ad"))) {
      CommandLine.parseArgs(args)
    }
  }
}
