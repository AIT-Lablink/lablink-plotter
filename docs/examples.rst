Overview
========

These examples show a plotter drawing two signals that a data source publishes.

* A signal of type ``DOUBLE`` is ``10 * cos(pi * n / 20)``, where ``n`` is the sample index.
* A signal of type ``LONG`` is the integer part of ``5 * cos(pi * n / 30)``, where ``n`` is the sample index.

In the **asynchronous** example the source sends a sample every 300 ms.
The horizontal axis of the plotter shows wall-clock time.

In the **synchronous** example the source sends one sample at each synchronization step.
The sync host runs one minute of emulation in steps of 200 ms.
The horizontal axis of the plotter shows that synchronization time.


Prerequisites
=============

The examples run with Docker Compose.
These images are published on Docker Hub and are pulled automatically:

* ``ait1/lablink-config-server:latest``
* ``ait1/lablink-datapoint-bridge:latest``
* ``ait1/lablink-plotter:latest``
* ``ait1/lablink-sync-host:latest``

Compose builds the data source itself from :github_blob:`Dockerfile.source <examples/docker/Dockerfile.source>`.
Both examples publish the configuration server on port ``10101`` and the plotter on port ``8080``, so run only one of them at a time.

The stacks share :github_blob:`test-config.db <examples/config/test-config.db>` and :github_blob:`mosquitto.conf <examples/config/mosquitto.conf>`.
Inside the containers the broker host is ``mqtt`` and the configuration server host is ``lablink-config``.
The sync host does not load the shared Lablink properties, so its own entry in that database sets ``lowLevelComm.brokerAddress`` to ``mqtt``.

.. note:: Once a stack is running, the available configurations are listed at http://localhost:10101.

.. seealso:: A convenient tool for viewing the content of the database file (and editing it for experimenting with the examples) is `DB Browser for SQLite <https://sqlitebrowser.org/>`_.


Example 1: Asynchronous plotter
===============================

The Compose file is :github_blob:`examples/async/docker-compose.yml <examples/async/docker-compose.yml>`.
From :github_tree:`examples/async`:

.. code-block:: bash

   docker compose up

This starts Mosquitto, the configuration server, the datapoint bridge, the data source, and the plotter.
The plotter window is at http://localhost:8080.
CSV output is written to ``examples/async/data``.


Example 2: Synchronous plotter
==============================

The Compose file is :github_blob:`examples/sync/docker-compose.yml <examples/sync/docker-compose.yml>`.
From :github_tree:`examples/sync`:

.. code-block:: bash

   docker compose up

This starts Mosquitto, the configuration server, the datapoint bridge, the data source, and the plotter.
The plotter window is at http://localhost:8080.
CSV output is written to ``examples/sync/data``.

Start the sync host only after the datapoint bridge reports that the clients are connected:

.. code-block:: bash

   docker compose --profile sync-host up sync-host

The scenario file is :github_blob:`sync_config_plotter.json <examples/sync/sync_config_plotter.json>`.
