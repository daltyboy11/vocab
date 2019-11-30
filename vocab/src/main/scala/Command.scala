package commandlineparser

// Top of the hierarchy for supported commands
sealed trait Command

// Top of the hierarchy for parts of speech. Used to further categorize words
// beyond the spelling of the word itself
sealed trait SpeechPart

// Top of the hierarchy for practice session types. Type not be the right word
// here because really its the number of words in the practice session that
// changes, not the form or structure of the session itself.
sealed trait PracticeSessionType

// Word Types
case object Noun                    extends SpeechPart
case object Verb                    extends SpeechPart
case object Pronoun                 extends SpeechPart
case object Adjective               extends SpeechPart
case object Adverb                  extends SpeechPart
case object Preposition             extends SpeechPart
case object Conjuction              extends SpeechPart
case object Interjection            extends SpeechPart
case class  Invalid(given: String)  extends SpeechPart

// Practice Sesssion Types
case object All                                   extends PracticeSessionType
case object Half                                  extends PracticeSessionType
case class  ExplicitNumeric(numWords: Int)        extends PracticeSessionType
case class  PercentageNumeric(percentage: Float)  extends PracticeSessionType

// Commands
case class  Add(word: String, definition: String, partOfSpeech: Option[SpeechPart])         extends Command
case class  Modify(word: String, newDefinition: String, partOfSpeech: Option[SpeechPart])   extends Command
case class  Delete(word: String, partOfSpeech: Option[SpeechPart])                          extends Command
case class  Practice(sessionType: Option[PracticeSessionType])                              extends Command
case class  Unknown(hint: Option[String])                                                   extends Command
case object Words                                                                           extends Command
case object Help                                                                            extends Command
case object NoArgs                                                                          extends Command
case object Version                                                                         extends Command

// Command Error Hints
sealed trait CommandHint {
  def message: String
}
case class CommandHintInvalidWord(word: String) extends CommandHint {
  val message = s"$word is not a valid word"
}
case class CommandHintInvalidPartOfSpeech(invalid: Invalid) extends CommandHint {
  val message = s"${invalid.given} is not a valid part of speech"
}

// Why is my command line parse so outrageously complicated??!?! Because it's purely
// functional, and (I hope) somewhat extensible.
object CommandLine {
  object Args {
    val addArgName                    = "add"
    val wordsArgName                  = "words"
    val modifyArgName                 = "modify"
    val deleteArgName                 = "delete"
    val practiceArgName               = "practice"
    val practiceAllArgName            = "all"
    val practiceHalfArgName           = "half"
    val helpArgName                   = "--help"
    val versionArgName                = "--version"
    val nounArg                       = "--noun"
    val verbArg                       = "--verb"
    val pronounArg                    = "--pronoun"
    val adjectiveArg                  = "--adjective"
    val adverbArg                     = "--adverb"
    val prepositionArg                = "--preposition"
    val conjunctionArg                = "--conjunction"
    val interjectionArg               = "--interjectionArg"
  }

  val placeholder                   = "-1"

  import Args._

  def parseArgs(args: String): Command = {
    // split by whitespace, remove extra whitespace, and drop the first
    // argument (the name of the program)
    val argList = args.split(" ").map(_.trim).drop(1).toList

    // parse first arg
    argList.headOption match {
      case Some(rawArg) => rawArg match {
        case `addArgName` | `modifyArgName` | `deleteArgName` | `practiceArgName` => parseMultiArgCommand(argList)
        case `helpArgName` => Help
        case `versionArgName` => Version
        case `wordsArgName` => Words
        case _ => Unknown(None)
      }
      case None => NoArgs
    }
  }

  private def parseMultiArgCommand(argList: List[String]): Command = {
    def extractCommand[A <: Command, B <: Command](either: Either[A, B]) = either match {
      case Right(a) => a
      case Left(b) => b
    }

    argList.head match {
      case `addArgName` =>
        extractCommand(argList.tail.foldLeft(Right(Add(placeholder, placeholder, None)): Either[Unknown, Add])(parseAdd))
      case `modifyArgName` =>
        extractCommand(argList.tail.foldLeft(Right(Modify(placeholder, placeholder, None)): Either[Unknown, Modify])(parseModify))
      case `deleteArgName` =>
        extractCommand(argList.tail.foldLeft(Right(Delete(placeholder, None)): Either[Unknown, Delete])(parseDelete))
      case `practiceArgName` =>
        extractCommand(argList.tail.foldLeft(Right(Practice(None)): Either[Unknown, Practice])(parsePractice))
    }
  }

