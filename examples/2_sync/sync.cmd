@ECHO OFF

REM Load the setup for the examples.
CALL %~DP0\..\setup.cmd

REM Data point bridge configuration.
SET CONFIG_URI=%LLCONFIG%ait.test.plotter.sync.sync-host.properties

REM Sync host scenario file must be copied to the current working directory.
COPY /Y %~DP0\sync_config_plotter.json .

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.all.all.log4j2

java.exe %LOGGER_CONFIG% -jar %SYNC_JAR_FILE% %CONFIG_URI%