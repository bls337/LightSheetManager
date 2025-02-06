package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.api.data.AutofocusMode;
import org.micromanager.lightsheetmanager.api.data.AutofocusType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.ComboBox;
import org.micromanager.lightsheetmanager.gui.components.Label;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;

import java.awt.Font;
import java.util.Objects;

/**
 * This tab contains autofocus related settings.
 */
public class AutofocusTab extends Panel implements ListeningPanel {

    // general autofocus options
    private CheckBox cbxShowImages_;
    private CheckBox cbxShowGraph_;
    private Spinner spnNumImages_;
    private Spinner spnStepSize_;
    //private Spinner spnToleranceUm_;
    private ComboBox cmbAutofocusMode_;
    private ComboBox cmbScoringMethod_;
    private Button btnRunAutofocus_;

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

    private final LightSheetManager model_;

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
        final Label lblStepSizeUm = new Label("Step Size [µm]:");
        final Label lblMode = new Label("Mode:");
        final Label lblScoringAlgorithm = new Label("Scoring algorithm:");
       // final Label lblToleranceUm = new Label("Tolerance [µm]");

        cbxShowImages_ = new CheckBox("Show Images", 12,
                acqSettings.autofocusSettings().showImages(), CheckBox.RIGHT);
        cbxShowGraph_ = new CheckBox("Show Graph", 12,
                acqSettings.autofocusSettings().showGraph(), CheckBox.RIGHT);

        spnNumImages_ = Spinner.createIntegerSpinner(
                acqSettings.autofocusSettings().numImages(), 0, Integer.MAX_VALUE, 1);
        spnStepSize_ = Spinner.createDoubleSpinner(
                acqSettings.autofocusSettings().stepSizeUm(), 0.0, 100.0, 1.0);
//        spnToleranceUm_ = Spinner.createDoubleSpinner(
//                acqSettings.autofocusSettings().toleranceUm(), 0.0, 1.0, 0.01);

        ComboBox.setDefaultSize(120, 20);
        cmbScoringMethod_ = new ComboBox(AutofocusType.toArray(),
                acqSettings.autofocusSettings().scoringMethod().toString());
        ComboBox.setDefaultSize(140, 20);
        cmbAutofocusMode_ = new ComboBox(AutofocusMode.toArray(),
                acqSettings.autofocusSettings().mode().toString());

        btnRunAutofocus_ = new Button("Run Autofocus", 120, 30);
        btnRunAutofocus_.setEnabled(false); // FIXME: impl autofocus

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
        lblMaxOffsetSetupUm_ = new Label("µm (±)");
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
        pnlGeneralOptions.add(cbxShowGraph_, "wrap");
        pnlGeneralOptions.add(lblNumImages, "");
        pnlGeneralOptions.add(spnNumImages_, "wrap");
        pnlGeneralOptions.add(lblStepSizeUm, "");
        pnlGeneralOptions.add(spnStepSize_, "wrap");
     //   pnlGeneralOptions.add(lblToleranceUm, "");
     //   pnlGeneralOptions.add(spnToleranceUm_, "wrap");
        pnlGeneralOptions.add(lblScoringAlgorithm, "");
        pnlGeneralOptions.add(cmbScoringMethod_, "wrap");
        pnlGeneralOptions.add(lblMode, "");
        pnlGeneralOptions.add(cmbAutofocusMode_, "wrap");
        pnlGeneralOptions.add(btnRunAutofocus_, "");

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
        pnlAcqActiveOptions.add(new Label("µm (±)"), "");

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
        pnlMoveCorrection.add(new Label("µm (±)"), "wrap");
        pnlMoveCorrection.add(lblMinMovement, "");
        pnlMoveCorrection.add(spnMinMovement, "");
        pnlMoveCorrection.add(new Label("µm (±)"), "");

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
        cbxShowImages_.registerListener(e -> model_.acquisitions().settingsBuilder()
                .autofocusSettingsBuilder().showImages(cbxShowImages_.isSelected()));

        cbxShowGraph_.registerListener(e -> model_.acquisitions().settingsBuilder()
                .autofocusSettingsBuilder().showGraph(cbxShowGraph_.isSelected()));

        spnNumImages_.registerListener(e -> model_.acquisitions().settingsBuilder()
                .autofocusSettingsBuilder().numImages(spnNumImages_.getInt()));

        spnStepSize_.registerListener(e -> model_.acquisitions().settingsBuilder()
                .autofocusSettingsBuilder().stepSizeUm(spnStepSize_.getDouble()));

     //   spnToleranceUm_.registerListener(e -> model_.acquisitions().settingsBuilder()
     //          .autofocusSettingsBuilder().toleranceUm(spnToleranceUm_.getDouble()));

        cmbAutofocusMode_.registerListener(e -> model_.acquisitions().settingsBuilder()
                .autofocusSettingsBuilder().mode(AutofocusMode.fromString(cmbAutofocusMode_.getSelected())));

        cmbScoringMethod_.registerListener(e -> model_.acquisitions().settingsBuilder()
                .autofocusSettingsBuilder().scoringMethod(AutofocusType.fromString(cmbScoringMethod_.getSelected())));

        btnRunAutofocus_.registerListener(e -> model_.acquisitions().autofocus().run());

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
