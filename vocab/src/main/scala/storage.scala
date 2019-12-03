package storage

import scala.io.Source
import java.io._
import models._
import implicits._

case class Storage(pathToStorage: String, wordStorage: String = "words.csv", practiceSessionStorage: String = "practice_sessions.csv") {
  import Implicits._

  val wordStoragePath = pathToStorage + "/" + wordStorage
  val practiceSessionStoragePath = pathToStorage + "/" + practiceSessionStorage

  private var words: Seq[Word] =
    (for (line <- Source.fromFile(wordStoragePath).getLines) yield line.toWord).toSeq

  private var practiceSessions: Seq[PracticeSession] =
    (for (line <- Source.fromFile(practiceSessionStoragePath).getLines) yield line.toPracticeSession).toSeq

  def getWords: Seq[Word] = words
  def getPracticeSessions: Seq[PracticeSession] = practiceSessions

  def addWord(word: Word): Boolean = {
    val willAdd = words.contains(word)
    words = words :+ word
    willAdd
  }

  def addPracticeSession(session: PracticeSession): Boolean = {
    val willAdd = practiceSessions.contains(session)
    practiceSessions = practiceSessions :+ session
    willAdd
  }

  def commit(): Unit = {
    def writeFile(filename: String, lines: Seq[String]): Unit = {
      val file = new File(filename)
      val bufferedWriter = new BufferedWriter(new FileWriter(file))
      for (line <- lines) {
        bufferedWriter.write(line)
      }
      bufferedWriter.close()
    }
    val wordLines = words.map(_.toCSVRepr)
    val practiceSessionLines = practiceSessions.map(_.toCSVRepr)
    writeFile(wordStoragePath, wordLines)
    writeFile(practiceSessionStoragePath, practiceSessionLines)
  }
}
