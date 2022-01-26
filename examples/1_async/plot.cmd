@ECHO OFF

SETLOCAL

REM Load the setup for the examples.
CALL "%~DP0\..\setup.cmd"

REM Path to class implementing the main routine.
SET PLOT=at.ac.ait.lablink.clients.plotter.PlotterAsync

REM Data point bridge configuration.
SET CONFIG_FILE_URI=%LLCONFIG%ait.test.plotter.async.config

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.all.all.log4j2

REM Set the directory containing this file as the base directory for writing CSV output files.
SET DATA_DIR_CONFIG=-DdataDir=%~DP0

REM Specify the maximum size (in bytes) of the memory allocation pool.
SET MEMORY_FLAG=-Xmx1024M

REM Run the example.
"%JAVA_HOME%\bin\java.exe" %MEMORY_FLAG% %LOGGER_CONFIG% %DATA_DIR_CONFIG% -cp "%PLOTTER_JAR_FILE%" %PLOT% -c %CONFIG_FILE_URI%

PAUSE
