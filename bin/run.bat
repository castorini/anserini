@echo off
REM This script is a Windows equivalent of bin/run.sh.
REM It finds the latest fatjar and runs the specified Anserini command

setlocal enabledelayedexpansion

cd /d "%~dp0"

REM Locating the latest fatjar in the target directory
for /f "delims=" %%f in ('dir /b /o-n ..\target\*-fatjar.jar 2^>nul') do (
    set FATJAR=..\target\%%f
    goto :found
)

echo No fatjar found in target directory!
exit /b 1

:found
REM Running the specified command using the found fatjar with memory and module settings
java -cp "!FATJAR!" -Xms512M -Xmx64G --add-modules jdk.incubator.vector %*