package commandlineparser

import models._

object CommandLine {
  object Args {
    val addArgName                    = "add"
    val wordsArgName                  = "words"
    val modifyArgName                 = "modify"
    val deleteArgName                 = "delete"
    val practiceArgName               = "practice"
    val practiceAllArgName            = "all"
    val practiceHalfArgName           = "half"
    val helpArgName                   = "help"
    val versionArgName                = "version"
    val nounArg                       = "noun"
    val verbArg                       = "verb"
    val pronounArg                    = "pronoun"
    val adjectiveArg                  = "adjective"
    val adverbArg                     = "adverb"
    val prepositionArg                = "preposition"
    val conjunctionArg                = "conjunction"
    val interjectionArg               = "interjection"
    val clearArg                      = "clear"
  }

  import Args._

  // TODO: Add error messages
  
  /** Parses the arguments passed to the program and returns a command (if the
   *  arguments were valid) or a parsing error giving the reason for the parse
   *  failure.
   *
   *  Assumes the program name arg has already been removed.
   *  Ignores any input that comes after a valid command.
   */
  def parseArgs(args: Seq[String]): Either[ParseError, Command] = {
    args.toList match {
      // add word definition type
      case `addArgName` :: word :: definition :: typeName :: _ =>
        if (isValidSpeechPart(typeName)) {
          Right(Add(word, definition, Some(partOfSpeechFromString(typeName))))
        } else {
          Left(ParseErrorInvalidAddCommand())
        }
      // add word definition
      case `addArgName` :: word :: definition :: _ => Right(Add(word, definition, None))
      // add word
      case `addArgName` :: word :: Nil => Left(ParseErrorInvalidAddCommand()) // missing definition
      // add
      case `addArgName` :: Nil => Left(ParseErrorInvalidAddCommand()) // missing word

      // modify word newDefinition type
      case `modifyArgName` :: word :: newDefinition :: typeName :: _ =>
        if (isValidSpeechPart(typeName)) {
          Right(Modify(word, newDefinition, Some(partOfSpeechFromString(typeName))))
        } else {
          Left(ParseErrorInvalidModifyCommand())
        }
      // modify word newDefinition
      case `modifyArgName` :: word :: newDefinition :: _ => Right(Modify(word, newDefinition, None))
      // modify word
      case `modifyArgName` :: word :: Nil => Left(ParseErrorInvalidModifyCommand()) // missing definition
      // modify
      case `modifyArgName` :: Nil => Left(ParseErrorInvalidModifyCommand()) // missing word

      // delete word type
      case `deleteArgName` :: word :: typeName :: _ =>
        if (isValidSpeechPart(typeName)) {
          Right(Delete(word, Some(partOfSpeechFromString(typeName))))
        } else {
          Left(ParseErrorInvalidDeleteCommand())
        }
      // delete word
      case `deleteArgName` :: word :: _ => Right(Delete(word, None))

      // practice practiceSessionType
      case `practiceArgName` :: practiceSessionType :: _ => practiceSessionType match {
        case `practiceAllArgName` => Right(Practice(Some(All)))
        case `practiceHalfArgName` => Right(Practice(Some(Half)))
        case n => if (n.toIntOption.isDefined && n.toInt > 0) {
          Right(Practice(Some(ExplicitNumeric(n.toInt))))
        } else if (n.toFloatOption.isDefined && n.toFloat > 0.0 && n.toFloat <= 1.0) {
          Right(Practice(Some(PercentageNumeric(n.toFloat))))
        } else {
          Left(ParseErrorInvalidPracticeCommand())
        }
      }
      // practice
      case `practiceArgName` :: _ => Right(Practice(None))

      // words
      case `wordsArgName` :: _ => Right(Words)

      // help
      case `helpArgName` :: _ => Right(Help)

      // version
      case `versionArgName` :: _ => Right(Version)

      // clear
      case `clearArg` :: _ => Right(Clear)

      // generic parse error
      case _ => Left(ParseErrorUnsupportedCommand())
    }
  }

  // Converts a string to its corresponding SpeechPart case class/object
  private def partOfSpeechFromString(s: String): SpeechPart = s match {
    case `nounArg`                      => Noun
    case `verbArg`                      => Verb
    case `pronounArg`                   => Pronoun
    case `adjectiveArg`                 => Adjective
    case `adverbArg`                    => Adverb
    case `prepositionArg`               => Preposition
    case `conjunctionArg`               => Conjunction
    case `interjectionArg`              => Interjection
    case invalid                        => Invalid(invalid.substring(2))
  }
  
  private def isValidSpeechPart(speechPart: String) =
    speechPart == nounArg |
    speechPart == verbArg |
    speechPart == pronounArg |
    speechPart == adjectiveArg |
    speechPart == adverbArg |
    speechPart == prepositionArg |
    speechPart == conjunctionArg |
    speechPart == interjectionArg
}
