#!/bin/bash
set -eoux
echo "Exporting libs..."

LIBS="json-20140107.jar:javafx-sdk-19.0.2.1/lib/*"

echo "Compiling..."
javac --module-path javafx-sdk-19.0.2.1/lib --add-modules javafx.controls,javafx.fxml -cp "$LIBS" -d out src/*.java

echo "Running..."
java \
--module-path javafx-sdk-19.0.2.1/lib \
--add-modules javafx.controls,javafx.fxml,javafx.media \
--add-exports=javafx.graphics/com.sun.glass.utils=ALL-UNNAMED \
--add-exports=javafx.base/com.sun.javafx=ALL-UNNAMED \
-cp "out:json-20140107.jar:javafx-sdk-19.0.2.1/lib/*" \
GUI


