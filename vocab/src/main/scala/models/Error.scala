package models

sealed trait ParseError {
  def message: String
}
case class ParseErrorUnsupportedCommand() extends ParseError {
  val message = "Try `vocab help` for proper usage."
}
case class ParseErrorInvalidWord(word: String) extends ParseError {
  val message = s"$word is not a valid word."
}
case class ParseErrorInvalidAddCommand() extends ParseError {
  val message = "Invalid usage of vocab add. Try `vocab help` for proper usage."
}
case class ParseErrorInvalidModifyCommand() extends ParseError {
  val message = "Invalid usage of vocab modify. Try `vocab help` for proper usage."
}
case class ParseErrorInvalidDeleteCommand() extends ParseError {
  val message = "Invalid usage of vocab delete. Try `vocab help` for proper usage."
}
case class ParseErrorInvalidPracticeCommand() extends ParseError {
  val message = "Invalid usage of vocab practice. Try `vocab help` for proper usage."
}

