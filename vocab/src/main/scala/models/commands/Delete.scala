package models
import storage._

object Delete {
  sealed trait Result
  object Result {
    final object NotFound extends Result
    final case class Deleted(words: Set[Word]) extends Result
  }
}

case class Delete(word: String, partOfSpeech: Option[SpeechPart]) extends Command {
  import Delete._

  def run(implicit storage: Storage): Unit = {
    runLogic match {
      case Result.NotFound => println(s"$word not found")
      case Result.Deleted(words) => words foreach { case word =>
        val wordString = word.partOfSpeech match {
          case None => s"deleted ${word.word}"
          case Some(speechPart) => s"deleted ${word.word} ($speechPart)"
        }
        println(wordString)
      }
    }
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
        Result.Deleted(dupes.toSet)
      }
      case Some(speechPart) => if (dupes.isEmpty) {
        Result.NotFound
      } else {
        storage.deleteWord(word, speechPart)
        storage.commit
        Result.Deleted(Set(dupes.head))
      }
    }
  }
}