  // True if all the characters in `s` are lowercase alphanumeric.
  // False otherwise.
  private def containsNonLowercaseAlphanumeric(s: String): Boolean = !s.forall(('a' to 'z').contains(_))

  // Converts a string to its corresponding SpeechPart case class/object
  private def partOfSpeechFromString(s: String): SpeechPart = s match {
    case `nounArg`                      => Noun
    case `verbArg`                      => Verb
    case `pronounArg`                   => Pronoun
    case `adjectiveArg`                 => Adjective
    case `adverbArg`                    => Adverb
    case `prepositionArg`               => Preposition
    case `conjunctionArg`               => Conjuction
    case `interjectionArg`              => Interjection
    case invalid                        => Invalid(invalid.substring(2))
  }
  
  // Assuming `vocab add` prefix, parses `word definition partOfSpeech` or `word partOfSpeech definition`.
  // If partOfSpeech comes after definition than any input after partOfSpeech is ignored.
  private def parseAdd(tentativeAdd: Either[Unknown, Add], nextArg: String): Either[Unknown, Add] = {
    tentativeAdd match {
      case Left(_) => tentativeAdd
      case Right(add @ Add(word, description, partOfSpeech)) => word match {
        case `placeholder` if containsNonLowercaseAlphanumeric(nextArg) => Left(Unknown(Some(s"$nextArg not a valid word")))
        case `placeholder` => Right(Add(nextArg, description, partOfSpeech))

        // Excess input when parsing is finished. just continue along.
        case word if description != placeholder
        && !partOfSpeech.isEmpty =>
          Right(add)

        // Hit the description
        case word if partOfSpeech.isEmpty
        && description == placeholder =>
          if (!containsNonLowercaseAlphanumeric(nextArg))
            Right(Add(word, nextArg, partOfSpeech))
          else
            Left(Unknown(Some(s"${nextArg} is not a valid description")))

        // Currently parsing description valid
        case word if description != placeholder
        && !containsNonLowercaseAlphanumeric(nextArg) =>
          Right(Add(word, description + " " + nextArg, partOfSpeech))

        // Currently parsing description invalid
        case word if description != placeholder
        && containsNonLowercaseAlphanumeric(nextArg)
        && !nextArg.startsWith("--") =>
          Left(Unknown(Some(s"${description + " " + nextArg} is not a valid description")))

        // Currently parsing description and hit part of speech
        case word if partOfSpeech.isEmpty
        && description != placeholder
        && nextArg.startsWith("--") =>
          partOfSpeechFromString(nextArg) match {
            case Invalid(invalidPartOfSpeech) =>
              Left(Unknown(Some(s"$invalidPartOfSpeech is not a valid part of speech")))
            case speechPart => Right(Add(word, description, Some(speechPart)))
          }
        
        // We should not be here, but just in case, leave an unexpected error unknown
        case _ => {
          val message = s"""
            word: $word,
            description: $description,
            partOfSpeech: $partOfSpeech,
            nextArg: $nextArg
          """
          Left(Unknown(Some(message)))
        }
      }
    }
  }

  private def parseModify(tentativeModify: Either[Unknown, Modify], nextArg: String): Either[Unknown, Modify] = {
    // TODO - implement me
    Left(Unknown(None))
  }

  private def parseDelete(tentativeDelete: Either[Unknown, Delete], nextArg: String): Either[Unknown, Delete] = {
    // TODO - implement me
    Left(Unknown(None))
  }

  private def parsePractice(tentativePractice: Either[Unknown, Practice], nextArg: String): Either[Unknown, Practice] = {
    // TODO - implement me
    Left(Unknown(None))
  }

}
