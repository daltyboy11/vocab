package models

sealed trait ParseError {
  def message: String
}
case class ParseErrorUnknown() extends ParseError {
  val message = "an unknown error ocurred during parsing"
}
case class ParseErrorInvalidWord(word: String) extends ParseError {
  val message = s"$word is not a valid word"
}
case class ParseErrorInvalidAddCommand() extends ParseError {
  val message = "error - usage: vocab add <word> <definition> [--type type]"
}
case class ParseErrorInvalidModifyCommand() extends ParseError {
  val message = ""
}
case class ParseErrorInvalidDeleteCommand() extends ParseError {
  val message = ""
}
case class ParseErrorInvalidPracticeCommand() extends ParseError {
  val message = ""
}

