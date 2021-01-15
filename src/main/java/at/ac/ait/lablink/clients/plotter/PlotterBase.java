//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter;

import at.ac.ait.lablink.clients.plotter.services.DoubleInputData;
import at.ac.ait.lablink.clients.plotter.services.DoubleInputDataNotifier;
import at.ac.ait.lablink.clients.plotter.services.LongInputData;
import at.ac.ait.lablink.clients.plotter.services.LongInputDataNotifier;

import at.ac.ait.lablink.core.client.ci.mqtt.impl.MqttCommInterfaceUtility;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException;
import at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType;
import at.ac.ait.lablink.core.client.impl.LlClient;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.utility.Utility;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ptolemy.util.StringUtilities;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Class PlotterBase.
 */
public abstract class PlotterBase extends LablinkPlotLive {

  /** Logger. */
  protected static final Logger logger = LogManager.getLogger( "Plotter" );

  protected static final String CLIENT_CONFIG_TAG = "Client";
  protected static final String CLIENT_DESC_TAG = "ClientDescription";
  protected static final String CLIENT_GROUP_NAME_TAG = "GroupName";
  protected static final String CLIENT_NAME_TAG = "ClientName";
  protected static final String CLIENT_SCENARIO_NAME_TAG = "ScenarioName";
  protected static final String CLIENT_SHELL_TAG = "ClientShell";
  protected static final String CLIENT_URI_LL_PROPERTIES = "labLinkPropertiesUrl";
  protected static final String CLIENT_URI_SYNC_PROPERTIES = "syncHostPropertiesUrl";

  protected static final String INPUT_CONFIG_TAG = "Input";
  protected static final String INPUT_CONNECT_TAG = "Connected";
  protected static final String INPUT_DATATYPE_TAG = "DataType";
  protected static final String INPUT_ID_TAG = "InputID";
  protected static final String INPUT_IMPULSE_TAG = "Impulses";
  protected static final String INPUT_LINE_TAG = "LineStyle";
  protected static final String INPUT_MARKS_TAG = "MarksStyle";
  protected static final String INPUT_UNIT_TAG = "Unit";

  protected static final String PLOT_CONFIG_TAG = "Plot";
  protected static final String PLOT_AUTOMATIC_RESCALE_TAG = "AutomaticRescale";
  protected static final String PLOT_DISPLAY_GRID_TAG = "DisplayGrid";
  protected static final String PLOT_PERSISTENCE_POINTS_TAG = "PersistencePoints";
  protected static final String PLOT_PERSISTENCE_X_TAG = "PersistenceX";
  protected static final String PLOT_TITLE_TAG = "Title";
  protected static final String PLOT_XAXIS_LABEL_TAG = "XLabel";
  protected static final String PLOT_XMAX_TAG = "XMax";
  protected static final String PLOT_XMIN_TAG = "XMin";
  protected static final String PLOT_YAXIS_LABEL_TAG = "YLabel";
  protected static final String PLOT_YMAX_TAG = "YMax";
  protected static final String PLOT_YMIN_TAG = "YMin";

  private static final String CLI_CONF_FLAG = "c";
  private static final String CLI_CONF_LONG_FLAG = "config";
  private static final String CLI_TEST_FLAG = "w";

  /** Flag for testing (write config and exit). */
  private static boolean writeConfigAndExitFlag;

  /** Lablink client instance. */
  protected LlClient client;

  /** Title of the plotter window. */
  private String plotWindowTitle;


  /**
   * Parse the command line arguments to retrieve the configuration.
   *
   * @param args arguments to main method
   * @return configuration data (JSON format)
   * @throws org.apache.commons.cli.ParseException
   *   parse exception
   * @throws org.apache.commons.configuration.ConfigurationException
   *   configuration error
   * @throws org.json.simple.parser.ParseException
   *   parse error
   * @throws java.io.IOException
   *   IO error
   * @throws java.net.MalformedURLException
   *   malformed URL
   * @throws java.util.NoSuchElementException
   *   no such element
   */
  protected static JSONObject getConfig( String[] args ) throws
      org.apache.commons.cli.ParseException,
      org.apache.commons.configuration.ConfigurationException,
      org.json.simple.parser.ParseException,
      java.io.IOException,
      java.net.MalformedURLException,
      java.util.NoSuchElementException {

    // Define command line option.
    Options cliOptions = new Options();
    cliOptions.addOption( CLI_CONF_FLAG, CLI_CONF_LONG_FLAG, true, "plotter configuration URI" );
    cliOptions.addOption( CLI_TEST_FLAG, false, "write config and exit" );

    // Parse command line options.
    CommandLineParser parser = new BasicParser();
    CommandLine commandLine = parser.parse( cliOptions, args );

    // Set flag for testing (write config and exit).
    PlotterBase.writeConfigAndExitFlag = commandLine.hasOption( CLI_TEST_FLAG );

    // Retrieve plotter configuration URI from command line.
    String configUri = commandLine.getOptionValue( CLI_CONF_FLAG );

    // Get plotter configuration URL, resolve environment variables if necessary.
    URL fullConfigUrl = new URL( Utility.parseWithEnvironmentVariable( configUri ) );

    // Read plotter configuration, remove existing comments.
    Scanner scanner = new Scanner( fullConfigUrl.openStream() );
    String rawConfig = scanner.useDelimiter( "\\Z" ).next();
    rawConfig = rawConfig.replaceAll( "#.*#", "" );

    // Check if comments have been removed properly.
    int still = rawConfig.length() - rawConfig.replace( "#", "" ).length();
    if ( still > 0 ) {
      throw new IllegalArgumentException(
          String.format( "Config file contains at least %1$d line(s) with incorrectly"
              + "started/terminated comments: %2$s", still, fullConfigUrl.toString() )
        );
    }

    logger.info( "Parsing configuration file..." );

    // Parse plotter configuration (JSON format).
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonConfig = ( JSONObject ) jsonParser.parse( rawConfig );

    return jsonConfig;
  }


