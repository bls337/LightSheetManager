package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.AutofocusFitType;
import org.micromanager.lightsheetmanager.api.data.AutofocusMode;
import org.micromanager.lightsheetmanager.api.data.AutofocusType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.LightSheetManager;

import java.awt.Font;
import java.util.Objects;

/**
 * This tab contains autofocus related settings.
 */
public class AutofocusTab extends Panel implements ListeningPanel {

    // general autofocus options
    private CheckBox cbxShowImages_;
    private CheckBox cbxShowPlot_;
    private Spinner spnNumImages_;
    private Spinner spnStepSize_;
    private Spinner spnMinimumR2_;
    private ComboBox cmbAutofocusMode_;
    private ComboBox cmbScoringAlgorithm_;
    private ComboBox cmbFittingFunction_;

    // autofocus options during setup
    private CheckBox cbxAutofocusEveryPass_;
    private CheckBox cbxAutofocusBeforeAcq_;
    private ComboBox cmbAutofocusChannel_;
    private Spinner spnAutofocusEveryX_;
    private Spinner spnMaxOffset_;

    // autofocus options during setup
    private Label lblMaxOffsetSetup_;
    private Label lblMaxOffsetSetupUm_;
    private Spinner spnMaxOffsetSetup_;
    private CheckBox cbxAutoUpdateFocusFound_;

    private LightSheetManager model_;

