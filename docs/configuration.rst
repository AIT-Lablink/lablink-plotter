Overview
========

The configuration has to be JSON-formatted.
It is divided into the following three categories:

* **Client**: basic configuration of the Lablink client (JSON object)
* **Input**: configuration of the client's inputs, each visualized as individual dataset (JSON array of JSON objects)
* **Plot**: configuration of the plot (JSON object)

Client configuration
====================

* **ClientName**: client name
* **GroupName**: group name
* **ScenarioName**: scenario name
* **labLinkPropertiesUrl**: URI to Lablink configuration
* **syncHostPropertiesUrl**: URI to sync host configuration
* **ClientDescription**: description of the client (optional)
* **ClientShell** activate Lablink shell (optional, default: ``false``).

Input configuration
===================

Configuration for each input:

* **InputID**: name of the input, used in plot legend
* **DataType**: data type of the input, allowed values are ``double`` and ``int``
* **Unit**: unit associated to the input, used in plot legend (optional)
* **LineStyle**: string specifying the color for points, allowed values are ``solid``, ``dotted``, ``dashed``, ``dotdashed`` and ``dotdotdashed`` (optional, default: ``solid``)
* **MarksStyle**: set the marks style, allowed values are ``none``, ``points`` and ``dots`` (optional, default: ``dots``)
* **Connected**: if ``true``, subsequent points in the plot are connected with a line (optional, default: ``true``)
* **Impulses**: if ``true``, then a line will be drawn from any plotted point down to the x axis (optional, default: ``false``)


Plot configuration
==================

Either **AutomaticRescale** has to be set to ``true`` or **XMin**, **XMax**, **YMin** and **YMax** have to be specified:

* **AutomaticRescale**: if ``true``, axes are rescaled automatically at runtime to fit all data on the plot canvas
* **XMin**: left bound of x-axis
* **XMax**: right bound of x-axis
* **YMin**: lower bound of y-axis
* **YMax**: upper bound of y-axis

Other parameters:

* **Title**: title of the plot (optional, default: ``Plotter``)
* **XLabel**: x-axis label (optional, default: ``time``)
* **YLabel**: y-axis label (optional, default: ``value``)
* **DisplayGrid**: control whether the grid is drawn (optional, default: ``true``)
* **PersistencePoints**: a positive argument sets the persistence of the plot to the given number of points, calling with a zero argument turns off this feature, reverting to infinite memory (optional, default: ``0``)
* **PersistenceX**: a positive argument sets the persistence of the plot to the given width in units of the horizontal axis, calling with a zero argument turns off this feature, reverting to infinite memory (optional, default: ``0.0``)

Example configuration
=====================

.. code-block:: json


   {
     "Client": {
       "ClientDescription": "A simple plotter.",
       "ClientName": "TestPlotterSync",
       "ClientShell": false,
       "GroupName": "PlotterDemo",
       "ScenarioName": "PlotterSync",
       "labLinkPropertiesUrl": "http://localhost:10101/get?id=ait.all.all.llproperties",
       "syncHostPropertiesUrl": "http://localhost:10101/get?id=ait.test.plotter.sync.sync-host.properties"
     },
     "Input": [
       {
         "Connected": true,
         "DataType": "Double",
         "Impulses": true,
         "InputID": "Input1",
         "LineStyle": "dashed",
         "MarksStyle": "dots",
         "Unit": "Unit1"
       },
       {
         "DataType": "long",
         "InputID": "Input2"
       }
     ],
     "Plot": {
       "AutomaticRescale": false,
       "DisplayGrid": true,
       "PersistencePoints": 0,
       "PersistenceX": 0,
       "Title": "Asynchronous Plotter Demo",
       "XLabel": "runtime in s",
       "XMax": 60,
       "XMin": 0,
       "YLabel": "test data",
       "YMax": 10,
       "YMin": -10
     }
   }
