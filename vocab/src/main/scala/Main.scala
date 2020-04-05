import application._
import storage._
import java.nio.file.Paths

object Main {
  def main(args: Array[String]): Unit = {
    val projectPath = Paths.get(".").toAbsolutePath
    val storagePath = s"$projectPath/src/main/resources/"
    implicit val storage: Storage = Storage(storagePath)
    val app = Application[Application.State.ParseArgs]()
    app.parseArgs(args.tail.toIndexedSeq).runCommand
  }
}
