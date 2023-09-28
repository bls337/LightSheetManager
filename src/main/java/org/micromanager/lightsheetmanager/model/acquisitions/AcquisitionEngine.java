package org.micromanager.lightsheetmanager.model.acquisitions;

import mmcorej.CMMCore;
import mmcorej.StrVector;
import mmcorej.org.json.JSONArray;
import mmcorej.org.json.JSONException;
import mmcorej.org.json.JSONObject;
import org.micromanager.PositionList;
import org.micromanager.Studio;
import org.micromanager.acqj.api.AcquisitionHook;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.acquisition.internal.MMAcquisition;
import org.micromanager.acquisition.internal.MMAcquistionControlCallbacks;
import org.micromanager.acquisition.internal.acqengjcompat.AcqEngJAdapter;
import org.micromanager.acquisition.internal.acqengjcompat.AcqEngJMDADataSink;
import org.micromanager.acquisition.internal.acqengjcompat.speedtest.SpeedTest;
import org.micromanager.data.DataProvider;
import org.micromanager.data.Datastore;
import org.micromanager.data.Pipeline;
import org.micromanager.data.SummaryMetadata;
import org.micromanager.data.internal.DefaultDatastore;
import org.micromanager.data.internal.DefaultSummaryMetadata;
import org.micromanager.data.internal.PropertyKey;
import org.micromanager.data.internal.ndtiff.NDTiffAdapter;
import org.micromanager.internal.MMStudio;
import org.micromanager.lightsheetmanager.api.AcquisitionManager;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.gui.LightSheetManagerFrame;
import org.micromanager.lightsheetmanager.model.autofocus.AutofocusRunner;
import org.micromanager.lightsheetmanager.model.DataStorage;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.PLogicDISPIM;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;
import org.micromanager.lightsheetmanager.api.data.MultiChannelMode;
import org.micromanager.lightsheetmanager.model.devices.NIDAQ;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.utils.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AcquisitionEngine implements AcquisitionManager, MMAcquistionControlCallbacks {

    private final Studio studio_;
    private final CMMCore core_;

    private DefaultAcquisitionSettingsDISPIM.Builder asb_;
    private DefaultAcquisitionSettingsDISPIM acqSettings_;

    private final ExecutorService acquisitionExecutor_ = Executors.newSingleThreadExecutor(
            r -> new Thread(r, "Acquisition Thread"));
    private final LightSheetManagerModel model_;
    private volatile Acquisition currentAcquisition_ = null;

    private AutofocusRunner autofocusRunner_;

    private DataStorage data_;
    private Datastore curStore_;
    private Pipeline curPipeline_;
    private long nextWakeTime_ = -1;


    // TODO: remove later, hacky method to stop position updater for now
    private LightSheetManagerFrame frame_;

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
                    // TODO: put this here?
                    if (acqSettings_.acquisitionMode() == AcquisitionMode.NONE) {
                        studio_.logs().showError("please select a valid acquisition mode!");
                        return;
                    }
                    // Every one of these modality-specific functions should block until the
                    // acquisition is complete
                    GeometryType geometryType = model_.devices()
                            .getDeviceAdapter().getMicroscopeGeometry();
                    switch (geometryType) {
                        case DISPIM:
                            runAcquisitionDISPIM();
                            break;
                        case SCAPE:
                            runAcquisitionSCAPE();
                            break;
                        default:
                            studio_.logs().showError(
                                  "Acquisition Engine is not implemented for " + geometryType);
                            break;
                    }
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

    @Override
    public boolean isRunning() {
        return currentAcquisition_ != null && !currentAcquisition_.areEventsFinished();
    }

    private void runAcquisitionSCAPE() {
//        System.out.println(asb_);
//        System.out.println(asb_.build());
        final boolean isPolling = frame_.getNavigationPanel().isPolling();
        if (isPolling) {
            System.out.println("stopped!");
            frame_.getNavigationPanel().stopPolling();
        }

        // TODO: remove this when acqSettings for SCAPE are implemented
        asb_.useAdvancedTiming(true);

        asb_.sheetCalibrationBuilder(1).useAutoSheetWidth(true);
        asb_.sheetCalibrationBuilder(1).autoSheetWidthPerPixel(0.0);

        // TODO: put this here for now... (make sure vars are updated)
        setAcquisitionSettings(asb_.build());

        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }

        // TODO: check for both TriggerCamera and TriggerLaser
        final String deviceName = model_.devices().getDevice("TriggerCamera").getDeviceName();
        final String deviceLibrary = model_.devices().getDeviceLibrary(deviceName);
        boolean isUsingPLC = deviceLibrary.equals("ASITiger");

        PLogicDISPIM controller = null;

        // Assume demo mode if default camera is DemoCamera
        boolean demoMode = false;
        try {
            demoMode = core_.getDeviceLibrary(core_.getCameraDevice()).equals("DemoCamera");
        } catch (Exception e) {
            studio_.logs().logError(e);
        }
//        boolean demoMode = acqSettings_.demoMode();

        if (!demoMode) {

            if (isUsingPLC) {
                controller = new PLogicDISPIM(model_);

                final boolean success = doHardwareCalculationsSCAPE(controller);
                if (!success) {
                    return; // early exit => could not set up hardware
                }
            } else {
                doHardwareCalculationsNIDAQ();
            }

            // --- testing code below ---
//            String plcName = "Scanner:AB:33";
//            StrVector propertyNames;
//            try {
//                propertyNames = core_.getDevicePropertyNames(plcName);
//            } catch (Exception e) {
//                propertyNames = null;
//            }
//
//            Gson gsonObj = new Gson();
//            HashMap<String, String> deviceProps = new HashMap<>();
//            for (String propName : propertyNames) {
//                String propValue;
//                try {
//                    propValue = core_.getProperty(plcName, propName);
//                } catch (Exception e) {
//                    propValue = "";
//                    System.out.println("failed!");
//                }
//                deviceProps.put(propName, propValue);
//                //System.out.println(propName);
//            }
//            String jsonStr = gsonObj.toJson(deviceProps);
//            System.out.println(jsonStr);

            setAcquisitionSettings(asb_.build());

            String saveDir = acqSettings_.saveDirectory();
            String saveName = acqSettings_.saveNamePrefix();

            DefaultDatastore result = new DefaultDatastore(studio_);
            try {
                if (acqSettings_.saveMode() == DataStorage.SaveMode.NDTIFF) {
                    DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.ND_TIFF);
                } else if (acqSettings_.saveMode() == DataStorage.SaveMode.MULTIPAGE_TIFF) {
                    DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.MULTIPAGE_TIFF);
                } else if (acqSettings_.saveMode() == DataStorage.SaveMode.SINGLEPLANE_TIFF_SERIES) {
                    DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.SINGLEPLANE_TIFF_SERIES);
                } else {
                    studio_.logs().showError("Unsupported save mode: " + acqSettings_.saveMode());
                    return;
                }
                result.setStorage(new NDTiffAdapter(result, saveDir, true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            // Projection mode
            //TODO: where should this come from in settings?
            boolean projectionMode = false;

            //////////////////////////////////////
            // Begin AcqEngJ integration
            //      The acqSettings object should be static at this point, it will now
            //      be parsed and used to create acquisition events, each of which
            //      will "order" the acquisition of 1 image (per each camera)
            //////////////////////////////////////
            // Create acquisition
            AcqEngJMDADataSink sink = new AcqEngJMDADataSink(studio_.events(), new AcqEngJAdapter(studio_));

            currentAcquisition_ = new Acquisition(sink);

            JSONObject summaryMetadata = currentAcquisition_.getSummaryMetadata();
            DefaultSummaryMetadata dsmd = addMMSummaryMetadata(summaryMetadata, projectionMode);

            // TODO(Brandon): where should i get this from?
            SequenceSettings.Builder sequenceSettingsBuilder = new SequenceSettings.Builder();
            sequenceSettingsBuilder.shouldDisplayImages(true);

            MMAcquisition acq = new MMAcquisition(studio_, dsmd,
                        this, sequenceSettingsBuilder.build());

            curStore_ = acq.getDatastore();
            curPipeline_ = acq.getPipeline();
            sink.setDatastore(curStore_);
            sink.setPipeline(curPipeline_);

            studio_.events().registerForEvents(this);
            // commented because this is prob specific to MM MDAs
//        studio_.events().post(new DefaultAcquisitionStartedEvent(curStore_, this,
//              acquisitionSettings));


            // TODO if position time ordering ever implemented, this should be reactivated and the
            //  timelapse hook copied from org.micromanager.acquisition.internal.acqengjcompat.AcqEngJAdapter
//        if (sequenceSettings_.acqOrderMode() == AcqOrderMode.POS_TIME_CHANNEL_SLICE
//              || sequenceSettings_.acqOrderMode() == AcqOrderMode.POS_TIME_SLICE_CHANNEL) {
//            // Pos_time ordered acquisition need their timelapse minimum start time to be
//            // adjusted for each position.  The only place to do that seems to be a hardware hook.
//            currentAcquisition_.addHook(timeLapseHook(acquisitionSettings),
//                  AcquisitionAPI.BEFORE_HARDWARE_HOOK);
//        }




            if (projectionMode) {
                // DiSPIM always uses 45 degrees
                double theta = Math.PI / 4;
                double zStep = acqSettings_.volumeSettings().sliceStepSize();
                int numZSlices = acqSettings_.volumeSettings().slicesPerView();
                int cameraWidth = (int) core_.getImageWidth();
                int cameraHeight = (int) core_.getImageHeight();
                double pixelSizeXYUm = core_.getPixelSizeUm();

                int numUniqueProcessorsNeeded = 2; // Always keep enough around for 2 views
                if (acqSettings_.isUsingChannels()) {
                    numUniqueProcessorsNeeded *= acqSettings_.channels().length;
                }

                AcqEngJStackProcessors proc =
                        new AcqEngJStackProcessors(ObliqueStackProcessor.YX_PROJECTION, theta,
                                pixelSizeXYUm,
                                zStep, numZSlices, cameraWidth, cameraHeight, numUniqueProcessorsNeeded);
                currentAcquisition_.addImageProcessor(proc);
            }

            long acqButtonStart = System.currentTimeMillis();


            ////////////  Acquisition hooks ////////////////////
            // These functions will be run on different threads during the acquisition process
            //    Hooks will run on the Acquisition Engine thread--the one that controls all hardware

            // TODO add any code that needs to be executed on the acquisition thread (i.e. the one
            //  that controls hardware)

            // TODO: autofocus
            currentAcquisition_.addHook(new AcquisitionHook() {
                @Override
                public AcquisitionEvent run(AcquisitionEvent event) {
                    // TODO: does the Tiger controller need to be cleared and/or checked for errors here?

                    if (event.isAcquisitionFinishedEvent()) {
                        // Acquisition is finished, pass along event so things shut down properly
                        return event;
                    }

                    if (event.getMinimumStartTimeAbsolute() != null) {
                        nextWakeTime_ = event.getMinimumStartTimeAbsolute();
                    }

                    // Translate event to timeIndex/channel/etc
                    AcquisitionEvent firstAcqEvent = event.getSequence().get(0);
                    int timePoint = firstAcqEvent.getTIndex();

                    ////////////////////////////////////
                    ///////// Run autofocus ////////////
                    ///////////////////////////////////

                    // TODO: where should these come from? In diSPIM they appear to come from preferences,
                    //  not settings...
                    boolean doAutofocus = acqSettings_.isUsingAutofocus();

                    boolean autofocusAtT0 = false;
                    // TODO: this is where they come from in diSPIM?
//                prefs_.getBoolean(org.micromanager.asidispim.Data.MyStrings.PanelNames.AUTOFOCUS.toString(),
//                      org.micromanager.asidispim.Data.Properties.Keys.PLUGIN_AUTOFOCUS_ACQBEFORESTART, false);
                    boolean autofocusEveryStagePass = false;
                    boolean autofocusEachNFrames = false;
                    boolean autofocusChannel = false;

                    // TODO: this is the diSPIM plugin's autofocus code, which needs to be reimplemented
                    //   and translated. There are also currently no autofocus related things in the acqSettings_
//                if (acqSettings_.isUsingAutofocus()) {
//                    // (Copied from diSPIM): Note that we will not autofocus as expected when using hardware
//                    // timing.  Seems OK, since hardware timing will result in short
//                    // acquisition times that do not need autofocus.
//                    if ( (autofocusAtT0 && timePoint == 0) || ( (timePoint > 0) &&
//                          (timePoint % autofocusEachNFrames == 0 ) ) ) {
//                        if (acqSettings.useChannels) {
//                            multiChannelPanel_.selectChannel(autofocusChannel);
//                        }
//                        if (sideActiveA) {
//                            if (acqSettings.usePathPresets) {
//                                controller_.setPathPreset(org.micromanager.asidispim.Data.Devices.Sides.A);
//                                // blocks until all devices done
//                            }
//                            org.micromanager.asidispim.Utils.AutofocusUtils.FocusResult score = autofocus_.runFocus(
//                                  this, org.micromanager.asidispim.Data.Devices.Sides.A, false,
//                                  sliceTiming_, false);
//                            updateCalibrationOffset(org.micromanager.asidispim.Data.Devices.Sides.A, score);
//                        }
//                        if (sideActiveB) {
//                            if (acqSettings.usePathPresets) {
//                                controller_.setPathPreset(org.micromanager.asidispim.Data.Devices.Sides.B);
//                                // blocks until all devices done
//                            }
//                            org.micromanager.asidispim.Utils.AutofocusUtils.FocusResult score = autofocus_.runFocus(
//                                  this, org.micromanager.asidispim.Data.Devices.Sides.B, false,
//                                  sliceTiming_, false);
//                            updateCalibrationOffset(org.micromanager.asidispim.Data.Devices.Sides.B, score);
//                        }
//                        // Restore settings of the controller
//                        controller_.prepareControllerForAquisition(acqSettings, extraChannelOffset_);
//                        if (acqSettings.useChannels && acqSettings.channelMode != org.micromanager.asidispim.Data.MultichannelModes.Keys.VOLUME) {
//                            controller_.setupHardwareChannelSwitching(acqSettings, hideErrors);
//                        }
//                    }
//                }

                    return event;
                }

                @Override
                public void close() {

                }
            }, Acquisition.BEFORE_HARDWARE_HOOK);



            final PLogicDISPIM controllerInstance = controller;
            // TODO This after camera hook is called after the camera has been readied to acquire a
            //  sequence. I assume we want to tell the Tiger to start sending TTLs etc here
            currentAcquisition_.addHook(new AcquisitionHook() {
                @Override
                public AcquisitionEvent run(AcquisitionEvent event) {
                    // TODO: Cameras are now ready to receive triggers, so we can send (software) trigger
                    //  to the tiger to tell it to start outputting TTLs

                    if (isUsingPLC && controllerInstance != null) { // if not in demo mode
                        int side = 0;
                        // TODO: enable 2 sided acquisition
                        controllerInstance.triggerControllerStartAcquisitionSCAPE(
                                asb_.acquisitionMode(), side);
                    }
                    return event;
                }

                @Override
                public void close() {

                }
            }, Acquisition.AFTER_CAMERA_HOOK);


            ///////////// Turn off autoshutter /////////////////
            final boolean isShutterOpen;
            try {
                isShutterOpen = core_.getShutterOpen();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // TODO: should the shutter be left open for the full duration of acquisition?
            //  because that's what this code currently does
            boolean autoShutter = core_.getAutoShutter();
            if (autoShutter) {
                core_.setAutoShutter(false);
                if (!isShutterOpen) {
                    try {
                        core_.setShutterOpen(true);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            currentAcquisition_.start();

            ////////////  Create and submit acquisition events ////////////////////
            // Create iterators of acquisition events and submit them to the engine for execution
            // The engine will (try to) automatically iterate over the AcquisitionEvents of each
            // iterator, but not over multiple iterators. So there should be one iterator submitted for
            // each expected triggering of the Tiger controller.

            // TODO: execute any start-acquisition runnables


            // Loop 1: XY positions
            PositionList pl = MMStudio.getInstance().positions().getPositionList();
            if (acqSettings_.isUsingMultiplePositions() && (pl.getNumberOfPositions() == 0)) {
                throw new RuntimeException("XY positions expected but position list is empty");
            }

            String[] cameraNames;
            if (demoMode) {
                ArrayList<String> cameraDeviceNames = new ArrayList<>();
                StrVector loadedDevices = core_.getLoadedDevices();
                for (int i = 0; i < loadedDevices.size(); i++) {
                    try {
                        if (core_.getDeviceType(loadedDevices.get(i)).toString().equals("CameraDevice")) {
                            cameraDeviceNames.add(loadedDevices.get(i));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
                if (cameraDeviceNames.size() < 2) {
                    throw new RuntimeException("Need two cameras for diSPIM simulation");
                }
                cameraNames = cameraDeviceNames.toArray(new String[0]);
            } else {
                if (asb_.vsb().numViews() > 1) {
                    cameraNames = new String[]{
                            model_.devices().getDevice("Imaging1Camera").getDeviceName(),
                            model_.devices().getDevice("Imaging2Camera").getDeviceName()
                    };
                } else {
                    cameraNames = new String[]{
                            model_.devices().getDevice("ImagingCamera").getDeviceName()
                    };
                }
            }

            for (int positionIndex = 0; positionIndex < (acqSettings_.isUsingMultiplePositions() ?
                    pl.getNumberOfPositions() : 1); positionIndex++) {
                AcquisitionEvent baseEvent = new AcquisitionEvent(currentAcquisition_);
                if (acqSettings_.isUsingMultiplePositions()) {
                    baseEvent.setAxisPosition(LSMAcquisitionEvents.POSITION_AXIS, positionIndex);
                }
                // TODO: what to do if multiple positions not defined: acquire at current stage position?
                //  If yes, then nothing more to do here.

                if (acqSettings_.isUsingHardwareTimePoints()) {
                    // create a full iterator of TCZ acquisition events, and Tiger controller
                    // will handle everything else
                    if (acqSettings_.isUsingChannels()) {
                        currentAcquisition_.submitEventIterator(
                                LSMAcquisitionEvents.createTimelapseMultiChannelVolumeAcqEvents(
                                        baseEvent.copy(), acqSettings_, cameraNames, null));
                    } else {
                        currentAcquisition_.submitEventIterator(
                                LSMAcquisitionEvents.createTimelapseVolumeAcqEvents(
                                        baseEvent.copy(), acqSettings_, cameraNames, null));
                    }
                } else {
                    // Loop 2: Multiple time points
                    for (int timeIndex = 0; timeIndex < (acqSettings_.isUsingTimePoints() ?
                            acqSettings_.numTimePoints() : 1); timeIndex++) {
                        baseEvent.setTimeIndex(timeIndex);
                        // Loop 3: Channels; Loop 4: Z slices (non-interleaved)
                        // Loop 3: Channels; Loop 4: Z slices (interleaved)
                        if (acqSettings_.isUsingChannels()) {
                            currentAcquisition_.submitEventIterator(
                                    LSMAcquisitionEvents.createMultiChannelVolumeAcqEvents(
                                            baseEvent.copy(), acqSettings_, cameraNames, null,
                                            acqSettings_.acquisitionMode() ==
                                                    AcquisitionMode.STAGE_SCAN_INTERLEAVED));
                        } else {
                            currentAcquisition_.submitEventIterator(
                                    LSMAcquisitionEvents.createVolumeAcqEvents(
                                            baseEvent.copy(), acqSettings_, cameraNames, null));
                        }
                    }
                }
            }



            // No more instructions (i.e. AcquisitionEvents); tell the acquisition to initiate shutdown
            // once everything finishes
            currentAcquisition_.finish();


            currentAcquisition_.waitForCompletion();

            // cleanup
            studio_.logs().logMessage("SCAPE plugin acquisition " +
                    " took: " + (System.currentTimeMillis() - acqButtonStart) + "ms");


            // clean up controller settings after acquisition
            // want to do this, even with demo cameras, so we can test everything else
            // TODO: figure out if we really want to return piezos to 0 position (maybe center position,
            //   maybe not at all since we move when we switch to setup tab, something else??)
            if (isUsingPLC) {
                controller.cleanUpControllerAfterAcquisition(acqSettings_, true);
            }

            // Restore shutter/autoshutter to original state
            try {
                core_.setShutterOpen(isShutterOpen);
                core_.setAutoShutter(autoShutter);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't restore shutter to original state");
            }

            // Check if acquisition ended due to an exception and show error to user if it did
            try {
                currentAcquisition_.checkForExceptions();
            } catch (Exception e){
                studio_.logs().showError(e);
            }

            // TODO: execute any end-acquisition runnables

            currentAcquisition_ = null;

            // start polling for navigation panel
            if (isPolling) {
                frame_.getNavigationPanel().startPolling();
            }
        }
    }

    /**
     * This is a bunch of logic copied from the diSPIM plugin
     * @return
     */
    private boolean doHardwareCalculations(PLogicDISPIM plc) {

        // make sure slice timings are up-to-date

        recalculateSliceTiming(asb_);
        System.out.println("after recalculateSliceTiming: " + asb_.timingSettingsBuilder());


        // TODO: was only checked in light sheet mode
//        if (core_.getPixelSizeUm() < 1e-6) {
//            studio_.logs().showError("Need to set the pixel size in Micro-Manager.");
//        }

        // setup channels
        int nrChannelsSoftware = asb_.numChannels();  // how many times we trigger the controller per stack
        int nrSlicesSoftware = asb_.volumeSettingsBuilder().slicesPerVolume();
                //acqSettings_.volumeSettings().slicesPerView();
        // TODO: channels need to modify panels and need extraChannelOffset_
        boolean changeChannelPerVolumeSoftware = false;
        boolean changeChannelPerVolumeDoneFirst = false;
        if (acqSettings_.isUsingChannels()) {
            if (asb_.numChannels() > 1) {
                studio_.logs().showError("\"Channels\" is checked, but no channels are selected");
                return false; // early exit
            }
            switch (acqSettings_.channelMode()) {
                case VOLUME:
                    changeChannelPerVolumeSoftware = true;
                    changeChannelPerVolumeDoneFirst = true;
                    break;
                case VOLUME_HW:
                case SLICE_HW:
                    if (acqSettings_.numChannels() == 1) {
                        // only 1 channel selected so don't have to really use hardware switching
                    } else {
                        // we have at least 2 channels
                        // intentionally leave extraChannelOffset_ untouched so that it can be specified by user by choosing a preset
                        //   for the channel in the main Micro-Manager window
                    }
                    final boolean success = plc.setupHardwareChannelSwitching(acqSettings_);
                    if (!success) {
                        studio_.logs().showError("Couldn't set up slice hardware channel switching.");
                        return false; // early exit
                    }
                    nrChannelsSoftware = 1;
                    nrSlicesSoftware = asb_.volumeSettingsBuilder().slicesPerVolume() * asb_.numChannels();
                    break;
                default:
                    studio_.logs().showError(
                            "Unsupported multichannel mode \"" + acqSettings_.channelMode().toString() + "\"");
                    return false; // early exit
            }
        }
        // TODO: code that doubles nrSlicesSoftware if (twoSided && acqBothCameras) missing

        CameraBase camera = model_.devices().getDevice("Imaging1Camera");
        CameraMode camMode = camera.getTriggerMode();
        final float cameraReadoutTime = camera.getReadoutTime(camMode);
        //final double exposureTime = acqSettings_.timingSettings().cameraExposure();
        final double exposureTime = asb_.timingSettingsBuilder().cameraExposure();

        // test acq was here

        double volumeDuration = computeActualVolumeDuration(acqSettings_);
        double timepointDuration = computeTimePointDuration();
        long timepointIntervalMs = Math.round(acqSettings_.timePointInterval()*1000.0f);

        // use hardware timing if < 1 second between time points
        // experimentally need ~0.5 sec to set up acquisition, this gives a bit of cushion
        // cannot do this in getCurrentAcquisitionSettings because of mutually recursive
        // call with computeActualVolumeDuration()
        if (acqSettings_.isUsingTimePoints()
              && acqSettings_.numTimePoints() > 1
              && timepointIntervalMs < (timepointDuration + 750)
              && !acqSettings_.isUsingStageScanning()) {
           // acqSettings_.useHardwareTimesPoints(true);
            asb_.useHardwareTimePoints(true);
        }

        if (acqSettings_.isUsingMultiplePositions()) {
            if ((acqSettings_.isUsingHardwareTimePoints()
                  || acqSettings_.numTimePoints() > 1)
                  && (timepointIntervalMs < timepointDuration*1.2)) {
                //acqSettings_.setHardwareTimesPoints(false);
                asb_.useHardwareTimePoints(false);
                // TODO: WARNING
            }
        }


        // TODO: make sure position updater is turned off!

        //double sliceDuration = acqSettings_.timingSettings().sliceDuration();
        double sliceDuration = asb_.timingSettingsBuilder().sliceDuration();
        if (exposureTime + cameraReadoutTime > sliceDuration) {
            // should only possible to mess this up using advanced timing settings
            // or if there are errors in our own calculations
            studio_.logs().showError("Exposure time of " + exposureTime +
                  " is longer than time needed for a line scan with" +
                  " readout time of " + cameraReadoutTime + "\n" +
                  "This will result in dropped frames. " +
                  "Please change input");
            return false; // early exit
        }


        // TODO: diSPIM has the following code, which is apparently needed for autofocusing
//        boolean sideActiveA, sideActiveB;
//        final boolean twoSided = acqSettingsOrig.numSides > 1;
//        if (twoSided) {
//            sideActiveA = true;
//            sideActiveB = true;
//        } else {
//            if (!acqSettingsOrig.acquireBothCamerasSimultaneously) {
//                secondCamera = null;
//            }
//            if (firstSideA) {
//                sideActiveA = true;
//                sideActiveB = false;
//            } else {
//                sideActiveA = false;
//                sideActiveB = true;
//            }
//        }

        double extraChannelOffset = 0.0;
        plc.prepareControllerForAcquisition(acqSettings_, extraChannelOffset);
        return true;
    }

    private boolean doHardwareCalculationsSCAPE(PLogicDISPIM plc) {

        // make sure slice timings are up-to-date
        final double sliceDuration1 = getSliceDuration(asb_.timingSettingsBuilder());
        asb_.timingSettingsBuilder().sliceDuration(sliceDuration1);
        
        recalculateSliceTiming(asb_);
        System.out.println("after recalculateSliceTiming: " + asb_.timingSettingsBuilder());


        // TODO: was only checked in light sheet mode
//        if (core_.getPixelSizeUm() < 1e-6) {
//            studio_.logs().showError("Need to set the pixel size in Micro-Manager.");
//        }

        // setup channels
        int nrChannelsSoftware = asb_.numChannels();  // how many times we trigger the controller per stack
        int nrSlicesSoftware = asb_.volumeSettingsBuilder().slicesPerVolume();
        //acqSettings_.volumeSettings().slicesPerView();
        // TODO: channels need to modify panels and need extraChannelOffset_
        boolean changeChannelPerVolumeSoftware = false;
        boolean changeChannelPerVolumeDoneFirst = false;
        if (acqSettings_.isUsingChannels()) {
            if (asb_.numChannels() > 1) {
                studio_.logs().showError("\"Channels\" is checked, but no channels are selected");
                return false; // early exit
            }
            switch (acqSettings_.channelMode()) {
                case VOLUME:
                    changeChannelPerVolumeSoftware = true;
                    changeChannelPerVolumeDoneFirst = true;
                    break;
                case VOLUME_HW:
                case SLICE_HW:
                    if (acqSettings_.numChannels() == 1) {
                        // only 1 channel selected so don't have to really use hardware switching
                    } else {
                        // we have at least 2 channels
                        // intentionally leave extraChannelOffset_ untouched so that it can be specified by user by choosing a preset
                        //   for the channel in the main Micro-Manager window
                    }
                    final boolean success = plc.setupHardwareChannelSwitching(acqSettings_);
                    if (!success) {
                        studio_.logs().showError("Couldn't set up slice hardware channel switching.");
                        return false; // early exit
                    }
                    nrChannelsSoftware = 1;
                    nrSlicesSoftware = asb_.volumeSettingsBuilder().slicesPerVolume() * asb_.numChannels();
                    break;
                default:
                    studio_.logs().showError(
                            "Unsupported multichannel mode \"" + acqSettings_.channelMode().toString() + "\"");
                    return false; // early exit
            }
        }
        // TODO: code that doubles nrSlicesSoftware if (twoSided && acqBothCameras) missing

        CameraBase camera = model_.devices().getDevice("ImagingCamera");
        CameraMode camMode = camera.getTriggerMode();
        final float cameraReadoutTime = camera.getReadoutTime(camMode);
        //final double exposureTime = acqSettings_.timingSettings().cameraExposure();
        final double exposureTime = asb_.timingSettingsBuilder().cameraExposure();

        // test acq was here

        double volumeDuration = computeActualVolumeDuration(acqSettings_);
        double timepointDuration = computeTimePointDuration();
        long timepointIntervalMs = Math.round(acqSettings_.timePointInterval()*1000.0f);

        // use hardware timing if < 1 second between time points
        // experimentally need ~0.5 sec to set up acquisition, this gives a bit of cushion
        // cannot do this in getCurrentAcquisitionSettings because of mutually recursive
        // call with computeActualVolumeDuration()
        if (acqSettings_.isUsingTimePoints()
                && acqSettings_.numTimePoints() > 1
                && timepointIntervalMs < (timepointDuration + 750)
                && !acqSettings_.isUsingStageScanning()) {
            // acqSettings_.useHardwareTimesPoints(true);
            asb_.useHardwareTimePoints(true);
        }

        if (acqSettings_.isUsingMultiplePositions()) {
            if ((acqSettings_.isUsingHardwareTimePoints()
                    || acqSettings_.numTimePoints() > 1)
                    && (timepointIntervalMs < timepointDuration*1.2)) {
                //acqSettings_.setHardwareTimesPoints(false);
                asb_.useHardwareTimePoints(false);
                // TODO: WARNING
            }
        }

        // TODO: make sure position updater is turned off!

        // FIXME: what is happening here? getting timing error
        double sliceDuration = asb_.timingSettingsBuilder().sliceDuration();
        if (exposureTime + cameraReadoutTime > sliceDuration) {
            //System.out.println(sliceDuration);
            // should only possible to mess this up using advanced timing settings
            // or if there are errors in our own calculations
            studio_.logs().showError("Exposure time of " + exposureTime +
                    " is longer than time needed for a line scan with" +
                    " readout time of " + cameraReadoutTime + "\n" +
                    "This will result in dropped frames. " +
                    "Please change input");
            return false; // early exit
        }

        double extraChannelOffset = 0.0;
        plc.prepareControllerForAcquisitionSCAPE(acqSettings_, extraChannelOffset);
        return true;
    }

    private void doHardwareCalculationsNIDAQ() {
        NIDAQ daq = model_.devices().getDevice("TriggerCamera");

        //daq.setProperty("PropertyName", "1");
    }

    /**
     * Higher level stuff in MM may depend on many hidden, poorly documented
     * ways on summary metadata generated by the acquisition engine.
     * This function adds in its fields in order to achieve compatibility.
     */
    // TODO remove doProjections when it clear where it should come from in settings
    private DefaultSummaryMetadata addMMSummaryMetadata(JSONObject summaryMetadata, boolean doProjections) {
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

    private void runAcquisitionDISPIM() {

        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }

        // TODO: check for both TriggerCamera and TriggerLaser
        final String deviceName = model_.devices().getDevice("TriggerCamera").getDeviceName();
        final String deviceLibrary = model_.devices().getDeviceLibrary(deviceName);
        boolean isUsingPLC = deviceLibrary.equals("ASITiger");

        PLogicDISPIM controller = null;

        // Assume demo mode if default camera is DemoCamera
        boolean demoMode = false;
        try {
            demoMode = core_.getDeviceLibrary(core_.getCameraDevice()).equals("DemoCamera");
        } catch (Exception e) {
            studio_.logs().logError(e);
        }
//        boolean demoMode = acqSettings_.demoMode();

        if (!demoMode) {

            if (isUsingPLC) {
                controller = new PLogicDISPIM(model_);

                final boolean success = doHardwareCalculations(controller);
                if (!success) {
                    return; // early exit => could not set up hardware
                }
            } else {
                doHardwareCalculationsNIDAQ();
            }
//            String plcName = "PLogic:E:36";
//            try {
//                core_.setProperty(plcName, "EnableAdvancedProperties", "Yes");
//            } catch (Exception e1) {
//                System.out.println("failed to enable adv props");
//            }
//        StrVector propertyNames;
//        try {
//            propertyNames = core_.getDevicePropertyNames(plcName);
//        } catch (Exception e) {
//            propertyNames = null;
//        }
//        Gson gsonObj = new Gson();
//        HashMap<String, String> deviceProps = new HashMap<>();
//        for (String propName : propertyNames) {
//            String propValue;
//            try {
//                propValue = core_.getProperty(plcName, propName);
//            } catch (Exception e) {
//                propValue = "";
//                System.out.println("failed!");
//            }
//            deviceProps.put(propName, propValue);
//            //System.out.println(propName);
//        }
//        String jsonStr = gsonObj.toJson(deviceProps);
//        System.out.println(jsonStr);
            //String jsonStr = "{\"PCell_03_CellType\":\"0 - constant\",\"PCell_09_CellType\":\"0 - constant\",\"BackplaneOutputState\":\"130\",\"IOFrontpanel_7_SourceAddress\":\"0\",\"PCell_12_CellType\":\"3 - 3-input LUT\",\"IOBackplane_2_SourceAddress\":\"0\",\"IOFrontpanel_2_SourceAddress\":\"43\",\"PCell_14_Input2\":\"0\",\"PCell_14_Input1\":\"0\",\"PCell_09_Config\":\"0\",\"PCell_16_Config\":\"0\",\"PCell_10_Input1\":\"42\",\"PCell_10_Input2\":\"8\",\"PointerPosition\":\"48\",\"PCell_05_Config\":\"0\",\"PCell_07_Config\":\"0\",\"PCell_12_Input2\":\"10\",\"PCell_16_Input2\":\"0\",\"PCell_12_Input1\":\"44\",\"PCell_16_Input1\":\"0\",\"PCell_10_Input3\":\"0\",\"PCell_12_Input4\":\"0\",\"PCell_14_Input4\":\"0\",\"PCell_16_Input4\":\"0\",\"PCell_10_Input4\":\"0\",\"PCell_12_Input3\":\"1\",\"PCell_14_Input3\":\"0\",\"PCell_16_Input3\":\"0\",\"PCell_03_Config\":\"0\",\"IOFrontpanel_5_SourceAddress\":\"0\",\"PCell_12_Config\":\"168\",\"PCell_14_Config\":\"0\",\"PCell_10_Config\":\"0\",\"PCell_04_CellType\":\"0 - constant\",\"Description\":\"ASI Programmable Logic HexAddr\u003d36\",\"RefreshPropertyValues\":\"No\",\"EditCellUpdateAutomatically\":\"Yes\",\"IOBackplane_6_SourceAddress\":\"0\",\"IOBackplane_0_SourceAddress\":\"0\",\"NumLogicCells\":\"16\",\"OutputChannel\":\"none of outputs 5-8\",\"PCell_14_CellType\":\"0 - constant\",\"PCell_08_CellType\":\"0 - constant\",\"PCell_06_Input3\":\"0\",\"PCell_06_Input2\":\"0\",\"PCell_13_Input1\":\"0\",\"IOFrontpanel_8_SourceAddress\":\"0\",\"PCell_06_Input4\":\"0\",\"PCell_06_Input1\":\"0\",\"PCell_02_Input1\":\"0\",\"PCell_02_Input2\":\"0\",\"PCell_02_Input3\":\"0\",\"PCell_02_Input4\":\"0\",\"PCell_08_Config\":\"0\",\"PCell_13_Input4\":\"0\",\"PCell_13_Input2\":\"0\",\"IOBackplane_7_SourceAddress\":\"0\",\"PCell_13_Input3\":\"0\",\"FirmwareDate\":\"Oct 05 2020:06:42:01\",\"PCell_04_Config\":\"0\",\"SetCardPreset\":\"14 - diSPIM TTL\",\"PCell_11_Config\":\"0\",\"PCell_15_Config\":\"0\",\"PCell_01_CellType\":\"0 - constant\",\"EditCellConfig\":\"0\",\"PCell_15_CellType\":\"0 - constant\",\"IOBackplane_2_IOType\":\"0 - input\",\"PCell_06_CellType\":\"0 - constant\",\"PLogicOutputState\":\"1\",\"IOBackplane_1_IOType\":\"0 - input\",\"IOBackplane_3_IOType\":\"0 - input\",\"IOBackplane_4_IOType\":\"0 - input\",\"IOBackplane_5_IOType\":\"0 - input\",\"IOBackplane_6_IOType\":\"0 - input\",\"IOBackplane_7_IOType\":\"0 - input\",\"Name\":\"PLogic:E:36\",\"IOBackplane_0_IOType\":\"0 - input\",\"IOFrontpanel_3_IOType\":\"2 - output (push-pull)\",\"EditCellCellType\":\"0 - input\",\"IOFrontpanel_2_IOType\":\"2 - output (push-pull)\",\"IOFrontpanel_4_IOType\":\"2 - output (push-pull)\",\"IOFrontpanel_6_SourceAddress\":\"0\",\"IOFrontpanel_1_IOType\":\"2 - output (push-pull)\",\"IOFrontpanel_5_IOType\":\"2 - output (push-pull)\",\"FrontpanelOutputState\":\"0\",\"IOFrontpanel_7_IOType\":\"2 - output (push-pull)\",\"IOFrontpanel_6_IOType\":\"2 - output (push-pull)\",\"TriggerSource\":\"1 - Micro-mirror card\",\"PCell_05_Input1\":\"0\",\"PCell_05_Input2\":\"0\",\"PCell_05_Input3\":\"0\",\"PCell_07_Input3\":\"0\",\"PCell_05_Input4\":\"0\",\"PCell_07_Input4\":\"0\",\"PCell_16_CellType\":\"0 - constant\",\"PCell_07_Input1\":\"0\",\"PCell_07_Input2\":\"0\",\"PCell_03_Input2\":\"0\",\"PCell_03_Input1\":\"0\",\"PCell_03_Input4\":\"0\",\"PCell_09_Input2\":\"0\",\"PCell_03_Input3\":\"0\",\"PCell_09_Input1\":\"0\",\"PCell_09_Input4\":\"0\",\"PCell_09_Input3\":\"0\",\"AxisLetter\":\"E\",\"IOBackplane_1_SourceAddress\":\"0\",\"PCell_01_Input4\":\"0\",\"PCell_01_Input3\":\"0\",\"PCell_01_Input2\":\"0\",\"PCell_01_Input1\":\"0\",\"IOFrontpanel_1_SourceAddress\":\"41\",\"PCell_10_CellType\":\"5 - 2-input AND\",\"EnableAdvancedProperties\":\"Yes\",\"IOBackplane_5_SourceAddress\":\"0\",\"PCell_01_Config\":\"1\",\"ClearAllCellStates\":\"Not done\",\"EditCellInput1\":\"0\",\"PCell_02_CellType\":\"0 - constant\",\"EditCellInput2\":\"0\",\"EditCellInput3\":\"0\",\"PCell_05_CellType\":\"0 - constant\",\"PCell_11_CellType\":\"0 - constant\",\"EditCellInput4\":\"0\",\"IOFrontpanel_4_SourceAddress\":\"12\",\"SaveCardSettings\":\"no action\",\"PCell_15_Input4\":\"0\",\"IOFrontpanel_3_SourceAddress\":\"0\",\"PCell_13_CellType\":\"0 - constant\",\"PCell_08_Input4\":\"0\",\"PCell_04_Input3\":\"0\",\"PCell_08_Input3\":\"0\",\"IOBackplane_4_SourceAddress\":\"0\",\"IOFrontpanel_8_IOType\":\"2 - output (push-pull)\",\"PCell_04_Input2\":\"0\",\"PCell_08_Input2\":\"0\",\"PCell_04_Input1\":\"0\",\"PCell_08_Input1\":\"0\",\"PCell_11_Input4\":\"0\",\"PCell_07_CellType\":\"0 - constant\",\"PCell_04_Input4\":\"0\",\"PCell_06_Config\":\"0\",\"PCell_11_Input1\":\"0\",\"PCell_15_Input1\":\"0\",\"FirmwareVersion\":\"3.3300\",\"PCell_11_Input2\":\"0\",\"PCell_15_Input2\":\"0\",\"PCell_11_Input3\":\"0\",\"PCell_15_Input3\":\"0\",\"PCell_02_Config\":\"0\",\"FirmwareBuild\":\"PLOGIC_16\",\"PCell_13_Config\":\"0\",\"TigerHexAddress\":\"36\",\"PLogicMode\":\"diSPIM Shutter\",\"IOBackplane_3_SourceAddress\":\"0\"}";

//            System.out.println("create JSON...");
//            JSONObject jsonObj = null;
//            try {
//                jsonObj = new JSONObject(jsonStr);
//            } catch (JSONException e) {
//                System.out.println("failed to create json object!");
//            }
////            jsonObj.keys().forEachRemaining(key -> {
////
////            });
//
//            for (Iterator<String> it = jsonObj.keys(); it.hasNext(); ) {
//                String s = it.next();
//                String value;
//                try {
//                    value = jsonObj.getString(s);
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//                try {
//                    core_.setProperty(plcName, s, value);
//                } catch (Exception e) {
//                    System.out.println("failed to set property " + s + " " + value);
//                }
//                System.out.println(s + " " + value);
//            }

//            // TODO: match settings from 1.4 plugin (delete later)
//            ASIScanner scanner = model_.devices().getDevice(DISPIMDevice.getIllumBeam(1));
//            scanner.sa().setAmplitudeX(4.1f);
//            scanner.sa().setOffsetY(-0.0336f);
        }
//        try {
//            core_.setProperty("Andor sCMOS Camera A", "TriggerMode", "Internal (Recommended for fast acquisitions)");
//            core_.setProperty("Andor sCMOS Camera B", "TriggerMode", "Internal (Recommended for fast acquisitions)");
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
        setAcquisitionSettings(asb_.build());

        String saveDir = acqSettings_.saveDirectory();
        String saveName = acqSettings_.saveNamePrefix();

        DefaultDatastore result = new DefaultDatastore(studio_);
        try {
            if (acqSettings_.saveMode() == DataStorage.SaveMode.NDTIFF) {
                DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.ND_TIFF);
            } else if (acqSettings_.saveMode() == DataStorage.SaveMode.MULTIPAGE_TIFF) {
                DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.MULTIPAGE_TIFF);
            } else if (acqSettings_.saveMode() == DataStorage.SaveMode.SINGLEPLANE_TIFF_SERIES) {
                DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.SINGLEPLANE_TIFF_SERIES);
            } else {
                studio_.logs().showError("Unsupported save mode: " + acqSettings_.saveMode());
                return;
            }
            result.setStorage(new NDTiffAdapter(result, saveDir, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Projection mode
        //TODO: where should this come from in settings?
        boolean projectionMode = false;

        //////////////////////////////////////
        // Begin AcqEngJ integration
        //      The acqSettings object should be static at this point, it will now
        //      be parsed and used to create acquisition events, each of which
        //      will "order" the acquisition of 1 image (per each camera)
        //////////////////////////////////////
        // Create acquisition
       AcqEngJMDADataSink sink = new AcqEngJMDADataSink(studio_.events(), new AcqEngJAdapter(studio_));

       currentAcquisition_ = new Acquisition(sink);

        JSONObject summaryMetadata = currentAcquisition_.getSummaryMetadata();
        DefaultSummaryMetadata dsmd = addMMSummaryMetadata(summaryMetadata, projectionMode);

        // TODO(Brandon): where should i get this from?
        SequenceSettings.Builder sequenceSettingsBuilder = new SequenceSettings.Builder();
        sequenceSettingsBuilder.shouldDisplayImages(true);

        MMAcquisition acq = new MMAcquisition(studio_, dsmd,
                this, sequenceSettingsBuilder.build());

        curStore_ = acq.getDatastore();
        curPipeline_ = acq.getPipeline();
        sink.setDatastore(curStore_);
        sink.setPipeline(curPipeline_);

        studio_.events().registerForEvents(this);
        // commented because this is prob specific to MM MDAs
//        studio_.events().post(new DefaultAcquisitionStartedEvent(curStore_, this,
//              acquisitionSettings));


        // TODO if position time ordering ever implemented, this should be reactivated and the
        //  timelapse hook copied from org.micromanager.acquisition.internal.acqengjcompat.AcqEngJAdapter
//        if (sequenceSettings_.acqOrderMode() == AcqOrderMode.POS_TIME_CHANNEL_SLICE
//              || sequenceSettings_.acqOrderMode() == AcqOrderMode.POS_TIME_SLICE_CHANNEL) {
//            // Pos_time ordered acquisition need their timelapse minimum start time to be
//            // adjusted for each position.  The only place to do that seems to be a hardware hook.
//            currentAcquisition_.addHook(timeLapseHook(acquisitionSettings),
//                  AcquisitionAPI.BEFORE_HARDWARE_HOOK);
//        }




        if (projectionMode) {
            // DiSPIM always uses 45 degrees
            double theta = Math.PI / 4;
            double zStep = acqSettings_.volumeSettings().sliceStepSize();
            int numZSlices = acqSettings_.volumeSettings().slicesPerView();
            int cameraWidth = (int) core_.getImageWidth();
            int cameraHeight = (int) core_.getImageHeight();
            double pixelSizeXYUm = core_.getPixelSizeUm();

            int numUniqueProcessorsNeeded = 2; // Always keep enough around for 2 views
            if (acqSettings_.isUsingChannels()) {
                numUniqueProcessorsNeeded *= acqSettings_.channels().length;
            }

            AcqEngJStackProcessors proc =
                  new AcqEngJStackProcessors(ObliqueStackProcessor.YX_PROJECTION, theta,
                        pixelSizeXYUm,
                        zStep, numZSlices, cameraWidth, cameraHeight, numUniqueProcessorsNeeded);
            currentAcquisition_.addImageProcessor(proc);
        }

        long acqButtonStart = System.currentTimeMillis();


        ////////////  Acquisition hooks ////////////////////
        // These functions will be run on different threads during the acquisition process
        //    Hooks will run on the Acquisition Engine thread--the one that controls all hardware

        // TODO add any code that needs to be executed on the acquisition thread (i.e. the one
        //  that controls hardware)

        // TODO: autofocus
        currentAcquisition_.addHook(new AcquisitionHook() {
            @Override
            public AcquisitionEvent run(AcquisitionEvent event) {
                // TODO: does the Tiger controller need to be cleared and/or checked for errors here?

                if (event.isAcquisitionFinishedEvent()) {
                    // Acquisition is finished, pass along event so things shut down properly
                    return event;
                }

                if (event.getMinimumStartTimeAbsolute() != null) {
                    nextWakeTime_ = event.getMinimumStartTimeAbsolute();
                }

                // Translate event to timeIndex/channel/etc
                AcquisitionEvent firstAcqEvent = event.getSequence().get(0);
                int timePoint = firstAcqEvent.getTIndex();

                ////////////////////////////////////
                ///////// Run autofocus ////////////
                ///////////////////////////////////

                // TODO: where should these come from? In diSPIM they appear to come from preferences,
                //  not settings...
                boolean doAutofocus = acqSettings_.isUsingAutofocus();

                boolean autofocusAtT0 = false;
                // TODO: this is where they come from in diSPIM?
//                prefs_.getBoolean(org.micromanager.asidispim.Data.MyStrings.PanelNames.AUTOFOCUS.toString(),
//                      org.micromanager.asidispim.Data.Properties.Keys.PLUGIN_AUTOFOCUS_ACQBEFORESTART, false);
                boolean autofocusEveryStagePass = false;
                boolean autofocusEachNFrames = false;
                boolean autofocusChannel = false;

                  // TODO: this is the diSPIM plugin's autofocus code, which needs to be reimplemented
                  //   and translated. There are also currently no autofocus related things in the acqSettings_
//                if (acqSettings_.isUsingAutofocus()) {
//                    // (Copied from diSPIM): Note that we will not autofocus as expected when using hardware
//                    // timing.  Seems OK, since hardware timing will result in short
//                    // acquisition times that do not need autofocus.
//                    if ( (autofocusAtT0 && timePoint == 0) || ( (timePoint > 0) &&
//                          (timePoint % autofocusEachNFrames == 0 ) ) ) {
//                        if (acqSettings.useChannels) {
//                            multiChannelPanel_.selectChannel(autofocusChannel);
//                        }
//                        if (sideActiveA) {
//                            if (acqSettings.usePathPresets) {
//                                controller_.setPathPreset(org.micromanager.asidispim.Data.Devices.Sides.A);
//                                // blocks until all devices done
//                            }
//                            org.micromanager.asidispim.Utils.AutofocusUtils.FocusResult score = autofocus_.runFocus(
//                                  this, org.micromanager.asidispim.Data.Devices.Sides.A, false,
//                                  sliceTiming_, false);
//                            updateCalibrationOffset(org.micromanager.asidispim.Data.Devices.Sides.A, score);
//                        }
//                        if (sideActiveB) {
//                            if (acqSettings.usePathPresets) {
//                                controller_.setPathPreset(org.micromanager.asidispim.Data.Devices.Sides.B);
//                                // blocks until all devices done
//                            }
//                            org.micromanager.asidispim.Utils.AutofocusUtils.FocusResult score = autofocus_.runFocus(
//                                  this, org.micromanager.asidispim.Data.Devices.Sides.B, false,
//                                  sliceTiming_, false);
//                            updateCalibrationOffset(org.micromanager.asidispim.Data.Devices.Sides.B, score);
//                        }
//                        // Restore settings of the controller
//                        controller_.prepareControllerForAquisition(acqSettings, extraChannelOffset_);
//                        if (acqSettings.useChannels && acqSettings.channelMode != org.micromanager.asidispim.Data.MultichannelModes.Keys.VOLUME) {
//                            controller_.setupHardwareChannelSwitching(acqSettings, hideErrors);
//                        }
//                    }
//                }

                return event;
            }

            @Override
            public void close() {

            }
        }, Acquisition.BEFORE_HARDWARE_HOOK);



        final PLogicDISPIM controllerInstance = controller;
        // TODO This after camera hook is called after the camera has been readied to acquire a
        //  sequence. I assume we want to tell the Tiger to start sending TTLs etc here
        currentAcquisition_.addHook(new AcquisitionHook() {
            @Override
            public AcquisitionEvent run(AcquisitionEvent event) {
                // TODO: Cameras are now ready to receive triggers, so we can send (software) trigger
                //  to the tiger to tell it to start outputting TTLs

                if (isUsingPLC && controllerInstance != null) { // if not in demo mode
                    int side = 0;
                    // TODO: enable 2 sided acquisition
                    controllerInstance.triggerControllerStartAcquisition(
                          asb_.acquisitionMode(), side);
                }
                return event;
            }

            @Override
            public void close() {

            }
        }, Acquisition.AFTER_CAMERA_HOOK);


        ///////////// Turn off autoshutter /////////////////
        final boolean isShutterOpen;
        try {
            isShutterOpen = core_.getShutterOpen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // TODO: should the shutter be left open for the full duration of acquisition?
        //  because that's what this code currently does
        boolean autoShutter = core_.getAutoShutter();
        if (autoShutter) {
            core_.setAutoShutter(false);
            if (!isShutterOpen) {
                try {
                    core_.setShutterOpen(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        currentAcquisition_.start();

        ////////////  Create and submit acquisition events ////////////////////
        // Create iterators of acquisition events and submit them to the engine for execution
        // The engine will (try to) automatically iterate over the AcquisitionEvents of each
        // iterator, but not over multiple iterators. So there should be one iterator submitted for
        // each expected triggering of the Tiger controller.

        // TODO: execute any start-acquisition runnables


        // Loop 1: XY positions
        PositionList pl = MMStudio.getInstance().positions().getPositionList();
        if (acqSettings_.isUsingMultiplePositions() && (pl.getNumberOfPositions() == 0)) {
            throw new RuntimeException("XY positions expected but position list is empty");
        }

        String[] cameraNames;
        if (demoMode) {
            ArrayList<String> cameraDeviceNames = new ArrayList<>();
            StrVector loadedDevices = core_.getLoadedDevices();
            for (int i = 0; i < loadedDevices.size(); i++) {
                try {
                    if (core_.getDeviceType(loadedDevices.get(i)).toString().equals("CameraDevice")) {
                        cameraDeviceNames.add(loadedDevices.get(i));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            if (cameraDeviceNames.size() < 2) {
                throw new RuntimeException("Need two cameras for diSPIM simulation");
            }
            cameraNames = cameraDeviceNames.toArray(new String[0]);
        } else {
            if (asb_.vsb().numViews() > 1) {
                cameraNames = new String[]{
                        model_.devices().getDevice("Imaging1Camera").getDeviceName(),
                        model_.devices().getDevice("Imaging2Camera").getDeviceName()
                };
            } else {
                cameraNames = new String[]{
                        model_.devices().getDevice("Imaging1Camera").getDeviceName()
                };
            }
        }

        for (int positionIndex = 0; positionIndex < (acqSettings_.isUsingMultiplePositions() ?
              pl.getNumberOfPositions() : 1); positionIndex++) {
            AcquisitionEvent baseEvent = new AcquisitionEvent(currentAcquisition_);
            if (acqSettings_.isUsingMultiplePositions()) {
                baseEvent.setAxisPosition(LSMAcquisitionEvents.POSITION_AXIS, positionIndex);
            }
            // TODO: what to do if multiple positions not defined: acquire at current stage position?
            //  If yes, then nothing more to do here.

            if (acqSettings_.isUsingHardwareTimePoints()) {
                // create a full iterator of TCZ acquisition events, and Tiger controller
                // will handle everything else
                if (acqSettings_.isUsingChannels()) {
                    currentAcquisition_.submitEventIterator(
                          LSMAcquisitionEvents.createTimelapseMultiChannelVolumeAcqEvents(
                                baseEvent.copy(), acqSettings_, cameraNames, null));
                } else {
                    currentAcquisition_.submitEventIterator(
                          LSMAcquisitionEvents.createTimelapseVolumeAcqEvents(
                                baseEvent.copy(), acqSettings_, cameraNames, null));
                }
            } else {
                // Loop 2: Multiple time points
                for (int timeIndex = 0; timeIndex < (acqSettings_.isUsingTimePoints() ?
                      acqSettings_.numTimePoints() : 1); timeIndex++) {
                    baseEvent.setTimeIndex(timeIndex);
                    // Loop 3: Channels; Loop 4: Z slices (non-interleaved)
                    // Loop 3: Channels; Loop 4: Z slices (interleaved)
                    if (acqSettings_.isUsingChannels()) {
                        currentAcquisition_.submitEventIterator(
                              LSMAcquisitionEvents.createMultiChannelVolumeAcqEvents(
                                    baseEvent.copy(), acqSettings_, cameraNames, null,
                                    acqSettings_.acquisitionMode() ==
                                          AcquisitionMode.STAGE_SCAN_INTERLEAVED));
                    } else {
                        currentAcquisition_.submitEventIterator(
                              LSMAcquisitionEvents.createVolumeAcqEvents(
                                    baseEvent.copy(), acqSettings_, cameraNames, null));
                    }
                }
            }
        }



        // No more instructions (i.e. AcquisitionEvents); tell the acquisition to initiate shutdown
        // once everything finishes
        currentAcquisition_.finish();


        currentAcquisition_.waitForCompletion();

        // cleanup
        studio_.logs().logMessage("diSPIM plugin acquisition " +
              " took: " + (System.currentTimeMillis() - acqButtonStart) + "ms");


        // clean up controller settings after acquisition
        // want to do this, even with demo cameras, so we can test everything else
        // TODO: figure out if we really want to return piezos to 0 position (maybe center position,
        //   maybe not at all since we move when we switch to setup tab, something else??)
        if (isUsingPLC && controller != null) {
            controller.cleanUpControllerAfterAcquisition(acqSettings_, true);
        }

        // Restore shutter/autoshutter to original state
        try {
            core_.setShutterOpen(isShutterOpen);
            core_.setAutoShutter(autoShutter);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't restore shutter to original state");
        }

        // Check if acquisition ended due to an exception and show error to user if it did
        try {
            currentAcquisition_.checkForExceptions();
        } catch (Exception e){
            studio_.logs().showError(e);
        }

        // TODO: execute any end-acquisition runnables

        currentAcquisition_ = null;
    }

    private void stopSPIMStateMachines(DefaultAcquisitionSettingsDISPIM acqSettings) {
        final int numViews = acqSettings_.volumeSettings().numViews();
        if (numViews == 1) {
            ASIScanner scanner = model_.devices().getDevice("IllumBeam");
            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);
        }
        for (int i = 1; i <= numViews; i++) {
            ASIScanner scanner = model_.devices().getDevice("Illum" + i + "Beam");
            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);
        }
        // TODO: ASI stage scanning conditionals
    }

    public void recalculateSliceTiming(DefaultAcquisitionSettingsDISPIM.Builder asb) {
        // don't change timing settings if user is using advanced timing
        if (asb.isUsingAdvancedTiming()) {
            return;
        }
        // TODO: update builder here
        DefaultTimingSettings.Builder tsb = getTimingFromPeriodAndLightExposure(asb);
        asb_.timingSettingsBuilder(tsb);
        //acqSettings.timingSettings(getTimingFromPeriodAndLightExposure(acqSettings));
        // TODO: update gui (but not in the model)
    }

    public DefaultTimingSettings.Builder getTimingFromPeriodAndLightExposure(DefaultAcquisitionSettingsDISPIM.Builder asb) {
        // uses algorithm Jon worked out in Octave code; each slice period goes like this:
        // 1. camera readout time (none if in overlap mode, 0.25ms in pseudo-overlap)
        // 2. any extra delay time
        // 3. camera reset time
        // 4. start scan 0.25ms before camera global exposure and shifted up in time to account for delay introduced by Bessel filter
        // 5. turn on laser as soon as camera global exposure, leave laser on for desired light exposure time
        // 7. end camera exposure in final 0.25ms, post-filter scan waveform also ends now
        ASIScanner scanner1 = model_.devices().getDevice("Illum1Beam");
        ASIScanner scanner2 = model_.devices().getDevice("Illum2Beam");

        CameraBase camera = model_.devices().getDevice("Imaging1Camera"); //.getImagingCamera(0);
        if (camera == null) {
            // just a dummy to test demo mode
            return new DefaultTimingSettings.Builder();
        }
        // TODO: do this in ui?
        camera.setTriggerMode(asb.cameraMode());

        System.out.println(camera.getDeviceName());
        CameraMode camMode = camera.getTriggerMode();
        System.out.println(camMode);

        DefaultTimingSettings.Builder tsb = new DefaultTimingSettings.Builder();

        final float scanLaserBufferTime = NumberUtils.roundToQuarterMs(0.25f);  // below assumed to be multiple of 0.25ms

        final float cameraResetTime = camera.getResetTime(camMode);      // recalculate for safety, 0 for light sheet
        final float cameraReadoutTime = camera.getReadoutTime(camMode);  // recalculate for safety, 0 for overlap

        final float cameraReadoutMax = NumberUtils.ceilToQuarterMs(cameraReadoutTime);
        final float cameraResetMax = NumberUtils.ceilToQuarterMs(cameraResetTime);

        // we will wait cameraReadoutMax before triggering camera, then wait another cameraResetMax for global exposure
        // this will also be in 0.25ms increment
        final float globalExposureDelayMax = cameraReadoutMax + cameraResetMax;
        float laserDuration = NumberUtils.roundToQuarterMs((float)asb.sliceSettingsBuilder().sampleExposure());
        float scanDuration = laserDuration + 2*scanLaserBufferTime;
        // scan will be longer than laser by 0.25ms at both start and end


        // account for delay in scan position due to Bessel filter by starting the scan slightly earlier
        // than we otherwise would (Bessel filter selected b/c stretches out pulse without any ripples)
        // delay to start is (empirically) 0.07ms + 0.25/(freq in kHz)
        // delay to midpoint is empirically 0.38/(freq in kHz)
        // group delay for 5th-order bessel filter ~0.39/freq from theory and ~0.4/freq from IC datasheet
        final float scanFilterFreq = Math.max(scanner1.getFilterFreqX(), scanner2.getFilterFreqX());

        float scanDelayFilter = 0;
        if (scanFilterFreq != 0) {
            scanDelayFilter = NumberUtils.roundToQuarterMs(0.39f/scanFilterFreq);
        }
        // If the PLogic card is used, account for 0.25ms delay it introduces to
        // the camera and laser trigger signals => subtract 0.25ms from the scanner delay
        // (start scanner 0.25ms later than it would be otherwise)
        // this time-shift opposes the Bessel filter delay
        // scanDelayFilter won't be negative unless scanFilterFreq is more than 3kHz which shouldn't happen
        // TODO: only do this when PLC exists
        scanDelayFilter -= 0.25f;

        float delayBeforeScan = globalExposureDelayMax - scanLaserBufferTime   // start scan 0.25ms before camera's global exposure
                - scanDelayFilter; // start galvo moving early due to card's Bessel filter and delay of TTL signals via PLC
        float delayBeforeLaser = globalExposureDelayMax; // turn on laser as soon as camera's global exposure is reached
        float delayBeforeCamera = cameraReadoutMax; // camera must read out last frame before triggering again
        int scansPerSlice = 1;

        float cameraDuration = 0; // set in the switch statement below
        double sliceDuration = 0;

        // figure out desired time for camera to be exposing (including reset time)
        // because both camera trigger and laser on occur on 0.25ms intervals (i.e. we may not
        //    trigger the laser until 0.24ms after global exposure) use cameraReset_max
        // special adjustment for Photometrics cameras that possibly has extra clear time which is counted in reset time
        //    but not in the camera exposure time
        // TODO: skipped PVCAM case, update comment
        float cameraExposure = NumberUtils.ceilToQuarterMs(cameraResetTime) + laserDuration;

        switch (asb.cameraMode()) {
            case EDGE:
                cameraDuration = 1;  // doesn't really matter, 1ms should be plenty fast yet easy to see for debugging
                cameraExposure += 0.1f; // add 0.1ms as safety margin, may require adding an additional 0.25ms to slice
                // slight delay between trigger and actual exposure start
                //   is included in exposure time for Hamamatsu and negligible for Andor and PCO cameras
                // ensure not to miss triggers by not being done with readout in time for next trigger, add 0.25ms if needed
                sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
                if (sliceDuration < (cameraExposure + cameraReadoutTime)) {
                    delayBeforeCamera += 0.25f;
                    delayBeforeLaser += 0.25;
                    delayBeforeScan += 0.25f;
                }
                break;
            case LEVEL: // AKA "bulb mode", TTL rising starts exposure, TTL falling ends it
                cameraDuration = NumberUtils.ceilToQuarterMs(cameraExposure);
                cameraExposure = 1; // doesn't really matter, controlled by TTL
                break;
            case OVERLAP: // only Hamamatsu or Andor
                cameraDuration = 1;  // doesn't really matter, 1ms should be plenty fast yet easy to see for debugging
                cameraExposure = 1;  // doesn't really matter, controlled by interval between triggers
                break;
            case PSEUDO_OVERLAP:// PCO or Photometrics, enforce 0.25ms between end exposure and start of next exposure by triggering camera 0.25ms into the slice
                cameraDuration = 1;  // doesn't really matter, 1ms should be plenty fast yet easy to see for debugging
                // TODO: not dealing with PVCAM (maybe throw error on unknown cam lib)
                sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
                cameraExposure = (float)sliceDuration - delayBeforeCamera;  // s.cameraDelay should be 0.25ms for PCO
                if (cameraReadoutMax < 0.24f) {
                    studio_.logs().showError("Camera delay should be at least 0.25ms for pseudo-overlap mode.");
                }
                break;
            case VIRTUAL_SLIT:
                // each slice period goes like this:
                // 1. scan reset time (use to add any extra settling time to the start of each slice)
                // 2. start scan, wait scan settle time
                // 3. trigger camera/laser when scan settle time elapses
                // 4. scan for total of exposure time plus readout time (total time some row is exposing) plus settle time plus extra 0.25ms to prevent artifacts
                // 5. laser turns on 0.25ms before camera trigger and stays on until exposure is ending
                // TODO revisit this after further experimentation
                cameraDuration = 1;  // only need to trigger camera
                final float shutterWidth = (float) asb.sliceSettingsLSBuilder().shutterWidth();
                final float shutterSpeed = (float) asb.sliceSettingsLSBuilder().shutterSpeedFactor();
                ///final float shutterWidth = props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_WIDTH);
                //final int shutterSpeed = props_.getPropValueInteger(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SHUTTER_SPEED);
                float pixelSize = (float) core_.getPixelSizeUm();
                if (pixelSize < 1e-6) {  // can't compare equality directly with floating point values so call < 1e-9 is zero or negative
                    pixelSize = 0.1625f;  // default to pixel size of 40x with sCMOS = 6.5um/40
                }
                final double rowReadoutTime = camera.getRowReadoutTime();
                cameraExposure = (float)(rowReadoutTime * (int)(shutterWidth/pixelSize) * shutterSpeed);
                // s.cameraExposure = (float) (rowReadoutTime * shutterWidth / pixelSize * shutterSpeed);
                final float totalExposureMax = NumberUtils.ceilToQuarterMs(cameraReadoutTime + cameraExposure + 0.05f);  // 50-300us extra cushion time
                final float scanSettle = (float) asb.sliceSettingsLSBuilder().scanSettleTime();
                final float scanReset = (float) asb.sliceSettingsLSBuilder().scanResetTime();
                delayBeforeScan = scanReset - scanDelayFilter;
                scanDuration = scanSettle + (totalExposureMax*shutterSpeed) + scanLaserBufferTime;
                delayBeforeCamera = scanReset + scanSettle;
                delayBeforeLaser = delayBeforeCamera - scanLaserBufferTime; // trigger laser just before camera to make sure it's on already
                laserDuration = (totalExposureMax*shutterSpeed) + scanLaserBufferTime; // laser will turn off as exposure is ending
                break;
            case INTERNAL: // case fall through
            default:
                studio_.logs().showError("Invalid camera mode");
                // FIXME: set to invalid!
                break;
        }

        // fix corner case of negative calculated scanDelay
        if (delayBeforeScan < 0) {
            delayBeforeCamera-= delayBeforeScan;
            delayBeforeLaser -= delayBeforeScan;
            delayBeforeScan = 0;  // same as (-= delayBeforeScan)
        }

        // fix corner case of (exposure time + readout time) being greater than the slice duration
        // most of the time the slice duration is already larger
        sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
        float globalDelay = NumberUtils.ceilToQuarterMs((cameraExposure + cameraReadoutTime) - (float)sliceDuration);
        if (globalDelay > 0) {
            delayBeforeCamera += globalDelay;
            delayBeforeLaser += globalDelay;
            delayBeforeScan += globalDelay;
        }

        // update the slice duration based on our new values
        sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);

        tsb.scansPerSlice(scansPerSlice);
        tsb.scanDuration(scanDuration);
        tsb.cameraExposure(cameraExposure);
        tsb.laserTriggerDuration(laserDuration);
        tsb.cameraTriggerDuration(cameraDuration);
        tsb.delayBeforeCamera(delayBeforeCamera);
        tsb.delayBeforeLaser(delayBeforeLaser);
        tsb.delayBeforeScan(delayBeforeScan);
        tsb.sliceDuration(sliceDuration);
        return tsb;
    }

    public double getSliceDuration(
            final double delayBeforeScan,
            final double scanDuration,
            final double scansPerSlice,
            final double delayBeforeLaser,
            final double laserDuration,
            final double delayBeforeCamera,
            final double cameraDuration) {
        // slice duration is the max out of the scan time, laser time, and camera time
        return Math.max(Math.max(
                delayBeforeScan + (scanDuration * scansPerSlice),   // scan time
                delayBeforeLaser + laserDuration                    // laser time
            ),
            delayBeforeCamera + cameraDuration                      // camera time
        );
    }

    public double getSliceDuration(DefaultTimingSettings.Builder tsb) {
        DefaultTimingSettings s = tsb.build();
        // slice duration is the max out of the scan time, laser time, and camera time
        return Math.max(Math.max(
                        s.delayBeforeScan() + (s.scanDuration() * s.scansPerSlice()),   // scan time
                        s.delayBeforeLaser() + s.laserTriggerDuration()                 // laser time
                ),
                s.delayBeforeCamera() + s.cameraTriggerDuration()                      // camera time
        );
    }

    /**
     * Compute the volume duration in ms based on controller's timing settings.
     * Includes time for multiple channels.  However, does not include for multiple positions.
     * @param acqSettings Settings for the acquisition
     * @return duration in ms
     */
    public double computeActualVolumeDuration(final DefaultAcquisitionSettingsDISPIM acqSettings) {
        final MultiChannelMode channelMode = acqSettings.channelMode();
        final int numChannels = acqSettings.numChannels();
        final int numViews = acqSettings.volumeSettings().numViews();
        final double delayBeforeSide = acqSettings.volumeSettings().delayBeforeView();
        int numCameraTriggers = acqSettings.volumeSettings().slicesPerView();
        if (acqSettings.cameraMode() == CameraMode.OVERLAP) {
            numCameraTriggers += 1;
        }
        // stackDuration is per-side, per-channel, per-position

        final double stackDuration = numCameraTriggers * acqSettings.timingSettings().sliceDuration();
        if (acqSettings.isUsingStageScanning()) { // || acqSettings.isStageStepping) {
            // TODO: stage scanning code
            return 0;
        } else {
            // piezo scan
            double channelSwitchDelay = 0;
            if (channelMode == MultiChannelMode.VOLUME) {
                channelSwitchDelay = 500;   // estimate channel switching overhead time as 0.5s
                // actual value will be hardware-dependent
            }
            if (channelMode == MultiChannelMode.SLICE_HW) {
                return numViews * (delayBeforeSide + stackDuration * numChannels);  // channelSwitchDelay = 0
            } else {
                return numViews * numChannels
                        * (delayBeforeSide + stackDuration)
                        + (numChannels - 1) * channelSwitchDelay;
            }
        }
    }

    /**
     * Compute the time point duration in ms.  Only difference from computeActualVolumeDuration()
     * is that it also takes into account the multiple positions, if any.
     * @return duration in ms
     */
    private double computeTimePointDuration() {
        final double volumeDuration = computeActualVolumeDuration(acqSettings_);
        if (acqSettings_.isUsingMultiplePositions()) {
            // use 1.5 seconds motor move between positions
            // (could be wildly off but was estimated using actual system
            // and then slightly padded to be conservative to avoid errors
            // where positions aren't completed in time for next position)
            // could estimate the actual time by analyzing the position's relative locations
            //   and using the motor speed and acceleration time
            return studio_.positions().getPositionList().getNumberOfPositions() *
                    (volumeDuration + 1500 + acqSettings_.postMoveDelay());
        }
        return volumeDuration;
    }

    public void updateAcquisitionStatus() {

    }

    // TODO: keep long method name versions or just use aliases?

    public DefaultAcquisitionSettingsDISPIM settings() {
        return acqSettings_;
    }

    public DefaultAcquisitionSettingsDISPIM getAcquisitionSettings() {
        return acqSettings_;
    }

    public DefaultAcquisitionSettingsDISPIM.Builder settingsBuilder() {
        return asb_;
    }

    public DefaultAcquisitionSettingsDISPIM.Builder getAcquisitionSettingsBuilder() {
        return asb_;
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
