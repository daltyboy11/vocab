package models

import storage._
import scala.io.StdIn.readLine

case object Clear extends Command {
  def run(implicit storage: Storage): Unit = {
    def go(input: String): Unit = input match {
      case "yes" => {
        storage.clear()
        storage.commit()
      }
      case "no" => ()
      case _ => go(readLine("Are you sure you want to delete all data? (yes/no): "))
    }
    go("")
  }
}
