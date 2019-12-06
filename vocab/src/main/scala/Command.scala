package commandlineparser

import models._
import commands._

// Why is my command line parse so outrageously complicated??!?! Because it's purely
// functional, and (I hope) somewhat extensible.
object CommandLine {
  object Args {
    val addArgName                    = "add"
    val wordsArgName                  = "words"
    val modifyArgName                 = "modify"
    val deleteArgName                 = "delete"
    val listArgName                   = "list"
    val practiceArgName               = "practice"
    val practiceAllArgName            = "all"
    val practiceHalfArgName           = "half"
    val helpArgName                   = "help"
    val versionArgName                = "version"
    val nounArg                       = "--noun"
    val verbArg                       = "--verb"
    val pronounArg                    = "--pronoun"
    val adjectiveArg                  = "--adjective"
    val adverbArg                     = "--adverb"
    val prepositionArg                = "--preposition"
    val conjunctionArg                = "--conjunction"
    val interjectionArg               = "--interjection"
  }

  val placeholder                   = "-1"

  import Args._

  def parseArgs(args: String): Either[ParseError, Command] = {
    // split by whitespace, remove extra whitespace, and drop the first
    // argument (the name of the program)
    val argList = args.split(" ").map(_.trim).drop(1).toList

    // parse first arg
    argList.headOption match {
      case Some(rawArg) => rawArg match {
        case `addArgName` | `modifyArgName` | `deleteArgName` | `practiceArgName` => parseMultiArgCommand(argList)
        case `helpArgName` => Right(Help)
        case `versionArgName` => Right(Version)
        case `wordsArgName` => Right(Words)
        case unsupported => Left(ParseErrorUnsupportedCommand(unsupported))
      }
      case None => Left(ParseErrorNoArgs())
    }
  }

  private def parseMultiArgCommand(argList: List[String]): Either[ParseError, Command] = {
    argList.head match {
      case `addArgName` =>
        argList.tail.foldLeft(Right(Add(placeholder, placeholder, None)): Either[ParseError, Add])(parseAdd) match {
          case Right(Add(word, `placeholder`, _)) => Left(ParseErrorMissingDefinition(word))
          case valid => valid
        }
  
      case `modifyArgName` =>
        argList.tail.foldLeft(Right(Modify(placeholder, placeholder, None)): Either[ParseError, Modify])(parseModify) match {
          case Right(Modify(word, `placeholder`, _)) => Left(ParseErrorMissingDefinition(word))
          case valid => valid
        }

      case `deleteArgName` =>
        argList.tail.foldLeft(Right(Delete(placeholder, None)): Either[ParseError, Delete])(parseDelete)

      case `practiceArgName` =>
        argList.tail.foldLeft(Right(Practice(None)): Either[ParseError, Practice])(parsePractice)
    }
  }

  // True if all the characters in `s` are lowercase alphanumeric.
  // False otherwise.
  private def containsNonLowercaseAlphabetical(s: String): Boolean = !s.forall(('a' to 'z').contains(_))

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
  
  // Assuming `vocab add` prefix, parses `word definition partOfSpeech`.
  // Anything after `partOfSpeech` is ignored.
  private def parseAdd(tentativeAdd: Either[ParseError, Add], nextArg: String): Either[ParseError, Add] = {
    tentativeAdd match {
      case Left(_) => tentativeAdd
      case Right(add @ Add(word, description, partOfSpeech)) => word match {
        case `placeholder` if containsNonLowercaseAlphabetical(nextArg) =>
          Left(ParseErrorUnexpectedNonAlphabeticalToken(nextArg))
        case `placeholder` =>
          Right(Add(nextArg, description, partOfSpeech))

        // Excess input when parsing is finished. just continue along.
        case word if description != placeholder
        && !partOfSpeech.isEmpty =>
          Right(add)

        // Hit the description
        case word if partOfSpeech.isEmpty
        && description == placeholder =>
          if (!containsNonLowercaseAlphabetical(nextArg))
            Right(Add(word, nextArg, partOfSpeech))
          else
            Left(ParseErrorUnexpectedNonAlphabeticalToken(nextArg))

        // Currently parsing description valid
        case word if description != placeholder
        && !containsNonLowercaseAlphabetical(nextArg) =>
          Right(Add(word, description + " " + nextArg, partOfSpeech))

        // Currently parsing description invalid
        case word if description != placeholder
        && containsNonLowercaseAlphabetical(nextArg)
        && !nextArg.startsWith("--") =>
          Left(ParseErrorUnexpectedNonAlphabeticalToken(nextArg))

        // Currently parsing description and hit part of speech
        case word if partOfSpeech.isEmpty
        && description != placeholder
        && nextArg.startsWith("--") =>
          partOfSpeechFromString(nextArg) match {
            case invalid @ Invalid(_) =>
              Left(ParseErrorInvalidPartOfSpeech(invalid))
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
          Left(ParseErrorUnknown())
        }
      }
    }
  }

