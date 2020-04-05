package models

sealed trait ParseError {
  def message: String
}
case class ParseErrorUnsupportedCommand() extends ParseError {
  val message = "Invalid use. Try `vocab help` for proper usage."
}
case class ParseErrorInvalidWord(word: String) extends ParseError {
  val message = s"$word is not a valid word"
}
case class ParseErrorInvalidAddCommand(msg: String) extends ParseError {
  val message = s"Invalid use of `vocab add`: $msg"
}
case class ParseErrorInvalidModifyCommand(msg: String) extends ParseError {
  val message = s"Invalid use of `vocab modify`: $msg"
}
case class ParseErrorInvalidDeleteCommand(msg: String) extends ParseError {
  val message = s"Invalid use of `vocab delete`: $msg"
}
case class ParseErrorInvalidPracticeCommand(msg: String) extends ParseError {
  val message = s"Invalid use of `vocab practice`: $msg"
}
