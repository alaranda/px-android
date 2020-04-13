#!/bin/bash
echo "Checing code format ..."
OUT="$( find . | grep '\.java' | xargs java -jar bin/google-java-format-1.7-all-deps.jar --set-exit-if-changed -n )"


if [[ ! -z ${OUT} ]];
then
    echo "There is/are invalid code formatted, remember to run make format-code before pushing :)"
    echo "Invalid formatted files:"
    echo $OUT
    exit 1
fi
