import org.scalatest.funsuite.AnyFunSuite
import java.io._
import scala.io.Source

import storage._
import models._

class BaseCommandTests extends AnyFunSuite {
  import models.implicits._

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

}
