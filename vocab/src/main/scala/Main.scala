import application._
import storage._
import io.github.soc.directories.ProjectDirectories
import java.nio.file.{Files, Paths}
import java.io.File

object Main {
  def main(args: Array[String]): Unit = {
    val dataDir = ProjectDirectories.from("io", "github.daltyboy11", "vocab").dataDir
    val wordPath = dataDir + "/main/words.csv"
    val practiceSessionPath = dataDir + "/main/practice_sessions.csv"

    // Create data directories and files if they don't already exist
    Files.createDirectories(Paths.get(dataDir + "/main"))
    (new File(wordPath)).createNewFile
    (new File(practiceSessionPath)).createNewFile

    implicit val storage: Storage = Storage(wordPath, practiceSessionPath)
    val app = Application[Application.State.ParseArgs]()
    app.parseArgs(args.tail.toIndexedSeq).runCommand
  }
}
