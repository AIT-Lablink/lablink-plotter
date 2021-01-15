Invoking the clients from the command line
==========================================

When running the clients, the use of the ``-c`` command line flag followed by the URI to the configuration (see :doc:`here <configuration>`) is mandatory.

For example, on Windows this could look something like this:

.. code-block:: winbatch

   SET PLOT=at.ac.ait.lablink.clients.plotter.PlotterSync
   SET LLCONFIG=http://localhost:10101/get?id=
   SET CONFIG_FILE_URI=%LLCONFIG%ait.test.plotter.sync.config
   SET MEMORY_FLAG=-Xmx1024M
   java.exe %MEMORY_FLAG% -cp \path\to\plotter-<VERSION>-jar-with-dependencies.jar %PLOT% -c %CONFIG_FILE_URI%
