Prerequisites
=============

Required LabLink resources
--------------------------

The following Lablink resources are required:

* `Configuration Server <https://ait-lablink.readthedocs.io/projects/lablink-config-server>`_: *config-0.0.1-jar-with-dependencies.jar*
* `Datapoint Bridge <https://ait-lablink.readthedocs.io/projects/lablink-datapoint-bridge>`_: *dpbridge-0.0.1-jar-with-dependencies.jar*
* `Simple Sync Host <https://ait-lablink.readthedocs.io/projects/lablink-sync-host)>`_: *sync-0.0.1-jar-with-dependencies.jar*

When :doc:`building from source <installation>`, the corresponding JAR files will be copied to directory *target/dependency*.


Starting the configuration server
---------------------------------

Start the configuration server by executing script :github_blob:`run_config.cmd <examples/0_config/run_config.cmd>` in subdirectory :github_tree:`examples/0_config`.
This will make the content of database file *test-config.db* available via http://localhost:10101.

**NOTE**:
Once the server is running, you can view the available configurations in a web browser via http://localhost:10101.

**TIP**:
A convenient tool for viewing the content of the database file (and editing it for experimenting with the examples) is `DB Browser for SQLite <https://sqlitebrowser.org/>`_.

Example 1: Asynchronous plotter
===============================

All relevant scripts can be found in subdirectory :github_tree:`examples/1_async`.
To run the example, execute all scripts either in separate command prompt windows or by double-clicking:

* :github_blob:`dpb.cmd <examples/1_async/dpb.cmd>`: runs the data point bridge service, connecting the data source and the plotter
* :github_blob:`source.cmd <examples/1_async/source.cmd>`: runs the data source, which will send data to the plotter
* :github_blob:`plot.cmd <examples/1_async/plot.cmd>`: runs the plotter, which will plot incoming data

The order in which the scripts are started is in principle arbitrary.

Example 2: Synchronous plotter
==============================

All relevant scripts can be found in subdirectory :github_tree:`examples/2_sync`.
To run the example, execute all scripts either in separate command prompt windows or by double-clicking:

* :github_blob:`dpb.cmd <examples/2_sync/dpb.cmd>`: runs the data point bridge service, connecting the data source and the plotter
* :github_blob:`source.cmd <examples/2_sync/source.cmd>`: runs the data source, which will send data to the plotter
* :github_blob:`plot.cmd <examples/2_sync/plot.cmd>`: runs the plotter, which will plot incoming data
* :github_blob:`sync.cmd <examples/2_sync/sync.cmd>`: runs the sync host

Start the data point bridge and the clients first (in arbitrary order).
**Before you start the sync host**, make sure that the **clients are already connected to the data point bridge** (check status messages of data point bridge).
