package models
import storage._

trait Command {
  def run(implicit storage: Storage): Unit
}

case object Version extends Command {
  def run(implicit storage: Storage): Unit = {
    val version = getClass.getPackage.getImplementationVersion
    println(s"vocab $version")
  }
}
