import models._
import org.scalatest.BeforeAndAfter
import scala.collection.mutable.ListBuffer

class AddCommandTests extends BaseCommandTests with BeforeAndAfter {
  private var copyFiles = new ListBuffer[String]()
  after {
    copyFiles foreach deleteFile
  }

  private def readonlyStorageWords: Seq[Word] = makeStorage("words_read_only.csv", "practice_sessions_read_only.csv").getWords

  test("add new word") {
    val testAddWordsCopy = makeFileCopy("test_add_words.csv")
    copyFiles += testAddWordsCopy
    implicit val addStorage = makeStorage(
      testAddWordsCopy,
      "practice_sessions_read_only.csv")

    Add("nadir", "the low point of the suffering of someone", None).run
    val addStorageCopy = makeStorage(testAddWordsCopy, "practice_sessions_read_only.csv")
    assertResult(readonlyStorageWords) {
        addStorageCopy.getWords
    }
    deleteFile(testAddWordsCopy)
  }

  test("add existing word") {
    val testAddWordsCopy = makeFileCopy("test_add_words.csv")
    copyFiles += testAddWordsCopy
    implicit val addStorage = makeStorage(
      testAddWordsCopy,
      "practice_sessions_read_only.csv")
    Add("ardor", "enthusiasm or passion", Some(Noun)).run
    val addStorageCopy = makeStorage(testAddWordsCopy, "practice_sessions_read_only.csv")
    // copy should equal the original
    assertResult(makeStorage("test_add_words.csv", "practice_sessions_read_only.csv").getWords) {
      addStorageCopy.getWords
    }
    deleteFile(testAddWordsCopy)
  }
}
