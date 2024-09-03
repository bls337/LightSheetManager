package org.micromanager.lightsheetmanager.gui.frames;

import net.miginfocom.swing.MigLayout;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.internal.utils.WindowPositioning;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.XYZGrid;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.Objects;

public class XYZGridFrame extends JFrame {

    private Button btnEditPositionList_;
    private Button btnComputeGrid_;
    private Button btnRunOverviewAcq_;

    private CheckBox cbxUseX_;
    private CheckBox cbxUseY_;
    private CheckBox cbxUseZ_;

    private Spinner spnXStart_;
    private Spinner spnXStop_;
    private Spinner spnXDelta_;

    private Spinner spnYStart_;
    private Spinner spnYStop_;
    private Spinner spnYDelta_;

    private Spinner spnZStart_;
    private Spinner spnZStop_;
    private Spinner spnZDelta_;

    private Spinner spnOverlapYZ_;
    private CheckBox cbxClearPositions_;

    private JLabel lblXCountValue_;
    private JLabel lblYCountValue_;
    private JLabel lblZCountValue_;

    private LightSheetManager model_;

    public XYZGridFrame(final LightSheetManager model) {
        model_ = Objects.requireNonNull(model);
        WindowPositioning.setUpBoundsMemory(this, this.getClass(), this.getClass().getSimpleName());
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        setTitle("XYZ Grid");
        setIconImage(Icons.MICROSCOPE.getImage());
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10 10 10 10",
                "[]10[]",
                "[]10[]"
        ));

        Spinner.setDefaultSize(7);
        Button.setDefaultSize(160, 26);
        btnComputeGrid_ = new Button("Compute Grid");
        btnEditPositionList_ = new Button("Edit Position List...");
        btnRunOverviewAcq_ = new Button("Run Overview Acquisition");
        btnRunOverviewAcq_.setEnabled(false);

        cbxUseX_ = new CheckBox("Slices from stage coordinates", false);
        cbxUseY_ = new CheckBox("Grid in Y", false);
        cbxUseZ_ = new CheckBox("Grid in Z", false);

        // init all values to zero
        lblXCountValue_ = new JLabel("0");
        lblYCountValue_ = new JLabel("0");
        lblZCountValue_ = new JLabel("0");

        Panel.setMigLayoutDefault(
                "insets 10 10 10 10",
                "[]10[]",
                "[]10[]"
        );
        final Panel pnlX = new Panel(cbxUseX_);
        final Panel pnlY = new Panel(cbxUseY_);
        final Panel pnlZ = new Panel(cbxUseZ_);
        final Panel pnlButtons = new Panel();

        // X
        final JLabel lblXStart = new JLabel("X start [µm]:");
        final JLabel lblXStop = new JLabel("X stop [µm]:");
        final JLabel lblXDelta = new JLabel("X delta [µm]:");
        final JLabel lblXCount = new JLabel("Slice count:");

        spnXStart_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);
        spnXStop_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);
        spnXDelta_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);

        // Y
        final JLabel lblYStart = new JLabel("Y start [µm]:");
        final JLabel lblYStop = new JLabel("Y stop [µm]:");
        final JLabel lblYDelta = new JLabel("Y delta [µm]:");
        final JLabel lblYCount = new JLabel("Y count:");

        spnYStart_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);
        spnYStop_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);
        spnYDelta_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);

        // Z
        final JLabel lblZStart = new JLabel("Z start [µm]:");
        final JLabel lblZStop = new JLabel("Z stop [µm]:");
        final JLabel lblZDelta = new JLabel("Z delta [µm]:");
        final JLabel lblZCount = new JLabel("Z count:");

        spnZStart_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);
        spnZStop_ = Spinner.createDoubleSpinner(0.0, -Double.MAX_VALUE, Double.MAX_VALUE, 100.0);
        spnZDelta_ = Spinner.createDoubleSpinner(0.0,-Double.MAX_VALUE, Double.MAX_VALUE, 100.0);

        final Panel pnlSettings = new Panel("Grid Settings");
        final JLabel lblOverlap = new JLabel("Overlap (Y and Z) [%]");
        Spinner.setDefaultSize(4);
        spnOverlapYZ_ = Spinner.createIntegerSpinner(10, 0, 100, 1);
        cbxClearPositions_ = new CheckBox("Clear position list if YZ unused", false);


        pnlX.add(lblXStart, "");
        pnlX.add(spnXStart_, "wrap");
        pnlX.add(lblXStop, "");
        pnlX.add(spnXStop_, "wrap");
        pnlX.add(lblXDelta, "");
        pnlX.add(spnXDelta_, "wrap");
        pnlX.add(lblXCount, "");
        pnlX.add(lblXCountValue_, "");

        pnlY.add(lblYStart, "");
        pnlY.add(spnYStart_, "wrap");
        pnlY.add(lblYStop, "");
        pnlY.add(spnYStop_, "wrap");
        pnlY.add(lblYDelta, "");
        pnlY.add(spnYDelta_, "wrap");
        pnlY.add(lblYCount, "");
        pnlY.add(lblYCountValue_, "");

        pnlZ.add(lblZStart, "");
        pnlZ.add(spnZStart_, "wrap");
        pnlZ.add(lblZStop, "");
        pnlZ.add(spnZStop_, "wrap");
        pnlZ.add(lblZDelta, "");
        pnlZ.add(spnZDelta_, "wrap");
        pnlZ.add(lblZCount, "");
        pnlZ.add(lblZCountValue_, "");

        pnlSettings.add(lblOverlap, "split 2");
        pnlSettings.add(spnOverlapYZ_, "wrap");
        pnlSettings.add(cbxClearPositions_, "wrap");

        pnlButtons.add(btnComputeGrid_, "wrap");
        pnlButtons.add(btnEditPositionList_, "wrap");
        pnlButtons.add(btnRunOverviewAcq_, "");

        add(pnlY, "growx");
        add(pnlZ, "growx, wrap");
        add(pnlX, "growx");
        add(pnlSettings, "wrap");
        add(pnlButtons, "");

        pack(); // fit window size to layout
        setIconImage(Icons.MICROSCOPE.getImage());
    }

    private void createEventHandlers() {
        final XYZGrid xyzGrid = model_.getXYZGrid();

        // Check Boxes
        cbxUseX_.registerListener(e ->
                xyzGrid.setUseX(cbxUseX_.isSelected()));
        cbxUseY_.registerListener(e ->
                xyzGrid.setUseY(cbxUseY_.isSelected()));
        cbxUseZ_.registerListener(e ->
                xyzGrid.setUseZ(cbxUseZ_.isSelected()));

        // Spinners X
        spnXStart_.registerListener(e ->
                xyzGrid.setStartX(spnXStart_.getDouble()));
        spnXStop_.registerListener(e ->
                xyzGrid.setStopX(spnXStop_.getDouble()));
        spnXDelta_.registerListener(e ->
                xyzGrid.setDeltaX(spnXDelta_.getDouble()));

        // Spinners Y
        spnYStart_.registerListener(e ->
                xyzGrid.setStartY(spnYStart_.getDouble()));
        spnYStop_.registerListener(e ->
                xyzGrid.setStopY(spnYStop_.getDouble()));
        spnYDelta_.registerListener(e ->
                xyzGrid.setDeltaY(spnYDelta_.getDouble()));

        // Spinners Z
        spnZStart_.registerListener(e ->
                xyzGrid.setStartZ(spnZStart_.getDouble()));
        spnZStop_.registerListener(e ->
                xyzGrid.setStopZ(spnZStop_.getDouble()));
        spnZDelta_.registerListener(e ->
                xyzGrid.setDeltaZ(spnZDelta_.getDouble()));

        // Overlap
        spnOverlapYZ_.registerListener(e ->
                xyzGrid.setOverlapYZ(spnOverlapYZ_.getInt()));

        // Buttons
        btnComputeGrid_.registerListener(e ->
                xyzGrid.computeGrid());
        btnEditPositionList_.registerListener(e ->
                model_.studio().app().showPositionList());
        btnRunOverviewAcq_.registerListener(e ->
                model_.studio().logs().showError("Not implemented yet!"));
    }
}
