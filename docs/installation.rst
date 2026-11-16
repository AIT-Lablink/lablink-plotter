Installation
============

Maven
-----

The Lablink plotter's compiled Java package is available on the |MCR|_.
Use it in your local Maven_ setup by including the following dependency into your *pom.xml*:

.. code-block:: xml

   <dependency>
     <groupId>at.ac.ait.lablink.clients</groupId>
     <artifactId>plotter</artifactId>
     <version>0.0.2</version>
   </dependency>

.. note:: You may have to adapt this snippet to use the latest version, please check the |MCR|_.


Building from source
--------------------

Installation from source requires a local **Java Development Kit** installation, for instance the `Oracle Java SE Development Kit 13 <https://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ or the `OpenJDK <https://openjdk.java.net/>`_.

Check out the project and compile it with Maven_:

.. code-block:: none

   git clone https://github.com/AIT-Lablink/lablink-plotter.git
   cd lablink-plotter
   mvnw clean package

This should create JAR file *plotter-<VERSION>-jar-with-dependencies.jar* in subdirectory *target/assembly*.

Docker image
------------

A ready-to-run image is published on `Docker Hub <https://hub.docker.com/r/ait1/lablink-plotter>`_ as ``ait1/lablink-plotter:latest``:

.. code-block:: winbatch

   docker pull ait1/lablink-plotter:latest

The plot window is served in a browser on port ``8080``.
See :doc:`here <running>` for how to start the container.
Building the image locally with :github_blob:`docker/build.cmd <docker/build.cmd>` tags the result with the same name.


.. |MCR| replace:: Maven Central Repository
.. _MCR: https://search.maven.org/artifact/at.ac.ait.lablink.clients/plotter
.. _Maven: https://maven.apache.org
