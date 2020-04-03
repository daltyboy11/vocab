package models
import storage._

trait Command {
  def run(implicit storage: Storage): Unit
}

case object NoArgs extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO: implement NoArgs command
  }
}

case object Version extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO: implement Version command
  }
}
