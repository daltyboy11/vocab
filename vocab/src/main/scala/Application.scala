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

case class Application[S <: Application.State](command: Option[Command] = None, error: Option[ParseError] = None) { 
  import Application.State._
  import commandlineparser._

  def parseArgs(args: Seq[String])(implicit ev: S =:= ParseArgs, storage: Storage): Application[RunCommand] =
    CommandLine.parseArgs(args mkString " ") match {
      case Left(e) => Application(None, Some(e))
      case Right(c) => Application(Some(c))
    }

  def runCommand(implicit ev: S =:= RunCommand, storage: Storage): Application[PostCommand] =
    if (command.isDefined) {
      command.get.run
      Application(command)
    } else {
      println(error.get.message)
      Application(None, error)
    }

  def postCommand(implicit ev: S =:= PostCommand, storage: Storage): Application[PostCommand] =
    Application(command, error)
}
