//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


/**
 * Class InputDataFileWriterBase
 *
 * <p>Base class providing functionality for input data notfiers to write data to file (CSV format).
 */
public class InputDataFileWriterBase {

  private CSVPrinter printer;
  private boolean useTimestamps;
  private DateTimeFormatter formatter;

  public InputDataFileWriterBase() {
    formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone( ZoneId.from( ZoneOffset.UTC ) );
  }

  /**
   * Initialize the CSV printer.
   *
   * @param inputName name of input data (used in header line of CSV output file)
   * @param fileName name of the CSV output file
   * @param ut if true, use timestamps instead of elapsed runtime in CSV files
   * @throws java.io.IOException adding header to the CSV output file failed
   */
  public void initializeFileWriter( String inputName, File fileName, boolean ut )
      throws java.io.IOException {
    // Initialize CSV printer.
    printer = new CSVPrinter( new FileWriter( fileName ), CSVFormat.DEFAULT );
    useTimestamps = ut;

    // Write header to CSV output file.
    printer.printRecord( "time", inputName );
    printer.flush();
  }

  /**
   * Add a new record to the CSV output file.
   *
   * @param time recording time
   * @param data value to be written
   * @throws java.io.IOException adding new record to the CSV output file failed
   */
  public void writeDataToFile( double time, Object data )
      throws java.io.IOException {
    if ( useTimestamps == false ) {
      printer.printRecord( time, data );
    } else {
      printer.printRecord( formatter.format( Instant.now() ), data );
    }
    printer.flush();
  }
}
