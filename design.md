Motivation, design, and specification for the vocab utility

# Motivation
I recently took the course Learning how To Learn on Coursera. Shortly before
that I began reading Ron Chernow's Alexander Hamilton biography. There are many
words in that biography I didn't know and was unable to infer from the context.
Because I was coming across so many unknown words during my reading, I
established a scheme for expanding my vocabulary that I would practice daily:

During the day, when I come across a new word, I write it down
in a little notebook. New words find me through my daily reading of books, web
articles, etc. Before I go to sleep I review the words recorded in the previous
five days, the current day inclusive. I consider a word "reviewed" when, during the study session, I can
recite its definition from memory AND come up with a sentence that uses it.
Using it in a sentence is important. What is the utility in knowing a word if
you cannot reognize the context in which it can be used?

As the Learning How To Learn instructors would describe it, this is like a chunk
where you don't understand the underlying ideas. Yes it's still a chunk, just
not a very useful one.

The study session is over when I can go through the entire list twice and review
each word without fail.

As you might guess, this scheme was very time consuming at first. However, over
time I would record fewer words each day because I had already learned so many!
After sticking with the initial onslaught of words it became much more
manageable. The workload is proportional to your current vocabulary AND how much
you read.

While taking Learning how to learn I realized I had been employing the technique
called "spaced repetition" in my vocabulary studies without knowing it by name
or recognizing its greater significance as a highly effective learning technique.

I aim to build a command line utility to automate the review process (i.e. I
don't have to go through the pages of my little notebook to find the words I
need to recite) and streamline my practice. It would be nice if I could easily
add words to my repretoire and invoke a study session buy typing only a few
words on the command line. Also, I will be able to do away with my little
notebook and [save the
trees](https://twosidesna.org/US/going-paperless-does-not-save-trees/).

Furthermore, [_the limits of your language are the limits of your
world_](https://oregonstate.edu/instruct/phl201/modules/Philosophers/Wittgenstein/wittgenstein.html).
I hope to provide a useful tool to others so that they may expand their
vocabulary and use more precise language to articulate their thoughts. Don't you
hate it when you can't satisfactorily convey an argument or idea to someone
because you forgot the word for it?

# Description
`vocab` is a command line utility to help you memorize words and expand your
vocabulary. It puts to use the concept of "spaced repetition" to maximize
efficiency in your studies. To learn more about spaced repetition and other
powerful learning techniques, review 

`vocab` is only available in English.

# V1 Features
The most basic feature set.

## Show help
`vocab help`
displays the man page.

## Add a word
`vocab add <word> <definition> [--type type]`
adds `word` to your practice set with the provided `definition`.
type = noun | verb | pronoun | adjective | adverb | preposition | conjuction | interjection

If you would like to add homonyms, the type must be specified. For example, "play" could refer
to the verb "to play" but it could also refer to a play you see in a theatre. To
add them both you may do the following:

`vocab add play to take part in a game --type verb`
`vocab add play a dramatic work for the stage --type noun`

If you already added "play" without a type, i.e.
`vocab add play to take part in a game`
but would like to add another word for "play" you must first use the `modify`
command to add a type to the existing "play".

## List the words
`vocab words`
List all the words in your repertoire.

## Modify a word
`vocab modify <word> <newDefinition> [--type type]`
Replace the definition for an existing word

## Show version
`vocab version`
Display the program's version number.

## Delete word(s)
`vocab delete <word> [--type type]`
deletes the words that match <word> and the optional type. If the type is not
specified, all words matching <word> are deleted.

## Practice your words
`vocab practice [ all | half | n | p ]`
Launches a study session with an optional argument to control the number of
words in the session
- `all`: practice all words
- `half`: practice half of all words
- `n`: practice n words, where n is positive integer
- `p`: practice a proportion of all words, where p is in (0.0, 1.0]

### Interactive practice - how it works
Launching a practice session makes the program interactive.

A word will be presented without its definition. At this point you should
attempt to recall its definition and/or use it in a sentence. When you are
satisfied with your attempt, use one of the commands to move on to the next word
or quit the session. To finish a session you must successfully recall each word
three times.

#### Available commands
The following commands are available to you during the practice session

- `r | recalled`: go to the next word after you successfully recall the current word's definition and/or used it in a sentence.
- `f | forgot`: move on to the next word after you failed to recall the current word's definition and/or used it in a sentence.
- `s | show`: show the current word's definition.
- `q | quit`: quit the practice session 

## How are words selected for a practice session?
If you have added words that have **never** been practice before these will be
selected first (at random from the set of all words that have never been
practiced). If the practice set is still not full, the remaining words are
randomly selected based on a probability weighting where the weight of the word
is the inverse of how many times it has been rehearsed before:

Assign a value of 1 / (practice count) to each
remaining word. Sum the total and divide each value by the total to get the weight.
Compute the array of cumulative sums of the weights. Generate a random number
between 0.0 and 1.0 and add the word associated with the range to the practice set. Remove
this word from the selection pool. Repeat until the set is full.

### Note on the selection algorithm
If m is the number of words needed for the practice session and n is the total
number of words then the selection algorithm is O(m * n). I believe this will
work in practice because the total word set should not get too large. As a user
becomes so familiar with a word that they no longer practice it, they will
delete it from the set. Furthermore, the rate at which users will learn new
words is small, even if they read a lot (words are expected to be added
organically as the user reads material while going about their daily lives...
this tool is **NOT** intended for someone to simply dump a dictionary into the
program and start practicing).
