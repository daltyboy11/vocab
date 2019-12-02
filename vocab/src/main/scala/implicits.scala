package implicits

import models._

// Implicit conversions between csv entry and data models

object Implicits {
  sealed trait ToCSVRepr {
    def toCSVRepr(): String
  }

  implicit class WordToCSVRepr(word: Word) extends ToCSVRepr {
    def toCSVRepr(): String = {
      val partOfSpeechString = word.partOfSpeech match {
        case None => ""
        case Some(partOfSpeech) => partOfSpeech.asString
      }
      word.word + "," + word.definition + "," + partOfSpeechString + "," + word.numTimesPracticed.toString
    }
  }

  implicit class PracticeSessionToCSVRepr(session: PracticeSession) extends ToCSVRepr {
    def toCSVRepr(): String = {
      session.sessionType.asString + "," + session.numWords.toString + "," + session.duration.toString + "," + session.timestamp.toString + "," + session.didFinish.toString
    }
  }

  // Convert a word represented as a CSV row to an instance of Word
  implicit class StringReprToWord(stringRepr: String) {
    def toWord(): Word = {
      val parts = stringRepr split ","
      val word = parts(0)
      val definition = parts(1)
      val speechPart = parts(2) match {
        case "" => None
        case speechPartStringRepr => Some(parts(2).toSpeechPart())
      }
      val numTimesPracticed = parts(3).toInt
      Word(word, definition, speechPart, numTimesPracticed)
    }
  }

  implicit class StringReprToPracticeSession(stringRepr: String) {
    def toPracticeSession(): PracticeSession = {
      val parts = stringRepr split ","
      val sessionType = parts(0).toPracticeSessionType
      val numWords = parts(1).toInt
      val duration = parts(2).toInt
      val timestamp = parts(3).toInt
      val didFinish = parts(4).toBoolean
      PracticeSession(sessionType, numWords, duration, timestamp, didFinish)
    }
  }

  implicit class StringReprToPracticeSessionType(stringRepr: String) {
    def toPracticeSessionType(): PracticeSessionType = stringRepr match {
      case All.asString => All
      case Half.asString => Half
      case possibleInt if stringRepr.toIntOption.isDefined => ExplicitNumeric(stringRepr.toInt)
      case possiblePercentage if stringRepr.toFloatOption.isDefined => PercentageNumeric(stringRepr.toFloat)
    }
  }

  // Convert a part of speech represented as a string to a SpeechPart instance
  implicit class StringReprToSpeechPart(stringRepr: String) {
    def toSpeechPart(): SpeechPart = stringRepr match {
      case Noun.asString => Noun
      case Verb.asString => Verb
      case Pronoun.asString => Pronoun
      case Adjective.asString => Adjective
      case Adverb.asString => Adverb
      case Preposition.asString => Preposition
      case Conjunction.asString => Conjunction
      case Interjection.asString => Interjection
      case _ => Invalid(stringRepr)
    }
  }
}
