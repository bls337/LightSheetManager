package org.micromanager.lightsheetmanager.gui.tabs;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Future;
import javax.swing.SwingUtilities;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.gui.tabs.channels.ChannelTablePanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.SliceSettingsPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.VolumeSettingsPanel;
import org.micromanager.lightsheetmanager.gui.frames.AdvancedTimingFrame;
import org.micromanager.lightsheetmanager.gui.frames.XYZGridFrame;
import org.micromanager.lightsheetmanager.gui.playlist.AcquisitionTableFrame;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.gui.components.ToggleButton;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;

import org.micromanager.internal.utils.NumberUtils;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;

import javax.swing.JLabel;
import java.util.Objects;

public class AcquisitionTab extends Panel {

    private Studio studio_;

    private ComboBox cmbAcquisitionModes_;

    private ToggleButton btnRunAcquisition_;
    private ToggleButton btnPauseAcquisition_;
    private Button btnTestAcquisition_;
    private Button btnOpenPlaylist_;
    private Button btnSpeedTest_;
    private Button btnRunOverviewAcq_;

    private CheckBox cbxUseChannels_;

    // durations
    private Label lblSliceTime_;
    private Label lblVolumeTime_;
    private Label lblTotalTime_;

    private Label lblSliceTimeValue_;
    private Label lblVolumeTimeValue_;
    private Label lblTotalTimeValue_;

    // time points
    private Label lblNumTimePoints_;
    private Label lblTimePointInterval_;
    private Spinner spnNumTimePoints_;
    private Spinner spnTimePointInterval_;
    private CheckBox cbxUseTimePoints_;

    // multiple positions
    private Label lblPostMoveDelay_;
    private Spinner spnPostMoveDelay_;
    private CheckBox cbxUseMultiplePositions_;
    private Button btnOpenXYZGrid_;
    private Button btnEditPositionList_;

    private Panel pnlButtons_;
    private Panel pnlDurations_;
    private Panel pnlTimePoints_;
    private Panel pnlMultiplePositions_;

    private ChannelTablePanel pnlChannelTable_;

    private VolumeSettingsPanel pnlVolumeSettings_;
    private SliceSettingsPanel pnlSliceSettings_;

    private XYZGridFrame xyzGridFrame_;
    private AdvancedTimingFrame advTimingFrame_;

    private AcquisitionTableFrame acqTableFrame_;

    private LightSheetManagerModel model_;

