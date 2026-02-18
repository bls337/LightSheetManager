package org.micromanager.lightsheetmanager.gui.tabs;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Future;
import javax.swing.SwingUtilities;
import org.micromanager.lightsheetmanager.LightSheetManagerFrame;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.AdvancedTimingPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.SaveDataPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.VolumeDurationPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.MultiPositionPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.TimePointsPanel;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.gui.tabs.channels.ChannelTablePanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.SliceSettingsPanel;
import org.micromanager.lightsheetmanager.gui.tabs.acquisition.VolumeSettingsPanel;
import org.micromanager.lightsheetmanager.gui.playlist.AcquisitionTableFrame;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.ToggleButton;
import org.micromanager.lightsheetmanager.api.data.AcquisitionMode;

import javax.swing.JLabel;
import java.util.Objects;

public class AcquisitionTab extends Panel implements ListeningPanel {

    // layout panel
    private Panel pnlRight_;
    private Panel pnlButtons_;

    private ComboBox<AcquisitionMode> cmbAcquisitionModes_;

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

    // save data
    private SaveDataPanel pnlSaveData_;

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

    private final LightSheetManager model_;
    private final LightSheetManagerFrame frame_;

    public AcquisitionTab(final LightSheetManager model, final LightSheetManagerFrame frame) {
        model_ = Objects.requireNonNull(model);
        frame_ = Objects.requireNonNull(frame);
        acqTableFrame_ = new AcquisitionTableFrame(model_.studio());
        createUserInterface();
        createEventHandlers();
    }

