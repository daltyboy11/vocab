package models
import scala.annotation.tailrec

package object models {
  object common {
    /*
     * Given a sequence of strings implied to separtated by whitespace this
     * function will transform that string into a sequence of "lines" satisfying
     * a maximum length requirement and ensuring that each string in the
     * original sequence is on one and ONLY one line.
     */
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

  }
  object implicits {
    sealed trait ToCSVRepr {
      def toCSVRepr(): String
    }

    implicit class WordToCSVRepr(word: Word) extends ToCSVRepr {
      def toCSVRepr: String = {
        val partOfSpeechCol = word.partOfSpeech match {
          case None => ""
          case Some(partOfSpeech) => partOfSpeech.asString
        }
        // Need to wrap raw strings that contain commas in double quotes so
        // they are not considered separate columns in the CSV row
        val wordCol = if (word.word contains ",") "\"" + word.word + "\"" else word.word
        val defCol = if (word.definition contains ",") "\"" + word.definition + "\"" else word.definition
        val numTimesPracticedCol = word.numTimesPracticed.toString
        List(wordCol, defCol, partOfSpeechCol, numTimesPracticedCol).mkString(",")
      }
    }

    implicit class PracticeSessionToCSVRepr(session: PracticeSession) extends ToCSVRepr {
      def toCSVRepr: String = List(
        session.sessionType.asString,
        session.numWords.toString,
        session.duration.toString,
        session.timestamp.toString,
        session.didFinish.toString
      ).mkString(",")
    }
    
    implicit class ModelAsString(stringRepr: String) {
      def get[T](implicit toT: String => T): T = toT(stringRepr)
    }

    /*
     * Produces the column values from a string representation of a CSV record
     */
    def parseCSVRow(s: String): Seq[String] = {
      /*
       * Returns the indices in the string s at which char occurs. Indices are in
       * sorted order
       */
      def indicesOfChar(char: Char, s: String): List[Int] =
        s.zipWithIndex.foldLeft(List.empty[Int]) { case (indices, (c, i)) =>
          if (char == c) indices :+ i else indices
        }
      /*
       * Groups adjacent elements in a list, producing a list of pairs. Throws an
       * exception if there is an odd number of elements in the supplied list.
       */
      def groupAdjacent[T](l: List[T]): List[List[T]] = l match {
        case Nil => Nil
        case x :: Nil => throw new IllegalArgumentException("List must contain an even number of elements")
        case x :: y :: lt => groupAdjacent(lt) :+ List(x, y)
      }

      val indicesOfCommas = indicesOfChar(',', s)
      val indicesOfQuotes = indicesOfChar('"', s)
      val quoteIndexPairs = groupAdjacent(indicesOfQuotes)

      val indicesOfCommasWrappedInQuotes = (indicesOfCommas filter { case index =>
        quoteIndexPairs.exists { case p =>
            index >= p(0) && index <= p(1)
        }
      }).toSet

      val replacementChar = '$'
      val sWithWrappedCommasReplaced = (s.zipWithIndex.map { case (c, i) =>
        if (indicesOfCommasWrappedInQuotes contains i)
          replacementChar
        else
          c
      }).mkString

      sWithWrappedCommasReplaced.split(",").map { case col =>
        col.map(c => if (c == replacementChar) ',' else c).filter(_ != '"')
      }
    }

    implicit val stringToWord: String => Word = (s: String) => {
      val parts = parseCSVRow(s)
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
      val parts = parseCSVRow(s)
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
}

