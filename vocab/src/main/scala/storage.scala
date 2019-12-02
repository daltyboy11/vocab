package implicits

import models._

// Implicit conversions between csv entry and data models

object Interpolators {
  sealed trait ToCSVRepr {
    def toCSVRepr(): String
  }

  implicit class WordToCSVRepr(word: Word) extends ToCSVRepr {
    def toCSVRepr(): String = {
      val partOfSpeechString = word.partOfSpeech match {
        case None => ""
        case Some(partOfSpeech) => partOfSpeech.toString
      }
      word.word + "," + word.definition + "," + partOfSpeechString + "," + word.numTimesPracticed.toString
    }
  }

  implicit class PracticeSessionToCSVRepr(session: PracticeSession) extends ToCSVRepr {
    def toCSVRepr(): String = {
      session.sessionType.toString + "," + session.numWords.toString + "," + session.duration.toString + "," + session.timestamp.toString + "," + session.didFinish.toString
    }
  }

  sealed trait CSVReprToModel[R <: CSVRepresentable] {
    def toModel(): R
  }

  implicit class CSVReprToWord(stringRepr: String) extends CSVReprToModel[Word] {
    override def toModel(): Word = {
      val parts = stringRepr split ","
      Word(parts(0), parts(1), None, 0)
    }
  }
}
