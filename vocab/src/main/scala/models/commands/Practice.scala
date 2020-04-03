package models

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn
import scala.math.min
import scala.util.Random

import storage._

object Practice {
  val recalledArg = "r"
  val forgotArg = "f"
  val quitArg = "q"
  val usageMessage = """
  commands:
   - r: go to the next word after successfully recalling the word
   - f: go to the next word after failing to recall the word
   - q: quit 
  """
  val noWordsMessage = "You have no words to practice!"
}

final case class Practice(sessionType: Option[PracticeSessionType]) extends Command {
  import Practice._

  object PracticeSessionRun {
    val recallThreshold = 3 // terminate after you have successfully recalled each word this many times
  }

  final case class PracticeSessionRun(words: Seq[Word], completions: Map[Word, Int]) {
    import PracticeSessionRun._
    
    private val _words = Random.shuffle(words)

    def isFinished = _words.isEmpty
    def currentWord = _words.head

    def practice(recalled: Boolean): PracticeSessionRun = recalled match {
      // TODO: randomally shuffling the word set every time is inefficient
      // asymptotically. This is ok as long as it doesn't affect the user.
      // Investigation is required.
      case true => {
        val numRecalls = completions(currentWord) + 1
        val nextWords = numRecalls match {
          case `recallThreshold` => _words.tail
          case _ => _words
        }
        PracticeSessionRun(Random.shuffle(nextWords), completions + (currentWord -> numRecalls))
      }
      case false => PracticeSessionRun(Random.shuffle(_words), completions)
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
      practiceSessionWords.toSeq,
      practiceSessionWords.foldLeft(Map[Word, Int]())((map, word) => map + (word -> 0))
    )
    var didQuit = false

    while (!(practiceSession.isFinished || didQuit)) {
      val currentWord = practiceSession.currentWord
      val wordMsg = currentWord.partOfSpeech match {
        case Some(speechPart) => s"${currentWord.word} (${speechPart})"
        case None => s"${currentWord.word}"
      }
      println(wordMsg)

      StdIn.readChar().toString match {
        case `recalledArg` => {
          println(Console.GREEN + s"${currentWord.definition}" + Console.WHITE + "\r")
          practiceSession = practiceSession.practice(true)
        }
        case `forgotArg` => {
          println(Console.RED + s"${currentWord.definition}" + Console.WHITE + "\r")
          practiceSession = practiceSession.practice(false)
        }
        case `quitArg` => didQuit = true
        case _ => println(usageMessage)
      }
    }

    // If the user finished then increment the practice counts of
    // the words in this practice session
    if (practiceSession.isFinished) {
      storage.incrementPracticeCounts(practiceSessionWords)
    }

    // Show completion message
    val completionMsg = s"practice session complete (${practiceSession.completions.size}/${practiceSessionWords.size})"
    println(completionMsg)

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

  @inline private def defaultPracticeSessionType = All
}

