import application._
import storage._

object Main extends App {
 
  println("Hello, World!")

  implicit val storage: Storage = ???
  val app = Application[Application.State.ParseArgs]()

  app.parseArgs(List("Hello, World!")).runCommand
}
