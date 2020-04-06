package models
import storage._

object Add {
  sealed trait Result
  object Result {
    final case class AlreadyExistsWithSpeechPart(speechPart: SpeechPart)  extends Result
    final object AlreadyExistsWithoutSpeechPart extends Result
    final case class AddedWithSpeechPart(speechPart: SpeechPart) extends Result
    final case object AddedWithoutSpeechPart extends Result
  }
}

case class Add(word: String, definition: String, partOfSpeech: Option[SpeechPart]) extends Command {
  import Add._

  def run(implicit storage: Storage): Unit = {
    val consoleOutput = runLogic match {
      case Result.AlreadyExistsWithSpeechPart(speechPart) => s"unable to add - $word ($speechPart) already exists"
      case Result.AlreadyExistsWithoutSpeechPart => s"unable to add - $word already exists"
      case Result.AddedWithSpeechPart(speechPart) => s"added $word ($speechPart)"
      case Result.AddedWithoutSpeechPart => s"added $word"
    }
    println(consoleOutput)
  }

  def runLogic(implicit storage: Storage): Result = {
    val words = storage.getWords
    val wordToAdd = Word(word, definition, partOfSpeech, 0)
    val dupes = words filter (_.word == word)

    // If our partOfSpeech is None then no entry can exist for word
    // If our partOfSpeech is Some then the word can be added if there is no
    // word with None AND the word with the same Some does not exist.
    partOfSpeech match {
      case None => {
        if (dupes.isEmpty) {
          storage.addWord(wordToAdd)
          storage.commit
          Result.AddedWithoutSpeechPart
        } else {
          val dupe = dupes.head
          dupe.partOfSpeech match {
            case None => Result.AlreadyExistsWithoutSpeechPart
            case Some(speechPart) => Result.AlreadyExistsWithSpeechPart(speechPart)
          }
        }
      }
      case Some(speechPart) => {
        if (dupes map (_.partOfSpeech) contains None) {
          Result.AlreadyExistsWithoutSpeechPart
        } else if (dupes map (_.partOfSpeech) contains partOfSpeech) {
          Result.AlreadyExistsWithSpeechPart(speechPart)
        } else {
          storage.addWord(wordToAdd)
          storage.commit
          Result.AddedWithSpeechPart(speechPart)
        }
      }
    }
  }
}
