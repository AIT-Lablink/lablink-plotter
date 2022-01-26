//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter.services;

import at.ac.ait.lablink.clients.plotter.PlotterBase;

import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;

import java.io.File;


/**
 * Class LongInputDataNotifier.
 */
public class LongInputDataNotifier
    extends InputDataFileWriterBase
    implements IServiceStateChangeNotifier<LlService, Long> {

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
   * @param pf path for writing output file
   * @param ut if true, use timestamps instead of elapsed runtime in CSV files
   * @throws java.io.IOException adding header to the CSV output file failed
   */
  public LongInputDataNotifier( String id, PlotterBase pl, int ds,
      boolean wf, String pf, boolean ut ) throws
    java.io.IOException {

    plotter = pl;
    dataset = ds;
    writeToFile = wf;

    if ( writeToFile ) {
      // Initialize CSV output file.
      File outputFile = new File( pf, id + ".csv" );
      initializeFileWriter( id, outputFile, ut );
    }
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
