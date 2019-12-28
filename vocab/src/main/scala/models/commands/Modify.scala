package models
import storage._

object Modify {
  sealed trait Result
  object Result {
    final object Modified extends Result
    final object NotFound extends Result
  }
}

case class Modify(word: String, newDefinition: String, partOfSpeech: Option[SpeechPart]) extends Command {
  import Modify._

  def run(implicit storage: Storage): Unit = {
    val consoleOutput = runLogic match {
      case Result.Modified => s"$word modified"
      case Result.NotFound => s"$word not found" 
    }
    println(consoleOutput)
  }

  def runLogic(implicit storage: Storage): Result = {
    val words = storage.getWords
    val exists = words.filter(w => w.word == word && w.partOfSpeech == partOfSpeech).nonEmpty
    if (exists) {
      storage.setWord(word, newDefinition, partOfSpeech)
      storage.commit
      Result.Modified
    } else {
      Result.NotFound
    }
  }
}

