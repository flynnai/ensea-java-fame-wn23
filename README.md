Open in Intellij, then create a build configuration with these options:

"Application" Build Configuration

Click "Modify Options" -> "Add VM Options"

VM Options:
--module-path "./javafx-sdk-19.0.2.1/lib" --add-modules javafx.controls,javafx.media,javafx.fxml


Main Class:
GUI

Then, hit Run and see the result.
