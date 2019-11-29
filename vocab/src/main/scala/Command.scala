package commandline

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

object CommandLine {
  val addArgName                    = "add"
  val wordsArgName                  = "words"
  val typeArgName                   = "--type="
  val modifyArgName                 = "modify"
  val deleteArgName                 = "delete"
  val practiceArgName               = "practice"
  val practiceAllArgName            = "all"
  val practiceHalfArgName           = "half"
  val helpArgName                   = "--help"
  val versionArgName                = "--version"
  val placeholder                   = "-1"

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
      case `addArgName` => extractCommand(argList.tail.foldLeft(Right(Add(placeholder, placeholder, None)): Either[Unknown, Add])(parseAdd))
      case `modifyArgName` => extractCommand(argList.tail.foldLeft(Right(Modify(placeholder, placeholder, None)): Either[Unknown, Modify])(parseModify))
      case `deleteArgName` => extractCommand(argList.tail.foldLeft(Right(Delete(placeholder, None)): Either[Unknown, Delete])(parseDelete))
      case `practiceArgName` => extractCommand(argList.tail.foldLeft(Right(Practice(None)): Either[Unknown, Practice])(parsePractice))
    }
  }
  
  private def parseAdd(tentativeAdd: Either[Unknown, Add], nextArg: String): Either[Unknown, Add] = {
    // TODO - implement me
    Left(Unknown(None))
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