    public AcquisitionTab(final LightSheetManagerModel model) {
        model_ = Objects.requireNonNull(model);
        studio_ = model_.getStudio();
        acqTableFrame_ = new AcquisitionTableFrame(studio_);
        advTimingFrame_ = new AdvancedTimingFrame(model_);
        xyzGridFrame_ = new XYZGridFrame(model_);
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
        Panel pnlLeft = new Panel();
        Panel pnlCenter = new Panel();
        Panel pnlRight = new Panel();

        pnlVolumeSettings_ = new VolumeSettingsPanel(model_);
        pnlSliceSettings_ = new SliceSettingsPanel(model_, advTimingFrame_);

        // check boxes for panels
        cbxUseMultiplePositions_ = new CheckBox("Multiple positions (XY)", acqSettings.isUsingMultiplePositions());
        cbxUseTimePoints_ = new CheckBox("Time Points", acqSettings.isUsingTimePoints());
        cbxUseChannels_ = new CheckBox("Channels", acqSettings.isUsingChannels());

        // panels
        pnlButtons_ = new Panel();
        pnlDurations_ = new Panel("Durations");
        pnlTimePoints_ = new Panel(cbxUseTimePoints_);
        pnlMultiplePositions_ = new Panel(cbxUseMultiplePositions_);

        // durations
        lblSliceTime_ = new Label("Slice");
        lblVolumeTime_ = new Label("Volume:");
        lblTotalTime_ = new Label("Total:");

        lblSliceTimeValue_ = new Label("0.0");
        lblVolumeTimeValue_ = new Label("0.0");
        lblTotalTimeValue_ = new Label("0.0");

        // TODO: is there a reasonable max value for these spinners?
        // time points
        lblNumTimePoints_ = new Label("Number:");
        lblTimePointInterval_ = new Label("Interval [s]:");
        spnNumTimePoints_ = Spinner.createIntegerSpinner(acqSettings.numTimePoints(), 1, Integer.MAX_VALUE,1);
        spnTimePointInterval_ = Spinner.createIntegerSpinner(acqSettings.timePointInterval(), 0, Integer.MAX_VALUE, 1);

        // disable elements based on acqSettings
        setTimePointSpinnersEnabled(acqSettings.isUsingTimePoints());

        // multiple positions
        Spinner.setDefaultSize(7);
        lblPostMoveDelay_ = new Label("Post-move delay [ms]:");
        spnPostMoveDelay_ = Spinner.createIntegerSpinner(acqSettings.postMoveDelay(), 0, Integer.MAX_VALUE, 100);
        btnEditPositionList_ = new Button("Edit Position List", 120, 24);
        btnOpenXYZGrid_ = new Button("XYZ Grid", 80, 24);

        // disable elements based on acqSettings
        setMultiPositionsEnabled(acqSettings.isUsingMultiplePositions());

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

        pnlChannelTable_ = new ChannelTablePanel(model_, cbxUseChannels_);
        // disable elements based on acqSettings
        if (!acqSettings.isUsingChannels()) {
            pnlChannelTable_.setItemsEnabled(false);
        }

        cmbAcquisitionModes_ = new ComboBox(AcquisitionModes.toArray(),
                acqSettings.acquisitionMode().toString(),
                180, 24);

        // durations
        pnlDurations_.add(lblSliceTime_, "");
        pnlDurations_.add(lblSliceTimeValue_, "wrap");
        pnlDurations_.add(lblVolumeTime_, "");
        pnlDurations_.add(lblVolumeTimeValue_, "wrap");
        pnlDurations_.add(lblTotalTime_, "");
        pnlDurations_.add(lblTotalTimeValue_, "");

        // time points
        pnlTimePoints_.add(lblNumTimePoints_, "");
        pnlTimePoints_.add(spnNumTimePoints_, "wrap");
        pnlTimePoints_.add(lblTimePointInterval_, "");
        pnlTimePoints_.add(spnTimePointInterval_, "");

        // multiple positions
        pnlMultiplePositions_.add(btnEditPositionList_, "");
        pnlMultiplePositions_.add(btnOpenXYZGrid_, "wrap");
        pnlMultiplePositions_.add(lblPostMoveDelay_, "");
        pnlMultiplePositions_.add(spnPostMoveDelay_, "");

        // acquisition buttons
        pnlButtons_.add(btnRunAcquisition_, "");
        pnlButtons_.add(btnPauseAcquisition_, "");
        pnlButtons_.add(btnTestAcquisition_, "");
        pnlButtons_.add(btnOpenPlaylist_, "");
        pnlButtons_.add(btnSpeedTest_, "");
        pnlButtons_.add(btnRunOverviewAcq_, "");

        // 3 panel layout
        pnlLeft.add(pnlDurations_, "growx, growy");
        pnlLeft.add(pnlTimePoints_, "growx, growy, wrap");
        pnlLeft.add(pnlMultiplePositions_, "growx, span 2");

        pnlCenter.add(pnlChannelTable_, "wrap");
        pnlCenter.add(new JLabel("Acquisition mode:"), "split 2");
        pnlCenter.add(cmbAcquisitionModes_, "");

        pnlRight.add(pnlVolumeSettings_, "growx, wrap");
        pnlRight.add(pnlSliceSettings_, "growx, wrap");

        // TODO: consider putting durations into the model, since recalculating the slice timing shouldn't necessarily happen here
        // includes calculating the slice timing
        updateDurationLabels();

        add(pnlLeft, "");
        add(pnlCenter, "");
        add(pnlRight, "wrap");
        add(pnlButtons_, "span 3");
    }

