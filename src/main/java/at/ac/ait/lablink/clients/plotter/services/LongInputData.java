//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.clients.plotter.services;

import at.ac.ait.lablink.core.service.LlServiceLong;


/**
 * Class LongInputData.
 * 
 * <p>Data service for plotter input data of type long.
 */
public class LongInputData extends LlServiceLong {
  /**
   * @see at.ac.ait.lablink.core.service.LlService#get()
   */
  @Override
  public Long get() {
    return this.getCurState();
  }

  /**
   * @see at.ac.ait.lablink.core.service.LlService#set( java.lang.Object )
   */
  @Override
  public boolean set( Long newVal ) {
    this.setCurState( newVal );
    return true;
  }
}