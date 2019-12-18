package storage

import scala.io.Source
import java.io._
import models._

case class Storage(pathToStorage: String, wordStorage: String = "words.csv", practiceSessionStorage: String = "practice_sessions.csv") {

  import models.implicits._

  val wordStoragePath = pathToStorage + "/" + wordStorage
  val practiceSessionStoragePath = pathToStorage + "/" + practiceSessionStorage

  private var words: Seq[Word] =
    (for (line <- Source.fromFile(wordStoragePath).getLines) yield line.get[Word]).toSeq

  private var practiceSessions: Seq[PracticeSession] =
    (for (line <- Source.fromFile(practiceSessionStoragePath).getLines) yield line.get[PracticeSession]).toSeq

  def getWords: Seq[Word] = words
  def getPracticeSessions: Seq[PracticeSession] = practiceSessions

  def addWord(word: Word): Unit = {
    words = words :+ word
  }

  def addPracticeSession(session: PracticeSession): Boolean = {
    val willAdd = practiceSessions.contains(session)
    practiceSessions = practiceSessions :+ session
    willAdd
  }

  /**
   * Deletes the given word + speech part pair from the word repository. If no
   * speech part is given then all words of the given name are deleted. If a
   * speech part is given then only the specific word + speech part pair is
   * deleted.
   *
   * @return the set of words that were deleted.
   */
  def deleteWord(word: String, partOfSpeech: Some[SpeechPart]): Set[Word] = {
    ???
  }

  def commit(): Unit = {
    def writeFile(filename: String, lines: Seq[String]): Unit = {
      val file = new File(filename)
      val bufferedWriter = new BufferedWriter(new FileWriter(file))
      for (line <- lines) {
        bufferedWriter.write(line + "\n")
      }
      bufferedWriter.close()
    }
    val wordLines = words.map(_.toCSVRepr)
    val practiceSessionLines = practiceSessions.map(_.toCSVRepr)
    writeFile(wordStoragePath, wordLines)
    writeFile(practiceSessionStoragePath, practiceSessionLines)
  }
}
