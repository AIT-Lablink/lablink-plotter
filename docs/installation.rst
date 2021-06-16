Maven
=====

Include the Lablink plotter clients to your Maven setup by including the following dependency into your *pom.xml*:

.. code-block:: xml

   <dependency>
     <groupId>at.ac.ait.lablink.clients</groupId>
     <artifactId>plotter</artifactId>
     <version>0.0.1</version>
   </dependency>

Building from source
====================

Installation from source requires a local **Java Development Kit** installation, for instance the `Oracle Java SE Development Kit 13 <https://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ or the `OpenJDK <https://openjdk.java.net/>`_.

Check out the project and compile it with `Maven <https://maven.apache.org/>`__:

.. code-block:: none

   git clone https://github.com/AIT-Lablink/lablink-plotter.git
   cd lablink-plotter
   mvnw clean package

This should create JAR file *plotter-<VERSION>-jar-with-dependencies.jar* in subdirectory *target/assembly*.
Also, all additional Lablink resources needed for running the :doc:`examples <examples>` will be copied to directory *target/dependency*.
