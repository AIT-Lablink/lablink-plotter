//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

////////////////////////////////////////////////////////////////////////////////////////////
// Class LablinkPlotLive is a modified version of PtPlots's class PlotLive, for details see:
// https://ptolemy.berkeley.edu/java/ptplot5.10/doc/codeDoc/ptolemy/plot/PlotLive.html
//
// Hence, the following lines reproduce the original copyright notice of PtPlot:
//
// Copyright (c) 1998-2018 The Regents of the University of California.
// All rights reserved.
//
// IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
// FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
//
// THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.
////////////////////////////////////////////////////////////////////////////////////////////

package at.ac.ait.lablink.clients.plotter;

import ptolemy.plot.Plot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


/**
 * Class LablinkPlotLive.
 *
 * <p>Plot signals dynamically, where points can be added at any time and the display 
 * will be updated. This class is abstract, so it must be used by creating a derived class. 
 * To use it, create a derived class that uses the addPoint(...) method. This method is called
 * within a thread separate from the applet thread, so the zooming mechanism and buttons
 * remain live.Your class may also set graph parameters like titles and axis labels in the
 * constructor by calling methods in the Plot or PlotBox classes (both of which are base
 * classes).
 *
 * <p>Class LablinkPlotLive is a modified version of PtPlots's class PlotLive, for details see:
 * https://ptolemy.berkeley.edu/java/ptplot5.10/doc/codeDoc/ptolemy/plot/PlotLive.html
 */
public abstract class LablinkPlotLive extends Plot implements Runnable {

  /** Main execution thread. */
  private Thread plotLiveThread = null;

  /** This flag indicates if the plotter is in pause mode. */
  private boolean paused = false;

  /** Continue button. */
  private JButton continueButton;

  /** Pause button. */
  private JButton pauseButton;


  /**
   * Main event loop of the execution thread.
   */
  @Override
  public void run() {
    while ( true ) {
      if ( paused ) {
        synchronized ( this ) {
          try {
            wait();
          } catch ( InterruptedException ex ) {
            // Nothing to be done here.
          }
        }
      } else {
        Thread.yield();
      }
    }
  }


  /**
   * Adds the "Continue" and "Pause" button.
   */
  @Override
  public void setButtons( boolean visible ) {
    super.setButtons( visible );

    if ( continueButton == null ) {
      continueButton = new JButton( "Continue" );
      continueButton.addActionListener( new ContinueButtonListener() );
      add( continueButton );
    }

    continueButton.setVisible( visible );

    if ( pauseButton == null ) {
      pauseButton = new JButton( "Pause" );
      pauseButton.addActionListener( new PauseButtonListener() );
      add( pauseButton );
    }

    pauseButton.setVisible( visible );

    if ( visible ) {
      pauseButton.setEnabled( true );
      continueButton.setEnabled( false );
    }
  }


  /**
   * Start the execution of the live plotter.
   */
  public synchronized void start() {
    if ( pauseButton != null ) {
      pauseButton.setEnabled( true );
    }
    if ( continueButton != null ) {
      continueButton.setEnabled( false );
    }
    if ( plotLiveThread == null ) {
      plotLiveThread = new Thread( this, "LablinkPlotLive Thread" );
      plotLiveThread.start();
    } else {
      notifyAll();
    }
  }


  /**
   * Enter pause mode.
   */
  public synchronized void pausePlotting() {
    paused = true;
  }


  /**
   * Exit pause mode.
   */
  public synchronized void continuePlotting() {
    paused = false;
  }


  /**
   * Check if pause mode is activated.
   *
   * @return true if plotter in pause mode
   */
  public synchronized boolean isPaused() {
    return paused;
  }


  /**
   * This function should be called when the plotter window is closed.
   */
  public void stop() {
    paused = false;
    plotLiveThread = null;
  }


  /**
   * Class ContinueButtonListener.
   */
  class ContinueButtonListener implements ActionListener {
    
    /**
     * Implements the functionality of the "Continue" button.
     */
    @Override
    public void actionPerformed( ActionEvent event ) {
      continuePlotting();

      if ( pauseButton != null ) {
        pauseButton.setEnabled( true );
      }
      if ( continueButton != null ) {
        continueButton.setEnabled( false );
      }
    }
  }


  /**
   * Class PauseButtonListener.
   */
  class PauseButtonListener implements ActionListener {

    /**
      * Implements the functionality of the "Pause" button.
      */
    @Override
    public void actionPerformed(ActionEvent event) {
      pausePlotting();

      if (pauseButton != null) {
        pauseButton.setEnabled(false);
      }
      if (continueButton != null) {
        continueButton.setEnabled(true);
      }
    }
  }
}
