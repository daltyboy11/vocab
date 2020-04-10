#!/bin/bash - 

if ! [ -x "$(command -v sbt)" ]; then
  echo 'Error: sbt is not installed.' >&2
  exit 1
fi

if ! [ -x "$(command -v python3)" ]; then
  echo 'Error: python3 is not installed.' >&2
  exit 1
fi

# Setup the jar
echo assembling jar...
sbt assembly &>/dev/null

# Move everyting to /usr/bin
echo adding vocab to /usr/local/bin
cp target/scala-2.13/vocab-assembly*.jar /usr/local/bin/vocab.jar
cp vocab.py /usr/local/bin/vocab
chmod +x /usr/local/bin/vocab

# done! :)
echo vocab is ready for use!
