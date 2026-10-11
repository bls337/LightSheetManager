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
     * <p>The future completes normally however the run ends: completed, stopped, failed with
     * an exception, or refused before it started. It tells when the request is over, not how
     * it went, and waiting on it blocks until then.
     *
     * @param speedTest true to run Micro-Manager's speed test, which streams the camera to the
     *                  save directory, instead of the acquisition
     * @return a future that completes when the request is over
     */
    Future<?> requestRun(boolean speedTest);

    /**
     * Request that a test acquisition is run.
     *
     * <p>A test acquisition runs a single time point and does not save anything to disk. The
     * settings shown in the user interface are left unchanged.
     *
     * <p>The future completes normally however the run ends: completed, stopped, failed with
     * an exception, or refused before it started. It tells when the request is over, not how
     * it went, and waiting on it blocks until then.
     *
     * @return a future that completes when the request is over
     */
    Future<?> requestTestAcquisition();

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
