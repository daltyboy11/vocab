import org.scalatest.BeforeAndAfter
import models._

/*
class DeleteCommandTests extends BaseCommandTests with BeforeAndAfter {
  private def readonlyStorageWords: Seq[Word] = makeStorage("test_delete_words.csv",
    "practice_sessions_read_only.csv").getWords

  test("delete word with speech part") {
    val testDeleteWordsCopy = makeFileCopy("test_delete_words.csv")
    copyFiles += testDeleteWordsCopy
    implicit val storage = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    Delete("ardor", Some(Noun)).run

    val storageCopy = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    val expected = readonlyStorageWords.filter {
      case Word("ardor", "enthusiasm or passion", Some(Noun), 0) => false
      case _ => true
    }

    assertResult(expected) {
      storageCopy.getWords
    }
  }

  test("delete word without part of speech") {
    val testDeleteWordsCopy = makeFileCopy("test_delete_words.csv")
    copyFiles += testDeleteWordsCopy
    implicit val storage = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    Delete("test", None).run

    val storageCopy = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    val expected = readonlyStorageWords.filter {
      case Word("test", _, _, _) => false
      case _ => true
    }

    assertResult(expected) {
      storageCopy.getWords
    }
  }

  test("delete non-existent word") {
    val testDeleteWordsCopy = makeFileCopy("test_delete_words.csv")
    copyFiles += testDeleteWordsCopy
    implicit val storage = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    Delete("hahehahhaha", Some(Interjection)).run

    val storageCopy = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    assertResult(readonlyStorageWords) {
      storageCopy.getWords
    }
  }

  test("delete several words") {
    val testDeleteWordsCopy = makeFileCopy("test_delete_words.csv")
    copyFiles += testDeleteWordsCopy
    implicit val storage = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    Delete("test", Some(Adjective)).run
    Delete("ardor", Some(Noun)).run
    Delete("risible", Some(Adjective)).run
    Delete("nadir", None).run

    val storageCopy = makeStorage(testDeleteWordsCopy, "practice_sessions_read_only.csv")
    val expected = readonlyStorageWords filter {
      case Word("test", "this is a test word", Some(Adjective), 5) => false
      case Word("ardor", "enthusiasm or passion", Some(Noun), 0) => false
      case Word("risible", "such as to provoke laughter", Some(Adjective), 0) => false
      case Word("nadir", "the low point of the suffering of someone", None, 0) => false
      case _ => true
    }
    assertResult(expected) {
      storageCopy.getWords
    }
  }
}
*/
