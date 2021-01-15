//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter.services;

import at.ac.ait.lablink.clients.plotter.PlotterSync;

import at.ac.ait.lablink.core.service.sync.ISyncParameter;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PlotterSyncConsumer implements ISyncConsumer {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "PlotterSyncConsumer" );

  /** Synchronized plotter. */
  private PlotterSync plotter;


  /** 
   * Constructor.
   *
   * @param plotter synchronized plotter
   */
  public PlotterSyncConsumer( PlotterSync plotter ) {
    this.plotter = plotter;
  }


  @Override
  public boolean init( ISyncParameter scs ) {
    logger.info( "Intialize sync client" );
    logger.info( "Sync client parameters: {}, {}, {}, {}, {}", scs.getSimMode(),
        scs.getScaleFactor(), scs.getSimBeginTime(), scs.getSimEndTime(), scs.getStepSize() );
    logger.info( "Sync client extra config: {}", scs.getClientConfig() );
    
    plotter.setSyncStartTime( scs.getSimBeginTime() );
    
    return true;
  }


  @Override
  public long go( long currentSimTime, long until, ISyncParameter scs ) {
    logger.info( "synchronization point at {}", currentSimTime );

    plotter.setSyncTime( until );

    return ( until + scs.getStepSize() );
  }

  @Override
  public boolean stop( ISyncParameter scs ) {
    logger.debug( "Sync Client stopped!" );
    return true;
  }

}
