import org.scalatest.funsuite.AnyFunSuite
import java.io.{File, FileInputStream, FileOutputStream}
import io.github.soc.directories.ProjectDirectories

import models._
import storage._

class StorageTests extends AnyFunSuite {

  val dataDir = ProjectDirectories.from("io", "github.daltyboy11", "vocab").dataDir + "/test"

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
    val storage = Storage(dataDir + "/words1.csv", dataDir + "/practice_sessions1.csv")
    assertResult(csv1Words) {
      storage.getWords
    }
    assertResult(csv1PracticeSessions) {
      storage.getPracticeSessions
    }
  }

  test("addWord succesfully inserts word") {
    val storage = Storage(dataDir + "/words1.csv", dataDir + "/practice_sessions1.csv")
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
    val storage = Storage(dataDir + "/words1.csv", dataDir + "/practice_sessions1.csv")
    val practiceSessionToAdd = PracticeSession(All, 1, 1, 1, true)
    assertResult(csv1PracticeSessions :+ practiceSessionToAdd) {
      storage.addPracticeSession(practiceSessionToAdd)
      storage.getPracticeSessions
    }
  }

  test("commit") {
    // Set up - create temp files
    val srcWordsFile = new File(dataDir + "/words1.csv")
    val srcPracticeSessionsFile = new File(dataDir + "/practice_sessions1.csv")
    val destWordsFile = new File(dataDir + "/words2.csv")
    val destPracticeSessionsFile = new File(dataDir + "/practice_sessions2.csv")
    (new FileOutputStream( destWordsFile ))
      .getChannel
      .transferFrom( new FileInputStream( srcWordsFile ).getChannel, 0, Long.MaxValue )
    (new FileOutputStream( destPracticeSessionsFile ))
      .getChannel
      .transferFrom( new FileInputStream( srcPracticeSessionsFile ).getChannel, 0, Long.MaxValue )

    // Test that committing will overwrite the contents of the storage files
    val storage = Storage(dataDir + "/words2.csv", dataDir + "/practice_sessions2.csv")

    val word1 = Word( "hoise", "to lift raise", Some( Verb ), 0 )
    val word2 = Word( "ambidextrous", "using both hands with equal dexterity", Some( Adjective ), 5 )
    val practiceSession1 = PracticeSession( All, 10, 100000, 123456789, true )
    storage addWord word1
    storage addWord word2
    storage deleteWords "rapine"
    storage addPracticeSession practiceSession1
    storage.commit()

    val expectedWords = Seq(
      Word("ardor", "enthusiasm or passion", Some(Noun), 0),
      Word("irascible", "easily angered", Some(Adjective), 1),
      Word("rapacious", "aggresively greedy or grasping", Some(Adjective), 1),
      Word("risible","such as to provoke laughter", Some(Adjective), 0),
      Word("peregrination", "a long and meandering journey", None, 3),
      Word("mellifluous", "sweet or musical sounding", Some(Adjective), 1),
      Word("nadir", "the low point of the suffering of someone", None, 0),
      word1,
      word2)

    val expectedPracticeSessions = Seq(
      PracticeSession(All, 1, 1, 1, true),
      PracticeSession(Half, 1, 1, 1, true),
      PracticeSession(ExplicitNumeric(1), 1, 1, 1, true),
      PracticeSession(PercentageNumeric(0.5f), 1, 1, 1, false),
      practiceSession1)

    // Create a new storage with the same source files. This will be a fresh reload of the files
    val testStorage = Storage(dataDir + "/words2.csv", dataDir + "/practice_sessions2.csv")

    assertResult( expectedWords ) {
      testStorage.getWords
    }
    assertResult( expectedPracticeSessions ) {
      testStorage.getPracticeSessions
    }

    destWordsFile.delete()
    destPracticeSessionsFile.delete()
  }

  test("increment word counts") {
    val storage = Storage(dataDir + "/words1.csv", dataDir + "/practice_sessions1.csv")

    val wordsToIncrement = Set(
      Word("ardor", "enthusiasm or passion", Some(Noun), 0),
      Word("peregrination", "a long and meandering journey", None, 3),
      Word("nadir", "the low point of the suffering of someone", None, 0))
    val expected = csv1Words map {
      case word => word.word match {
        case "ardor" | "peregrination" | "nadir" =>
          Word(word.word, word.definition, word.partOfSpeech, word.numTimesPracticed + 1)
        case _ => word
      }
    }

    assertResult(expected) {
      storage.incrementPracticeCounts(wordsToIncrement)
      storage.getWords
    }
  }
}
