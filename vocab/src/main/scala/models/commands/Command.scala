package models

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.math.min
import scala.util.Random
import storage._

trait Command {
  def run(implicit storage: Storage): Unit
}

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
    val addMessage = if (partOfSpeech.isDefined) s"$word - ${partOfSpeech.get} added" else s"$word added"
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


case object Words extends Command {

  implicit val maxColumnWidth = 35

  val wordsColumnTitle = "Words"
  val definitionColumnTitle = "Definition"
  val partOfSpeechColumnTitle = "Part of Speech"
  val timesPracticedColumnTitle = "Times Practiced"

  def splitDefinition(definition: Seq[String])(implicit maxColumnWidth: Int): Seq[String] = {
    @tailrec
    def splitDefinitionAcc(definition: Seq[String], result: Seq[String]): Seq[String] = {
      val headAndTail = definition.foldLeft((Seq.empty[String], Seq.empty[String])) { case ((head, tail), word) =>
        val numWhitespaceChars = if (head.length > 1) head.length - 1 else 0
        val totalCharsOnLine = numWhitespaceChars + (if (head.length > 0) head.map(_.length).reduce(_ + _) else 0)
        if (totalCharsOnLine + word.length + 1 <= maxColumnWidth) {
          (head :+ word, tail)
        } else {
          (head, tail :+ word)
        }
      }

      if (headAndTail._2.length == 0) {
        // we are done
        result ++ List(headAndTail._1.mkString(" "))
      } else {
        splitDefinitionAcc(headAndTail._2, result ++ List(headAndTail._1.mkString(" ")))
      }
    }
    splitDefinitionAcc(definition, Seq.empty[String])
  }
  
  def generateTable(implicit storage: Storage): Seq[String] = {
    val words = storage.getWords
    val wordColumnWidth = words.map(_.word.length).max
    val definitionColumnWidth = this.maxColumnWidth
    val partOfSpeechColumnWidth = this.partOfSpeechColumnTitle.length
    val timesPracticedColumnWidth = this.timesPracticedColumnTitle.length

    val header = "| " +
                 "word" + " " * (wordColumnWidth - "word".length) +
                 " | " +
                 "definition" + " " * (definitionColumnWidth - "definition".length) +
                 " | " +
                 "part of speech" + " " * (partOfSpeechColumnWidth - "part of speech".length) +
                 " | " +
                 "times practiced" + " " * (timesPracticedColumnWidth - "times practiced".length) +
                 " |"

    // 8 for whitespace, 5 for vertical lines
    val endBorder = "-" * (wordColumnWidth + definitionColumnWidth + partOfSpeechColumnWidth + timesPracticedColumnWidth + 8 + 5) 

    val middleBorder = "|" + "-" * (wordColumnWidth + 2) +
                       "|" + "-" * (definitionColumnWidth + 2) +
                       "|" + "-" * (partOfSpeechColumnWidth + 2) +
                       "|" + "-" * (timesPracticedColumnWidth + 2) +
                       "|"

    val lines = words.zipWithIndex flatMap { case (word, wordIndex) =>
      val partOfSpeech = if (word.partOfSpeech.isDefined) word.partOfSpeech.get.asString else ""
      val definitionLines = splitDefinition(word.definition.split(" "))
      definitionLines.zipWithIndex flatMap { case (definitionLine, definitionIndex) =>
        val line = if (definitionIndex == 0) {
          "| " +
          word.word +
          " " * (wordColumnWidth - word.word.length) +
          " | " +
          definitionLine +
          " " * (definitionColumnWidth - definitionLine.length) +
          " | " +
          partOfSpeech +
          " " * (partOfSpeechColumnWidth - partOfSpeech.length) +
          " | " +
          word.numTimesPracticed.toString +
          " " * (timesPracticedColumnWidth - word.numTimesPracticed.toString.length) +
          " |"
        } else {
          "| " +
          " " * wordColumnWidth +
          " | " +
          definitionLine +
          " " * (definitionColumnWidth - definitionLine.length) +
          " | " +
          " " * partOfSpeechColumnWidth +
          " | " +
          " " * timesPracticedColumnWidth +
          " |"
        }
        if (wordIndex == words.length - 1 && definitionIndex == definitionLines.length - 1) {
          // Last word and last definition line yield the line + endborder
          Seq(line, endBorder)
        } else if (definitionIndex == definitionLines.length - 1) {
          // Last definition line but not last word yield the line + middleborder
          Seq(line, middleBorder)
        } else {
          // Yield just the line
          Seq(line)
        }
      }
    }

    Seq(endBorder, header, middleBorder) ++ lines
  }

  def run(implicit storage: Storage): Unit = generateTable foreach println 
}

