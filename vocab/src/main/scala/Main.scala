import application._
import storage._

object Main extends App {
 
  println("Hello, World!")

  val storage: Storage = ???
  val app = Application[Application.State.ParseArgs](storage)

  app.parseArgs(List("Hello, World!")).runCommand.postCommand
}
