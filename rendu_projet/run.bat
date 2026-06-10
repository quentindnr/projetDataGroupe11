@echo off
set JAVAFX_PATH=%~dp0lib\openjfx-21.0.10_linux-x64_bin-sdk\javafx-sdk-21.0.10\lib
java -cp "%~dp0facial-recognition.jar;%~dp0lib\*" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics Main
pause
