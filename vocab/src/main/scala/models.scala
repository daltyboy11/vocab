package models

// Top of the hierarchy for supported commands
sealed trait Command

// Top of the hierarchy for parts of speech. Used to further categorize words
// beyond the spelling of the word itself
sealed trait SpeechPart

// Top of the hierarchy for practice session types. Type not be the right word
// here because really its the number of words in the practice session that
// changes, not the form or structure of the session itself.
sealed trait PracticeSessionType

// Word Types
case object Noun extends SpeechPart {
  override def toString(): String = "noun"
}

case object Verb extends SpeechPart {
  override def toString(): String = "verb"
}

case object Pronoun extends SpeechPart {
  override def toString(): String = "pronoun"
}

case object Adjective extends SpeechPart {
  override def toString(): String = "adjective"
}

case object Adverb extends SpeechPart {
  override def toString(): String = "adverb"
}

case object Preposition extends SpeechPart {
  override def toString(): String = "preposition"
}

case object Conjuction extends SpeechPart {
  override def toString(): String = "conjuction"
}

case object Interjection extends SpeechPart {
  override def toString(): String = "interjection"
}

case class Invalid(given: String) extends SpeechPart

// Practice Sesssion Types
case object All extends PracticeSessionType {
  override def toString(): String = "all"
}
case object Half extends PracticeSessionType {
  override def toString(): String = "half"
}
case class ExplicitNumeric(numWords: Int) extends PracticeSessionType {
  override def toString(): String = numWords.toString
}
case class  PercentageNumeric(percentage: Float) extends PracticeSessionType {
  override def toString(): String = percentage.toString
}

// Commands
case class  Add(word: String, definition: String, partOfSpeech: Option[SpeechPart])         extends Command
case class  Modify(word: String, newDefinition: String, partOfSpeech: Option[SpeechPart])   extends Command
case class  Delete(word: String, partOfSpeech: Option[SpeechPart])                          extends Command
case class  Practice(sessionType: Option[PracticeSessionType])                              extends Command
case object Words                                                                           extends Command
case object Help                                                                            extends Command
case object NoArgs                                                                          extends Command
case object Version                                                                         extends Command

// Data Models
sealed trait CSVRepresentable
case class Word(
  word: String, 
  definition: String,
  partOfSpeech: Option[SpeechPart],
  numTimesPracticed: Int // how many times it has been a part of a successful practice session
) extends CSVRepresentable

case class PracticeSession(
  sessionType: PracticeSessionType,
  numWords: Int, // the total number of words in the practice session
  duration: Int, // the duration of the practice session in seconds
  timestamp: Int, // the unix timestamp of when the practice session started
  didFinish: Boolean // a boolean indicating if the practice session was finished or was interrupted by the user
) extends CSVRepresentable

// Informative parsing errors
sealed trait ParseError {
  def message: String
}
case class ParseErrorUnknown() extends ParseError {
  val message = "an unknown error ocurred during parsing"
}
case class ParseErrorNoArgs() extends ParseError {
  val message = "no arguments were supplied"
}
case class ParseErrorUnsupportedCommand(command: String) extends ParseError {
  val message = s"$command is not a supported command"
}
case class ParseErrorUnexpectedNonAlphabeticalToken(token: String) extends ParseError {
  val message = s"received $token but expected an alphabetical string"
}
case class ParseErrorMissingDefinition(word: String) extends ParseError {
  val message = s"missing definition for supplied word: $word"
}
case class ParseErrorInvalidPartOfSpeech(invalid: Invalid) extends ParseError {
  val message = s"${invalid.given} is not a valid part of speech"
}
case class ParseErrorInvalidPercentageNumeric(float: Float) extends ParseError {
  val message = s"${float} is not a decimal in the range (0, 1]"
}
case class ParseErrorInvalidExplicitNumeric(int: Int) extends ParseError {
  val message = s"${int} must be greater than 0"
}
case class ParseErrorInvalidPracticeArg(arg: String) extends ParseError {
  val message = s"${arg} is not a valid argument for starting a practice session"
}

