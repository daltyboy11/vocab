`vocab` is a command line utility for learning new words and expanding your
vocabulary.

# Introduction
> The limits of my language mean the limits of my world.
  - Ludwig Wittgenstein

Knowing lots of words isn't pedantry. Perhaps memorizing them is. But being able
to draw on a large vocabulary to precisely express thoughts and ideas is an
incredible ability; one we can all improve on.

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

// TODO - remove --type requirement

`vocab add peregrination "a long and meandering journel" --type noun`

## Deleting words
Delete words you're so familiar with that you no longer need practice.

`vocab delete recrudescence`

## Modifying words
Go back and modify a word's definition if you make a mistake.

`vocab add affect "a change which is a result or consequence of an action or other cause"`

Confusing affect with effect... whoops!

`vocab modify affect "have an effect on; make a difference to."`

## Practicing
`vocab practice` launches a practice session in your console. See `vocab help`
for flags to configure the number of words in a practice session.

# Installation
