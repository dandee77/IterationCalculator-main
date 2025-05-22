@echo off
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/*" -d bin src/*.java
if %errorlevel% neq 0 (
    echo Compilation failed! Please check your code for errors.
    pause
    exit /b 1
)
java -cp "lib/*;bin" src/CalculatorSwing