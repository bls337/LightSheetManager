package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.model.DeviceManager;
import org.micromanager.lightsheetmanager.model.acquisitions.AcquisitionEngine;

/**
 * Root interface to everything related to Light Sheet Microscopes.
 *
 */
public interface LightSheetManagerApi {

   /**
    * Return the acquisition engine.
    *
    * @return the acquisition engine
    */
   AcquisitionEngine acquisitions();

   /**
    * Return the device manager.
    *
    * @return the device manager
    */
   DeviceManager devices();

}
