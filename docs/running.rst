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

Running with Docker
===================

The image ``ait1/lablink-plotter:latest`` (see :doc:`installation`) starts the class named by ``PLOT_CLASS`` with ``-c`` set to ``${LLCONFIG}${PLOT_CONFIG}``.
The defaults are ``LLCONFIG=http://lablink-config:10101/get?id=``, ``PLOT_CONFIG=lablink-plotter.config``, and ``PLOT_CLASS=at.ac.ait.lablink.clients.plotter.PlotterAsync``, so the image expects a configuration server reachable as the host ``lablink-config``.
``PLOT_CLASS`` selects ``at.ac.ait.lablink.clients.plotter.PlotterAsync`` or ``at.ac.ait.lablink.clients.plotter.PlotterSync``.
``MEMORY_FLAG`` is the JVM heap setting and defaults to ``-Xmx512m``.
``LOGGER_ID`` is the logging configuration id and defaults to ``ait.all.log4j2``.
``SESSION_NAME`` is the browser tab title and defaults to ``Lablink Plotter``.
When ``DATA_DIR`` is set, the client is started with ``-DdataDir`` set to that path, which is the base for a ``plotter://`` output directory (see :doc:`configuration`).

The plot window is an HTML desktop on port ``8080``.
Publish that port and open http://localhost:8080.

On Windows, a configuration server on the host is reachable as ``host.docker.internal``:

.. code-block:: winbatch

   docker run --rm -p 8080:8080 -e LLCONFIG=http://host.docker.internal:10101/get?id= -e PLOT_CONFIG=ait.test.plotter.async.config -e MEMORY_FLAG=-Xmx1024m ait1/lablink-plotter:latest

On Linux, add that hostname explicitly:

.. code-block:: bash

   docker run --rm -p 8080:8080 --add-host=host.docker.internal:host-gateway -e LLCONFIG=http://host.docker.internal:10101/get?id= -e PLOT_CONFIG=ait.test.plotter.async.config -e MEMORY_FLAG=-Xmx1024m ait1/lablink-plotter:latest

To keep CSV output on the host, mount a directory and set ``DATA_DIR`` to the path inside the container:

.. code-block:: bash

   docker run --rm -p 8080:8080 --add-host=host.docker.internal:host-gateway -e LLCONFIG=http://host.docker.internal:10101/get?id= -e PLOT_CONFIG=ait.test.plotter.async.config -e DATA_DIR=/data -v /path/to/data:/data ait1/lablink-plotter:latest
