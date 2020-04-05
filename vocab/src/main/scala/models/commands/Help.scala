package models
import storage._

case object Help extends Command {
  val helpMessage = """
  |vocab: vocab command arguments
  |     Expand your vocabulary using this command line utility.
  |     
  |     Commands:
  |
  |       - vocab add <word> <definition> [type]
  |         Adds word to your vocabulary set with the given definition.
  |         Providing a word type is optional. <definition> MUST be 
  |         enclosed in double quotes.
  |       
  |       - vocab modify <word> <newDefinition> [type]
  |         Change the definition of an exisiting <word> and (optional)
  |         type to <newDefinition>. <newDefinition> MUST be enclosed in
  |         double quotes.
  |       
  |       - vocab delete <word> [type]
  |         Deletes the words that match <word> and the optional type.
  |         If the type is not specified all words matching <word> are
  |         deleted.
  |       
  |       Accepted values for [type]:
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
  |       - vocab clear
  |         Clears all word and practice session data. You will be asked for
  |         confirmation because this is a non-recoverable operation.
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
  |         - r: go to the next word after you successfully recall
  |              the current word's definition and/or use it in a
  |              sentence.
  |
  |         - f: go to the next word after you failed to recall the
  |              current word's definition and/or use it in a sentence.
  |
  |         - q: quit the practice session.
  |
  |     Example Usage:
  |
  |       - add a word to the vocabulary set and then modify its definition
  |           vocab add cat \"man\'s best friend\" noun
  |           vocab modidfy cat \"a domesticated feline\" noun
  |
  |       - start a practice session and review a quarter of all words
  |           vocab practice 0.25
  |
  |       - delete a word that you no longer need to review
  |           vocab delete ardor \"enthusiasm or passion\" adjective
  |
  |     Contributing:
  |       vocab is open source and available on github
  |       https://github.com/daltyboy11/vocab
  |""".stripMargin

  def run(implicit storage: Storage): Unit = {
    println(helpMessage)
  }
}