case object Help extends Command {
  val helpMessage = """
  |vocab: vocab command arguments
  |     Expand your vocabulary using this command line utility.
  |     
  |     Commands:
  |
  |       - vocab add <word> <definition> [--type type]
  |         Adds word to your vocabulary set with the given definition.
  |         Providing a word type is optional. <definition> MUST be 
  |         enclosed in double quotes.
  |       
  |       - vocab modify <word> <newDefinition> [--type type]
  |         Change the definition of an exisiting <word> and (optional)
  |         type to <newDefinition>. <newDefinition> MUST be enclosed in
  |         double quotes.
  |       
  |       - vocab delete <word> [--type type]
  |         Deletes the words that match <word> and the optional type.
  |         If the type is not specified all words matching <word> are
  |         deleted.
  |       
  |       Accepted values for [--type type]:
  |         The type argument in the add, modify, and delete commands correspond
  |         to what part of speech the word belongs to
  |         
  |         type = noun | verb | pronoun | adjective | adverb | preposition | conjunction | interjection
  |
  |         The usage of types enables homonyms. E.g.
  |           vocab add play \"to take part in a game\" --type verb
  |           vocab add play \"a dramatic work for the stage\" --type noun
  |       
  |       - vocab practice [ all | half | n | p ]
  |         Launches a practice session with an optional argument to control
  |         the number of words in the session.
  |         - all: practice all the words in your vocabulary set
  |         - half: practice half of the words in your vocabulary set
  |         - n: practice n words, where n is a positive integer
  |         - p: practice a proportion of all words, where p is in (0.0, 1.0]
  |
  |       - vocab words
  |         Shows all the words in your vocabulary set along with the number of
  |         times they have been practiced.
  |
  |       - vocab version
  |         Show the program version number.
  |
  |       - vocab help
  |         Show this help page.
  |
  |     Practice Sessions:
  |       vocab practice [arg] will start an interactive practice session.
  |       
  |       A word (and possible word type) will be presented in the terminal
  |       without its definition. At this point you should attempt to recall
  |       its definition and/or use it in a sentence. When you are satisfied
  |       with your attempt use one of the interactive practice session
  |       commands to move on to the next word or quit the sessions. To complete
  |       a session you must successfully recall each word 3 times.
  |       
  |       Available commands:
  |         - r | recalled: go to the next word after you successfully recall
  |                         the current word's definition and/or use it in a
  |                         sentence.
  |
  |         - f | forgot:   go to the next word after you failed to recall the
  |                         current word's definition and/or use it in a sentence.
  |
  |         - s | show:     show the current word's definition and go to the next
  |                         word.
  |
  |         - q | quit:     quit the practice session.
  |
  |     Example Usage:
  |
  |       - add a word to the vocabulary set and then modify its definition
  |           vocab add cat \"man\'s best friend\" --type noun
  |           vocab modidfy cat \"a domesticated feline\" --type noun
  |
  |       - start a practice session and review a quarter of all words
  |           vocab practice 0.25
  |
  |       - delete a word that you no longer need to review
  |           vocab delete ardor \"enthusiasm or passion\" --type adjective
  |
  |     Contributing:
  |       vocab is open source and available on github
  |       https://github.com/daltyboy11/vocab
  |""".stripMargin

  def run(implicit storage: Storage): Unit = {
    println(helpMessage)
  }
}

case object NoArgs extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}

case object Version extends Command {
  def run(implicit storage: Storage): Unit = {
    // TODO
  }
}
