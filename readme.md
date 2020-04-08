`vocab` is a command line utility for learning new words and expanding your
vocabulary.

# Introduction
> The limits of my language mean the limits of my world.
  - Ludwig Wittgenstein

This quote has several interpretations but my personal interpretation (taken out of
context from Wittgenstein's philosophy) is this:

We use language to express ideas and reason about the world. Knowing
and using more words can improve the clarity and precision of those
thoughts and ideas, thus improving our clarity and precision when
reasoning about the world.

Conveying your thoughts clearly and precisely to others is an excellent skill to
have; one everyone can improve on. Doesn't it suck when you stumble in the
middle of a conversation because the word you're looking for is on the tip of
your tongue?

# Installation Guide

Available on Linux and macOS. Requires [sbt](https://www.scala-sbt.org/download.html), [python](https://www.python.org/downloads/), and [java](https://www.java.com/en/download/).
```bash
git clone https://github.com/daltyboy11/vocab.git
cd vocab/vocab/
chmod +x setup.sh
./setup.sh
# uncomment to optionally seed vocab with some start words
# python3 seed_words.py
```

# Usage
Use `vocab` to manage a list of words you'd like to become familiar with.
Practice your words by running a *practice session*. Vocab presents words to you
like flashcards, utilizing the principle of [spaced
repetition](https://en.wikipedia.org/wiki/Spaced_repetition) to decide what
words to show you.

## Adding words to your vocabulary list
You add a word by providing its name, definition, and optionally what part of
speech it belongs to.

`vocab add peregrination "a long and meandering journey" noun`

## Deleting words
Delete words you don't need to practice anymore.

`vocab delete recrudescence`

## Practicing
`vocab practice` launches an interactive practice session in your console.
See `vocab help` for flags to configure the number of words in a practice session.

You will see a word in the console without its definition. This is when you
should try to recall its definition. To maximize learning we recommend you also
try to come up with a sentence using the word. Once you are satisfied with your
attempt you can move on to the next word.

**Your console after starting a practice session**
```
---------------------------------------------------------------------
| word       | definition                          | part of speech |
---------------------------------------------------------------------
| paroxysm   |                                     | noun           |
|            |                                     |                |
|            |                                     |                |
---------------------------------------------------------------------
input:
```

If you can't remember what the word means despite your best effort, or you made
a guess but aren't entirely sure, input `s` (for show) to display the
definition:

**Your console after using the `show` command**
```
---------------------------------------------------------------------
| word       | definition                          | part of speech |
---------------------------------------------------------------------
| paroxysm   | a sudden attack or violent          | noun           |
|            | expression of a particular emotion  |                |
|            | or activity                         |                |
---------------------------------------------------------------------
input:
```

Input `r` (for recalled, a successful attempt) or `f` (for forgot, a failed attempt
at remembering) to move onto the next word:

**Your console after "recalling" the word**
```
---------------------------------------------------------------------
| word       | definition                          | part of speech |
---------------------------------------------------------------------
| surfeit    | an excessive amount of something    | noun           |
---------------------------------------------------------------------
input: r
Recalled!
```

**Your console after "forgetting" the word**
```
---------------------------------------------------------------------
| word       | definition                          | part of speech |
---------------------------------------------------------------------
| surfeit    | an excessive amount of something    | noun           |
---------------------------------------------------------------------
input: f
Not Recalled!
```

The practice session ends when you've successfully recalled each word three
times.

## Modifying words
Go back and modify a word's definition if you make a mistake.

`vocab add affect "a change which is a result or consequence of an action or other cause"`

Confusing affect with effect... whoops!

`vocab modify affect "have an effect on; make a difference to."`

## Getting help
For a full list of flags and features, run

`vocab help`

## Some starter words
Here is a list of words I was able to remember by practicing with `vocab`. This
is just a recommendation based on my personal readings! After setup, go ahead
and run `seed_words.py` to add these words to your practice.

- clerisy
- convoke
- deracinate
- deviltry
- effete
- fecund
- harried
- irredentist
- lugubrious
- munificent
- parlous
- paroxysm
- patina
- penury
- perquisite
- philippic
- phillistine
- recondite
- recrudescence
- saccharine
- senescence
- surfeit
- sybaritic
- vertiginous

# Why?
Unlike the plethora of generic flashcard/memorization software available, `vocab`
is lightweight, designed for a very specific purpose, and intended to be used by
those who already spend a lot of their computer time in the console.
