package application

import models.{Command, ParseError}
import storage._

object Application {
  // use phantom types to enforce the ordering parseArgs -> runCommand -> postCommand at compile time.
  sealed trait State
  object State {
    sealed trait ParseArgs extends State
    sealed trait RunCommand extends State
    sealed trait PostCommand extends State
  }
}

case class Application[S <: Application.State](storage: Storage, command: Option[Command] = None, error: Option[ParseError] = None) { 

  import Application.State._
  import commandlineparser._

  implicit val s = storage

  def parseArgs(args: Seq[String])(implicit ev: S =:= ParseArgs): Application[RunCommand] = CommandLine.parseArgs(args mkString " ") match {
    case Left(e) => Application(storage, None, Some(e))
    case Right(c) => Application(storage, Some(c))
  }

  def runCommand(implicit ev: S =:= RunCommand): Application[PostCommand] =
    if (command.isDefined) {
      command.get.run
      Application(storage, command)
    } else {
      println(error.get.message)
      Application(storage, None, error)
    }

  def postCommand(implicit ev: S =:= PostCommand): Application[PostCommand] = {
    Application(storage, command, error)
  }
}