  // Asusming `vocab modify` prefix, parses `word definition --type`. Parsing is
  // identical to parseAdd - TODO - how can I consolidate these, given that the
  // types are different.
  private def parseModify(tentativeModify: Either[ParseError, Modify], nextArg: String): Either[ParseError, Modify] = {
    tentativeModify match {
      case Left(_) => tentativeModify 
      case Right(modify @ Modify(word, newDefinition, partOfSpeech)) => word match {
        case `placeholder` if containsNonLowercaseAlphabetical(nextArg) =>
          Left(ParseErrorUnexpectedNonAlphabeticalToken(nextArg))
        case `placeholder` =>
          Right(Modify(nextArg, newDefinition, partOfSpeech))

        // Parsing finished, carry along. 
        case word if newDefinition != placeholder && !partOfSpeech.isEmpty => Right(modify)

        // Start of description
        case word if newDefinition == placeholder && partOfSpeech.isEmpty =>
          if (!containsNonLowercaseAlphabetical(nextArg))
            Right(Modify(word, nextArg, partOfSpeech))
          else
            Left(ParseErrorUnexpectedNonAlphabeticalToken(nextArg))

        // Continue parsing valid definition
        case word if newDefinition != placeholder
        && !containsNonLowercaseAlphabetical(nextArg) =>
          Right(Modify(word, newDefinition + " " + nextArg, partOfSpeech))

        // Continue parsing definition invalid - next arg is not a "--"
        case word if newDefinition != placeholder
        && containsNonLowercaseAlphabetical(nextArg)
        && !nextArg.startsWith("--") =>
          Left(ParseErrorUnexpectedNonAlphabeticalToken(nextArg))

        case word if partOfSpeech.isEmpty
        && newDefinition != placeholder
        && nextArg.startsWith("--") => partOfSpeechFromString(nextArg) match {
          case invalid: Invalid => // TODO - match on type instead of @ ...
            Left(ParseErrorInvalidPartOfSpeech(invalid))
          case speechPart => Right(Modify(word, newDefinition, Some(speechPart)))
        }

        // We should not be here
        case _ => Left(ParseErrorUnknown())
      }
    }
  }

  // Assuming `vocab delete` prefix, parses `word type`, where `type` is
  // optional.
  private def parseDelete(tentativeDelete: Either[ParseError, Delete], nextArg: String): Either[ParseError, Delete] = {
    tentativeDelete match {
      case Left(_) => tentativeDelete

      case Right(Delete(word, partOfSpeech)) => word match {
        case `placeholder` if !containsNonLowercaseAlphabetical(nextArg) =>
          Right(Delete(nextArg, partOfSpeech))

        case `placeholder` =>
          Left(ParseErrorUnexpectedNonAlphabeticalToken(nextArg))

        case word if partOfSpeech.isEmpty =>
          if (nextArg.startsWith("--"))
            partOfSpeechFromString(nextArg) match {
              case invalid: Invalid => Left(ParseErrorInvalidPartOfSpeech(invalid))
              case speechPart => Right(Delete(word, Some(speechPart)))
            }
          else
            Left(ParseErrorInvalidPartOfSpeech(Invalid(nextArg)))

        case word if !partOfSpeech.isEmpty => tentativeDelete

        case _ => Left(ParseErrorUnknown())
      }
    }
  }

  // Assuming `vocab practice` prefix, parses
  private def parsePractice(tentativePractice: Either[ParseError, Practice], nextArg: String): Either[ParseError, Practice] = {
    tentativePractice match {
      case Left(_) => tentativePractice

      case Right(Practice(practiceSession)) => practiceSession match {
        case None => nextArg match {
          case `practiceAllArgName` => Right(Practice(Some(All)))
          
          case `practiceHalfArgName` => Right(Practice(Some(Half)))

          // Here you see the limitations of this foldLeft approach of
          // cumulative argument parsing. The cumulative parser can not look
          // ahead :( so I have to do match to see if any of the required
          // arguments remain placeholders
          case intAsString if nextArg.toIntOption.isDefined => nextArg.toIntOption.get match {
            case int if int > 0 => Right(Practice(Some(ExplicitNumeric(int))))
            case int => Left(ParseErrorInvalidExplicitNumeric(int))
          }

          case fraction if nextArg.toFloatOption.isDefined => nextArg.toFloatOption.get match {
            case fraction if fraction > 0 && fraction <= 1 => Right(Practice(Some(PercentageNumeric(fraction))))
            case fraction => Left(ParseErrorInvalidPercentageNumeric(fraction))
          }
          
          case _ => Left(ParseErrorInvalidPracticeArg(nextArg))
        }

        case Some(_) => tentativePractice
      }
    }
  }
}
