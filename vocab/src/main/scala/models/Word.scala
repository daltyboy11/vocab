package models

case class Word(
  word: String, 
  definition: String,
  partOfSpeech: Option[SpeechPart],
  numTimesPracticed: Int // how many times it has been a part of a successful practice session
) extends CSVRepresentable