    /**
     * Create the user interface.
     */
    private void createUserInterface() {

        final DefaultAcquisitionSettingsSCAPE acqSettings = model_.acquisitions().settings();

        setMigLayout(
                "insets 10 10 10 10",
                "[]5[]",
                "[]5[]"
        );

        // layout panels
        final Panel pnlLeft = new Panel();
        final Panel pnlCenter = new Panel();
        pnlRight_ = new Panel();

        // durations
        pnlDurations_ = new VolumeDurationPanel(model_);

        pnlVolumeSettings_ = new VolumeSettingsPanel(model_);

        // switch between these two panels
        pnlSliceSettings_ = new SliceSettingsPanel(model_);
        pnlAdvancedTiming_ = new AdvancedTimingPanel(model_);

        // multiple positions
        cbxUseMultiplePositions_ = new CheckBox(
                "Multiple Positions", acqSettings.isUsingMultiplePositions());
        pnlMultiPositions_ = new MultiPositionPanel(model_, cbxUseMultiplePositions_);
        // disable elements based on acqSettings
        pnlMultiPositions_.setPanelEnabled(acqSettings.isUsingMultiplePositions());

        pnlSaveData_ = new SaveDataPanel(model_, frame_);

        // time points
        cbxUseTimePoints_ = new CheckBox(
                "Time Points", acqSettings.isUsingTimePoints());
        pnlTimePoints_ = new TimePointsPanel(model_, cbxUseTimePoints_);
        // disable elements based on acqSettings
        pnlTimePoints_.setPanelEnabled(acqSettings.isUsingTimePoints());

        // acquisition buttons
        pnlButtons_ = new Panel();
        pnlButtons_.setMigLayout(
                "",
                "[]24[]",
                ""
        );

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

        final boolean isUsingChannels = acqSettings.isUsingChannels();
        cbxUseChannels_ = new CheckBox("Channels", isUsingChannels);
        pnlChannelTable_ = new ChannelTablePanel(model_, cbxUseChannels_);
        pnlChannelTable_.setMaximumSize(new Dimension(270, 400));

        // disable elements based on acqSettings
        pnlChannelTable_.setItemsEnabled(isUsingChannels);

        // acquisition mode combo box
        final boolean isUsingScanSettings = model_.devices().isUsingStageScanning();
        final GeometryType geometryType = model_.devices().adapter().geometry();
        cmbAcquisitionModes_ = new ComboBox<>(AcquisitionMode.getValidModes(geometryType, isUsingScanSettings),
                acqSettings.acquisitionMode(),
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
        pnlLeft.add(pnlMultiPositions_, "growx, span 2, wrap");
        pnlLeft.add(pnlSaveData_, "growx, span 2");

        pnlCenter.add(pnlChannelTable_, "wrap");
        pnlCenter.add(new JLabel("Acquisition mode:"), "split 2");
        pnlCenter.add(cmbAcquisitionModes_, "");

        final boolean isUsingAdvSettings =
                model_.acquisitions().settings().isUsingAdvancedTiming();

        pnlRight_.add(pnlVolumeSettings_, "growx, wrap");
        if (isUsingAdvSettings) {
            pnlRight_.add(pnlAdvancedTiming_, "growx, wrap");
        } else {
            pnlRight_.add(pnlSliceSettings_, "growx, wrap");
        }
        pnlRight_.add(cbxUseAdvancedTiming_, "growx");

        // TODO: consider putting durations into the model, since recalculating the slice timing shouldn't happen here
        // includes calculating the slice timing
        //updateDurationLabels();

        add(pnlLeft, "");
        add(pnlCenter, "");
        add(pnlRight_, "wrap");
        add(pnlButtons_, "span 3, gaptop 60");
    }

    /**
     * Create event handlers for the user interface.
     */
    private void createEventHandlers() {

        // start/stop acquisitions
        btnRunAcquisition_.registerListener(e -> {
            if (btnRunAcquisition_.isSelected()) {
                runAcquisition(false);
            } else {
                model_.acquisitions().requestStop();
            }
        });

        btnPauseAcquisition_.registerListener(e -> {
            if (btnPauseAcquisition_.isSelected()) {
                model_.acquisitions().requestPause();
            } else {
                model_.acquisitions().requestResume();
            }
        });

        btnOpenPlaylist_.registerListener(e -> acqTableFrame_.setVisible(true));
        btnOpenPlaylist_.setEnabled(false); // TODO: enable when playlist is implemented

        btnSpeedTest_.registerListener(e -> runAcquisition(true));
        btnRunOverviewAcq_.registerListener(e -> {
            // TODO: run the overview acq
        });

        // multiple positions
        cbxUseMultiplePositions_.registerListener(e -> {
            final boolean isSelected = cbxUseMultiplePositions_.isSelected();
            model_.acquisitions().settingsBuilder().useMultiplePositions(isSelected);
            pnlMultiPositions_.setPanelEnabled(isSelected);
        });

        // time points
        cbxUseTimePoints_.registerListener(e -> {
            final boolean selected = cbxUseTimePoints_.isSelected();
            model_.acquisitions().settingsBuilder().useTimePoints(selected);
            pnlTimePoints_.setPanelEnabled(selected);
            //updateDurationLabels();
        });

        // use channels
        cbxUseChannels_.registerListener(e -> {
            final boolean state = cbxUseChannels_.isSelected();
            model_.acquisitions().settingsBuilder().useChannels(state);
            pnlChannelTable_.setItemsEnabled(state);
        });

        // select the acquisition mode
        cmbAcquisitionModes_.registerListener(e ->
                model_.acquisitions().settingsBuilder().acquisitionMode(cmbAcquisitionModes_.getSelected()));

        // TODO: should timing recalc be part of setting use advanced timing value in model?
        // switches timing panels based on check box
        cbxUseAdvancedTiming_.registerListener(e -> {
            final boolean useAdvTiming = cbxUseAdvancedTiming_.isSelected();
            model_.acquisitions().settingsBuilder().useAdvancedTiming(useAdvTiming);
            swapTimingSettingsPanels(useAdvTiming);
            if (useAdvTiming) {
                pnlAdvancedTiming_.updateSpinners();
            } else {
                model_.acquisitions().updateAcquisitionSettings();
                model_.acquisitions().recalculateSliceTiming();
            }
        });
    }

    /**
     * Switch between slice timing panel and advanced timing panel.
     *
     * @param state the state of the CheckBox
     */
    private void swapTimingSettingsPanels(final boolean state) {
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

    public SliceSettingsPanel getSliceSettingsPanel() {
        return pnlSliceSettings_;
    }

    public MultiPositionPanel getMultiPositionPanel() {
        return pnlMultiPositions_;
    }

    private void acqFinishedCallback() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                btnRunAcquisition_.setState(false);
                btnPauseAcquisition_.setEnabled(false);
                btnSpeedTest_.setEnabled(true);
            });
        } catch (InterruptedException e) {
            model_.studio().logs().logError("Acquisition was interrupted!");
        } catch (InvocationTargetException e) {
            model_.studio().logs().logError("Could not update UI components.");
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
                model_.studio().logs().logError("error in runAcquisition");
            }
            // update the GUI when acquisition complete
            acqFinishedCallback();
        }).start();
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }

}