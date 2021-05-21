//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter.services;

import at.ac.ait.lablink.clients.plotter.PlotterBase;

import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;


/**
 * Class DoubleInputDataNotifier.
 */
public class DoubleInputDataNotifier
    extends InputDataFileWriterBase
    implements IServiceStateChangeNotifier<LlService, Double> {

  private final PlotterBase plotter;
  private final int dataset;
  private final boolean writeToFile;

  /**
   * Constructor.
   *
   * @param id input name
   * @param pl associated plotter instance
   * @param ds unique integer ID of associated client input
   * @param wf if true, in addition to plotting also write new values to CSV file
   * @throws java.io.IOException adding header to the CSV output file failed
   */
  public DoubleInputDataNotifier( String id, PlotterBase pl, int ds, boolean wf )
      throws java.io.IOException {
    plotter = pl;
    dataset = ds;
    writeToFile = wf;

    if ( writeToFile ) {
      // Initialize CSV output file.
      initializeFileWriter( id, id + ".csv" );
    }
  }


  /**
   * Whenever a the state of the associated data service changes (i.e., a new
   * input arrives), add a new data point to the associated plotter.
   */
  @Override
  public void stateChanged( LlService service, Double oldVal, Double newVal ) {
    double time = plotter.getElapsedTimeInSeconds();

    if ( false == plotter.isPaused() ) {
      plotter.addPoint( dataset, time, newVal, true );

      if ( true == writeToFile ) {
        try {
          // Write new value to CSV output file.
          writeDataToFile( time, newVal );
        } catch ( java.io.IOException ex ) {
          System.out.println( "Failed to print data to file." );
        }
      }
    }
  }
}
