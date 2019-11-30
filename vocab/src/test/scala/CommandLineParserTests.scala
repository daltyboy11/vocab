import org.scalatest.FunSuite

import commandlineparser._

class CommandLineParserTests extends FunSuite {
  // Parse Add tests
  test("Add no part of speech") {
    val args = "vocab add ardor enthusiasm or passion"
    assertResult(Add("ardor", "enthusiasm or passion", None)) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech after") {
    val args = "vocab add ardor enthusiasm or passion --noun some excess words"
    assertResult(Add("ardor", "enthusiasm or passion", Some(Noun))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with invalid part of speech") {
    val args = "vocab add ardor enthusiasm or passion --notAValidType"
    assertResult(Unknown(Some("notAValidType is not a valid part of speech"))) {
      CommandLine.parseArgs(args)
    }
  }

  test("Add with part of speech first") {
    val args = "vocab add benighted --adjective in a state of contemptible moral or intellectual ignorance typically owing to a lack of opportunity"
    assertResult(Add("benighted", "in a state of contemptible moral or intellectual ignorance typically owning to a lack of opportunity", Some(Adjective))) {
      CommandLine.parseArgs(args)
    }
  }

}
