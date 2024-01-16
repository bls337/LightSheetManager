package org.micromanager.lightsheetmanager.model.acquisitions;

import mmcorej.StrVector;
import mmcorej.org.json.JSONObject;
import org.micromanager.MultiStagePosition;
import org.micromanager.PositionList;
import org.micromanager.acqj.api.AcquisitionHook;
import org.micromanager.acqj.main.Acquisition;
import org.micromanager.acqj.main.AcquisitionEvent;
import org.micromanager.acquisition.SequenceSettings;
import org.micromanager.acquisition.internal.MMAcquisition;
import org.micromanager.acquisition.internal.acqengjcompat.AcqEngJAdapter;
import org.micromanager.acquisition.internal.acqengjcompat.AcqEngJMDADataSink;
import org.micromanager.data.Datastore;
import org.micromanager.data.internal.DefaultDatastore;
import org.micromanager.data.internal.DefaultSummaryMetadata;
import org.micromanager.internal.MMStudio;
import org.micromanager.lightsheetmanager.api.data.CameraLibrary;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.data.MultiChannelMode;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.model.DataStorage;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.PLogicSCAPE;
import org.micromanager.lightsheetmanager.model.devices.NIDAQ;
import org.micromanager.lightsheetmanager.model.devices.cameras.AndorCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;
import org.micromanager.lightsheetmanager.model.devices.cameras.DemoCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.HamamatsuCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.PCOCamera;
import org.micromanager.lightsheetmanager.model.devices.cameras.PVCamera;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.utils.FileUtils;
import org.micromanager.lightsheetmanager.model.utils.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;

public class AcquisitionEngineSCAPE extends AcquisitionEngine {

//    private DefaultAcquisitionSettingsDISPIM.Builder asb_;
//    private DefaultAcquisitionSettingsDISPIM acqSettings_;

    public AcquisitionEngineSCAPE(final LightSheetManagerModel model) {
        super(model);
    }

    @Override
    boolean setup() {
        return true;
    }

    @Override
    boolean run() {
        // TODO: delete later, this is the settings before everything is set up in doHardwareCalculations (used to debug)
        studio_.logs().logMessage("debug info:\n" + acqSettings_.toPrettyJson());

        final boolean isPolling = frame_.getNavigationPanel().isPolling();
        if (isPolling) {
            studio_.logs().logMessage("stopped position polling");
            frame_.getNavigationPanel().stopPolling();
        }

        // TODO: remove this when acqSettings for SCAPE are implemented
        asb_.useAdvancedTiming(true);

        asb_.sheetCalibrationBuilder(1).useAutoSheetWidth(true);
        asb_.sheetCalibrationBuilder(1).autoSheetWidthPerPixel(0.0);

        // set acquisition settings from the builder
        setAcquisitionSettings(asb_.build());

        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }

        final boolean isUsingPLC = model_.devices().isUsingPLogic();

        PLogicSCAPE controller = null;

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
                controller = new PLogicSCAPE(model_);

