package org.micromanager.lightsheetmanager.gui.tabs;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Future;
import javax.swing.SwingUtilities;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.AdvancedTimingPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.VolumeDurationPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.MultiPositionPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.TimePointsPanel;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.gui.tabs.channels.ChannelTablePanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.SliceSettingsPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.VolumeSettingsPanel;
import org.micromanager.lightsheetmanager.gui.playlist.AcquisitionTableFrame;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.ToggleButton;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;

import javax.swing.JLabel;
import java.util.Objects;

public class AcquisitionTab extends Panel {

    private Studio studio_;

    // layout panel
    private Panel pnlRight_;
    private Panel pnlButtons_;

    private ComboBox cmbAcquisitionModes_;

    // acquisition buttons
    private ToggleButton btnRunAcquisition_;
    private ToggleButton btnPauseAcquisition_;
    private Button btnTestAcquisition_;
    private Button btnOpenPlaylist_;
    private Button btnSpeedTest_;
    private Button btnRunOverviewAcq_;

    // durations
    private VolumeDurationPanel pnlDurations_;

    // time points
    private TimePointsPanel pnlTimePoints_;
    private CheckBox cbxUseTimePoints_;

    // multiple positions
    private MultiPositionPanel pnlMultiPositions_;
    private CheckBox cbxUseMultiplePositions_;

    // channels
    private CheckBox cbxUseChannels_;
    private ChannelTablePanel pnlChannelTable_;

    // right panel
    private VolumeSettingsPanel pnlVolumeSettings_;
    private SliceSettingsPanel pnlSliceSettings_;
    private AdvancedTimingPanel pnlAdvancedTiming_;
    private CheckBox cbxUseAdvancedTiming_;

    // acquisition playlist
    private final AcquisitionTableFrame acqTableFrame_;

    private final LightSheetManagerModel model_;

    public AcquisitionTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.studio();
        acqTableFrame_ = new AcquisitionTableFrame(studio_);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {

        final DefaultAcquisitionSettingsDISPIM acqSettings =
                model_.acquisitions().getAcquisitionSettings();

        Panel.setMigLayoutDefault(
                "",
                "[]5[]",
                "[]5[]"
        );

        // layout panels
        final Panel pnlLeft = new Panel();
        final Panel pnlCenter = new Panel();
        pnlRight_ = new Panel();

        pnlVolumeSettings_ = new VolumeSettingsPanel(model_);

        // switch between these two panels
        pnlSliceSettings_ = new SliceSettingsPanel(model_);
        pnlAdvancedTiming_ = new AdvancedTimingPanel(model_);

        // durations
        pnlDurations_ = new VolumeDurationPanel(model_);

        // multiple positions
        cbxUseMultiplePositions_ = new CheckBox(
                "Multiple positions (XY)", acqSettings.isUsingMultiplePositions());
        pnlMultiPositions_ = new MultiPositionPanel(model_, cbxUseMultiplePositions_);
        // disable elements based on acqSettings
        pnlMultiPositions_.setEnabled(acqSettings.isUsingMultiplePositions());

        // time points
        cbxUseTimePoints_ = new CheckBox(
                "Time Points", acqSettings.isUsingTimePoints());
        pnlTimePoints_ = new TimePointsPanel(model_, cbxUseTimePoints_);
        // disable elements based on acqSettings
        pnlTimePoints_.setEnabled(acqSettings.isUsingTimePoints());

        // acquisition buttons
        pnlButtons_ = new Panel();

        ToggleButton.setDefaultSize(120, 30);
        btnRunAcquisition_ = new ToggleButton(
                "Start Acquisition", "Stop Acquisition",
                Icons.ARROW_RIGHT, Icons.CANCEL
        );
        btnRunAcquisition_.setEnabled(true);

        btnPauseAcquisition_ = new ToggleButton(
                "Pause", "Resume",
                Icons.PAUSE, Icons.PLAY
        );
        btnPauseAcquisition_.setEnabled(false);

        Button.setDefaultSize(120, 30);
        btnTestAcquisition_ = new Button("Test Acquisition");
        btnOpenPlaylist_ = new Button("Playlist...");
        btnSpeedTest_ = new Button("Speed test");

        Button.setDefaultSize(140, 30);
        btnRunOverviewAcq_ = new Button("Overview Acquisition");

        cbxUseChannels_ = new CheckBox(
                "Channels", acqSettings.isUsingChannels());
        pnlChannelTable_ = new ChannelTablePanel(model_, cbxUseChannels_);
        // disable elements based on acqSettings
        if (!acqSettings.isUsingChannels()) {
            pnlChannelTable_.setItemsEnabled(false);
        }

        cmbAcquisitionModes_ = new ComboBox(AcquisitionModes.toArray(),
                acqSettings.acquisitionMode().toString(),
                180, 24);

        cbxUseAdvancedTiming_ = new CheckBox("Use advanced timing settings",
                12, acqSettings.isUsingAdvancedTiming(), CheckBox.RIGHT);

        btnRunOverviewAcq_.setEnabled(false); // TODO: re-enable when these features are put in
        btnTestAcquisition_.setEnabled(false);

        // acquisition buttons
        pnlButtons_.add(btnRunAcquisition_, "");
        pnlButtons_.add(btnPauseAcquisition_, "");
        pnlButtons_.add(btnTestAcquisition_, "");
        pnlButtons_.add(btnRunOverviewAcq_, "");
        pnlButtons_.add(btnOpenPlaylist_, "");
        pnlButtons_.add(btnSpeedTest_, "");

        // 3 panel layout
        pnlLeft.add(pnlDurations_, "growx, growy");
        pnlLeft.add(pnlTimePoints_, "growx, growy, wrap");
        pnlLeft.add(pnlMultiPositions_, "growx, span 2");

        pnlCenter.add(pnlChannelTable_, "wrap");
        pnlCenter.add(new JLabel("Acquisition mode:"), "split 2");
        pnlCenter.add(cmbAcquisitionModes_, "");

        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        switch (geometryType) {
            case DISPIM:
                pnlRight_.add(pnlVolumeSettings_, "growx, wrap");
                pnlRight_.add(pnlSliceSettings_, "growx, wrap");
                pnlRight_.add(cbxUseAdvancedTiming_, "growx");
                break;
            case SCAPE:
                pnlRight_.add(pnlVolumeSettings_, "growx, wrap");
                pnlRight_.add(pnlAdvancedTiming_, "growx, wrap");
                break;
            default:
                break;
        }
        // TODO: consider putting durations into the model, since recalculating the slice timing shouldn't necessarily happen here
        // includes calculating the slice timing
        //updateDurationLabels();

        add(pnlLeft, "");
        add(pnlCenter, "");
        add(pnlRight_, "wrap");
        add(pnlButtons_, "span 3");
    }

