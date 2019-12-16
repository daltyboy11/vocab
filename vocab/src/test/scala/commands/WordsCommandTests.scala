import models._
import storage._

class WordsCommandTests extends BaseCommandTests {
  test("split definition 1") {
    implicit val maxColumnWidth = 35
    val definition = Seq(
      "enthusiasm",
      "or",
      "passion")
    val expected = Seq("enthusiasm or passion")
    assertResult(expected) {
      Words.splitDefinition(definition)
    }
  }

  test("split definition 2") {
    implicit val maxColumnWidth = 35
    val definition = Seq(
      "the", "violent", "seizure", "of", "the", "property", "belonging", "to", "someone")
    val expected = Seq(
      "the violent seizure of the property",
      "belonging to someone")
    assertResult(expected) {
      Words.splitDefinition(definition)
    }
  }

  test("split definition 3") {
    implicit val maxColumnWidth = 5
    val definition = Seq(
      "four", "four", "four", "four", "four", "four")
    val expected = definition
    assertResult(expected) {
      Words.splitDefinition(definition)
    }
  }

  test("vocab words one") {
    implicit val storage = makeStorage("words_read_only.csv", "practice_sessions_read_only.csv")
    val expected = List(
    "------------------------------------------------------------------------------------------",
    "| word          | definition                          | part of speech | times practiced |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| ardor         | enthusiasm or passion               | noun           | 0               |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| irascible     | easily angered                      | adjective      | 1               |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| rapacious     | aggresively greedy or grasping      | adjective      | 1               |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| risible       | such as to provoke laughter         | adjective      | 0               |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| peregrination | a long and meandering journey       |                | 3               |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| rapine        | the violent seizure of the property |                | 10              |",
    "|               | belonging to someone                |                |                 |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| mellifluous   | sweet or musical sounding           | adjective      | 1               |",
    "|---------------|-------------------------------------|----------------|-----------------|",
    "| nadir         | the low point of the suffering of   |                | 0               |",
    "|               | someone                             |                |                 |",
    "------------------------------------------------------------------------------------------")
    assertResult(expected) {
      Words.generateTable
    }
  }
}