    public AutofocusTab(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    public void createUserInterface() {
        final DefaultAcquisitionSettingsSCAPE acqSettings =
                model_.acquisitions().settings();

        final Label lblTitle = new Label("Autofocus Settings", Font.BOLD, 18);

        setMigLayout(
            "",
            "[]10[]",
            "[]10[]"
        );

        String[] labels = {"None"};

        final Panel pnlGeneralOptions = new Panel("General Autofocus Options");
        final Panel pnlAcqActiveOptions = new Panel("Autofocus Options During Acquisition");
        final Panel pnlAcqSetupOptions = new Panel("Autofocus Options During Setup");
        final Panel pnlMoveCorrection = new Panel("Movement Correction Options");

        // general autofocus options
        final Label lblNumImages = new Label("Number of Images:");
        final Label lblStepSize = new Label("Step Size [\u00B5m]:");
        final Label lblMode = new Label("Mode:");
        final Label lblScoringAlgorithm = new Label("Scoring algorithm:");
        final Label lblFittingFunction = new Label("Fit using:");
        final Label lblMinimumR2 = new Label("<html>Minimum R<sup>2</sup></html>");

        cbxShowImages_ = new CheckBox("Show Images", 12, true, CheckBox.RIGHT);
        cbxShowPlot_ = new CheckBox("Show Plot", 12, true, CheckBox.RIGHT);

        spnNumImages_ = Spinner.createIntegerSpinner(
                acqSettings.autofocusSettings().numImages(), 0, Integer.MAX_VALUE, 1);
        spnStepSize_ = Spinner.createDoubleSpinner(
                acqSettings.autofocusSettings().stepSize(), 0.0, 100.0, 1.0);
        spnMinimumR2_ = Spinner.createDoubleSpinner(
                acqSettings.autofocusSettings().r2(), 0.0, 1.0, 0.01);

        ComboBox.setDefaultSize(120, 20);
        cmbScoringAlgorithm_ = new ComboBox(AutofocusType.toArray(),
                acqSettings.autofocusSettings().scoringAlgorithm().toString());
        cmbFittingFunction_ = new ComboBox(AutofocusFitType.toArray(),
                acqSettings.autofocusSettings().fit().toString());
        ComboBox.setDefaultSize(140, 20);
        cmbAutofocusMode_ = new ComboBox(AutofocusMode.toArray(),
                acqSettings.autofocusSettings().mode().toString());

        // autofocus options during acquisition
        final Label lblTimePoints = new Label("time points");
        final Label lblMaxOffsetActive = new Label("Max offset change: ");
        final Label lblAutofocusEveryX = new Label("Autofocus every ");
        final Label lblAutofocusChannel = new Label("Autofocus channel:");

        cbxAutofocusEveryPass_ = new CheckBox("Autofocus every stage pass", 12, false, CheckBox.RIGHT);
        cbxAutofocusBeforeAcq_ = new CheckBox("Autofocus before starting acquisition", 12, false, CheckBox.RIGHT);

        cmbAutofocusChannel_ = new ComboBox(labels, "None", 60, 20);
        spnAutofocusEveryX_ = Spinner.createIntegerSpinner(10, 0, 1000, 1);
        spnMaxOffset_ = Spinner.createIntegerSpinner(3, 0, 10, 1);

        // autofocus options during setup
        lblMaxOffsetSetup_ = new Label("Max offset change: ");
        lblMaxOffsetSetupUm_ = new Label("\u00B5m (\u00B1)");
        cbxAutoUpdateFocusFound_ = new CheckBox("Automatically update offset if focus found", 12, false, CheckBox.RIGHT);
        spnMaxOffsetSetup_ = Spinner.createIntegerSpinner(3, 0, 10, 1);
        setSetupOptionsState(false);

        // movement correction options
        final Label lblTimePoints2 = new Label("time points"); // TODO: share this value?
        final Label lblCorrectEveryX = new Label("Correct every");
        final Label lblChannel = new Label("Channel:");
        final Label lblMaxDistance = new Label("Max distance:");
        final Label lblMinMovement = new Label("Min movement:");
        final Spinner spnCorrectEveryX = Spinner.createIntegerSpinner(100, 0, 1000, 1);
        final Spinner spnMaxDistance = Spinner.createIntegerSpinner(96, 0, 100, 1);
        final Spinner spnMinMovement = Spinner.createDoubleSpinner(1.0, 0.0, 10.0, 0.5);
        final ComboBox cmbChannel = new ComboBox(labels, "None", 60, 20);

        // add ui elements to the panel
        add(lblTitle, "span 2, wrap");

        // general autofocus options
        pnlGeneralOptions.add(cbxShowImages_, "");
        pnlGeneralOptions.add(cbxShowPlot_, "wrap");
        pnlGeneralOptions.add(lblNumImages, "");
        pnlGeneralOptions.add(spnNumImages_, "wrap");
        pnlGeneralOptions.add(lblStepSize, "");
        pnlGeneralOptions.add(spnStepSize_, "wrap");
        pnlGeneralOptions.add(lblMinimumR2, "");
        pnlGeneralOptions.add(spnMinimumR2_, "wrap");
        pnlGeneralOptions.add(lblScoringAlgorithm, "");
        pnlGeneralOptions.add(cmbScoringAlgorithm_, "wrap");
        pnlGeneralOptions.add(lblFittingFunction, "");
        pnlGeneralOptions.add(cmbFittingFunction_, "wrap");
        pnlGeneralOptions.add(lblMode, "");
        pnlGeneralOptions.add(cmbAutofocusMode_, "");

        // autofocus options during acquisition
        pnlAcqActiveOptions.add(cbxAutofocusEveryPass_, "span 3, wrap");
        pnlAcqActiveOptions.add(cbxAutofocusBeforeAcq_, "span 3, wrap");
        pnlAcqActiveOptions.add(lblAutofocusEveryX, "");
        pnlAcqActiveOptions.add(spnAutofocusEveryX_, "");
        pnlAcqActiveOptions.add(lblTimePoints, "wrap");
        pnlAcqActiveOptions.add(lblAutofocusChannel, "");
        pnlAcqActiveOptions.add(cmbAutofocusChannel_, "wrap");
        pnlAcqActiveOptions.add(lblMaxOffsetActive, "");
        pnlAcqActiveOptions.add(spnMaxOffset_, "");
        pnlAcqActiveOptions.add(new Label("\u00B5m (\u00B1)"), "");

        // autofocus options during setup
        pnlAcqSetupOptions.add(cbxAutoUpdateFocusFound_, "span 3, wrap");
        pnlAcqSetupOptions.add(lblMaxOffsetSetup_, "");
        pnlAcqSetupOptions.add(spnMaxOffsetSetup_, "");
        pnlAcqSetupOptions.add(lblMaxOffsetSetupUm_, "");

        // movement correction
        pnlMoveCorrection.add(lblCorrectEveryX, "");
        pnlMoveCorrection.add(spnCorrectEveryX, "");
        pnlMoveCorrection.add(lblTimePoints2, "wrap");
        pnlMoveCorrection.add(lblChannel, "");
        pnlMoveCorrection.add(cmbChannel, "wrap");
        pnlMoveCorrection.add(lblMaxDistance, "");
        pnlMoveCorrection.add(spnMaxDistance, "");
        pnlMoveCorrection.add(new Label("\u00B5m (\u00B1)"), "wrap");
        pnlMoveCorrection.add(lblMinMovement, "");
        pnlMoveCorrection.add(spnMinMovement, "");
        pnlMoveCorrection.add(new Label("\u00B5m (\u00B1)"), "");

        // add panels to tab
        add(pnlGeneralOptions, "");
        //add(pnlAcqActiveOptions, "wrap");
        //add(pnlAcqSetupOptions, "");
        //add(pnlMoveCorrection, "wrap"); // TODO: add planar correction
    }

    private void setSetupOptionsState(final boolean state) {
        lblMaxOffsetSetup_.setEnabled(state);
        spnMaxOffsetSetup_.setEnabled(state);
        lblMaxOffsetSetupUm_.setEnabled(state);

    }

    private void createEventHandlers() {

        // general autofocus settings
        cbxShowImages_.registerListener(e -> {
            //System.out.println("cbxShowImages_: " + cbxShowImages_.isSelected());
        });

        cbxShowPlot_.registerListener(e -> {
            //System.out.println("cbxShowPlot_: " + cbxShowPlot_.isSelected());
        });

        spnNumImages_.registerListener(e ->
                model_.acquisitions().settingsBuilder()
                        .autofocusSettingsBuilder().numImages(spnNumImages_.getInt()));

        spnStepSize_.registerListener(e ->
                model_.acquisitions().settingsBuilder()
                        .autofocusSettingsBuilder().stepSize(spnStepSize_.getDouble()));

        spnMinimumR2_.registerListener(e -> {
            model_.acquisitions().settingsBuilder()
                    .autofocusSettingsBuilder().r2(spnMinimumR2_.getDouble());
        });

        cmbAutofocusMode_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().autofocusSettingsBuilder()
                    .mode(AutofocusMode.fromString(cmbAutofocusMode_.getSelected()));
        });

