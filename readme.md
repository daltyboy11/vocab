`vocab` is a command line utility for learning new words and expanding your
vocabulary.

# Introduction
> The limits of my language mean the limits of my world.
  - Ludwig Wittgenstein

You can know a lot of words and not come off as pretentious. In fact, being able
to draw on a large vocabulary to precisely express thoughts and ideas is an
incredible ability to have; one we can all improve on. It sucks when the word
you're looking for is just on the tip of your tongue!

# Usage
Use `vocab` to manage a list of words you'd like to become familiar with.
Rehearse you understanding by running *practice sessions* in which you try to
recall definitions and use the words in sentences.

Under the hood `vocab` uses the principle of [spaced
repetition](https://en.wikipedia.org/wiki/Spaced_repetition) to maximize your
practice efficiency.

## Adding words to your vocabulary list
You add a word by providing its name, definition, and optinally what part of
speech to which it belongs

`vocab add peregrination "a long and meandering journel" noun`

## Deleting words
Delete words you're so familiar with that you no longer need practice.

`vocab delete recrudescence`

## Practicing
`vocab practice` launches an interactive practice session in your console.
See `vocab help` for flags to configure the number of words in a practice session.

You will see a word without its definition. This is when you try and recall its
definition. I recommend you come up with a sentence using it as well. Once you
are satisfied with your effort you can move on to the word.

// TODO - insert image

Input `r` (for recalled, a successful attempt) or `f` (for forgot, a failed attempt
at remembering) to move onto the next word.

// TODO - insert image

If you can't remember what the word means despite your best efforts, or you made
a guess but aren't entirely sure, input `s` (for show) to display the
definition.

// TODO - insert image

The practice session ends when you've successfully recalled each word three
times.

## Modifying words
Go back and modify a word's definition if you make a mistake.

`vocab add affect "a change which is a result or consequence of an action or other cause"`

Confusing affect with effect... whoops!

`vocab modify affect "have an effect on; make a difference to."`

# Installation

## Some starter words
Here is a list of words I was able to remember by practicing with `vocab`. This
is just a recommendation based on my personal experience! After setup, go ahead
and run `seed_words.sh` to add these words to your practice.

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
- senescene
- surfeit
- sybaritic
- vertiginous
