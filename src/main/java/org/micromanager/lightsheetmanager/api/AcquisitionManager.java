package org.micromanager.lightsheetmanager.api;

import org.micromanager.acqj.main.Acquisition;

import java.util.concurrent.Future;

public interface AcquisitionManager {

    /**
     * Return the acquisition currently being set up or running.
     *
     * @return the current acquisition, or null. Null before setup constructs it and again
     *         once the run finishes. Callers needing it after the run, for example to call
     *         checkForExceptions(), must retain their own reference.
     */
    Acquisition current();

    /**
     * Request that an acquisition is run.
     *
     * @return a future that completes when the acquisition finishes
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
