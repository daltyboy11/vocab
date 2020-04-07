#!/usr/bin/env python3
import os

words = [
    'clerisy',
    'convoke',
    'deracinate',
    'deviltry',
    'effete',
    'fecund',
    'harried',
    'irredentist',
    'lugubrious',
    'munificent',
    'parlous',
    'paroxysm',
    'patina',
    'penury',
    'perquisite',
    'philippic',
    'phillistine',
    'recondite',
    'recrudescence',
    'saccharine',
    'senescence',
    'surfeit',
    'sybaritic',
    'vertiginous',
]

parts_of_speech = [
    'noun',
    'verb',
    'verb',
    'noun',
    'adjective',
    'adjective',
    'adjective',
    'noun',
    'adjective',
    'adjective',
    'adjective',
    'noun',
    'noun',
    'noun',
    'noun',
    'noun',
    'noun',
    'adjective',
    'noun',
    'adjective',
    'noun',
    'noun',
    'adjective',
    'adjective'
]

definitions = [
    'a distinct class of learned or literary people',
    'call together or summon (an assembly or meeting)',
    'to tear something up by the roots',
    'wicked activity',
    '(of a person) overrefined and ineffectual',
    'capable of producing an abundance of offspring; fertile',
    'feeling strained as a result of having demands persistently made of one',
    'a person advocating for the restoration to their country of a territory formerly belonging to it',
    'looking or sounding sad and dismal',
    '(of a sum of money) more generous than necessary',
    'full of danger or uncertainty; precarious',
    'a sudden attack or violent expression of a particular emotion or activity',
    'the impression or appearance of something',
    'extreme poverty',
    'another term for perk',
    'a bitter attack or denunciation, especially a verbal one',
    'a person who is hostile or indifferent to culture and the arts, or who has no understanding of them',
    '(of a subject or knowledge) little known',
    'the recurrence of an undesirable condition',
    'excessively sweet or sentimental',
    'the process of deterioration with age',
    'an excessive amount of something',
    'fond of sensuous luxury or pleasure; self indulgent',
    'causing vertigo, especially by being extremely high or steep',
]

if __name__ == '__main__':
    for i in range(len(words)):
        word = words[i]
        definition = definitions[i]
        part_of_speech = parts_of_speech[i]
        cmd = 'vocab add {} \"{}\" {}'.format(word, definition, part_of_speech)
        print(cmd)
        os.system(cmd)
