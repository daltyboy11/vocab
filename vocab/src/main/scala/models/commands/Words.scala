package models
import scala.annotation.tailrec
import storage._

case object Words extends Command {
  implicit val maxColumnWidth = 35

  val wordsColumnTitle = "Words"
  val definitionColumnTitle = "Definition"
  val partOfSpeechColumnTitle = "Part of Speech"
  val timesPracticedColumnTitle = "Times Practiced"
  val noWordsMsg = """
  You have no words to review!
  """

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

  def run(implicit storage: Storage): Unit = storage.getWords match {
    case Nil => println(noWordsMsg)
    case _ => generateTable foreach println
  }
}

