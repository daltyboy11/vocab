package models

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn
import scala.math.min
import scala.util.Random

import storage._

object Practice {
  val recalledShortArg = "r"
  val recalledLongArg = "recalled"
  val forgotShortArg = "f"
  val forgotLongArg = "forgot"
  val showShortArg = "s"
  val showLongArg = "show"
  val quitShortArg = "q"
  val quitLongArg = "quit"
}

final case class Practice(sessionType: Option[PracticeSessionType]) extends Command {
  import Practice._

  object PracticeSessionRun {
    val shuffleThreshold = 5
    val successfulPracticeThreshold = 3
  }

  final case class PracticeSessionRun(words: Seq[Word],
    completions: Map[Word, Int],
    private val shuffleCount: Int) {
    import PracticeSessionRun._

    private val _words = if (shuffleCount % (words.length / shuffleThreshold) == 0) {
      Random.shuffle(words)
    } else {
      words
    }

    def isFinished = _words.isEmpty
    def currentWord = _words.head

    def practiceWord(recalled: Boolean): PracticeSessionRun = if (recalled) {
      val successfulPractices = completions(currentWord) + 1
      if (successfulPractices >= successfulPracticeThreshold) {
        PracticeSessionRun(_words.tail,
          completions + (currentWord -> successfulPractices),
          shuffleCount + 1)
      } else {
        PracticeSessionRun(_words.tail :+ currentWord,
          completions + (currentWord -> successfulPractices),
          shuffleCount + 1)
      }
    } else {
      PracticeSessionRun(_words.tail :+ currentWord, completions, shuffleCount + 1)
    }
  }

  def run(implicit storage: Storage): Unit = {
    val startTime = System.currentTimeMillis / 1000
    val practiceSessionWords = wordsForSession(sessionType.getOrElse(Half), storage.getWords)
    var practiceSession = PracticeSessionRun(practiceSessionWords.toSeq,
      practiceSessionWords.foldLeft(Map[Word, Int]())((map, word) => map + (word -> 0)),
      0)
    var didQuit = false

    while (!(practiceSession.isFinished || didQuit)) {
      // Display word
      val currentWord = practiceSession.currentWord
      if (currentWord.partOfSpeech.isDefined) {
        println(s"${currentWord.word} (${currentWord.partOfSpeech.get})")
      } else {
        println(s"${currentWord.word}")
      }

      // Wait for input
      StdIn.readLine() match {
        case `recalledShortArg` | `recalledLongArg` =>
          practiceSession = practiceSession.practiceWord(true)
        case `forgotShortArg` | `forgotLongArg` =>
          practiceSession = practiceSession.practiceWord(false)
        case `showShortArg` | `showLongArg` => println(s"definition: ${currentWord.definition}")
        case `quitShortArg` | `quitLongArg` => didQuit = true
        case _ => println("invalid input")
      }
    }

    // Save if practice session is finished. The case when the practice session
    // is not finished is when the user quits
    if (practiceSession.isFinished) {
      storage.incrementPracticeCounts(practiceSessionWords)
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

        // Get the index of the first element whose value is greater than or
        // equal to randomDouble
        val randomDouble = Random.nextDouble()
        val index = cumulativeWeights.zipWithIndex.dropWhile {
          case (weight, index) => weight < randomDouble
        }.head._2
        val word = wordPool(index)
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
    val wordsNeverPracticed = Random.shuffle(wordsSortedByNumTimesPracticed takeWhile (_.numTimesPracticed == 0)) take numWordsForThisSession
    val wordsPracticed = if (wordsNeverPracticed.length < numWordsForThisSession) {
      weightedSelect(ArrayBuffer(wordsSortedByNumTimesPracticed.dropWhile(_.numTimesPracticed == 0) : _*),
        Set.empty[Word],
        numWordsForThisSession - wordsNeverPracticed.length)
    } else {
      Set.empty[Word]
    }

    wordsNeverPracticed.toSet ++ wordsPracticed
  }

  @inline private def defaultPracticeSessionType = Half
}

