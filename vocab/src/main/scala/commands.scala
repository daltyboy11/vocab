package commands

import models._

sealed trait Command

case class  Add(word: String, definition: String, partOfSpeech: Option[SpeechPart])         extends Command
case class  Modify(word: String, newDefinition: String, partOfSpeech: Option[SpeechPart])   extends Command
case class  Delete(word: String, partOfSpeech: Option[SpeechPart])                          extends Command
case class  Practice(sessionType: Option[PracticeSessionType])                              extends Command
case object Words                                                                           extends Command
case object Help                                                                            extends Command
case object NoArgs                                                                          extends Command
case object Version                                                                         extends Command


