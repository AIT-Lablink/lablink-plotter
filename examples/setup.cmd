REM =============================================================
REM Edit the following variables to comply with your local setup.
REM =============================================================

REM Connection string for configuration server.
SET LLCONFIG=http://localhost:10101/get?id=

REM Version of plotter package.
SET VERSION=0.0.1-SNAPSHOT

REM Root directory of plotter package (only change this if you really know what you are doing).
SET PLOTTER_ROOT_DIR=%~DP0..

REM Path to Java JAR file of plotter package.
SET PLOTTER_JAR_FILE=%PLOTTER_ROOT_DIR%\target\assembly\plotter-0.0.1-jar-with-dependencies.jar 

REM Directory containing Java class files for testing.
SET PLOTTER_TEST_CLASS_DIR=%PLOTTER_ROOT_DIR%\target\test-classes

REM Path to Java JAR file of data point bridge.
SET DPB_JAR_FILE=%PLOTTER_ROOT_DIR%\target\dependency\dpbridge-0.0.1-jar-with-dependencies.jar

REM Path to Java JAR file of standalone sync host.
SET SYNC_JAR_FILE=%PLOTTER_ROOT_DIR%\target\dependency\sync-0.0.1-jar-with-dependencies.jar

REM Path to Java JAR file of config server.
SET CONFIG_JAR_FILE=%PLOTTER_ROOT_DIR%\target\dependency\config-0.0.1-jar-with-dependencies.jar
