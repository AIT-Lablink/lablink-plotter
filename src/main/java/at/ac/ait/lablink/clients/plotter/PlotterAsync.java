//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter;

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
import at.ac.ait.lablink.core.utility.Utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Class PlotterAsync.
 */
public class PlotterAsync extends PlotterBase {

  /** Reference timestamp, which is used as the origin of the plotter's time axis (x-axis). */
  private long startTime;


  /**
   * The main method.
   *
   * @param args arguments to main method
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
  public static void main( String[] args ) throws
      at.ac.ait.lablink.core.client.ex.ClientNotReadyException,
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException,
      at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException,
      at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException,
      at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException,
      at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException,
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException,
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType,
      org.apache.commons.cli.ParseException,
      org.apache.commons.configuration.ConfigurationException,
      org.json.simple.parser.ParseException,
      java.io.IOException,
      java.net.MalformedURLException,
      java.util.NoSuchElementException {

    // Retrieve configuration.
    JSONObject jsonConfig = PlotterBase.getConfig( args );

    // Instantiate plotter.
    PlotterAsync plotter = new PlotterAsync( jsonConfig );

    if ( true == PlotterBase.getWriteConfigAndExitFlag() ) {
      // Run a test (write client config and exit).
      PlotterBase.writeConfigAndExit( plotter );
    } else {
      // Start the live plotter.
      plotter.setStartTimeToNow();
      PlotterBase.startEventLoop( plotter );
    }
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
  public PlotterAsync( JSONObject jsonConfig ) throws
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

    super( jsonConfig );
  }


  /**
   * Instances of class PlotterAsync store a reference timestamp, which is used as
   * the origin of the plotter's time axis (x-axis). This function sets this reference
   * timestamp to the current system time.
   */
  public synchronized void setStartTimeToNow() {
    this.startTime = System.currentTimeMillis();
  }


  /**
   * Instances of class PlotterAsync store a reference timestamp, which is used as
   * the origin of the plotter's time axis (x-axis). This function returns the time
   * difference between the current system time and the reference timestamp in seconds.
   *
   * @return time difference between the current system time and the reference timestamp
   *     in seconds
   */
  public synchronized double getElapsedTimeInSeconds() {
    return ( System.currentTimeMillis() - this.startTime ) * 1e-3;
  }

  /**
   * Configure the Lablink client.
   *
   * @param jsonConfig configuration data (JSON format)
   * @throws at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException
   *   comm interface not supported
   */
  protected void configureClient( JSONObject jsonConfig ) throws
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException {

    logger.info( "Basic cient configuration ..." );

    JSONObject clientConfig = this.<JSONObject>getRequiredConfigParam( jsonConfig,
        CLIENT_CONFIG_TAG, String.format( "Client configuration (JSON object with tag '%1$s') "
        + "is missing", CLIENT_CONFIG_TAG ) );

    // General Lablink properties configuration.
    String llPropUri = this.<String>getRequiredConfigParam( clientConfig,
        CLIENT_URI_LL_PROPERTIES, String.format( "Lablink client configuration URI missing "
        + "(%1$s)", CLIENT_URI_LL_PROPERTIES ) );

    // Sync properties configuration.
    String llSyncUri = this.<String>getRequiredConfigParam( clientConfig,
        CLIENT_URI_SYNC_PROPERTIES, String.format( "Sync host configuration URI missing "
        + "(%1$s)", CLIENT_URI_SYNC_PROPERTIES ) );

    // Scenario name.
    String scenarioName = this.<String>getRequiredConfigParam( clientConfig,
        CLIENT_SCENARIO_NAME_TAG, String.format( "Scenario name missing (%1$s)",
        CLIENT_SCENARIO_NAME_TAG ) );

    // Group name.
    String groupName = this.<String>getRequiredConfigParam( clientConfig,
        CLIENT_GROUP_NAME_TAG, String.format( "Group name missing (%1$s)",
        CLIENT_GROUP_NAME_TAG ) );

    // Client name.
    String clientName = this.<String>getRequiredConfigParam( clientConfig,
        CLIENT_NAME_TAG, String.format( "Client name missing (%1$s)", CLIENT_NAME_TAG ) );

    // Client description (optional).
    String clientDesc = this.getOptionalConfigParam( clientConfig, CLIENT_DESC_TAG, clientName );

    // Activate shell (optional, default: false).
    boolean giveShell = this.getOptionalConfigParam( clientConfig, CLIENT_SHELL_TAG, false );

    boolean isPseudo = false;

    // Declare the client with required interface.
    client = new LlClient( clientName,
        MqttCommInterfaceUtility.SP_ACCESS_NAME, giveShell, isPseudo );

    // Specify client configuration (no sync host).
    MqttCommInterfaceUtility.addClientProperties( client, clientDesc,
        scenarioName, groupName, clientName, llPropUri, llSyncUri, null );
  }
}