                final boolean success = doHardwareCalculations(controller);
                if (!success) {
                    return false; // early exit => could not set up hardware
                }
            } else {
                doHardwareCalculationsNIDAQ();
            }
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

        studio_.logs().logMessage("Starting Acquisition with settings:\n" + acqSettings_.toPrettyJson());

        String saveDir = acqSettings_.saveDirectory();
        String saveName = acqSettings_.saveNamePrefix();

        // This sets the preferred save mode for DefaultDatastore, this value
        // is used in the MMAcquisition constructor to set the Storage object.
        if (acqSettings_.saveMode() == DataStorage.SaveMode.NDTIFF) {
            DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.ND_TIFF);
        } else if (acqSettings_.saveMode() == DataStorage.SaveMode.MULTIPAGE_TIFF) {
            DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.MULTIPAGE_TIFF);
        } else if (acqSettings_.saveMode() == DataStorage.SaveMode.SINGLEPLANE_TIFF_SERIES) {
            DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.SINGLEPLANE_TIFF_SERIES);
        } else {
            studio_.logs().showError("Unsupported save mode: " + acqSettings_.saveMode());
            return false;
        }

        // TODO: once saving NDTiff images works, delete this line, always set to MULTIPAGE_TIFF for now
        DefaultDatastore.setPreferredSaveMode(studio_, Datastore.SaveMode.MULTIPAGE_TIFF);


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
                // TODO: add later when autofocus is complete, prevent errors if no time index is found for now
                //int timePoint = firstAcqEvent.getTIndex();

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
                //   and translated.
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



        final PLogicSCAPE controllerInstance = controller;
        // TODO This after camera hook is called after the camera has been readied to acquire a
        //  sequence. I assume we want to tell the Tiger to start sending TTLs etc here
        currentAcquisition_.addHook(new AcquisitionHook() {
            @Override
            public AcquisitionEvent run(AcquisitionEvent event) {
                // TODO: Cameras are now ready to receive triggers, so we can send (software) trigger
                //  to the tiger to tell it to start outputting TTLs

                if (isUsingPLC && controllerInstance != null) { // if not in demo mode
                    // TODO: is this the best place to set state to idle?
                    ASIScanner scanner = model_.devices().getDevice("IllumSlice");
                    // need to set to IDLE to re-arm for each z-stack
                    if (!acqSettings_.isUsingHardwareTimePoints()) {
                        if (scanner.getSPIMState().equals(ASIScanner.SPIMState.RUNNING)) {
                            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);
                        }
                    }
                    int side = 0;
                    // NOTE: not sure why this is being triggered twice with only 1 camera; so we need guard
                    // TODO: enable 2 sided acquisition
                    if (scanner.getSPIMState().equals(ASIScanner.SPIMState.IDLE)) {
                        controllerInstance.triggerControllerStartAcquisition(acqSettings_.acquisitionMode(), side);
                    }
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
            }
            cameraNames = cameraDeviceNames.toArray(new String[0]);
        } else {
            if (acqSettings_.volumeSettings().numViews() > 1) {
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

        if (acqSettings_.isUsingHardwareTimePoints()) {
            AcquisitionEvent baseEvent = new AcquisitionEvent(currentAcquisition_);
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

            final int numPositions = acqSettings_.isUsingMultiplePositions() ? pl.getNumberOfPositions() : 1;
            final int numTimePoints = acqSettings_.isUsingTimePoints() ? acqSettings_.numTimePoints() : 1;

            // Loop 1: Multiple time points
            for (int timeIndex = 0; timeIndex < numTimePoints; timeIndex++) {
                //System.out.println("time index: " + timeIndex);
                AcquisitionEvent baseEvent = new AcquisitionEvent(currentAcquisition_);
                if (acqSettings_.isUsingTimePoints()) {
                    baseEvent.setAxisPosition(LSMAcquisitionEvents.TIME_AXIS, timeIndex);
                }
                // Loop 2: XY positions
                for (int positionIndex = 0; positionIndex < numPositions; positionIndex++) {
                    //System.out.println("pos index: " + positionIndex);
                    if (acqSettings_.isUsingMultiplePositions()) {
                        baseEvent.setAxisPosition(LSMAcquisitionEvents.POSITION_AXIS, positionIndex);
                        // is this the best way to do stage movements with new acq engine?
                        MultiStagePosition position = pl.getPosition(positionIndex);
                        baseEvent.setX(position.getX());
                        baseEvent.setY(position.getY());
                    }
                    // TODO: what to do if multiple positions not defined: acquire at current stage position?
                    //  If yes, then nothing more to do here.

                    // Loop 3: Channels; Loop 4: Z slices
                    if (acqSettings_.isUsingChannels()) {
                        currentAcquisition_.submitEventIterator(
                                LSMAcquisitionEvents.createChannelAcqEvents(
                                        baseEvent.copy(), acqSettings_, cameraNames, null));
                    } else {
                        currentAcquisition_.submitEventIterator(
                                LSMAcquisitionEvents.createAcqEvents(
                                        baseEvent.copy(), acqSettings_, cameraNames, null));
                    }
                }
            }

        }

//            for (int positionIndex = 0; positionIndex < numPositions; positionIndex++) {
//                AcquisitionEvent baseEvent = new AcquisitionEvent(currentAcquisition_);
//                if (acqSettings_.isUsingMultiplePositions()) {
//                    baseEvent.setAxisPosition(LSMAcquisitionEvents.POSITION_AXIS, positionIndex);
//                    // is this the best way to do stage movements with new acq engine?
//                    MultiStagePosition position = pl.getPosition(positionIndex);
//                    baseEvent.setX(position.getX());
//                    baseEvent.setY(position.getY());
//                }
//                // TODO: what to do if multiple positions not defined: acquire at current stage position?
//                //  If yes, then nothing more to do here.
//
//                if (acqSettings_.isUsingHardwareTimePoints()) {
//                    // create a full iterator of TCZ acquisition events, and Tiger controller
//                    // will handle everything else
//                    if (acqSettings_.isUsingChannels()) {
//                        currentAcquisition_.submitEventIterator(
//                                LSMAcquisitionEvents.createTimelapseMultiChannelVolumeAcqEvents(
//                                        baseEvent.copy(), acqSettings_, cameraNames, null));
//                    } else {
//                        currentAcquisition_.submitEventIterator(
//                                LSMAcquisitionEvents.createTimelapseVolumeAcqEvents(
//                                        baseEvent.copy(), acqSettings_, cameraNames, null));
//                    }
//                } else {
//                    // Loop 2: Multiple time points
//                    for (int timeIndex = 0; timeIndex < numTimePoints; timeIndex++) {
//                        baseEvent.setTimeIndex(timeIndex);
//                        // Loop 3: Channels; Loop 4: Z slices (non-interleaved)
//                        // Loop 3: Channels; Loop 4: Z slices (interleaved)
//                        if (acqSettings_.isUsingChannels()) {
//                            currentAcquisition_.submitEventIterator(
//                                    LSMAcquisitionEvents.createMultiChannelVolumeAcqEvents(
//                                            baseEvent.copy(), acqSettings_, cameraNames, null,
//                                            acqSettings_.acquisitionMode() ==
//                                                    AcquisitionMode.STAGE_SCAN_INTERLEAVED));
//                        } else {
//                            currentAcquisition_.submitEventIterator(
//                                    LSMAcquisitionEvents.createVolumeAcqEvents(
//                                            baseEvent.copy(), acqSettings_, cameraNames, null));
//                        }
//                    }
//                }
//            }



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
            controller.stopSPIMStateMachines();
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
        } catch (Exception e) {
            studio_.logs().showError(e);
        }

        // TODO: execute any end-acquisition runnables

        // set the camera trigger mode back to internal for live mode
        CameraBase camera = model_.devices().getDevice("ImagingCamera");
        camera.setTriggerMode(CameraMode.INTERNAL);

        currentAcquisition_ = null;

        if (acqSettings_.isSavingImagesDuringAcquisition()) {
            final String savePath = FileUtils.createUniquePath(saveDir, saveName);
            //System.out.println("savePath: " + savePath);
            try {
                curStore_.save(Datastore.SaveMode.MULTIPAGE_TIFF, savePath);
            } catch (IOException e) {
                model_.studio().logs().showError("could not save the acquisition data to: \n" + savePath);
            }
        }

        // start polling for navigation panel
        if (isPolling) {
            studio_.logs().logMessage("started position polling after acquisition");
            frame_.getNavigationPanel().startPolling();
        }
        return true;
    }

    @Override
    boolean finish() {
        return true;
    }

    private boolean doHardwareCalculations(PLogicSCAPE plc) {

        // make sure slice timings are up-to-date
        final double sliceDuration = getSliceDuration(asb_.timingSettingsBuilder());
        asb_.timingSettingsBuilder().sliceDuration(sliceDuration);

        //studio_.logs().logMessage("TimingSettings sliceDuration: " + sliceDuration1);
        System.out.println("computed sliceDuration: " +  sliceDuration);

        recalculateSliceTiming();
        System.out.println("after recalculateSliceTiming: " + asb_.timingSettingsBuilder());


        // TODO: was only checked in light sheet mode
//        if (core_.getPixelSizeUm() < 1e-6) {
//            studio_.logs().showError("Need to set the pixel size in Micro-Manager.");
//        }

        // setup channels
        int nrChannelsSoftware = acqSettings_.numChannels();  // how many times we trigger the controller per stack
        int nrSlicesSoftware = acqSettings_.volumeSettings().slicesPerView();
        //acqSettings_.volumeSettings().slicesPerView();
        // TODO: channels need to modify panels and need extraChannelOffset_
        boolean changeChannelPerVolumeSoftware = false;
        boolean changeChannelPerVolumeDoneFirst = false;
        if (acqSettings_.isUsingChannels()) {
            if (acqSettings_.numChannels() == 0) {
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
                    nrSlicesSoftware = acqSettings_.volumeSettings().slicesPerView() * acqSettings_.numChannels();
                    break;
                default:
                    studio_.logs().showError(
                            "Unsupported multichannel mode \"" + acqSettings_.channelMode().toString() + "\"");
                    return false; // early exit
            }
        }
        // TODO: code that doubles nrSlicesSoftware if (twoSided && acqBothCameras) missing

        // TODO: maybe wrap this up into a method for clarity
        float cameraReadoutTime;
        final CameraLibrary cameraLibrary = CameraLibrary.fromString(
                model_.devices().getDeviceLibrary("ImagingCamera"));
        switch (cameraLibrary) {
            case HAMAMATSU: {
                HamamatsuCamera camera = model_.devices().getDevice("ImagingCamera");
                cameraReadoutTime = camera.getReadoutTime(acqSettings_.cameraMode());
                break;
            }
            case PVCAM: {
                PVCamera camera = model_.devices().getDevice("ImagingCamera");
                cameraReadoutTime = camera.getReadoutTime(acqSettings_.cameraMode());
                break;
            }
            case PCOCAMERA: {
                PCOCamera camera = model_.devices().getDevice("ImagingCamera");
                cameraReadoutTime = camera.getReadoutTime(acqSettings_.cameraMode());
                break;
            }
            case ANDORSDK3: {
                AndorCamera camera = model_.devices().getDevice("ImagingCamera");
                cameraReadoutTime = camera.getReadoutTime(acqSettings_.cameraMode());
                break;
            }
            case DEMOCAMERA: {
                DemoCamera camera = model_.devices().getDevice("ImagingCamera");
                cameraReadoutTime = camera.getReadoutTime(acqSettings_.cameraMode());
                break;
            }
            default:
                CameraBase camera = model_.devices().getDevice("ImagingCamera");
                cameraReadoutTime = camera.getReadoutTime(acqSettings_.cameraMode());
                break;
        }
        final double exposureTime = acqSettings_.timingSettings().cameraExposure();

        // test acq was here

        double volumeDuration = computeActualVolumeDuration(acqSettings_);
        double timepointDuration = computeTimePointDuration();
        long timepointIntervalMs = Math.round(acqSettings_.timePointInterval()*1000.0f);

        // use hardware timing if < 1 second between time points
        // experimentally need ~0.5 sec to set up acquisition, this gives a bit of cushion
        // cannot do this in getCurrentAcquisitionSettings because of mutually recursive
        // call with computeActualVolumeDuration()
        boolean isUsingHardwareTimePoints = false; // TODO: asb_ not built yet
        if (acqSettings_.isUsingTimePoints()
                && acqSettings_.numTimePoints() > 1
                && timepointIntervalMs < (timepointDuration + 750)
                && !acqSettings_.isUsingStageScanning()) {
            asb_.useHardwareTimePoints(true);
            isUsingHardwareTimePoints = true;
        }

        // TODO: implement multiple positions using hardware time points, currently
        //  set hardware time points to false if using multiple positions
        if (acqSettings_.isUsingMultiplePositions()) {
            if (acqSettings_.isUsingHardwareTimePoints()) {
//                    || acqSettings_.numTimePoints() > 1)
//                    && (timepointIntervalMs < timepointDuration*1.2)) {
                asb_.useHardwareTimePoints(false);
//                studio_.logs().showError("Time point interval may not be sufficient "
//                        + "depending on actual time required to change positions. "
//                        + "Proceed at your own risk.");
            }
        }

        // only use hardware time points when use time points is checked
        if (acqSettings_.isUsingHardwareTimePoints()) {
            if (!acqSettings_.isUsingTimePoints()) {
                asb_.useHardwareTimePoints(false);
            }
        }

        // TODO: this is not necessary below
//        if (acqSettings_.isUsingHardwareTimePoints()) {
//            final int numTimePoints = acqSettings_.numTimePoints();
//            final int numChannels = acqSettings_.numChannels();
//            final int slicePerView = acqSettings_.volumeSettings().slicesPerView();
//            // in hardwareTimepoints case we trigger controller once for all timepoints => need to
//            //   adjust number of frames we expect back from the camera during MM's SequenceAcquisition
//            if (acqSettings_.cameraMode() == CameraMode.OVERLAP) {
//                // For overlap mode we are send one extra trigger per channel per side for volume-switching (both PLogic and not)
//                // This holds for all multichannel modes, just the order in which the extra trigger comes varies
//                // Very last trigger won't ever return a frame so subtract 1.
//                final int hardwareSlicesPerView = (slicePerView + 1) * numChannels * numTimePoints;
//                asb_.volumeSettingsBuilder().slicesPerView(hardwareSlicesPerView - 1);
//            } else {
//                asb_.volumeSettingsBuilder().slicesPerView(slicePerView * numTimePoints);
//            }
//        }

        // sliceDuration is computed at start of this method and put into the AcquisitionSettingsBuilder
        // because the asb is not built before this check, use the value computed above
        if (exposureTime + cameraReadoutTime > sliceDuration) {
            // should only possible to mess this up using advanced timing settings
            // or if there are errors in our own calculations
            studio_.logs().showError("Exposure time of " + exposureTime +
                    " is longer than time needed for a line scan with" +
                    " readout time of " + cameraReadoutTime + "\n" +
                    "This will result in dropped frames. " +
                    "Please change input. " +
                    "Formula: (" + exposureTime + " + " + cameraReadoutTime + ") > " + sliceDuration);
            return false; // early exit
        }

        // must use PLogic for channels when using hardware time points
        if (isUsingHardwareTimePoints) {
            if (acqSettings_.isUsingChannels() && acqSettings_.channelMode() == MultiChannelMode.VOLUME) {
                studio_.logs().showError("Cannot use hardware time points (small time point interval) " +
                        "with software channels (need to use PLogic channel switching).");
                return false;
            }
        }

        double extraChannelOffset = 0.0;
        plc.prepareControllerForAcquisitionSCAPE(acqSettings_, extraChannelOffset);
        return true;
    }

    private void doHardwareCalculationsNIDAQ() {
        NIDAQ daq = model_.devices().getDevice("TriggerCamera");

        //daq.setProperty("PropertyName", "1");
    }

    public void recalculateSliceTiming() {
        // TODO: find a better place to set the camera trigger mode for SCAPE
        CameraBase camera = model_.devices().getDevice("ImagingCamera");
        camera.setTriggerMode(acqSettings_.cameraMode());
        studio_.logs().logMessage("camera \"" + camera.getDeviceName()
                + "\" set to mode: " + camera.getTriggerMode());
        // update timing settings if not using advanced timing
        if (!acqSettings_.isUsingAdvancedTiming()) {
            DefaultTimingSettings.Builder tsb = getTimingFromExposure();
            asb_.timingSettingsBuilder(tsb);
        }
    }

    public DefaultTimingSettings.Builder getTimingFromExposure() {
        DefaultTimingSettings.Builder tsb = new DefaultTimingSettings.Builder();

        final double desiredExposure =
                model_.acquisitions().settings().sliceSettings().sampleExposure();

        tsb.scansPerSlice(1);
        tsb.scanDuration(0.25);
        tsb.cameraExposure(desiredExposure);
        tsb.laserTriggerDuration(desiredExposure);
        tsb.cameraTriggerDuration(1);
        tsb.delayBeforeCamera(0);
        tsb.delayBeforeLaser(0.25); // TODO: experiment with 0
        tsb.delayBeforeScan(0.25);  // TODO: experiment with 0
        //tsb.sliceDuration(desiredExposure); // FIXME: where does this get calculated?
        return tsb;
    }

    public DefaultTimingSettings.Builder getTimingFromPeriodAndLightExposure() {
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
        camera.setTriggerMode(acqSettings_.cameraMode());

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
        float laserDuration = NumberUtils.roundToQuarterMs((float)acqSettings_.sliceSettings().sampleExposure());
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
        // TODO: skipped PVCAM case, this should already be handled by camera.getResetTime(camMode); but there may be differences

        float cameraExposure = NumberUtils.ceilToQuarterMs(cameraResetTime) + laserDuration;

        switch (acqSettings_.cameraMode()) {
            case EDGE:
                cameraDuration = 1;  // doesn't really matter, 1ms should be plenty fast yet easy to see for debugging
                cameraExposure += 0.1f; // add 0.1ms as safety margin, may require adding an additional 0.25ms to slice
                // slight delay between trigger and actual exposure start
                //   is included in exposure time for Hamamatsu and negligible for Andor and PCO cameras
                // ensure not to miss triggers by not being done with readout in time for next trigger, add 0.25ms if needed
                sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
                if (sliceDuration < (cameraExposure + cameraReadoutTime)) {
                    delayBeforeCamera += 0.25f;
                    delayBeforeLaser += 0.25f;
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
                // leave cameraExposure alone if using PVCAM device library
                if (!camera.getDeviceLibrary().equals("PVCAM")) {
                    sliceDuration = getSliceDuration(delayBeforeScan, scanDuration, scansPerSlice, delayBeforeLaser, laserDuration, delayBeforeCamera, cameraDuration);
                    cameraExposure = (float) sliceDuration - delayBeforeCamera;  // s.cameraDelay should be 0.25ms for PCO
                }
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
                final float shutterWidth = (float) acqSettings_.sliceSettingsLS().shutterWidth();
                final float shutterSpeed = (float) acqSettings_.sliceSettingsLS().shutterSpeedFactor();
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
                final float scanSettle = (float) acqSettings_.sliceSettingsLS().scanSettleTime();
                final float scanReset = (float) acqSettings_.sliceSettingsLS().scanResetTime();
                delayBeforeScan = scanReset - scanDelayFilter;
                scanDuration = scanSettle + (totalExposureMax*shutterSpeed) + scanLaserBufferTime;
                delayBeforeCamera = scanReset + scanSettle;
                delayBeforeLaser = delayBeforeCamera - scanLaserBufferTime; // trigger laser just before camera to make sure it's on already
                laserDuration = (totalExposureMax*shutterSpeed) + scanLaserBufferTime; // laser will turn off as exposure is ending
                break;
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

    private double getSliceDuration(
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

    private double getSliceDuration(DefaultTimingSettings.Builder tsb) {
        DefaultTimingSettings s = tsb.build();
        // slice duration is the max out of the scan time, laser time, and camera time
        return Math.max(Math.max(
                        s.delayBeforeScan() + (s.scanDuration() * s.scansPerSlice()),   // scan time
                        s.delayBeforeLaser() + s.laserTriggerDuration()                 // laser time
                ),
                s.delayBeforeCamera() + s.cameraTriggerDuration()                      // camera time
        );
    }

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

    private double computeActualVolumeDuration(final DefaultAcquisitionSettingsDISPIM acqSettings) {
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
//    @Override
//    public DefaultAcquisitionSettingsDISPIM settings() {
//        return acqSettings_;
//    }

//    @Override
//    public DefaultAcquisitionSettingsDISPIM.Builder settingsBuilder() {
//        return asb_;
//    }

}
