package org.micromanager.lightsheetmanager.model.acquisitions;

import mmcorej.CMMCore;
import mmcorej.org.json.JSONArray;
import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.Studio;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acquisition.internal.MMAcquistionControlCallbacks;
import org.micromanager.acquisition.internal.acqengjcompat.speedtest.SpeedTest;
import org.micromanager.data.DataProvider;
import org.micromanager.data.Datastore;
import org.micromanager.data.Pipeline;
import org.micromanager.data.SummaryMetadata;
import org.micromanager.data.internal.DefaultSummaryMetadata;
import org.micromanager.data.internal.PropertyKey;
import org.micromanager.lightsheetmanager.api.AcquisitionManager;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.LightSheetManagerFrame;
import org.micromanager.lightsheetmanager.model.autofocus.AutofocusRunner;
import org.micromanager.lightsheetmanager.model.DataStorage;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AcquisitionEngine implements AcquisitionManager, MMAcquistionControlCallbacks {

    protected final Studio studio_;
    protected final CMMCore core_;

    private DefaultAcquisitionSettingsDISPIM.Builder asb_;
    private DefaultAcquisitionSettingsDISPIM acqSettings_;

    private final ExecutorService acquisitionExecutor_ = Executors.newSingleThreadExecutor(
            r -> new Thread(r, "Acquisition Thread"));
    protected volatile Acquisition currentAcquisition_ = null; // TODO: consider making a getter rather than protected?

    private AutofocusRunner autofocusRunner_;

    private DataStorage data_; // TODO: use this, has enum that needs moved/deleted?
    protected Datastore curStore_;
    protected Pipeline curPipeline_;
    protected long nextWakeTime_ = -1;


    // TODO: remove later, hacky method to stop position updater for now
    protected LightSheetManagerFrame frame_;

    protected final LightSheetManagerModel model_;

    public AcquisitionEngine(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model.studio();
        core_ = model.core();

        data_ = new DataStorage(studio_);
        autofocusRunner_ = new AutofocusRunner(model_);

        // default settings
        asb_ = new DefaultAcquisitionSettingsDISPIM.Builder();
        acqSettings_ = asb_.build();
    }


    //public abstract DefaultAcquisitionSettingsDISPIM settings();

    //public abstract <T extends DefaultAcquisitionSettings.Builder<Builder>> T settingsBuilder();

    abstract boolean setup();

    abstract boolean run();

    abstract boolean finish();

    public void setFrame(final LightSheetManagerFrame frame) {
        frame_ = Objects.requireNonNull(frame);
    }

    /**
     * Sets the acquisition settings and populates the acquisition settings builder with its values.
     *
     * @param acqSettings the acquisition settings to set
     */
    public void setAcquisitionSettings(final DefaultAcquisitionSettingsDISPIM acqSettings) {
        asb_ = new DefaultAcquisitionSettingsDISPIM.Builder(acqSettings);
        acqSettings_ = acqSettings;
    }

    @Override
    public Future<?> requestRun(boolean speedTest) {
        // Run on a new thread, so it doesn't block the EDT
        Future<?> acqFinished = acquisitionExecutor_.submit(() -> {
            if (currentAcquisition_ != null) {
                studio_.logs().showError("Acquisition is already running.");
                return;
            }

            try {
                // make sure AcquisitionSettings are up-to-date
                acqSettings_ = asb_.build();

                if (speedTest) {
                    try {
                        SpeedTest.runSpeedTest(acqSettings_.saveDirectory(),
                              acqSettings_.saveNamePrefix(),
                              core_, acqSettings_.numTimePoints(), true);
                    } catch (Exception e) {
                        studio_.logs().showError(e);
                    }
                } else {
                    // TODO: put this here? generic setup tasks?
                    if (acqSettings_.acquisitionMode() == AcquisitionMode.NONE) {
                        studio_.logs().showError("please select a valid acquisition mode!");
                        return; // early exit
                    }
                    // run abstract methods implemented by geometry types
                    if (!setup()) {
                        studio_.logs().logError("error during setup!");
                        return; // early exit => stop acquisition
                    }
                    run(); // run the acquisition and block until complete
                    finish(); // cleanup any resources
                }
            } catch (Exception e) {
                studio_.logs().showError(e);
            }
        });
        return acqFinished;
    }

    @Override
    public void requestStop() {
        if (currentAcquisition_ == null || currentAcquisition_.getDataSink().isFinished()) {
            studio_.logs().showError("Acquisition is not running.");
            return;
        }
        currentAcquisition_.abort();
    }

    @Override
    public void requestPause() {
        if (currentAcquisition_ == null) {
            studio_.logs().showError("Acquisition is not running.");
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
    // TODO remove doProjections when it is clear where it should come from in settings
    protected DefaultSummaryMetadata addMMSummaryMetadata(JSONObject summaryMetadata, boolean doProjections) {
        try {
            // These are the ones from the clojure engine that may yet need to be translated
            //        "Channels" -> {Long@25854} 2

            summaryMetadata.put(PropertyKey.CHANNEL_GROUP.key(), acqSettings_.channelGroup());
            JSONArray chNames = new JSONArray();
            JSONArray chColors = new JSONArray();
            if (acqSettings_.isUsingChannels() && acqSettings_.channels().length > 0) {
                for (ChannelSpec c : acqSettings_.channels()) {
                    chNames.put(c.getName());
//                chColors.put(c.getRGB());
                }
            } else {
                chNames.put("Default");
            }
            summaryMetadata.put(PropertyKey.CHANNEL_NAMES.key(), chNames);
            summaryMetadata.put(PropertyKey.CHANNEL_COLORS.key(), chColors);

            // MM MDA acquisitions have a defined number of
            // frames/slices/channels/positions at the outset
            summaryMetadata.put(PropertyKey.FRAMES.key(), acqSettings_.isUsingTimePoints() ?
                  acqSettings_.numTimePoints() : 1);

            summaryMetadata
                  .put(PropertyKey.SLICES.key(), doProjections ? 1 : acqSettings_.volumeSettings().slicesPerView());

            summaryMetadata.put(PropertyKey.CHANNELS.key(), acqSettings_.isUsingChannels() ?
                  acqSettings_.channels().length : 1);
            summaryMetadata
                  .put(PropertyKey.POSITIONS.key(), acqSettings_.isUsingMultiplePositions() ?
                        studio_.positions().getPositionList().getNumberOfPositions() : 1);

            // MM MDA acquisitions have a defined order
            summaryMetadata.put(PropertyKey.SLICES_FIRST.key(),
                  acqSettings_.acquisitionMode() == AcquisitionMode.STAGE_SCAN_INTERLEAVED);
            summaryMetadata.put(PropertyKey.TIME_FIRST.key(),
                  false); // currently only position, time ordering

            SummaryMetadata.Builder dsmb = new DefaultSummaryMetadata.Builder();

            List<String> axesOrdered = dsmb.build().getOrderedAxes();
            axesOrdered.add(LSMAcquisitionEvents.CAMERA_AXIS);
            // convert to JSON array
            JSONArray axes = new JSONArray();
            for (String axis : axesOrdered) {
                axes.put(axis);
            }
            summaryMetadata.put(PropertyKey.AXIS_ORDER.key(), axes);

            DefaultSummaryMetadata dsmd = (DefaultSummaryMetadata) dsmb.build();

            summaryMetadata.put(PropertyKey.MICRO_MANAGER_VERSION.key(),
                  dsmd.getMicroManagerVersion());
            return dsmd;
        } catch (JSONException e) {
            studio_.logs().logError(e);
            throw new RuntimeException(e);
        }
    }

    // TODO: keep long method name versions or just use aliases?

    public DefaultAcquisitionSettingsDISPIM settings() {
        return acqSettings_;
    }
//
//    public DefaultAcquisitionSettingsDISPIM getAcquisitionSettings() {
//        return acqSettings_;
//    }
//
    public DefaultAcquisitionSettingsDISPIM.Builder settingsBuilder() {
        return asb_;
    }
//
//    public DefaultAcquisitionSettingsDISPIM.Builder getAcquisitionSettingsBuilder() {
//        return asb_;
//    }

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
        return acqSettings_.timePointInterval();
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
        if (currentAcquisition_ != null) {
            currentAcquisition_.abort();
        }
        return true;
    }

    @Override
    public DataProvider getAcquisitionDatastore() {
        return curStore_;
    }

    public AutofocusRunner getAutofocusRunner() {
        return autofocusRunner_;
    }

}
