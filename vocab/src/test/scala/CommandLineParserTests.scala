import org.scalatest.FunSuite

import commandlineparser._

class CommandLineParserTests extends FunSuite {

  // General parse tests
  test("attempt to use unsupported command") {
    val args = "vocab drink a cup of tea"
    assertResult(Left(ParseErrorUnsupportedCommand("drink"))) {
      CommandLine.parseArgs(args)
    }
  }

  // Parse Add tests
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

  // Parse modify tests
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

  // Parse delete tests
  test("Parse delete invalid word") {
    val args = "vocab delete not_a_word"
    assertResult(Left(ParseErrorUnexpectedNonAlphabeticalToken("not_a_word"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse delete valid word no part of speech") {
    val args = "vocab delete hat"
    assertResult(Right(Delete("hat", None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse delete valid word with valid part of speech") {
    val args = "vocab delete hat --noun"
    assertResult(Right(Delete("hat", Some(Noun)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse delete valid word with invalid part of speech") {
    val args = "vocab delete hat hehehehe"
    assertResult(Left(ParseErrorInvalidPartOfSpeech(Invalid("hehehehe")))) {
      CommandLine.parseArgs(args)
    }
  }

  // Parse Practice tests
  test("Parse practice invalid argument") {
    val args = "vocab practice I think therefore I am"
    assertResult(Left(ParseErrorInvalidPracticeArg("I"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice no args supplied") {
    val args = "vocab practice"
    assertResult(Right(Practice(None))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse `practice all`") {
    val args = "vocab practice all"
    assertResult(Right(Practice(Some(All)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse `practice half`") {
    val args = "vocab practice half"
    assertResult(Right(Practice(Some(Half)))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice numeric valid") {
    val args = "vocab practice 120"
    assertResult(Right(Practice(Some(ExplicitNumeric(120))))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice numeric invalid") {
    val args = "vocab practice -120"
    assertResult(Left(ParseErrorInvalidExplicitNumeric(-120))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice percentage valid") {
    val args = "vocab practice 0.4"
    assertResult(Right(Practice(Some(PercentageNumeric(0.4f))))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Parse practice percentage invalid") {
    val args = "vocab practice 69.69"
    assertResult(Left(ParseErrorInvalidPercentageNumeric(69.69f))) {
      CommandLine.parseArgs(args)
    }
  }
}
