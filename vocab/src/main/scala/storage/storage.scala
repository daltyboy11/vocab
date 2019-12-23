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

  def deleteWords(word: String): Unit = {
    val newWords = words filter {
      case Word(word, _, _, _) => false
      case _ => true
    }
    words = newWords
  }

  def deleteWord(word: String, partOfSpeech: SpeechPart): Unit = {
    val newWords = words filter {
      case Word(word, _, Some(partOfSpeech), _) => false
      case _ => true
    }
    words = newWords
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
