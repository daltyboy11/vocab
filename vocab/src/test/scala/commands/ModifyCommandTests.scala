import org.scalatest.BeforeAndAfter
import models._

/*
class ModifyCommandTests extends BaseCommandTests with BeforeAndAfter {
  private def readonlyStorageWords = makeStorage("test_modify_words.csv",
    "practice_sessions_read_only.csv").getWords

  test("modify existing words") {
    val testModifyWordsCopy = makeFileCopy("test_modify_words.csv")
    copyFiles += testModifyWordsCopy
    implicit val storage = makeStorage(testModifyWordsCopy, "practice_sessions_read_only.csv")
    Modify("ardor", "enthusiasm and passion", Some(Noun)).run
    Modify("ardor", "blah blah blah", None).run
    Modify("ardor", "mua ha ha ha", Some(Noun)).run
    Modify("irascible", "angered easily", Some(Adjective)).run

    val storageCopy = makeStorage(testModifyWordsCopy, "practice_sessions_read_only.csv")
    val expected = Word("ardor", "mua ha ha ha", Some(Noun), 0) +: Word("irascible", "angered easily", Some(Adjective), 1) +: readonlyStorageWords.tail.tail
    assertResult(expected) {
      storageCopy.getWords
    }
  }

  test("modify some words") {
    val testModifyWordsCopy = makeFileCopy("test_modify_words.csv")
    copyFiles += testModifyWordsCopy
    implicit val storage = makeStorage(testModifyWordsCopy, "practice_sessions_read_only.csv")

    Modify("risible", "provoking laughter", Some(Adjective)).run
    Modify("nadir", "the low point of suffering", None).run

    val storageCopy = makeStorage(testModifyWordsCopy, "practice_sessions_read_only.csv")

    val indexOfRisbile = readonlyStorageWords.zipWithIndex.filter {
      case (w, _) => w.word == "risible"
    }.head._2
    val indexOfNadir = readonlyStorageWords.zipWithIndex.filter {
      case (w, _) => w.word == "nadir"
    }.head._2
    val expected = readonlyStorageWords
      .updated(indexOfRisbile, Word("risible", "provoking laughter", Some(Adjective), 0))
      .updated(indexOfNadir, Word("nadir", "the low point of suffering", None, 0))
    assertResult(expected) {
      storageCopy.getWords
    }
  }
}
*/
