@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME configurado: %JAVA_HOME%
echo Ejecutando el proyecto...
gradlew.bat bootRun