        cmbScoringAlgorithm_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().autofocusSettingsBuilder()
                    .scoringAlgorithm(AutofocusType.fromString(cmbScoringAlgorithm_.getSelected()));
        });

        cmbFittingFunction_.registerListener(e -> {
            model_.acquisitions().settingsBuilder().autofocusSettingsBuilder()
                    .fit(AutofocusFitType.fromString(cmbFittingFunction_.getSelected()));
        });

        // autofocus options during acquisition
        cbxAutofocusEveryPass_.registerListener(e -> {
            //System.out.println("cbxAutofocusEveryPass_: " + cbxAutofocusEveryPass_.isSelected());
        });
        cbxAutofocusBeforeAcq_.registerListener(e -> {
            //System.out.println("cbxAutofocusBeforeAcq_: " + cbxAutofocusBeforeAcq_.isSelected());
        });
        cmbAutofocusChannel_.registerListener(e -> {
            //System.out.println("cmbAutofocusChannel_");
        });

        spnAutofocusEveryX_.registerListener(e -> {

        });

        spnMaxOffsetSetup_.registerListener(e -> {

        });

        // autofocus options during setup
        cbxAutoUpdateFocusFound_.registerListener(e ->
                setSetupOptionsState(cbxAutoUpdateFocusFound_.isSelected()));
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
