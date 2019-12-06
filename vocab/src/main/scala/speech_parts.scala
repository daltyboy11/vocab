package speechparts 

sealed trait SpeechPart {
  val asString: String
}

// Word Types
case object Noun extends SpeechPart {
  override val asString: String = "noun"
}

case object Verb extends SpeechPart {
  override val asString: String = "verb"
}

case object Pronoun extends SpeechPart {
  override val asString: String = "pronoun"
}

case object Adjective extends SpeechPart {
  override val asString: String = "adjective"
}

case object Adverb extends SpeechPart {
  override val asString: String = "adverb"
}

case object Preposition extends SpeechPart {
  override val asString: String = "preposition"
}

case object Conjunction extends SpeechPart {
  override val asString: String = "conjunction"
}

case object Interjection extends SpeechPart {
  override val asString: String = "interjection"
}

case class Invalid(given: String) extends SpeechPart {
  override val asString: String = "invalid"
}

