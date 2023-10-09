package org.micromanager.lightsheetmanager.api;

import java.util.concurrent.Future;

public interface AcquisitionManager {

    /**
     * Request that an acquisition is run.
     * Returns a future tha can be gotton when acqusiiton is complete
     */
    Future<?> requestRun(boolean speedTest);

    /**
     * Request the running acquisition to stop.
     */
    void requestStop();

    /**
     * Request the running acquisition to pause.
     */
    void requestPause();

    /**
     * Resume acquisition after it was paused
     */
    void requestResume();

}
