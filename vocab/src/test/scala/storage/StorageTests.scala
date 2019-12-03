import org.scalatest.FunSuite

import models._
import storage._

class StorageTests extends FunSuite {

  val projectDir = System.getProperty("user.dir")

  val csv1Words = Seq(
    Word("ardor", "enthusiasm or passion", Some(Noun), 0),
    Word("irascible", "easily angered", Some(Adjective), 1),
    Word("rapacious", "aggresively greedy or grasping", Some(Adjective), 1),
    Word("risible","such as to provoke laughter", Some(Adjective), 0),
    Word("peregrination", "a long and meandering journey", None, 3),
    Word("rapine", "the violent seizure of the property belonging to someone", None, 10),
    Word("mellifluous", "sweet or musical sounding", Some(Adjective), 1),
    Word("nadir", "the low point of the suffering of someone", None, 0))

  val csv1PracticeSessions = Seq(
    PracticeSession(All, 1, 1, 1, true),
    PracticeSession(Half, 1, 1, 1, true),
    PracticeSession(ExplicitNumeric(1), 1, 1, 1, true),
    PracticeSession(PercentageNumeric(0.5f), 1, 1, 1, false))

  test("word and practice session test data successfully loads") {
    val storage = Storage(s"${projectDir}/src/test/scala/storage",
      "words1.csv",
      "practice_sessions1.csv")

    assertResult(csv1Words) {
      storage.getWords
    }

    assertResult(csv1PracticeSessions) {
      storage.getPracticeSessions
    }
  }

  test("addWord succesfully inserts word") {
   val storage = Storage(s"${projectDir}/src/test/scala/storage",
    "words1.csv",
    "practice_sessions1.csv")

   val wordToAdd = Word("peremptory",
      "insistion on immediate attention or obedience",
      Some(Adjective),
      0)

   assertResult(csv1Words :+ wordToAdd) {
     storage.addWord(wordToAdd)
     storage.getWords
   }
  }

  test("addPracticeSession succesfully inserts practice session") {
    val storage = Storage(s"${projectDir}/src/test/scala/storage",
    "words1.csv",
    "practice_sessions1.csv")

    val practiceSessionToAdd = PracticeSession(All, 1, 1, 1, true)
    assertResult(csv1PracticeSessions :+ practiceSessionToAdd) {
      storage.addPracticeSession(practiceSessionToAdd)
      storage.getPracticeSessions
    }
  }

  test("test commit") {
    // create temporary files words2.csv and practice_sessions2.csv because
    // committing overwrites the storage files and I need those for the other
    // tests...
    // TODO
  }

}
