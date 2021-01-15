//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter.services;

import at.ac.ait.lablink.core.service.LlServiceDouble;


/**
 * Class DoubleInputData.
 * 
 * <p>Data service for plotter input data of type double.
 */
public class DoubleInputData extends LlServiceDouble {
  /**
   * @see at.ac.ait.lablink.core.service.LlService#get()
   */
  @Override
  public Double get() {
    return this.getCurState();
  }

  /**
   * @see at.ac.ait.lablink.core.service.LlService#set( java.lang.Object )
   */
  @Override
  public boolean set( Double newVal ) {
    this.setCurState( newVal );
    return true;
  }
}