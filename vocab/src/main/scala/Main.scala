import application._
import storage._
import java.nio.file.Paths
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {
    implicit val storage: Storage = Storage()
    val app = Application[Application.State.ParseArgs]()
    app.parseArgs(args.tail.toIndexedSeq).runCommand
  }
}
