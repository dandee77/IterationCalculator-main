@echo off
echo Compiling the Iteration Calculator...
mkdir bin 2>nul
javac -cp "lib/*" -d bin src/*.java
if %errorlevel% neq 0 (
    echo Compilation failed! Please check your code for errors.
    pause
    exit /b 1
)
echo Compilation successful! Running the application...
java -cp "lib/*;bin" CalculatorSwing
pause