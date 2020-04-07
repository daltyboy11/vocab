package models
import storage._

case object Help extends Command {
  val helpMessage = """
  |vocab: vocab <command> <arguments>
  |     Expand your vocabulary using this command line utility.
  |     
  |     Commands:
  |
  |       - vocab add <word> <definition> [type]
  |           Adds <word> to your vocabulary set with the given <definition>.
  |           The <definition> MUST be enclosed in double quotes. See below
  |           for accepted values for [type].
  |       
  |       - vocab modify <word> <newDefinition> [type]
  |           Update <word>'s definition with <newDefinition>. The <newDefinition>
  |           MUST be enclosed in double quotes. See below for accepted values for
  |           [type].
  |       
  |       - vocab delete <word> [type]
  |           Deletes all words that match <word> and [type]. This command is useful
  |           when you become so familiar with a word you no longer need to include
  |           it in practice.
  |       
  |           Accepted values for [type]:
  |             The type argument used in the add, modify, and delete commands 
  |             corresponds to the word's part of speech.
  |         
  |             type = noun | verb | pronoun | adjective | adverb | preposition | conjunction | interjection
  |
  |             The usage of types enables homonyms. E.g.
  |             vocab add play "to take part in a game" --type verb
  |             vocab add play "a dramatic work for the stage" --type noun
  |       
  |       - vocab practice [ all | half | n | p ]
  |           Launches a practice session with an optional argument to control
  |           the number of words in the session:
  |
  |           - all: practice all the words in your vocabulary set
  |           - half: practice half of the words in your vocabulary set
  |           - n: practice n words, where n is a positive integer
  |           - p: practice a proportion of all words, where p is in (0.0, 1.0]
  |
  |       - vocab words
  |           Shows all the words in your vocabulary set along with the number of
  |           times they have been practiced.
  |
  |       - vocab clear
  |           Clears all word and practice session data. You will be asked for
  |           confirmation because this is a non-recoverable operation.
  |
  |       - vocab version
  |           Show the program version number.
  |
  |       - vocab help
  |           Show this help page.
  |
  |     Practice Sessions:
  |       vocab practice [session-type] will start an interactive practice session.
  |       
  |       Words are presented to you in the terminal one by one. The definition
  |       is hidden. You should attempt to recall the word's definition and/or
  |       use it in a sentence. If you're satisfied with your attempt you can
  |       move on to the next word. If you're unsure what the definition you can
  |       ask the program to reveal the definition. A session is complete when
  |       you successfully "recall" each word 3 times.
  |       
  |       Available commands:
  |         - r | recalled:
  |             Go to the next word after you successfully recall
  |             the current word's definition and/or use it in a
  |             sentence.
  |
  |         - f | forgot:
  |             Go to the next word after you failed to recall the
  |             current word's definition and/or use it in a sentence.
  |
  |         - s | show:
  |             Reveal the current word's definition.
  |
  |         - q | quit:
  |             Quit the practice session.
  |
  |     Example Usage:
  |
  |       - add a word to the vocabulary set and then modify its definition:
  |           `vocab add cat "man's best friend" noun`
  |           `vocab modidfy cat "a domesticated feline" noun`
  |
  |       - start a practice session and review one quarter of your words:
  |           `vocab practice 0.25`
  |
  |       - delete a word that you no longer need to review:
  |           `vocab delete ardor "enthusiasm or passion" adjective`
  |
  |     Contributing:
  |       vocab is open source and available on github
  |       https://github.com/daltyboy11/vocab
  |""".stripMargin

  def run(implicit storage: Storage): Unit = {
    println(helpMessage)
  }
}

