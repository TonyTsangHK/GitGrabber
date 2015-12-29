#!/bin/sh

url=$1
ref=$2
fileName=$3

git archive --remote="$url" "$ref" "$fileName" | tar -x "$fileName" -O | cat