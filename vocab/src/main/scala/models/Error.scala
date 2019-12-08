package models

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
