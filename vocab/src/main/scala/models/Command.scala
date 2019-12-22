package models

import scala.annotation.tailrec
import storage._

sealed trait Command {
  def run(implicit storage: Storage): Unit
}

case class Add(word: String, definition: String, partOfSpeech: Option[SpeechPart]) extends Command {
  def run(implicit storage: Storage): Unit = {
    val words = storage.getWords
    val wordToAdd = Word(word, definition, partOfSpeech, 0)
    val addMessage = if (partOfSpeech.isDefined) s"$word - ${partOfSpeech.get} added" else s"$word added"
    val dupes = words filter (_.word == word)

    // If our partOfSpeech is None then no entry can exist for word
    // If our partOfSpeech is Some then the word can be added if there is no
    // word with None AND the word with the same Some does not exist.
    val consoleOutput = partOfSpeech match {
      case None => {
        if (dupes.isEmpty) {
          storage.addWord(wordToAdd)
          storage.commit
          addMessage
        } else {
          val dupe = dupes.head
          dupe.partOfSpeech match {
            case None => s"${dupe.word} already exists"
            case Some(speechPart) => s"You can't add ${dupe.word} without part of speech because ${dupe.word} - ${speechPart} already exists"
          }
        }
      }
      case Some(speechPart) => {
        if (dupes map (_.partOfSpeech) contains None) {
          s"You can't add $word - $speechPart because $word without a part of speech already exists"
        } else if (dupes map (_.partOfSpeech) contains partOfSpeech) {
          s"You can't add $word - $speechPart because $word - $speechPart already exists"
        } else {
          storage.addWord(wordToAdd)
          storage.commit
          addMessage
        }
      }
    }
    
    println(consoleOutput)
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

  implicit val maxColumnWidth = 35

  val wordsColumnTitle = "Words"
  val definitionColumnTitle = "Definition"
  val partOfSpeechColumnTitle = "Part of Speech"
  val timesPracticedColumnTitle = "Times Practiced"

  def splitDefinition(definition: Seq[String])(implicit maxColumnWidth: Int): Seq[String] = {
    @tailrec
    def splitDefinitionAcc(definition: Seq[String], result: Seq[String]): Seq[String] = {
      val headAndTail = definition.foldLeft((Seq.empty[String], Seq.empty[String])) { case ((head, tail), word) =>
        val numWhitespaceChars = if (head.length > 1) head.length - 1 else 0
        val totalCharsOnLine = numWhitespaceChars + (if (head.length > 0) head.map(_.length).reduce(_ + _) else 0)
        if (totalCharsOnLine + word.length + 1 <= maxColumnWidth) {
          (head :+ word, tail)
        } else {
          (head, tail :+ word)
        }
      }

      if (headAndTail._2.length == 0) {
        // we are done
        result ++ List(headAndTail._1.mkString(" "))
      } else {
        splitDefinitionAcc(headAndTail._2, result ++ List(headAndTail._1.mkString(" ")))
      }
    }
    splitDefinitionAcc(definition, Seq.empty[String])
  }
  
  def generateTable(implicit storage: Storage): Seq[String] = {
    val words = storage.getWords
    val wordColumnWidth = words.map(_.word.length).max
    val definitionColumnWidth = this.maxColumnWidth
    val partOfSpeechColumnWidth = this.partOfSpeechColumnTitle.length
    val timesPracticedColumnWidth = this.timesPracticedColumnTitle.length

    val header = "| " +
                 "word" + " " * (wordColumnWidth - "word".length) +
                 " | " +
                 "definition" + " " * (definitionColumnWidth - "definition".length) +
                 " | " +
                 "part of speech" + " " * (partOfSpeechColumnWidth - "part of speech".length) +
                 " | " +
                 "times practiced" + " " * (timesPracticedColumnWidth - "times practiced".length) +
                 " |"

    // 8 for whitespace, 5 for vertical lines
    val endBorder = "-" * (wordColumnWidth + definitionColumnWidth + partOfSpeechColumnWidth + timesPracticedColumnWidth + 8 + 5) 

    val middleBorder = "|" + "-" * (wordColumnWidth + 2) +
                       "|" + "-" * (definitionColumnWidth + 2) +
                       "|" + "-" * (partOfSpeechColumnWidth + 2) +
                       "|" + "-" * (timesPracticedColumnWidth + 2) +
                       "|"

    val lines = words.zipWithIndex flatMap { case (word, wordIndex) =>
      val partOfSpeech = if (word.partOfSpeech.isDefined) word.partOfSpeech.get.asString else ""
      val definitionLines = splitDefinition(word.definition.split(" "))
      definitionLines.zipWithIndex flatMap { case (definitionLine, definitionIndex) =>
        val line = if (definitionIndex == 0) {
          "| " +
          word.word +
          " " * (wordColumnWidth - word.word.length) +
          " | " +
          definitionLine +
          " " * (definitionColumnWidth - definitionLine.length) +
          " | " +
          partOfSpeech +
          " " * (partOfSpeechColumnWidth - partOfSpeech.length) +
          " | " +
          word.numTimesPracticed.toString +
          " " * (timesPracticedColumnWidth - word.numTimesPracticed.toString.length) +
          " |"
        } else {
          "| " +
          " " * wordColumnWidth +
          " | " +
          definitionLine +
          " " * (definitionColumnWidth - definitionLine.length) +
          " | " +
          " " * partOfSpeechColumnWidth +
          " | " +
          " " * timesPracticedColumnWidth +
          " |"
        }
        if (wordIndex == words.length - 1 && definitionIndex == definitionLines.length - 1) {
          // Last word and last definition line yield the line + endborder
          Seq(line, endBorder)
        } else if (definitionIndex == definitionLines.length - 1) {
          // Last definition line but not last word yield the line + middleborder
          Seq(line, middleBorder)
        } else {
          // Yield just the line
          Seq(line)
        }
      }
    }

    Seq(endBorder, header, middleBorder) ++ lines
  }

  def run(implicit storage: Storage): Unit = generateTable foreach println 
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
