#!/bin/bash
BASEDIR=$(dirname "$0")

# Recherche dynamique du dossier lib de JavaFX dans l'archive extraite
JAVAFX_PATH=$(find "$BASEDIR/lib" -type d -path "*/javafx-sdk-*/lib" -o -path "*/openjfx-*/lib" | head -n 1)

if [ -z "$JAVAFX_PATH" ]; then
    JAVAFX_PATH="$BASEDIR/lib"
fi

java -cp "$BASEDIR/facial-recognition.jar:$BASEDIR/lib/*" \
     --module-path "$JAVAFX_PATH" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics \
     Main