    private void acqFinishedCallback() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    btnRunAcquisition_.setState(false);
                    btnPauseAcquisition_.setEnabled(false);
                    btnSpeedTest_.setEnabled(true);
                }
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
        Future acqFinished = model_.acquisitions().requestRun(speedTest);
        // Launch new thread to update the button when the acquisition is complete
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    acqFinished.get();
                } catch (Exception e ) {

                }
                // update the GUI when acquisition complete
                acqFinishedCallback();
            }
        }).start();
    }

    private void createEventHandlers() {
        final DefaultAcquisitionSettingsDISPIM.Builder asb_ = model_.acquisitions().getAcquisitionSettingsBuilder();

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

        btnSpeedTest_.registerListener(e -> runAcquisition(true));
        btnRunOverviewAcq_.registerListener(e -> {
            System.out.println("run overview acquisition");
            // TODO: run the overview acq
        });

        btnOpenXYZGrid_.registerListener(e -> xyzGridFrame_.setVisible(true));
        btnEditPositionList_.registerListener(e -> studio_.app().showPositionList());

        cbxUseMultiplePositions_.registerListener(e -> {
            final boolean isSelected = cbxUseMultiplePositions_.isSelected();
            asb_.useMultiplePositions(isSelected);
            setMultiPositionsEnabled(isSelected);
        });

        spnPostMoveDelay_.registerListener(e -> {
            asb_.postMoveDelay(spnPostMoveDelay_.getInt());
            //System.out.println("getPostMoveDelay: " + model_.acquisitions().getAcquisitionSettings().getPostMoveDelay());
        });

        // time points

        cbxUseTimePoints_.registerListener(e -> {
            final boolean selected = cbxUseTimePoints_.isSelected();
            asb_.useTimePoints(selected);
            setTimePointSpinnersEnabled(selected);
            updateDurationLabels();
        });

        spnNumTimePoints_.registerListener(e -> {
            asb_.numTimePoints(spnNumTimePoints_.getInt());
            updateDurationLabels();
            //System.out.println("getNumTimePoints: " + model_.acquisitions().getAcquisitionSettings().getNumTimePoints());
        });

        spnTimePointInterval_.registerListener(e -> {
            asb_.timePointInterval(spnTimePointInterval_.getInt());
            updateDurationLabels();
            //System.out.println("getTimePointInterval: " + model_.acquisitions().getAcquisitionSettings().getTimePointInterval());
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
    }

    public SliceSettingsPanel getSliceSettingsPanel() {
        return pnlSliceSettings_;
    }

    private void setTimePointSpinnersEnabled(final boolean state) {
        lblNumTimePoints_.setEnabled(state);
        lblTimePointInterval_.setEnabled(state);
        spnNumTimePoints_.setEnabled(state);
        spnTimePointInterval_.setEnabled(state);
    }

    private void setMultiPositionsEnabled(final boolean state) {
        lblPostMoveDelay_.setEnabled(state);
        spnPostMoveDelay_.setEnabled(state);
        btnEditPositionList_.setEnabled(state);
    }

    private void updateDurationLabels() {
        updateSlicePeriodLabel();
        updateVolumeDurationLabel();
        updateTotalTimeDurationLabel();
    }

    private void updateSlicePeriodLabel() {
        final DefaultAcquisitionSettingsDISPIM acqSettings = model_.acquisitions().getAcquisitionSettings();
        //model_.getAcquisitionEngine().recalculateSliceTiming(acqSettings);
        //lblSliceTimeValue_.setText(Double.toString(acqSettings.timingSettings().sliceDuration()));
        //System.out.println("updating slice label to: " + acqSettings.getTimingSettings().sliceDuration());
    }

    private void updateVolumeDurationLabel() {
        double duration = computeVolumeDuration(model_.acquisitions().getAcquisitionSettings());
        if (duration > 1000) {
            lblVolumeTimeValue_.setText(
                    NumberUtils.doubleToDisplayString(duration/1000d) +
                            " s"); // round to ms
        } else {
            lblVolumeTimeValue_.setText(
                    NumberUtils.doubleToDisplayString(Math.round(10*duration)/10d) +
                            " ms");  // round to tenth of ms
        }
        //System.out.println("updating volume label to: " + );
    }

    /**
     * Update the displayed total time duration.
     */
    private void updateTotalTimeDurationLabel() {
        String s = "";
        double duration = computeTotalTimeDuration();
        if (duration < 60) {  // less than 1 min
            s += NumberUtils.doubleToDisplayString(duration) + " s";
        } else if (duration < 60*60) { // between 1 min and 1 hour
            s += NumberUtils.doubleToDisplayString(Math.floor(duration/60)) + " min ";
            s += NumberUtils.doubleToDisplayString(Math.round(duration %  60)) + " s";
        } else { // longer than 1 hour
            s += NumberUtils.doubleToDisplayString(Math.floor(duration/(60*60))) + " hr ";
            s +=  NumberUtils.doubleToDisplayString(Math.round((duration % (60*60))/60)) + " min";
        }
        lblTotalTimeValue_.setText(s);
    }

    private double computeTotalTimeDuration() {
        final DefaultAcquisitionSettingsDISPIM acqSettings = model_.acquisitions().getAcquisitionSettings();
        final double duration = (acqSettings.numTimePoints() - 1) * acqSettings.timePointInterval()
                + computeTimePointDuration()/1000;
        return duration;
    }

    /**
     * Compute the time point duration in ms. Only difference from computeVolumeDuration()
     * is that it also takes into account the multiple positions, if any.
     * @return duration in ms
     */
    private double computeTimePointDuration() {
        final DefaultAcquisitionSettingsDISPIM acqSettings = model_.acquisitions().getAcquisitionSettings();
        final double volumeDuration = computeVolumeDuration(acqSettings);
        if (acqSettings.isUsingMultiplePositions()) {
            try {
                // use 1.5 seconds motor move between positions
                // (could be wildly off but was estimated using actual system
                // and then slightly padded to be conservative to avoid errors
                // where positions aren't completed in time for next position)
                // could estimate the actual time by analyzing the position's relative locations
                //   and using the motor speed and acceleration time
                return studio_.positions().getPositionList().getNumberOfPositions() *
                        (volumeDuration + 1500 + spnPostMoveDelay_.getInt());
            } catch (Exception e) {
                studio_.logs().showError("Error getting position list for multiple XY positions");
            }
        }
        return volumeDuration;
    }

    public double computeVolumeDuration(final DefaultAcquisitionSettingsDISPIM acqSettings) {
        final MultiChannelModes channelMode = acqSettings.channelMode();
        final int numChannels = acqSettings.numChannels();
        final int numViews = acqSettings.volumeSettings().numViews();
        final double delayBeforeView = acqSettings.volumeSettings().delayBeforeView();
        int numCameraTriggers = acqSettings.volumeSettings().slicesPerView();
        if (acqSettings.cameraMode() == CameraModes.OVERLAP) {
            numCameraTriggers += 1;
        }

        //System.out.println(acqSettings.getTimingSettings().sliceDuration());

        // stackDuration is per-side, per-channel, per-position
        final double stackDuration = numCameraTriggers * acqSettings.timingSettings().sliceDuration();
        //System.out.println("stackDuration: " + stackDuration);
        //System.out.println("numViews: " + numViews);
        //System.out.println("numCameraTriggers: " + numCameraTriggers);
        if (acqSettings.isUsingStageScanning()) {

        } else {
            double channelSwitchDelay = 0;
            if (channelMode == MultiChannelModes.VOLUME) {
                channelSwitchDelay = 500;   // estimate channel switching overhead time as 0.5s
                // actual value will be hardware-dependent
            }
            if (channelMode == MultiChannelModes.SLICE_HW) {
                return numViews * (delayBeforeView + stackDuration * numChannels);  // channelSwitchDelay = 0
            } else {
                return numViews * numChannels
                        * (delayBeforeView + stackDuration)
                        + (numChannels - 1) * channelSwitchDelay;
            }
        }
        // TODO: stage scanning still needs to be taken into consideration
//        if (acqSettings.isStageScanning || acqSettings.isStageStepping) {
//            final double rampDuration = getStageRampDuration(acqSettings);
//            final double retraceTime = getStageRetraceDuration(acqSettings);
//            // TODO double-check these calculations below, at least they are better than before ;-)
//            if (acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN) {
//                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                    return retraceTime + (numSides * ((rampDuration * 2) + (stackDuration * numChannels)));
//                } else {  // "normal" stage scan with volume channel switching
//                    if (numSides == 1) {
//                        // single-view so will retrace at beginning of each channel
//                        return ((rampDuration * 2) + stackDuration + retraceTime) * numChannels;
//                    } else {
//                        // will only retrace at very start/end
//                        return retraceTime + (numSides * ((rampDuration * 2) + stackDuration) * numChannels);
//                    }
//                }
//            } else if (acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN_UNIDIRECTIONAL
//                    || acqSettings.spimMode == AcquisitionModes.Keys.STAGE_STEP_SUPPLEMENTAL_UNIDIRECTIONAL
//                    || acqSettings.spimMode == AcquisitionModes.Keys.STAGE_SCAN_SUPPLEMENTAL_UNIDIRECTIONAL) {
//                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                    return ((rampDuration * 2) + (stackDuration * numChannels) + retraceTime) * numSides;
//                } else {  // "normal" stage scan with volume channel switching
//                    return ((rampDuration * 2) + stackDuration + retraceTime) * numChannels * numSides;
//                }
//            } else {  // interleaved mode => one-way pass collecting both sides
//                if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                    // single pass with all sides and channels
//                    return retraceTime + (rampDuration * 2 + stackDuration * numSides * numChannels);
//                } else {  // one-way pass collecting both sides, then rewind for next channel
//                    return ((rampDuration * 2) + (stackDuration * numSides) + retraceTime) * numChannels;
//                }
//            }
//        } else { // piezo scan
//            double channelSwitchDelay = 0;
//            if (channelMode == MultichannelModes.Keys.VOLUME) {
//                channelSwitchDelay = 500;   // estimate channel switching overhead time as 0.5s
//                // actual value will be hardware-dependent
//            }
//            if (channelMode == MultichannelModes.Keys.SLICE_HW) {
//                return numSides * (delayBeforeSide + stackDuration * numChannels);  // channelSwitchDelay = 0
//            } else {
//                return numSides * numChannels
//                        * (delayBeforeSide + stackDuration)
//                        + (numChannels - 1) * channelSwitchDelay;
//            }
//        }
        return 1.0;
    }
}