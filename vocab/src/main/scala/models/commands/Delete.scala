package models
import storage._

object Delete {
  sealed trait Result
  object Result {
    final object NotFound extends Result
    final object Deleted extends Result
  }
}

case class Delete(word: String, partOfSpeech: Option[SpeechPart]) extends Command {
  import Delete._

  def run(implicit storage: Storage): Unit = {
    val consoleOutput = runLogic match {
      case Result.NotFound => s"$word not found"
      case Result.Deleted => partOfSpeech match {
        case Some(speechPart) => s"$word - $speechPart deleted"
        case None => s"$word deleted"
      }
    }
    println(consoleOutput)
  }

  private def runLogic(implicit storage: Storage): Result = {
    val words = storage.getWords
    val dupes = words filter (_.word == word)
    partOfSpeech match {
      case None => if (dupes.isEmpty) {
        Result.NotFound
      } else {
        storage.deleteWords(word)
        storage.commit
        Result.Deleted
      }
      case Some(speechPart) => if (dupes.isEmpty) {
        Result.NotFound
      } else {
        storage.deleteWord(word, speechPart)
        storage.commit
        Result.Deleted
      }
    }
  }
}

