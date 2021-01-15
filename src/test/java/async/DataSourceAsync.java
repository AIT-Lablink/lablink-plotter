//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package async;

import at.ac.ait.lablink.core.client.ci.mqtt.impl.MqttCommInterfaceUtility;
import at.ac.ait.lablink.core.client.impl.LlClient;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException;
import at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlServiceDouble;
import at.ac.ait.lablink.core.service.LlServiceLong;
import at.ac.ait.lablink.core.utility.Utility;

import org.apache.commons.configuration.ConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Class DataSourceAsync.
 */
public class DataSourceAsync {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "DataSourceAsync" );

  /** Schedule executor for repeated ping messages. */
  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor();

  private static final String CLIENT_NAME = "DataSourceAsync";

  private static final String GROUP_NAME = "PlotterDemo";

  private static final String SCENARIO_NAME = "PlotterAsync";

  private static final String LL_PROP_URI =
      "$LLCONFIG$ait.all.all.llproperties";

  private static final String SYNC_PROP_URI =
      "$LLCONFIG$ait.test.plotter.async.sync-host.properties";

  private static final String DOUBLE_OUTPUT_SERVICE_NAME = "DoubleOutput";

  private static final String LONG_OUTPUT_SERVICE_NAME = "LongOutput";

  private int sendCounter = 0;

  /** Client. */
  private LlClient client;


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
   * @throws org.apache.commons.configuration.ConfigurationException
   *   configuration error
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
      org.apache.commons.configuration.ConfigurationException {

    DataSourceAsync app = new DataSourceAsync();

    app.setupClient();

    Runnable sendData = new Runnable() {
      public void run() {
        app.sendData();
      }
    };

    long initialDelay = 0;
    long period = 300;
    TimeUnit unit = TimeUnit.MILLISECONDS;

    // Schedule the data to be repeatedly sent with fixed time steps.
    executor.scheduleAtFixedRate( sendData, initialDelay, period, unit );
  }

  /**
   * Perform the client setup.
   *
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
   */
  public void setupClient() throws
      at.ac.ait.lablink.core.client.ex.ClientNotReadyException,
      at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException,
      at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException,
      at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException,
      at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException,
      at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException,
      at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException,
      at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType,
      org.apache.commons.configuration.ConfigurationException {

    String clientDescription = "Asynchronous data source for plotter demo.";
    boolean giveShell = true;
    boolean isPseudo = false;

    // Declare the client with required interface.
    client = new LlClient( CLIENT_NAME,
        MqttCommInterfaceUtility.SP_ACCESS_NAME, giveShell, isPseudo );

    // Specify client configuration (no sync host).
    MqttCommInterfaceUtility.addClientProperties( client, clientDescription,
        SCENARIO_NAME, GROUP_NAME, CLIENT_NAME, LL_PROP_URI, SYNC_PROP_URI, null );

    LlServiceDouble doubleOutService = new LlServiceDouble( DOUBLE_OUTPUT_SERVICE_NAME ) {
      @Override
      public Double get() {
        return this.getCurState();
      }

      @Override
      public boolean set( Double newVal ) {
        this.setCurState( newVal );
        return true;
      }
    };

    // Specify data service properties.
    MqttCommInterfaceUtility.addDataPointProperties( doubleOutService,
        DOUBLE_OUTPUT_SERVICE_NAME, DOUBLE_OUTPUT_SERVICE_NAME,
        DOUBLE_OUTPUT_SERVICE_NAME, "NONE" );

    client.addService( doubleOutService );

    LlServiceLong longOutService = new LlServiceLong( LONG_OUTPUT_SERVICE_NAME ) {
      @Override
      public Long get() {
        return this.getCurState();
      }

      @Override
      public boolean set( Long newVal ) {
        this.setCurState( newVal );
        return true;
      }
    };

    // Specify data service properties.
    MqttCommInterfaceUtility.addDataPointProperties( longOutService,
        LONG_OUTPUT_SERVICE_NAME, LONG_OUTPUT_SERVICE_NAME,
        LONG_OUTPUT_SERVICE_NAME, "NONE" );

    client.addService( longOutService );

    // Create the client.
    client.create();

    // Initialize the client.
    client.init();

    // Start the client.
    client.start();
  }


  void sendData() {
    @SuppressWarnings( "unchecked" )
    IImplementedService<Double> doubleOutService = ( IImplementedService<Double> )
        client.getImplementedServices().get( DOUBLE_OUTPUT_SERVICE_NAME );

    @SuppressWarnings( "unchecked" )
    IImplementedService<Long> longOutService = ( IImplementedService<Long> )
        client.getImplementedServices().get( LONG_OUTPUT_SERVICE_NAME );

    Double out1 = 10. * Math.cos( Math.PI * sendCounter / 20. );
    Double out2 = 5. * Math.cos( Math.PI * sendCounter / 30. );

    doubleOutService.setValue( out1 );
    longOutService.setValue( out2.longValue() );

    ++sendCounter;

    logger.info( "data sent: {} = {} - {} = {}",
        DOUBLE_OUTPUT_SERVICE_NAME, out1, LONG_OUTPUT_SERVICE_NAME, out2.longValue() );
  }
}
