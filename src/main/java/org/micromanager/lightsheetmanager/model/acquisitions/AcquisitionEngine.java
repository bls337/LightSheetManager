package org.micromanager.lightsheetmanager.model.acquisitions;

import mmcorej.CMMCore;
import mmcorej.org.json.JSONArray;
import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.Studio;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acquisition.internal.MMAcquistionControlCallbacks;
import org.micromanager.acquisition.internal.acqengjcompat.speedtest.SpeedTest;
import org.micromanager.data.Coords;
import org.micromanager.data.DataProvider;
import org.micromanager.data.Datastore;
import org.micromanager.data.Pipeline;
import org.micromanager.data.SummaryMetadata;
import org.micromanager.data.internal.DefaultSummaryMetadata;
import org.micromanager.data.internal.PropertyKey;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.api.AcquisitionManager;
import org.micromanager.lightsheetmanager.api.TimingSettings;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.internal.ScapeAcquisitionSettings;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.DurationPanel;
import org.micromanager.lightsheetmanager.model.autofocus.AutofocusAdapter;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AcquisitionEngine implements AcquisitionManager, MMAcquistionControlCallbacks {

    protected final Studio studio_;
    protected final CMMCore core_;

    private static final AtomicLong runIdCounter_ = new AtomicLong();

    protected ScapeAcquisitionSettings.Builder asb_;
    protected ScapeAcquisitionSettings acqSettings_;

    private final ExecutorService acquisitionExecutor_ = Executors.newSingleThreadExecutor(
            r -> new Thread(r, "Acquisition Thread"));
    protected volatile Acquisition currentAcquisition_ = null; // TODO: consider making a getter rather than protected?

    // true from the moment a run is requested until the acquisition thread exits. currentAcquisition_
    // cannot answer "is a run in flight?": the engines only assign it partway through run(), so it
    // stays null across all of setup() and the arming that follows.
    private volatile boolean acquisitionRequested_ = false;
    // a stop asked for before the acquisition was started, acted on by the checks in requestRun()
    // and in each engine's run()
    private volatile boolean stopRequested_ = false;

    private final AutofocusAdapter autofocus_;

    protected Datastore datastore_;
    protected Pipeline curPipeline_;
    protected long nextWakeTime_ = -1;

    // null until a run captures it, cleared by finish()
    protected ShutterState shutterState_;

    protected DurationPanel pnlDuration_;

    protected final LightSheetManager model_;

    /**
     * Validates that the acquisition can actually be written to disk, before anything is acquired.
     * <p>
     * The images are written by {@code finish()}, i.e. only AFTER the run completes, so without this
     * check an unusable save location is discovered at the very end and the data is lost.
     * <p>
     * Called from both geometry engines' {@code setup()} before any hardware is touched, so a failure
     * costs nothing and leaves the microscope untouched.
     *
     * @return true if saving is off, or the save location is usable; false to abort setup
     */
    protected boolean validateSaveLocation() {
        if (!acqSettings_.isSavingImagesDuringAcquisition()) {
            return true; // not saving => nothing to validate
        }

        final String saveNamePrefix = acqSettings_.saveNamePrefix();
        if (saveNamePrefix == null || saveNamePrefix.trim().isEmpty()) {
            model_.logging().reportError("The save name prefix is empty.\n\n"
                    + "Set a name on the Datastore panel, or uncheck \"Save images during acquisition\".");
            return false;
        }

        final String saveDirectory = acqSettings_.saveDirectory();
        if (saveDirectory == null || saveDirectory.trim().isEmpty()) {
            model_.logging().reportError("The save directory is not set.\n\n"
                    + "Set a directory on the Datastore panel, or uncheck "
                    + "\"Save images during acquisition\".");
            return false;
        }

        final File directory = new File(saveDirectory);
        if (!directory.exists()) {
            model_.logging().reportError("The save directory does not exist:\n\n" + saveDirectory
                    + "\n\nCreate it, or choose another directory on the Datastore panel.");
            return false;
        }
        if (!directory.isDirectory()) {
            model_.logging().reportError("The save directory is a file, not a directory:\n\n"
                    + saveDirectory);
            return false;
        }
        // canWrite() is advisory on Windows (it reports the read-only attribute, not the ACL), so it
        // catches the common cases without being authoritative; a real write failure still surfaces
        // at finish(). Cheap enough to be worth keeping.
        if (!directory.canWrite()) {
            model_.logging().reportError("The save directory is not writable:\n\n" + saveDirectory);
            return false;
        }

        studio_.logs().logMessage("save location validated: " + saveDirectory
                + File.separator + saveNamePrefix);
        return true;
    }

    /**
     * Validates that every imaging camera will deliver the same frame size, before anything is armed.
     *
     * <p>Cameras that disagree overrun the shared Core circular buffer and take the whole JVM with
     * them: {@code EXCEPTION_ACCESS_VIOLATION} inside {@code popNextImageMD}, no Java exception, no
     * recovery, no data. Refusing to arm is the only place this can be stopped from inside LSM.
     *
     * <p>Called from both geometry engines' {@code setup()} before any hardware is touched, so a
     * failure costs nothing and leaves the microscope untouched.
     *
     * @return true if the cameras agree, or there is only one; false to abort setup
     */
    protected boolean validateCameraFrameSizes() {
        final CameraBase[] cameras = model_.devices().imagingCameras();
        final String mismatch = CameraBase.describeFrameSizeMismatch(cameras);
        if (mismatch == null) {
            return true;
        }
        model_.logging().reportError("The imaging cameras have different frame sizes: " + mismatch
                + ".\n\nAcquiring with mismatched frame sizes crashes Micro-Manager outright, so this "
                + "acquisition was not started.\n\nSet the same ROI and binning on every imaging "
                + "camera from the Camera tab, then try again.");
        return false;
    }

    /**
     * Refuses to arm when the computed slice timing is not physically realizable.
     *
     * <p>In EDGE mode the solver derives the camera exposure by subtracting the camera's reset and
     * readout time from the sample exposure, and nothing stops the result going negative. A sample
     * exposure shorter than the camera needs to reset and read out therefore yields a negative
     * exposure, which is handed to {@code setExposure()} and accepted by the device without
     * complaint, so the run proceeds and produces nothing usable.
     *
     * <p>Deliberately conservative: only the two values that are meaningless at or below zero are
     * required to be positive. Delays and the remaining durations are allowed to be zero.
     *
     * <p>Note "minimize slice period" does not rescue this: it is only consulted on the galvo path
     * ({@code getTimingFromPeriodAndLightExposure}), while the stage-scan path derives the exposure
     * as sample exposure minus the camera's reset plus readout regardless.
     *
     * <p>Called from both geometry engines' {@code setup()} before any hardware is touched.
     *
     * @return true if the timing is usable; false to abort setup
     */
    protected boolean validateSliceTiming() {
        final String problems = describeUnusableTiming(acqSettings_.timing());
        if (problems == null) {
            return true;
        }
        model_.logging().reportError("The computed slice timing cannot be used: " + problems
                + ".\n\nThis happens when the sample exposure is shorter than the time the camera "
                + "needs to reset and read out, so the acquisition was not started.\n\nRaise the "
                + "sample exposure, or shorten the readout with a smaller camera ROI or higher "
                + "binning, then try again.");
        return false;
    }

    /**
     * Describes what is wrong with a computed timing schedule, or returns null when it is usable.
     *
     * @param timing the computed timing settings
     * @return a comma-separated description with no trailing punctuation, or null if usable
     */
    private static String describeUnusableTiming(final TimingSettings timing) {
        if (timing == null) {
            return "no timing has been computed";
        }
        final StringBuilder problems = new StringBuilder();
        // must be strictly positive: a slice that exposes for zero time images nothing
        appendTimingProblem(problems, "camera exposure", timing.cameraExposureMs(), true);
        appendTimingProblem(problems, "slice duration", timing.sliceDurationMs(), true);
        // may legitimately be zero, so only reject negatives
        appendTimingProblem(problems, "scan duration", timing.scanDurationMs(), false);
        appendTimingProblem(problems, "laser trigger duration", timing.laserTriggerDurationMs(), false);
        appendTimingProblem(problems, "camera trigger duration", timing.cameraTriggerDurationMs(), false);
        appendTimingProblem(problems, "delay before scan", timing.delayBeforeScanMs(), false);
        appendTimingProblem(problems, "delay before laser", timing.delayBeforeLaserMs(), false);
        appendTimingProblem(problems, "delay before camera", timing.delayBeforeCameraMs(), false);
        return problems.length() == 0 ? null : problems.toString();
    }

    private static void appendTimingProblem(final StringBuilder problems, final String name,
                                            final double valueMs, final boolean mustBePositive) {
        if (mustBePositive ? valueMs > 0.0 : valueMs >= 0.0) {
            return;
        }
        if (problems.length() > 0) {
            problems.append(", ");
        }
        problems.append(name).append(" is ").append(valueMs).append(" ms");
    }

    public AcquisitionEngine(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model.studio();
        core_ = model.core();

        autofocus_ = new AutofocusAdapter(model_);

        // default settings
        asb_ = ScapeAcquisitionSettings.builder();
        // seeded from the geometry because the angle has no single sensible default;
        // a saved profile replaces it later when UserSettings loads
        asb_.stageScanBuilder().firstViewAngle(
                model.devices().adapter().geometry().defaultFirstViewAngle());
        acqSettings_ = asb_.build();
    }


    //public abstract DefaultAcquisitionSettingsDISPIM settings();

    //public abstract <T extends DefaultAcquisitionSettings.Builder<Builder>> T settingsBuilder();

    abstract boolean setup();

    abstract boolean run();

    abstract void finish();

    /**
     * Whether the user asked to stop before the acquisition was started.
     * <p>
     * Everything from the moment a run is requested up to {@code currentAcquisition_.start()} is
     * preparation, and for the first part of it {@code currentAcquisition_} is still null, so
     * {@code requestStop()} has nothing to abort and only records the request. Engines must check
     * this before starting, or a stop clicked while arming is silently ignored and the run proceeds.
     *
     * @return true if the run should be given up instead of started
     */
    protected boolean isStopRequested() {
        return stopRequested_;
    }

    public abstract void recalculateSliceTiming();

    public abstract void updateDurationLabels();

    public void setDurationPanel(final DurationPanel panel) {
        pnlDuration_ = Objects.requireNonNull(panel);
    }

    /**
     * Sets the acquisition settings and update the acquisition settings builder with current values.
     * <p>
     * This is used to load the plugin settings from JSON.
     *
     * @param acqSettings the {@code DefaultAcquisitionSettingsSCAPE} to use
     */
    public void updateSettings(final ScapeAcquisitionSettings acqSettings) {
        asb_ = new ScapeAcquisitionSettings.Builder(acqSettings);
        acqSettings_ = acqSettings;
    }

    /**
     * Build the {@code DefaultAcquisitionSettingsSCAPE} with the builder and update settings.
     */
    public void updateSettings() {
        acqSettings_ = asb_.build();
    }

    public Future<?> requestRun() {
        return requestRun(false);
    }

    @Override
    public Future<?> requestRun(boolean speedTest) {
        // set here and not inside the task: a Stop clicked while the task is still queued, or
        // anywhere inside setup(), must find a run in flight
        acquisitionRequested_ = true;
        stopRequested_ = false; // never let a previous run's stop request kill this one

        // Run on a new thread, so it doesn't block the EDT
        Future<?> acqFinished = acquisitionExecutor_.submit(() -> {
            if (currentAcquisition_ != null) {
                model_.logging().reportError("Acquisition is already running.");
                return;
            }

            // marker data
            long runId = -1; // set at START; guards STOP so the speed-test path emits no marker
            long startNs = 0; // set alongside runId at START

            try {
                updateSettings(); // make sure settings are current

                if (speedTest) {
                    try {
                        SpeedTest.runSpeedTest(acqSettings_.saveDirectory(),
                              acqSettings_.saveNamePrefix(),
                              core_, acqSettings_.numTimePoints(), true);
                    } catch (Exception e) {
                        model_.logging().reportError(e);
                    }
                    return; // early exit => do speed test
                }

                // LSM-ACQ-START/STOP bracket the acquisition window for CoreLog diffing
                runId = runIdCounter_.incrementAndGet();
                // nanoTime() = monotonic; a wall-clock (currentTimeMillis) jump
                // mid-run can't corrupt the elapsed duration
                startNs = System.nanoTime();
                studio_.logs().logMessage("LSM-ACQ-START " + runId
                        + " " + acqSettings_.acquisitionMode());

                try {
                    if (!setup()) {
                        // every setup() failure path already showed its own specific error,
                        // so log this rather than stacking a second dialog on top of it
                        studio_.logs().logMessage("Error during setup!");
                        return; // early exit => stop acquisition
                    }
                } catch (Exception e) {
                    model_.logging().reportError(e, "Error during acquisition setup");
                    return; // early exit => stop acquisition
                }
                if (stopRequested_) {
                    // Stop was clicked during setup(), when there was no Acquisition to abort.
                    // Give up here so run() never touches the hardware. run() checks again just
                    // before it starts, which covers the rest of the window.
                    studio_.logs().logMessage("Acquisition stopped during setup.");
                    return; // early exit => finish() still tears down in the finally
                }

                run(); // run the acquisition and block until complete
            } catch (Exception e) {
                model_.logging().reportError(e);
            } finally {
                try {
                    finish(); // cleanup any resources
                } catch (Exception e) {
                    model_.logging().reportError(e, "Error during acquisition cleanup");
                } finally {
                    // must ALWAYS run: if currentAcquisition_ is left set, every future
                    // acquisition is rejected until the plugin restarts
                    currentAcquisition_ = null;
                    // cleared last of the run-state flags: while it is set, requestStop() treats
                    // a stop as something to act on rather than an error
                    acquisitionRequested_ = false;
                    // free the datastore so a large store isn't kept in memory (matches MM's
                    // AcqEngJAdapter.onAcquisitionEnded); also what the save guard checks to skip aborted/empty runs
                    datastore_ = null;
                    // LSM-ACQ-STOP in the innermost finally: fires on completion, error, abort, throwing finish()
                    if (runId != -1) {
                        final long elapsedMs = (System.nanoTime() - startNs) / 1_000_000L;
                        studio_.logs().logMessage("LSM-ACQ-STOP " + runId + " " + elapsedMs + " ms");
                    }
                }
            }
        });
        return acqFinished;
    }

    @Override
    public void requestStop() {
        if (!acquisitionRequested_) {
            model_.logging().reportError("Acquisition is not running.");
            return;
        }
        // record the request before trying to abort. During setup() there is nothing to abort yet,
        // and reporting "not running" there would leave the run going with the button reading
        // "Start Acquisition", which is also the way into a second, unwanted run.
        stopRequested_ = true;
        if (currentAcquisition_ != null && !currentAcquisition_.getDataSink().isFinished()) {
            currentAcquisition_.abort();
        }
    }

    @Override
    public void requestPause() {
        if (currentAcquisition_ == null) {
            model_.logging().reportError("Acquisition is not running.");
        } else {
            currentAcquisition_.setPaused(true);
        }
    }

    @Override
    public void requestResume() {
        if (currentAcquisition_ != null) {
            if (currentAcquisition_.isPaused()) {
                currentAcquisition_.setPaused(false);
            }
        }
    }

    /**
     * Higher level stuff in MM may depend on many hidden, poorly documented
     * ways on summary metadata generated by the acquisition engine.
     * This function adds in its fields in order to achieve compatibility.
     */
    protected DefaultSummaryMetadata addMMSummaryMetadata(JSONObject summaryMetadata) {
        try {
            // These are the ones from the clojure engine that may yet need to be translated
            //        "Channels" -> {Long@25854} 2

            summaryMetadata.put(PropertyKey.CHANNEL_GROUP.key(), acqSettings_.channels().group());

            // one name per position on the store's channel axis; with simultaneous cameras the
            // channel index varies fastest, so walk cameras outermost and repeat the whole channel
            // list per camera. This loop order is the slot order: reverse one and every image gets
            // the wrong name.
            final List<String> channelNames = new ArrayList<>();
            final List<String> baseChannelNames = new ArrayList<>();
            if (acqSettings_.channels().enabled() && acqSettings_.channels().count() > 0) {
                for (ChannelSpec c : acqSettings_.channels().used()) {
                    baseChannelNames.add(c.getName());
                }
            } else {
                baseChannelNames.add("Default");
            }
            if (model_.devices().adapter().numSimultaneousCameras() > 1) {
                for (CameraBase camera : model_.devices().imagingCameras()) {
                    for (String channelName : baseChannelNames) {
                        channelNames.add(acqSettings_.channels().enabled()
                                ? channelName + "-" + camera.getDeviceName()
                                : camera.getDeviceName());
                    }
                }
            } else {
                channelNames.addAll(baseChannelNames);
            }

            JSONArray chNames = new JSONArray();
            JSONArray chColors = new JSONArray();
            for (String channelName : channelNames) {
                chNames.put(channelName);
//                chColors.put(c.getRGB());
            }
            summaryMetadata.put(PropertyKey.CHANNEL_NAMES.key(), chNames);
            summaryMetadata.put(PropertyKey.CHANNEL_COLORS.key(), chColors);

            // MM MDA acquisitions have a defined number of
            // frames/slices/channels/positions at the outset
            summaryMetadata.put(PropertyKey.FRAMES.key(), acqSettings_.isUsingTimePoints() ? acqSettings_.numTimePoints() : 1);

            summaryMetadata.put(PropertyKey.SLICES.key(), acqSettings_.volume().slicesPerView());

            summaryMetadata.put(PropertyKey.CHANNELS.key(), channelNames.size());
            summaryMetadata.put(PropertyKey.POSITIONS.key(), acqSettings_.isUsingMultiplePositions() ?
                        studio_.positions().getPositionList().getNumberOfPositions() : 1);

            // MM MDA acquisitions have a defined order
            summaryMetadata.put(PropertyKey.SLICES_FIRST.key(),
                  acqSettings_.acquisitionMode() == AcquisitionMode.STAGE_SCAN_INTERLEAVED);
            summaryMetadata.put(PropertyKey.TIME_FIRST.key(),
                  false); // currently only position, time ordering

            SummaryMetadata.Builder dsmb = new DefaultSummaryMetadata.Builder();

            List<String> axesOrdered = dsmb.build().getOrderedAxes();
            axesOrdered.add(LightSheetEventAdapter.CAMERA_AXIS);
            // convert to JSON array
            JSONArray axes = new JSONArray();
            for (String axis : axesOrdered) {
                axes.put(axis);
            }
            summaryMetadata.put(PropertyKey.AXIS_ORDER.key(), axes);

            final int numPositions = studio_.positions().getPositionList().getNumberOfPositions();

            // channelNames.size() already includes the simultaneous-camera factor
            final Coords dims = studio_.data().coordsBuilder()
                    .channel(channelNames.size())
                    .z(acqSettings_.volume().slicesPerView())
                    .timePoint(acqSettings_.isUsingTimePoints() ? acqSettings_.numTimePoints() : 1)
                    .stagePosition(acqSettings_.isUsingMultiplePositions() ? numPositions : 1)
                    .build();

            final List<String> axisOrder = new ArrayList<>();
            axisOrder.add(Coords.T);
            axisOrder.add(Coords.P);
            axisOrder.add(Coords.C);
            axisOrder.add(Coords.Z);

            // add "z-step_um" metadata to the image viewer (used in the deskew plugin)
            final DefaultSummaryMetadata dsmd = (DefaultSummaryMetadata) dsmb
                    .prefix("")
                    .axisOrder(axisOrder)
                    .channelGroup(model_.acquisitions().settings().channels().group())
                    .channelNames(channelNames)
                    .imageWidth((int)core_.getImageWidth())
                    .imageHeight((int)core_.getImageHeight())
                    .zStepUm(acqSettings_.volume().sliceStepSize())
                    .intendedDimensions(dims)
                    .build();

            summaryMetadata.put(PropertyKey.MICRO_MANAGER_VERSION.key(), dsmd.getMicroManagerVersion());
            return dsmd;
        } catch (JSONException e) {
            studio_.logs().logError(e);
            throw new RuntimeException(e);
        }
    }

    public ScapeAcquisitionSettings settings() {
        return acqSettings_;
    }

    public ScapeAcquisitionSettings.Builder settingsBuilder() {
        return asb_;
    }

    public AutofocusAdapter autofocus() {
        return autofocus_;
    }

    @Override
    public Acquisition current() {
        return currentAcquisition_;
    }

//////////////////////// AcquisitionControl Callback methods ////////////////////////
    @Override
    public void stop(boolean interrupted) {
        // unclear that this parameter is used in other code
        if (currentAcquisition_ != null) {
            currentAcquisition_.abort();
        }
    }

    @Override
    public boolean isAcquisitionRunning() {
        return currentAcquisition_ != null && !currentAcquisition_.areEventsFinished();
    }

    @Override
    public double getFrameIntervalMs() {
        return acqSettings_.timePointIntervalSec();
    }

    @Override
    public long getNextWakeTime() {
        return nextWakeTime_;
    }

    @Override
    public boolean isPaused() {
        if (currentAcquisition_ != null) {
            return currentAcquisition_.isPaused();
        }
        return false;
    }

    @Override
    public void setPause(boolean b) {
        if (currentAcquisition_ != null) {
            currentAcquisition_.setPaused(b);
        }
    }

    @Override
    public boolean abortRequest() {
        // read once: the acquisition thread clears this field as soon as finish() returns
        final Acquisition acq = currentAcquisition_;
        if (acq == null) {
            return true; // nothing is running, so there is nothing to protect
        }
        // always refuse while a run is live: Micro-Manager vetoes the close, and that veto is what
        // keeps it from racing the abort into finish(), whose save closes the same datastore from
        // the acquisition thread. answering yes only aborts, so the close succeeds on the next
        // attempt. the display's abort button also lands here and ignores the result.
        if (model_.logging().confirmOrDefault("Abort Acquisition",
                "Abort the current acquisition task?", false)) {
            acq.abort();
        }
        return false;
    }

    @Override
    public DataProvider getAcquisitionDatastore() {
        return datastore_;
    }

    /**
     * Shutter state as it was before a run changed it.
     *
     * <p>finish() runs on paths that never reach the capture (setup failure, speed test), so the
     * reference is null until a run has captured it and is cleared again once restored. Assigning
     * it is the same statement as capturing it, so the two cannot disagree.
     */
    protected static final class ShutterState {
        final boolean isOpen;
        final boolean autoShutter;

        ShutterState(final boolean isOpen, final boolean autoShutter) {
            this.isOpen = isOpen;
            this.autoShutter = autoShutter;
        }
    }

}
