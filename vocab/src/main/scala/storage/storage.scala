package storage

import scala.io.Source
import java.io._
import models._

case class Storage(wordPath: String, practiceSessionPath: String) {
  import models.implicits._

  private var words: Seq[Word] =
    (for (line <- Source.fromFile(wordPath).getLines) yield line.get[Word]).toSeq

  private var practiceSessions: Seq[PracticeSession] =
    (for (line <- Source.fromFile(practiceSessionPath).getLines) yield line.get[PracticeSession]).toSeq

  def getWords: Seq[Word] = words
  def getPracticeSessions: Seq[PracticeSession] = practiceSessions

  def addWord(word: Word): Unit = {
    words = words :+ word
  }

  def addPracticeSession(session: PracticeSession): Unit = practiceSessions = practiceSessions :+ session

  /** Modifies an existing word. Assumes the word exists and will cause a
   *  runtime error if it doesn't
   */
  def setWord(word: String, newDefinition: String, partOfSpeech: Option[SpeechPart]): Unit = {
    val (oldWord, index) = words.zipWithIndex.filter {
      case (w, _) => w.word == word && w.partOfSpeech == partOfSpeech
    }.head
    val newWord = Word(word, newDefinition, partOfSpeech, oldWord.numTimesPracticed)
    words = words.updated(index, newWord)
  }

  /** Increments the practice counts for the given set of words
   */
  def incrementPracticeCounts(wordsToIncrement: Set[Word]): Unit = {
    words = words map {
      case word => if (wordsToIncrement contains word) {
        Word(word.word, word.definition, word.partOfSpeech, word.numTimesPracticed + 1)
      } else {
        word
      }
    }
  }

  /** Deletes all words matching `word`
   */
  def deleteWords(word: String): Unit = {
    val wordsToDelete = words filter (_.word == word)
    words = words filterNot wordsToDelete.contains
  }

  /**
   * Clears ALL data on words on practice sessions.
   */
  def clear(): Unit = {
    words = Seq.empty[Word]
    practiceSessions = Seq.empty[PracticeSession]
  }

  /** Deletes the word matching `word` and the given part of speech
   */
  def deleteWord(word: String, partOfSpeech: SpeechPart): Unit = {
    val wordsToDelete = words filter (w => w.word == word && w.partOfSpeech == Some(partOfSpeech))
    words = words filterNot wordsToDelete.contains
  }

  /** Saves the in-memory words to persistent storage. Call this function to
   *  make saves to persist any changes after calling methods defined in this
   *  class.
   */
  def commit(): Unit = {
    writeFile(wordPath, lines = words.map(_.toCSVRepr))
    writeFile(practiceSessionPath, lines = practiceSessions.map(_.toCSVRepr))
  }

  private def writeFile(path: String, lines: Seq[String]): Unit = {
    val bufferedWriter = new BufferedWriter(new FileWriter(new File(path)))
    for (line <- lines) {
      bufferedWriter.write(line + "\n")
    }
    bufferedWriter.close()
  }
}
