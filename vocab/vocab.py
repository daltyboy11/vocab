#!/usr/bin/env python3
import sys
import os

s = ""
for arg in sys.argv:
    s += "\"{}\" ".format(arg)

command = "java -jar /usr/local/bin/vocab.jar " + s
os.system(command)