  /**
   * Start the main event loop for the plotter, which brings up the plotter window.
   *
   * @param plotter asynchronous plotter
   */
  protected static void startEventLoop( PlotterBase plotter ) {

    // Run this in the Swing Event Thread.
    Runnable livePlotterWindow = new Runnable() {
      @Override
      public void run() {
        try {
          // Create plotter window.
          JFrame frame = new JFrame( plotter.getWindowTitle() );

          // Define behavior of plotter window's "close" button.
          frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent event ) {
                plotter.stop();
                StringUtilities.exit(0);
            }
          } );

          // Plotter window layout.
          frame.getContentPane().add( "Center", plotter );
          frame.setVisible( true );
          plotter.setButtons( true );

          // Start live plotter.
          plotter.start();

          // Resize window.
          frame.pack();
        } catch ( Exception ex ) {
          System.err.println( ex.toString() );
          ex.printStackTrace();
        }
      }
    };

    try {
      // Start event dispatch thread of plotter window.
      SwingUtilities.invokeAndWait( livePlotterWindow );
    } catch ( Exception ex ) {
      System.err.println( ex.toString() );
      ex.printStackTrace();
    }
  }


  /**
   * Returns true if this is a test run.
   *
   * @return test flag
   */
  protected static boolean getWriteConfigAndExitFlag() {
    return PlotterBase.writeConfigAndExitFlag;
  }


  /**
   * Run a test (write config and exit).
   *
   * @param plotter a plotter instance
   */
  protected static void writeConfigAndExit( PlotterBase plotter ) {

    logger.info( "run a test (write config and exit)" );

    String clientConfig = plotter.getYellowPageJson();

    try {
      Files.write( Paths.get( "client_config.json" ), clientConfig.getBytes() );
    } catch ( IOException ex ) {
      logger.error( ex );
    }

    System.exit( 0 );
  }


  /**
   * Constructor.
   *
   * @param jsonConfig configuration data (JSON format)
   * @throws at.ac.ait.lablink.core.client.ex.ClientNotReadyException
   *   client not ready
   * @throws at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException
   *   comm interface not supported
   * @throws at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException
   *   data type not supported
   * @throws at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException
   *   invalid cast for service value
   * @throws at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException
   *   no services in client logic
   * @throws at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException
   *   no such comm interface
   * @throws at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException
   *   service is not registered with client
   * @throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType
   *   service type does not match client type
   * @throws org.apache.commons.configuration.ConfigurationException
   *   configuration error
   * @throws java.util.NoSuchElementException
   *   no such element
   */
  public PlotterBase( JSONObject jsonConfig ) throws
      at.ac.ait.lablink.core.client.ex.ClientNotReadyException,
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException,
      at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException,
      at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException,
      at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException,
      at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException,
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException,
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType,
      org.apache.commons.configuration.ConfigurationException,
      java.util.NoSuchElementException {

    // Retrieve basic client configuration.
    configureClient( jsonConfig );

    // Configure the plotter (axis ranges, etc.).
    configurePlotter( jsonConfig );

    // Add inputs to the client, which provide the data to be plotted.
    configureClientInputs( jsonConfig );

    // Create the client.
    client.create();

    // Initialize the client.
    client.init();

    // Start the client.
    client.start();
  }


  /**
   * Instances of class PlotterBase store a reference timestamp, which is used as
   * the origin of the plotter's time axis (x-axis). This function returns the time
   * difference between the current system time and the reference timestamp in seconds.
   *
   * @return time difference between the current system time and the reference timestamp
   *     in seconds
   */
  public abstract double getElapsedTimeInSeconds();


  /**
   * Configure the Lablink client.
   *
   * @param jsonConfig configuration data (JSON format)
   * @throws at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException
   *   comm interface not supported
   */
  protected abstract void configureClient( JSONObject jsonConfig ) throws
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException;


  /**
   * Returns the title of the plotter window.
   *
   * @return title of the plotter window
   */
  public String getWindowTitle() {
    return this.plotWindowTitle;
  }


  /**
   * Returns the yellow pages info (JSON format) of the plotter' Lablink client.
   *
   * @return title of the plotter window
   */
  public String getYellowPageJson() {
    return this.client.getYellowPageJson();
  }


  /**
   * Configure the plotter (ranges and labels of axes, persistence, etc.).
   *
   * @param jsonConfig configuration data (JSON format)
   */
  private void configurePlotter( JSONObject jsonConfig ) {

    logger.info( "Configuring the plotter ..." );

    JSONObject plotConfig = this.<JSONObject>getRequiredConfigParam( jsonConfig,
        PLOT_CONFIG_TAG, String.format( "Plotter configuration (JSON object with tag '%1$s') "
        + "is missing", PLOT_CONFIG_TAG ) );

    boolean rescale = this.getOptionalConfigParam( plotConfig,
        PLOT_AUTOMATIC_RESCALE_TAG, false );

    if ( true == rescale ) {
      this.setAutomaticRescale( true );
    } else {
      Number xmin = this.<Number>getRequiredConfigParam( plotConfig, PLOT_XMIN_TAG,
          String.format( "X-axis range configuration missing (%1$s)", PLOT_XMIN_TAG ) );
      Number xmax = this.<Number>getRequiredConfigParam( plotConfig, PLOT_XMAX_TAG,
          String.format( "X-axis range configuration missing (%1$s)", PLOT_XMAX_TAG ) );
      this.setXRange( xmin.doubleValue(), xmax.doubleValue() );

      Number ymin = this.<Number>getRequiredConfigParam( plotConfig, PLOT_YMIN_TAG,
          String.format( "Y-axis range configuration missing (%1$s)", PLOT_YMIN_TAG ) );
      Number ymax = this.<Number>getRequiredConfigParam( plotConfig, PLOT_YMAX_TAG,
          String.format( "Y-axis range configuration missing (%1$s)", PLOT_YMAX_TAG ) );
      this.setYRange( ymin.doubleValue(), ymax.doubleValue() );
    }

    String title = this.getOptionalConfigParam( plotConfig, PLOT_TITLE_TAG, "" );
    //// Do not use "setTitle" method, because the resulting title in the
    //// plotter window may overlap with buttons. Set window title instead.
    //if ( false == title.isEmpty() ) {
    //  this.setTitle( title );
    //}
    this.plotWindowTitle = title.isEmpty() ? "Plotter" : title;

    String xlabel = this.getOptionalConfigParam( plotConfig, PLOT_XAXIS_LABEL_TAG, "time" );
    if ( false == xlabel.isEmpty() ) {
      this.setXLabel( xlabel );
    }

    String ylabel = this.getOptionalConfigParam( plotConfig, PLOT_YAXIS_LABEL_TAG, "value" );
    if ( false == ylabel.isEmpty() ) {
      this.setYLabel( ylabel );
    }

    Number persistencePoints = this.getOptionalConfigParam( plotConfig,
        PLOT_PERSISTENCE_POINTS_TAG, 0 );
    if ( persistencePoints.intValue() > 0 ) {
      this.setPointsPersistence( persistencePoints.intValue() );
    }

    Number persistenceX = this.getOptionalConfigParam( plotConfig, PLOT_PERSISTENCE_X_TAG, 0 );
    if ( persistenceX.doubleValue() > 0. ) {
      this.setXPersistence( persistenceX.doubleValue() );
    }

    boolean grid = this.getOptionalConfigParam( plotConfig, PLOT_DISPLAY_GRID_TAG, true );
    this.setGrid( grid );
  }


  /**
   * Configure the Lablink client data services, which serve as inputs for the plotter.
   *
   * @param jsonConfig configuration data (JSON format)
   * @throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType
   *   service type does not match client type
   */
  private void configureClientInputs( JSONObject jsonConfig ) throws
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType {

    logger.info( "Configuring client inputs..." );

    JSONArray inputConfigList = this.<JSONArray>getRequiredConfigParam( jsonConfig,
        INPUT_CONFIG_TAG, String.format( "Plotter input definition (JSON array with tag "
        + "'%1$s') is missing", INPUT_CONFIG_TAG ) );

    @SuppressWarnings( "rawtypes" )
    Iterator inputConfigListIter = inputConfigList.iterator();

    int intInput = 0;

    // Create data point consumer for each input.
    while ( inputConfigListIter.hasNext() ) {
      JSONObject inputConfig = (JSONObject) inputConfigListIter.next();

      String inputId = this.<String>getRequiredConfigParam( inputConfig, INPUT_ID_TAG,
          String.format( "Plotter input name missing (%1$s)", INPUT_ID_TAG ) );

      String dataType = this.<String>getRequiredConfigParam( inputConfig, INPUT_DATATYPE_TAG,
          String.format( "Plotter input data type missing (%1$s)", INPUT_DATATYPE_TAG ) );

      String unit = this.getOptionalConfigParam( inputConfig, INPUT_UNIT_TAG, "" );

      setInputPlottingAttributes( inputConfig, inputId, unit, intInput );

      addInputDataService( inputId, dataType, unit, intInput );

      ++intInput;
    }
  }


  /**
   * Configure the plot attributes for each client input.
   *
   * @param inputConfig configuration data (JSON format)
   * @param inputId name of input signal
   * @param unit unit associated to input signal
   * @param intInput unique integer ID of input signal
   */
  private void setInputPlottingAttributes( JSONObject inputConfig, String inputId,
      String unit, int intInput ) {

    boolean connected = this.getOptionalConfigParam( inputConfig, INPUT_CONNECT_TAG, true );
    boolean impulses = this.getOptionalConfigParam( inputConfig, INPUT_IMPULSE_TAG, false );
    String lineStyle = this.getOptionalConfigParam( inputConfig, INPUT_LINE_TAG, "solid" );
    String marksStyle = this.getOptionalConfigParam( inputConfig, INPUT_MARKS_TAG, "dots" );
    String legend = unit.isEmpty() ? inputId : String.format( "%1$s (%2$s)", inputId, unit );

    this.setConnected( connected, intInput );
    this.setImpulses( impulses, intInput );
    this.setLineStyle( lineStyle, intInput );
    this.setMarksStyle( marksStyle, intInput );
    this.addLegend( intInput, legend );
  }


  /**
   * Configure the plot attributes for each client input.
   *
   * @param inputId name of input signal
   * @param dataType type of data associated to input signal (double or long)
   * @param unit unit associated to input signal
   * @param intInput unique integer ID of input signal
   * @throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType
   *   service type does not match client type
   */
  private void addInputDataService( String inputId, String dataType, String unit, int intInput )
      throws at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType {

    // Data service description.
    String serviceDesc = String.format( "Plotter input variable %1$s (%2$s)",
        inputId, dataType );

    if ( dataType.toLowerCase().equals( "double" ) ) {
      // Create new data service.
      DoubleInputData dataService = new DoubleInputData();
      dataService.setName( inputId );

      // Specify data service properties.
      MqttCommInterfaceUtility.addDataPointProperties( dataService,
          inputId, serviceDesc, inputId, unit );

      // Add notifier.
      dataService.addStateChangeNotifier( new DoubleInputDataNotifier( this, intInput ) );

      // Add service to the client.
      client.addService( dataService );
    } else if ( dataType.toLowerCase().equals( "long" ) ) {
      // Create new data service.
      LongInputData dataService = new LongInputData();
      dataService.setName( inputId );

      // Specify data service properties.
      MqttCommInterfaceUtility.addDataPointProperties( dataService,
          inputId, serviceDesc, inputId, unit );

      // Add notifier.
      dataService.addStateChangeNotifier( new LongInputDataNotifier( this, intInput ) );

      // Add service to the client.
      client.addService( dataService );
    } else {
      throw new IllegalArgumentException(
          String.format( "Plotter input data type not supported: '%1$s'", dataType )
      );
    }
  }


  /**
   * Retrieve mandatory parameter from configuration. Throw an exception in case the
   * parameter is not found.
   *
   * @param <T> data type of parameter
   * @param config configuration data (JSON format)
   * @param tag JSON tag of the parameter
   * @param err error message to be displayed in case the parameter is not found
   * @return mandatory parameter from configuration
   * @throws NoSuchElementException specified element does not exist
   */
  protected <T> T getRequiredConfigParam( JSONObject config, String tag, String err )
      throws NoSuchElementException {

    if ( false == config.containsKey( tag ) ) {
      throw new NoSuchElementException( err );
    }

    @SuppressWarnings( "unchecked" )
    T result = (T) config.get( tag );

    return result;
  }


  /**
   * Retrieve optional parameter from configuration. Return a default value in case the
   * parameter is not found.
   *
   * @param <T> data type of parameter
   * @param config configuration data (JSON format)
   * @param tag JSON tag of the parameter
   * @param defaultVal default value
   * @return optional parameter from configuration or default value
   */
  protected <T> T getOptionalConfigParam( JSONObject config, String tag, T defaultVal ) {
    if ( false == config.containsKey( tag ) ) {
      return defaultVal;
    }

    @SuppressWarnings( "unchecked" )
    T result = (T) config.get( tag );

    return result;
  }

}
