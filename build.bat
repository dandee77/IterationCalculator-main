@echo off
echo Compiling the Iteration Calculator...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/*" -d bin src/*.java
if %errorlevel% neq 0 (
    echo Compilation failed! Please check your code for errors.
    pause
    exit /b 1
)
echo Compilation successful! Running the application...
java -cp "lib/*;bin" CalculatorSwing
pause