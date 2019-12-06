package implicits

import models._
import commands._
import speechparts._

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
  
  implicit class ModelAsString(stringRepr: String) {
    def get[T](implicit toT: String => T): T = toT(stringRepr)
  }

  implicit val stringToWord: String => Word = (s: String) => {
      val parts = s split ","
      val word = parts(0)
      val definition = parts(1)
      val speechPartOpt = parts(2).get[SpeechPart] match {
        case Invalid(_) => None
        case validSpeechPart => Some(validSpeechPart)
      }
      val numTimesPracticed = parts(3).toInt
      Word(word, definition, speechPartOpt, numTimesPracticed)
    }

  implicit val stringToPracticeSession: String => PracticeSession = (s: String) => {
      val parts = s split ","
      val sessionType = parts(0).get[PracticeSessionType]
      val numWords = parts(1).toInt
      val duration = parts(2).toInt
      val timestamp = parts(3).toInt
      val didFinish = parts(4).toBoolean
      PracticeSession(sessionType, numWords, duration, timestamp, didFinish)
  }

  implicit val stringToPracticeSessionType: String => PracticeSessionType = (s: String) => {
    s match {
      case All.asString => All
      case Half.asString => Half
      case possibleInt if s.toIntOption.isDefined => ExplicitNumeric(s.toInt)
      case possiblePercentage if s.toFloatOption.isDefined => PercentageNumeric(s.toFloat)
    }
  }

  implicit val stringToPartOfSpeech: String => SpeechPart = (s: String) => {
    s match {
      case Noun.asString => Noun
      case Verb.asString => Verb
      case Pronoun.asString => Pronoun
      case Adjective.asString => Adjective
      case Adverb.asString => Adverb
      case Preposition.asString => Preposition
      case Conjunction.asString => Conjunction
      case Interjection.asString => Interjection
      case _ => Invalid(s)
    }
  }
}
