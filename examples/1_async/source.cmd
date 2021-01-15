@ECHO OFF

SETLOCAL

REM Load the setup for the examples.
CALL %~DP0\..\setup.cmd

REM Path to class implementing the main routine.
SET SOURCE=async.DataSourceAsync

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.all.all.log4j2

REM Run the example.
java.exe %LOGGER_CONFIG% -cp %PLOTTER_TEST_CLASS_DIR%;%PLOTTER_JAR_FILE% %SOURCE%