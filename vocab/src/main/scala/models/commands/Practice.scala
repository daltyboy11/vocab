package models

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn
import scala.math.min
import scala.util.Random

import storage._
import models.common.splitDefinition

object Practice {
  val recalledArg = "r"
  val recalledLongArg = "recalled"
  val forgotArg = "f"
  val forgotLongArg = "forgot"
  val showArg = "s"
  val showLongArg = "show"
  val quitArg = "q"
  val quitLongArg = "quit"
  val usageMessage = """
commands:
 - r | recalled:  go to the next word after successfully recalling the word
 - f | forgot:    go to the next word after failing to recall the word
 - s | show:      display the definition of the word
 - q | quit:      end the practice session early
"""
  val noWordsMessage = "You have no words to practice!"
  val inputPrompt = "input: "
  val sleepDuration = 500
  val recalledMsg = "Recalled!"
  val notRecalledMsg = "Not Recalled!"
  val partOfSpeechColWidth = 10
  val definitionColWidth = 35 
  val completionMsg = (numComplete: Int, total: Int) => s"Practice Session Completed ($numComplete/$total words)"
  val quitMsg = (numComplete: Int, total: Int) => s"Practice Session Aborted ($numComplete/$total words)"
}

final case class Practice(sessionType: Option[PracticeSessionType]) extends Command {
  import Practice._

  object PracticeSessionRun {
    val recallThreshold = 3 // terminate after you have successfully recalled each word this many times
  }

  final case class PracticeSessionRun(words: List[Word], completions: Map[Word, Int]) {
    import PracticeSessionRun._
    
    private val _words = Random.shuffle(words)

    def isFinished = _words.isEmpty
    def currentWord = _words.head

    def practice(recalled: Boolean): PracticeSessionRun = recalled match {
      case true => {
        val numRecalls = completions(currentWord) + 1
        val nextWords = numRecalls match {
          case `recallThreshold` => _words.tail
          case _ => _words
        }
        PracticeSessionRun(nextWords, completions + (currentWord -> numRecalls))
      }
      case false => PracticeSessionRun(_words, completions)
    }
  }

  def run(implicit storage: Storage): Unit = {
    val startTime = System.currentTimeMillis / 1000
    val practiceSessionWords = wordsForSession(sessionType.getOrElse(All), storage.getWords)
    if (practiceSessionWords.isEmpty) {
      println(noWordsMessage)
      return
    }
    var practiceSession = PracticeSessionRun(
      practiceSessionWords.toList,
      practiceSessionWords.foldLeft(Map[Word, Int]())((map, word) => map + (word -> 0))
    )

    var didQuit = false
    var showDefinition = false
    val wordColWidth = practiceSessionWords.map(_.word.length).max

    while (!(practiceSession.isFinished || didQuit)) {
      val currentWord = practiceSession.currentWord
      // Display the word without its definition
      val wordMsg = tableRow(
        word = currentWord,
        wordColor = Console.WHITE,
        showDefinition = showDefinition,
        wordColWidth = wordColWidth,
        definitionColWidth = definitionColWidth,
        partOfSpeechColWidth = partOfSpeechColWidth 
      ) mkString "\n"

      println(wordMsg)

      StdIn.readLine(inputPrompt).toString match {
        case `recalledArg` | `recalledLongArg` => {
          println(Console.GREEN + recalledMsg + Console.WHITE)
          Thread.sleep(sleepDuration)
          println("\u001b[2J")
          practiceSession = practiceSession.practice(true)
          showDefinition = false
        }
        case `forgotArg`  | `forgotLongArg` => {
          println(Console.RED + notRecalledMsg + Console.WHITE)
          Thread.sleep(sleepDuration)
          println("\u001b[2J")
          practiceSession = practiceSession.practice(false)
          showDefinition = false
        }
        case `showArg` | `showLongArg` => {
          println("\u001b[2J")
          showDefinition = true
        }
        case `quitArg` | `quitLongArg` => {
          didQuit = true
          println("\u001b[2J")
        }
        case _ =>  {
          println("\u001b[2J")
          println(usageMessage)
        }
      }
    }

    // If the user finished then increment the practice counts of
    // the words in this practice session
    if (practiceSession.isFinished) {
      storage.incrementPracticeCounts(practiceSessionWords)
    }

    // Show completion message
    val maxWordLen = practiceSessionWords.map(_.word.length).max
    practiceSessionWords.toList.zipWithIndex.map { case (word, i) =>
      val wordColor = if (practiceSession.completions(word) < 3) Console.RED else Console.GREEN
      val row = tableRow(
        word = word,
        wordColor = wordColor,
        showDefinition = true,
        wordColWidth = maxWordLen,
        definitionColWidth = definitionColWidth,
        partOfSpeechColWidth = partOfSpeechColWidth 
      )
      if (i == 0) row else row.drop(3)
    }.map(_ mkString "\n").foreach(println)
    if (didQuit) {
      println(quitMsg(practiceSession.completions.filter(_._2 == 3).size, practiceSessionWords.size))
    } else {
      println(completionMsg(practiceSession.completions.filter(_._2 == 3).size, practiceSessionWords.size))
    }

    // Save the practice session
    val session = PracticeSession(
      sessionType = sessionType.getOrElse(defaultPracticeSessionType),
      numWords = practiceSessionWords.size,
      duration = (System.currentTimeMillis / 1000 - startTime).toInt,
      timestamp = startTime.toInt,
      didFinish = practiceSession.isFinished)
    storage.addPracticeSession(session)

    storage.commit
  }

  /* Non-deterministically selects the words for a practice session from the set
   * of all words.
   *
   * The number of words selected depends on sessionType.
   */
  private def wordsForSession(sessionType: PracticeSessionType, allWords: Seq[Word]): Set[Word] = {
    /*
     * Selects words from the word pool based on a weighted random selection
     * until we hit the target size. Words that have been practiced less are
     * weighter higher. Assumes that each word in the word pool has been
     * practiced at least once.
     */
    @tailrec
    def weightedSelect(wordPool: ArrayBuffer[Word], wordAcc: Set[Word], targetSize: Int): Set[Word] = {
      if (wordAcc.size == targetSize) {
        wordAcc
      } else {
        // Calculate weights
        val inversePracticeCounts = wordPool map (1.0 / _.numTimesPracticed)
        val sumInversePracticeCounts = inversePracticeCounts reduce (_ + _)
        val weights = inversePracticeCounts map (_ / sumInversePracticeCounts)
        val cumulativeWeights = weights.scanLeft(0.0)(_ + _)

        val randomDouble = Random.nextDouble()
        // Get the index of the first element whose value is greater than or
        // equal to randomDouble
        val index = cumulativeWeights.zipWithIndex.dropWhile {
          case (weight, index) => weight < randomDouble
        }.head._2
        // Limit the index so it's not out of bounds. This can happen when the
        // double generated is larger the the cumulative weight of the last
        // element.
        val word = wordPool(min(index, wordPool.length - 1))
        weightedSelect(wordPool -= word, wordAcc + word, targetSize)
      }
    }

    val totalNumWords = allWords.length
    val numWordsForThisSession = sessionType match {
      case All => totalNumWords
      case Half => totalNumWords / 2
      case ExplicitNumeric(numWords) => min(numWords, totalNumWords)
      case PercentageNumeric(percentage) => (totalNumWords.toFloat * percentage).toInt
    }

    val wordsSortedByNumTimesPracticed = allWords sortBy (_.numTimesPracticed)

    // Take words that have never been practiced first. Take until we hit
    // numWordsForThisSession or there are no more words that have never been
    // practiced, whichever comes first.
    val wordsNeverPracticed = Random.shuffle(wordsSortedByNumTimesPracticed takeWhile (_.numTimesPracticed == 0)) take numWordsForThisSession

    // If we don't have enough, add words from the weighted select
    val wordsPracticed = if (wordsNeverPracticed.length == numWordsForThisSession) {
      Set.empty[Word]
    } else {
      val numWordsRemaining = numWordsForThisSession - wordsNeverPracticed.length
      weightedSelect(
        ArrayBuffer(wordsSortedByNumTimesPracticed.drop(wordsNeverPracticed.length): _*),
        Set.empty[Word],
        numWordsRemaining
      )
    }

    wordsNeverPracticed.toSet ++ wordsPracticed
  }

  /*
   * Generates a table entry for a word + definition pair.
   * The color of the word will be `wordColor` (e.g. Console.BLUE, etc).
   * If showDefinition is false then the definition column will be empty.
   */
  def tableRow(
    word: Word,
    wordColor: String,
    showDefinition: Boolean,
    wordColWidth: Int,
    definitionColWidth: Int,
    partOfSpeechColWidth: Int
  ): Seq[String] = {
    implicit val maxColumnWidth = 35
    val header = "| " +
                 "word" + " " * (wordColWidth - "word".length) +
                 " | " +
                 "definition" + " " * (definitionColWidth - "definition".length) +
                 " | " +
                 "part of speech" + " " * (partOfSpeechColWidth - "part of speech".length) +
                 " |"
    val top = "-" * header.length

    val partOfSpeech = if (word.partOfSpeech.isDefined) word.partOfSpeech.get.asString else ""
    val definitionLines = splitDefinition(word.definition.split(" "))
    val body = definitionLines.zipWithIndex.map { case (line, index) =>
      val wordPart = if (index == 0) {
        wordColor + word.word + Console.WHITE + " " * (wordColWidth - word.word.length)
      } else {
        " " * wordColWidth
      }

      val definitionPart = if (showDefinition) {
        line + " " * (definitionColWidth - line.length)
      } else {
        " " * definitionColWidth
      }

      val partOfSpeechPart = if (index == 0) {
        partOfSpeech + " " * ("part of speech".length - partOfSpeech.length)
      } else {
        " " * "part of speech".length
      }

      "| " + wordPart + " | " + definitionPart + " | " + partOfSpeechPart + " |"
    }

    Seq(top, header, top) ++ body ++ Seq(top)
  }

  @inline private def defaultPracticeSessionType = All
}

