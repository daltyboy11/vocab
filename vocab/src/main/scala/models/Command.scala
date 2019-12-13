package models

import scala.annotation.tailrec
import storage._

sealed trait Command {
  def run(implicit storage: Storage): Unit
}

case class Add(word: String, definition: String, partOfSpeech: Option[SpeechPart]) extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

case class Modify(word: String, newDefinition: String, partOfSpeech: Option[SpeechPart]) extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

case class Delete(word: String, partOfSpeech: Option[SpeechPart]) extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

case class Practice(sessionType: Option[PracticeSessionType]) extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

case object Words extends Command {

  def splitDefinition(definition: Seq[String])(implicit maxColumnWidth: Int): Seq[String] = {
    @tailrec
    def splitDefinitionAcc(definition: Seq[String], result: Seq[String]): Seq[String] = {
      val headAndTail = definition.foldLeft((Array.empty[String], Array.empty[String])) { (headAndTailAcc, word) =>
        // TODO - carry the total length of elements in the head through the
        // fold left to avoid recomputation
        val numWhitespaceChars = headAndTailAcc._1.length - 1
        // TODO - carry the total num of chars in head through the fold left to
        // avoid recomputation
        val totalCharsOnLine = numWhitespaceChars + headAndTailAcc._1.map(_.length).reduce(_ + _)
        if (totalCharsOnLine + word.length + 1 < maxColumnWidth) {
          (headAndTailAcc._1 :+ word, headAndTailAcc._2)
        } else {
          (headAndTailAcc._1, headAndTailAcc._2 :+ word)
        }
      }

      if (headAndTail._2.length == 0) {
        // we are done
        result ++ List(headAndTail._1.mkString(" "))
      } else {
        splitDefinitionAcc(headAndTail._2, result ++ List(headAndTail._1.mkString(" ")))
      }
    }
    splitDefinitionAcc(definition, Array.empty[String])
  }

  def run(implicit storage: Storage): Unit = {
    implicit val maxColumnWidth = 35

    val words = storage.getWords
    val maxWordLength = words.map(_.word.length).max
    val maxNumDigits = words.map(_.numTimesPracticed.toString.length).max
    val maxLengthPartOfSpeech = Interjection.asString.length

    // The first has the word, num times practiced, speech part, and the first
    // part of the definition. The rest of the lines are just the definition
    val lines = for {
      word <- words
      partOfSpeech = if (word.partOfSpeech.isDefined) word.partOfSpeech.get.asString else ""
      (definitionPart, index) <- splitDefinition(word.definition.split(" ")).zipWithIndex
    } yield {
      if (index == 0) {
        "| " +
        word.word +
        " " * (maxWordLength - word.word.length) +
        " | " +
        definitionPart +
        " " * (maxColumnWidth - definitionPart.length) +
        "| " +
        partOfSpeech +
        " " * (maxLengthPartOfSpeech - partOfSpeech.length) +
        " | " +
        word.numTimesPracticed.toString +
        " " * (maxNumDigits - word.numTimesPracticed.toString.length) +
        " |\n"
      } else {
        "|" +
        " " * (maxWordLength + 2) +
        definitionPart + " " * (maxColumnWidth - definitionPart.length) +
        "|" +
        " " * (maxLengthPartOfSpeech + 2) +
        "|" +
        " " * (maxNumDigits + 2) +
        "|\n"
      }
    }

    lines foreach println
  }
}

case object Help extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

case object NoArgs extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

case object Version extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}
