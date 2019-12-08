package models

import storage._

sealed trait Command {
  def run(implicit storage: Storage): Unit
}

case class Add(word: String, definition: String, partOfSpeech: Option[SpeechPart]) extends Command {
  def run(implicit storage: Storage): Unit = ???
}

case class Modify(word: String, newDefinition: String, partOfSpeech: Option[SpeechPart]) extends Command {
  def run(implicit storage: Storage): Unit = ???
}

case class Delete(word: String, partOfSpeech: Option[SpeechPart]) extends Command {
  def run(implicit storage: Storage): Unit = ???
}

case class Practice(sessionType: Option[PracticeSessionType]) extends Command {
  def run(implicit storage: Storage): Unit = ???
}

case object Words extends Command {
  def run(implicit storage: Storage): Unit = ???
}

case object Help extends Command {
  def run(implicit storage: Storage): Unit = ???
}

case object NoArgs extends Command {
  def run(implicit storage: Storage): Unit = ???
}

case object Version extends Command {
  def run(implicit storage: Storage): Unit = ???
}