    private void acqFinishedCallback() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                btnRunAcquisition_.setState(false);
                btnPauseAcquisition_.setEnabled(false);
                btnSpeedTest_.setEnabled(true);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void runAcquisition(boolean speedTest) {
        btnPauseAcquisition_.setEnabled(true);
        btnSpeedTest_.setEnabled(false);
        Future<?> acqFinished = model_.acquisitions().requestRun(speedTest);
        // Launch new thread to update the button when the acquisition is complete
        new Thread(() -> {
            try {
                acqFinished.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // update the GUI when acquisition complete
            acqFinishedCallback();
        }).start();
    }

    private void createEventHandlers() {
        final DefaultAcquisitionSettingsDISPIM.Builder asb_ =
                model_.acquisitions().getAcquisitionSettingsBuilder();

        // start/stop acquisitions
        btnRunAcquisition_.registerListener(e -> {
            if (btnRunAcquisition_.isSelected()) {
                runAcquisition(false);
                System.out.println("request run");
            } else {
                model_.acquisitions().requestStop();
                System.out.println("request stop");
            }
        });

        btnPauseAcquisition_.registerListener(e -> {
            if (btnPauseAcquisition_.isSelected()) {
                model_.acquisitions().requestPause();
                System.out.println("request pause");
            } else {
                model_.acquisitions().requestResume();
                System.out.println("request resume");
            }
        });

        btnOpenPlaylist_.registerListener(e -> acqTableFrame_.setVisible(true));
        btnOpenPlaylist_.setEnabled(false); // TODO: enable when playlist is implemented

        btnSpeedTest_.registerListener(e -> runAcquisition(true));
        btnRunOverviewAcq_.registerListener(e -> {
            System.out.println("run overview acquisition");
            // TODO: run the overview acq
        });

        // multiple positions
        cbxUseMultiplePositions_.registerListener(e -> {
            final boolean isSelected = cbxUseMultiplePositions_.isSelected();
            asb_.useMultiplePositions(isSelected);
            pnlMultiPositions_.setEnabled(isSelected);
        });

        // time points
        cbxUseTimePoints_.registerListener(e -> {
            final boolean selected = cbxUseTimePoints_.isSelected();
            asb_.useTimePoints(selected);
            pnlTimePoints_.setEnabled(selected);
            //updateDurationLabels();
        });

        // use channels
        cbxUseChannels_.registerListener(e -> {
            final boolean state = cbxUseChannels_.isSelected();
            asb_.useChannels(state);
            pnlChannelTable_.setItemsEnabled(state);
        });

        cmbAcquisitionModes_.registerListener(e -> {
            final int index = cmbAcquisitionModes_.getSelectedIndex();
            asb_.acquisitionMode(AcquisitionModes.getByIndex(index));
            //System.out.println("getAcquisitionMode: " + model_.acquisitions().getAcquisitionSettings().getAcquisitionMode());
        });

        // switches timing panels based on check box
        cbxUseAdvancedTiming_.registerListener(
                e -> switchTimingSettings(cbxUseAdvancedTiming_.isSelected()));
    }

    public SliceSettingsPanel getSliceSettingsPanel() {
        return pnlSliceSettings_;
    }

    /**
     * Switch between slice timing panel and advanced timing panel.
     *
     * @param state the state of the CheckBox
     */
    private void switchTimingSettings(final boolean state) {
        pnlRight_.removeAll();
        if (state) {
            pnlRight_.add(pnlVolumeSettings_, "growx, wrap");
            pnlRight_.add(pnlAdvancedTiming_, "growx, wrap");
            pnlRight_.add(cbxUseAdvancedTiming_, "growx");
        } else {
            pnlRight_.add(pnlVolumeSettings_, "growx, wrap");
            pnlRight_.add(pnlSliceSettings_, "growx, wrap");
            pnlRight_.add(cbxUseAdvancedTiming_, "growx");
        }
        pnlRight_.revalidate();
        pnlRight_.repaint();
    }

//    private void updateDurationLabels() {
//        updateSlicePeriodLabel();
//        updateVolumeDurationLabel();
//        updateTotalTimeDurationLabel();
//    }

//    private void updateSlicePeriodLabel() {
//        final DefaultAcquisitionSettingsDISPIM acqSettings = model_.acquisitions().getAcquisitionSettings();
//        //model_.getAcquisitionEngine().recalculateSliceTiming(acqSettings);
//        //lblSliceTimeValue_.setText(Double.toString(acqSettings.timingSettings().sliceDuration()));
//        //System.out.println("updating slice label to: " + acqSettings.getTimingSettings().sliceDuration());
//    }
//
//    private void updateVolumeDurationLabel() {
//        double duration = computeVolumeDuration(model_.acquisitions().getAcquisitionSettings());
//        if (duration > 1000) {
//            lblVolumeTimeValue_.setText(
//                    NumberUtils.doubleToDisplayString(duration/1000d) +
//                            " s"); // round to ms
//        } else {
//            lblVolumeTimeValue_.setText(
//                    NumberUtils.doubleToDisplayString(Math.round(10*duration)/10d) +
//                            " ms");  // round to tenth of ms
//        }
//        //System.out.println("updating volume label to: " + );
//    }

    /**
     * Update the displayed total time duration.
     */
//    private void updateTotalTimeDurationLabel() {
//        String s = "";
//        double duration = computeTotalTimeDuration();
//        if (duration < 60) {  // less than 1 min
//            s += NumberUtils.doubleToDisplayString(duration) + " s";
//        } else if (duration < 60*60) { // between 1 min and 1 hour
//            s += NumberUtils.doubleToDisplayString(Math.floor(duration/60)) + " min ";
//            s += NumberUtils.doubleToDisplayString(Math.round(duration %  60)) + " s";
//        } else { // longer than 1 hour
//            s += NumberUtils.doubleToDisplayString(Math.floor(duration/(60*60))) + " hr ";
//            s +=  NumberUtils.doubleToDisplayString(Math.round((duration % (60*60))/60)) + " min";
//        }
//        lblTotalTimeValue_.setText(s);
//    }

//    private double computeTotalTimeDuration() {
//        final DefaultAcquisitionSettingsDISPIM acqSettings = model_.acquisitions().getAcquisitionSettings();
//        final double duration = (acqSettings.numTimePoints() - 1) * acqSettings.timePointInterval()
//                + computeTimePointDuration()/1000;
//        return duration;
//    }

    /**
     * Compute the time point duration in ms. Only difference from computeVolumeDuration()
     * is that it also takes into account the multiple positions, if any.
     * @return duration in ms
     */
//    private double computeTimePointDuration() {
//        final DefaultAcquisitionSettingsDISPIM acqSettings = model_.acquisitions().getAcquisitionSettings();
//        final double volumeDuration = computeVolumeDuration(acqSettings);
//        if (acqSettings.isUsingMultiplePositions()) {
//            try {
//                // use 1.5 seconds motor move between positions
//                // (could be wildly off but was estimated using actual system
//                // and then slightly padded to be conservative to avoid errors
//                // where positions aren't completed in time for next position)
//                // could estimate the actual time by analyzing the position's relative locations
//                //   and using the motor speed and acceleration time
//                return studio_.positions().getPositionList().getNumberOfPositions() *
//                        (volumeDuration + 1500 + spnPostMoveDelay_.getInt());
//            } catch (Exception e) {
//                studio_.logs().showError("Error getting position list for multiple XY positions");
//            }
//        }
//        return volumeDuration;
//    }

//    public double computeVolumeDuration(final DefaultAcquisitionSettingsDISPIM acqSettings) {
//        final MultiChannelModes channelMode = acqSettings.channelMode();
//        final int numChannels = acqSettings.numChannels();
//        final int numViews = acqSettings.volumeSettings().numViews();
//        final double delayBeforeView = acqSettings.volumeSettings().delayBeforeView();
//        int numCameraTriggers = acqSettings.volumeSettings().slicesPerView();
//        if (acqSettings.cameraMode() == CameraModes.OVERLAP) {
//            numCameraTriggers += 1;
//        }
//
//        //System.out.println(acqSettings.getTimingSettings().sliceDuration());
//
//        // stackDuration is per-side, per-channel, per-position
//        final double stackDuration = numCameraTriggers * acqSettings.timingSettings().sliceDuration();
//        //System.out.println("stackDuration: " + stackDuration);
//        //System.out.println("numViews: " + numViews);
//        //System.out.println("numCameraTriggers: " + numCameraTriggers);
//        if (acqSettings.isUsingStageScanning()) {
//
//        } else {
//            double channelSwitchDelay = 0;
//            if (channelMode == MultiChannelModes.VOLUME) {
//                channelSwitchDelay = 500;   // estimate channel switching overhead time as 0.5s
//                // actual value will be hardware-dependent
//            }
//            if (channelMode == MultiChannelModes.SLICE_HW) {
//                return numViews * (delayBeforeView + stackDuration * numChannels);  // channelSwitchDelay = 0
//            } else {
//                return numViews * numChannels
//                        * (delayBeforeView + stackDuration)
//                        + (numChannels - 1) * channelSwitchDelay;
//            }
//        }
//        // TODO: stage scanning still needs to be taken into consideration
////        if (acqSettings.isStageScanning || acqSettings.isStageStepping) {
////            final double rampDuration = getStageRampDuration(acqSettings);
////            final double retraceTime = getStageRetraceDuration(acqSettings);
////            // TODO double-check these calculations below, at least they are better than before ;-)
////            if (acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN) {
////                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
////                    return retraceTime + (numSides * ((rampDuration * 2) + (stackDuration * numChannels)));
////                } else {  // "normal" stage scan with volume channel switching
////                    if (numSides == 1) {
////                        // single-view so will retrace at beginning of each channel
////                        return ((rampDuration * 2) + stackDuration + retraceTime) * numChannels;
////                    } else {
////                        // will only retrace at very start/end
////                        return retraceTime + (numSides * ((rampDuration * 2) + stackDuration) * numChannels);
////                    }
////                }
////            } else if (acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN_UNIDIRECTIONAL
////                    || acqSettings.spimMode == AcquisitionModes.Keys.STAGE_STEP_SUPPLEMENTAL_UNIDIRECTIONAL
////                    || acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN_SUPPLEMENTAL_UNIDIRECTIONAL) {
////                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
////                    return ((rampDuration * 2) + (stackDuration * numChannels) + retraceTime) * numSides;
////                } else {  // "normal" stage scan with volume channel switching
////                    return ((rampDuration * 2) + stackDuration + retraceTime) * numChannels * numSides;
////                }
////            } else {  // interleaved mode => one-way pass collecting both sides
////                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
////                    // single pass with all sides and channels
////                    return retraceTime + (rampDuration * 2 + stackDuration * numSides * numChannels);
////                } else {  // one-way pass collecting both sides, then rewind for next channel
////                    return ((rampDuration * 2) + (stackDuration * numSides) + retraceTime) * numChannels;
////                }
////            }
////        } else { // piezo scan
////            double channelSwitchDelay = 0;
////            if (channelMode == MultichannelModes.Keys.VOLUME) {
////                channelSwitchDelay = 500;   // estimate channel switching overhead time as 0.5s
////                // actual value will be hardware-dependent
////            }
////            if (channelMode == MultichannelModes.Keys.SLICE_HW) {
////                return numSides * (delayBeforeSide + stackDuration * numChannels);  // channelSwitchDelay = 0
////            } else {
////                return numSides * numChannels
////                        * (delayBeforeSide + stackDuration)
////                        + (numChannels - 1) * channelSwitchDelay;
////            }
////        }
//        return 1.0;
//    }
}