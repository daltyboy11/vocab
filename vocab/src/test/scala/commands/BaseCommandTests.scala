/*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
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

  val projectDir = System.getProperty("user.dir")
  
  def makeStorage(wordFileName: String, practiceSessionFileName: String) =
    Storage(s"${projectDir}/src/test/scala/commands", wordFileName, practiceSessionFileName)

  def writeToFile(s: String, fileName: String): Unit = {
    val file = new File(s"${projectDir}/src/test/scala/commands/$fileName")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }

  def readFromWordFile(file: String): Seq[Word] = {
    val fileName = s"${projectDir}/src/test/scala/commands/$file"
    (for (line <- Source.fromFile(fileName).getLines) yield line.get[Word]).toSeq
  }

  def makeFileCopy(file: String): String = {
    val fullyQualifiedPath = s"${projectDir}/src/test/scala/commands/$file"
    val destinationSuffix = Random.alphanumeric.take(10).mkString("")
    val fullyQualifiedDestination = fullyQualifiedPath + destinationSuffix
    val src = new File(fullyQualifiedPath)
    val dest = new File(fullyQualifiedDestination)
    new FileOutputStream(dest).getChannel().transferFrom(
      new FileInputStream(src).getChannel(), 0, Long.MaxValue)
    file + destinationSuffix
  }

  def deleteFile(file: String) = { new File(s"${projectDir}/src/test/scala/commands/$file").delete() }
}
*/
