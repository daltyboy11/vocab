import org.scalatest.BeforeAndAfter
import scala.collection.mutable.ListBuffer

class DeleteCommandTests extends BaseCommandTests with BeforeAndAfter {
  private var copyFiles = new ListBuffer[String]()
  after {
    copyFiles foreach deleteFile
  }

  test("delete existing word") {
    // TODO
    val testDeleteWordsCopy = makeFileCopy("test_delete_words.csv")
    copyFiles += testDeleteWordsCopy
  }

  test("delete non-existent word") {
    // TODO
    val testDeleteWordsCopy = makeFileCopy("test_delete_words.csv")
    copyFiles += testDeleteWordsCopy
  }

  test("delete several words") {
    // TODO
    val testDeleteWordsCopy = makeFileCopy("test_delete_words.csv")
    copyFiles += testDeleteWordsCopy
  }
}
