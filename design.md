The Design and Specification for the vocab command line tool

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

## `vocab help`
displays the man page.

## `vocab add word definition [type]`
adds `word` to your practice set with the provided `definition`.

type = noun | verb | pronoun | adjective | adverb | preposition | conjuction | interjection

you must specify the type when adding homonyms. For example, "play" could refer
to the verb "to play" but it could also refer to a play you see in a theatre. To
add them both you may do the following:

`vocab add word play verb`
`vocab add word play noun`

## `vocab words`
lists all the words in your repetoire along with their definition and number of
times rehearsed.

## `vocab modify word definition`
changes the current definition of `word` to be `definition`.

## `vocab delete word`
deletes `word`, if it exists.

## `vocab practice [ all | half | n ]`
start a study session with either all, half, or a specific number (n) of words.

### interactive practice
the following commands are available to you during your study session:

- `r` | `recalled`: go to the next word after you successfully recall the current word's definition and/or used it in a sentence.
- `f` | `forgot`: move on to the next word after you failed to recall the current word's definition and/or used it in a sentence.
- `s` | `show`: show the definition of the word.
- `q` | `quit`: quit the study session.

## suggested method of practice
At each word, recite the definition and use it in a sentence, then move on to
the next word using either the `r` or `f` command. Use the `s` command to reveal the
definition for confirmation of your knowledge or because you forgot the
definition.

## word selection for study sessions
Words are selected based on a weighted probability, where the weight of the word
is the inverse of how many times it has been rehearsed before.

From the set of all words randomly select any words with a practice count of 0 either until there are no more
words with a frequency of 0 or the practice set is full.

If the practice set is full, we are done.

If the practice set is not full assign a value of 1 / (practice count) to each
remaining word. Sum the total and divide each value by the total to get the weight.
Compute the array of cumulative sums of the weights. Generate a random number
between 0 and 1 and add the word associated with the range to the set. Remove
this word from the selection pool. Repeat until the set is full.

### Note on the selection algorithm
If m is the number of words needed for the practice session and n is the total
number of words then the selection algorithm is O(m * n). For now this is fine,
as I don't anticipate word lists getting very large. But is there a more
efficient method to do this?
