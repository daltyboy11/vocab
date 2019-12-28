package models

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.math.min
import scala.util.Random
import storage._

final case class Practice(sessionType: Option[PracticeSessionType]) extends Command {
  class PracticeSession(words: Set[Word]) {
    private var _words = ArrayBuffer[Word](words.toSeq : _*)

    // A word must be successfully recalled 3 times before it is considered
    // "remembered".
    private val _successThreshold = 3
    private var _successCounts = scala.collection.mutable.Map(words.toSeq map(word => (word, 0)) : _*)

    // The implementation places forgotten words at the back of the list
    // Every time our shuffle counter hits the shuffle threshold we shuffle the
    // remaining words so the sequence is not predictable.
    private val _shuffleThreshold = _words.length / 5 
    private var _shuffleCounter = 0

    def isFinished: Boolean = _words.isEmpty
    def currentWord: Word = _words.head
    def wordsLeft: Set[Word] = _words.toSet

    def practice(recalled: Boolean): Unit = {
      val oldCurrentWord = currentWord
      _words = _words.tail
      
      if (recalled) {
        _successCounts(oldCurrentWord) += 1
      }

      if (_successCounts(oldCurrentWord) < _successThreshold) {
        _words += oldCurrentWord
      }

      _shuffleCounter += 1
      if (_shuffleCounter == _shuffleThreshold) {
        _words = Random.shuffle(_words)
        _shuffleCounter = 0
      }
    }
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

  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

