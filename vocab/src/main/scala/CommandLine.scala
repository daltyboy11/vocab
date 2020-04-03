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
    val typeArg                       = "--type"
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
  
  /** Parses the arguments passed to the program and returns a command (if the
   *  arguments were valid) or a parsing error giving the reason for the parse
   *  failure.
   *
   *  Assumes the program name arg has already been removed.
   *  Ignores any input that comes after a valid command.
   */
  def parseArgs(args: Seq[String]): Either[ParseError, Command] = {
    args.toList match {
      // Parse Add
      case `addArgName` :: word :: definition :: typeArgName :: typeName :: _ =>
        if (isLowercaseAlphabetical(word) && typeArgName == typeArg && isValidSpeechPart(typeName)) {
          Right(Add(word, definition, Some(partOfSpeechFromString(typeName))))
        } else {
          Left(ParseErrorInvalidAddCommand())
        }
      case `addArgName` :: word :: definition :: typeArgName :: _ => Left(ParseErrorInvalidAddCommand()) // missing type
      case `addArgName` :: word :: definition :: _ =>
        if (isLowercaseAlphabetical(word)) {
          Right(Add(word, definition, None))
        } else {
          Left(ParseErrorInvalidWord(word))
        }
      case `addArgName` :: word :: Nil => Left(ParseErrorInvalidAddCommand()) // missing definition
      case `addArgName` :: Nil => Left(ParseErrorInvalidAddCommand()) // missing word

      // Parse Modify
      case `modifyArgName` :: word :: newDefinition :: typeArgName :: typeName :: _ =>
        if (isLowercaseAlphabetical(word) && typeArgName == typeArg && isValidSpeechPart(typeName)) {
          Right(Modify(word, newDefinition, Some(partOfSpeechFromString(typeName))))
        } else {
          Left(ParseErrorInvalidModifyCommand())
        }
      case `modifyArgName` :: word :: newDefinition :: typeArgName :: _ => Left(ParseErrorInvalidModifyCommand()) // missing type
      case `modifyArgName` :: word :: newDefinition :: _ =>
        if (isLowercaseAlphabetical(word)) {
          Right(Modify(word, newDefinition, None))
        } else {
          Left(ParseErrorInvalidWord(word))
        }
      case `modifyArgName` :: word :: Nil => Left(ParseErrorInvalidModifyCommand()) // missing definition
      case `modifyArgName` :: Nil => Left(ParseErrorInvalidModifyCommand()) // missing word

      // Parse Delete
      case `deleteArgName` :: word :: typeArgName :: typeName :: _ =>
        if (isLowercaseAlphabetical(word) && typeArgName == typeArg && isValidSpeechPart(typeName)) {
          Right(Delete(word, Some(partOfSpeechFromString(typeName))))
        } else {
          Left(ParseErrorInvalidDeleteCommand())
        }
      case `deleteArgName` :: word :: typeArgName :: _ => Left(ParseErrorInvalidDeleteCommand()) // missing type
      case `deleteArgName` :: word :: _ =>
        if (isLowercaseAlphabetical(word)) {
          Right(Delete(word, None))
        } else {
          Left(ParseErrorInvalidWord(word))
        }

      // Parse Practice
      case `practiceArgName` :: practiceSessionType :: _ =>
        if (practiceSessionType == practiceAllArgName) {
          Right(Practice(Some(All)))
        } else if (practiceSessionType == practiceHalfArgName) {
          Right(Practice(Some(Half)))
        } else if (practiceSessionType.toIntOption.isDefined && practiceSessionType.toInt > 0) {
          Right(Practice(Some(ExplicitNumeric(practiceSessionType.toInt))))
        } else if (practiceSessionType.toFloatOption.isDefined && practiceSessionType.toFloat > 0.0 && practiceSessionType.toFloat <= 1.0) {
          Right(Practice(Some(PercentageNumeric(practiceSessionType.toFloat))))
        } else {
          Left(ParseErrorInvalidPracticeCommand())
        }
      case `practiceArgName` :: _ => Right(Practice(None))

      // Parse Words
      case `wordsArgName` :: _ => Right(Words)

      // Parse Help
      case `helpArgName` :: _ => Right(Help)

      // Parse Version
      case `versionArgName` :: _ => Right(Version)

      // Clear
      case `clearArg` :: _ => Right(Clear)

      // Generic Parse Error
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
  
  private def isValidSpeechPart(speechPart: String) = speechPart == nounArg |
    speechPart == verbArg |
    speechPart == pronounArg |
    speechPart == adjectiveArg |
    speechPart == prepositionArg |
    speechPart == conjunctionArg |
    speechPart == interjectionArg

  private def isLowercaseAlphabetical(w: String) = w forall (('a' to 'z') contains _)
}
