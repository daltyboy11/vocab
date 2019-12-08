package models

sealed trait PracticeSessionType {
  val asString: String
}

// Practice Sesssion Types
case object All extends PracticeSessionType {
  override val asString: String = "all"
}
case object Half extends PracticeSessionType {
  override val asString: String = "half"
}
case class ExplicitNumeric(numWords: Int) extends PracticeSessionType {
  override val asString: String = numWords.toString
}
case class  PercentageNumeric(percentage: Float) extends PracticeSessionType {
  override val asString: String = percentage.toString
}

