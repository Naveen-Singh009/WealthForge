@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.
@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM ----------------------------------------------------------------------------

@echo off
@setlocal

set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM Find the project base dir
set MAVEN_PROJECTBASEDIR=%~dp0

@REM Find the wrapper jar or download it
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"

if exist %WRAPPER_JAR% (
    goto runMaven
)

@REM Download wrapper jar
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
echo Downloading Maven Wrapper...
powershell -Command "Invoke-WebRequest -Uri %WRAPPER_URL% -OutFile %WRAPPER_JAR% -UseBasicParsing"

:runMaven
set MAVEN_OPTS=-Xmx512m

java %MAVEN_OPTS% -jar %WRAPPER_JAR% %*
