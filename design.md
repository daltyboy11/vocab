# The Design and Specification for the vocab command line tool

## Motivation
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

## Description
`vocab` is a command line utility to help you memorize words and expand your
vocabulary. It puts to use the concept of "spaced repetition" to maximize
efficiency in your studies. To learn more about spaced repetition and other
powerful learning techniques, review 

`vocab` is only available in English.
