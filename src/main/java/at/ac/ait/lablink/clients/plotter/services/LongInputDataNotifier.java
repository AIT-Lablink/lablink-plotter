//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter.services;

import at.ac.ait.lablink.clients.plotter.PlotterBase;

import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;


/**
 * Class LongInputDataNotifier.
 */
public class LongInputDataNotifier implements IServiceStateChangeNotifier<LlService, Long> {

  private final PlotterBase plotter;
  private final int dataset;


  /**
   * Constructor.
   *
   * @param pl associated plotter instance
   * @param ds unique integer ID of associated client input
   */
  public LongInputDataNotifier( PlotterBase pl, int ds ) {
    plotter = pl;
    dataset = ds;
  }


  /**
   * Whenever a the state of the associated data service changes (i.e., a new
   * input arrives), add a new data point to the associated plotter.
   */
  @Override
  public void stateChanged( LlService service, Long oldVal, Long newVal ) {
    double time = plotter.getElapsedTimeInSeconds();

    if ( false == plotter.isPaused() ) {
      plotter.addPoint( dataset, time, newVal.doubleValue(), true );
    }
  }
}
