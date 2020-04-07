import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import java.nio.file.Paths
import scala.util.Random
import scala.collection.mutable.ListBuffer
import java.io._
import scala.io.Source

import storage._
import models._

class BaseCommandTests extends AnyFunSuite with BeforeAndAfter {
  import models.implicits._

  protected var copyFiles = new ListBuffer[String]()
  after {
    copyFiles foreach deleteFile
  }

  val dataDir = Paths.get(".").toAbsolutePath.toString + "/src/test/scala/test_data"
  
  def makeStorage(wordFileName: String, practiceSessionFileName: String) =
    Storage(dataDir + s"/$wordFileName", dataDir + s"/$practiceSessionFileName")

  def writeToFile(s: String, fileName: String): Unit = {
    val file = new File(dataDir + s"/$fileName")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }

  def readFromWordFile(file: String): Seq[Word] = {
    val filePath = dataDir + s"/$file"
    (for (line <- Source.fromFile(filePath).getLines) yield line.get[Word]).toSeq
  }

  def makeFileCopy(file: String): String = {
    val fullyQualifiedPath = dataDir + s"/$file"
    val destinationSuffix = Random.alphanumeric.take(10).mkString("")
    val fullyQualifiedDestination = fullyQualifiedPath + destinationSuffix
    val src = new File(fullyQualifiedPath)
    val dest = new File(fullyQualifiedDestination)
    new FileOutputStream(dest).getChannel().transferFrom(
      new FileInputStream(src).getChannel(), 0, Long.MaxValue)
    file + destinationSuffix
  }

  def deleteFile(file: String) = new File(dataDir + s"/$file").delete()
}
