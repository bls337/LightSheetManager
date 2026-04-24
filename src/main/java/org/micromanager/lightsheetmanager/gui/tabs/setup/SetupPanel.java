package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;

import java.util.Objects;

/**
 * A setup panel for diSPIM.
 */
public class SetupPanel extends Panel implements ListeningPanel {

    private CalibrationPanel piezoPanel_;
    private LightSheetPanel beamSheetPanel_;

    private PositionPanel positionPanel_;

    private JoystickPanel joystickPanel_;
    private ScannerPanel scannerPanel_;
    private CameraPanel cameraPanel_;

    private Panel leftPanel_;
    private Panel rightPanel_;

    private final int pathNum_;

    private SingleAxisPanel singleAxisPanel_;

    private final LightSheetManager model_;

    public SetupPanel(final LightSheetManager model, final int pathNum) {
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;

       setMigLayout(
             "",
             "[]0[]",
             "[]5[]"
       );

        // layout panels
        leftPanel_ = new Panel();
        rightPanel_ = new Panel();

        beamSheetPanel_ = new LightSheetPanel(model_, pathNum);
        positionPanel_ = new PositionPanel(model_, pathNum);
        piezoPanel_ = new CalibrationPanel(model_, positionPanel_, pathNum);

        joystickPanel_ = new JoystickPanel(model_);
        scannerPanel_ = new ScannerPanel(model_);
        cameraPanel_ = new CameraPanel(model_);

        // TODO: add joystick panel back in
        leftPanel_.add(scannerPanel_, "growx, wrap");
        leftPanel_.add(joystickPanel_, "growx, wrap");
        if (model_.devices().adapter().geometry() == GeometryType.SCAPE) {
            singleAxisPanel_ = new SingleAxisPanel(model_);
            leftPanel_.add(singleAxisPanel_, "growx, wrap");
        }
        leftPanel_.add(cameraPanel_, "growx, wrap");

        rightPanel_.add(positionPanel_, "growy");
        rightPanel_.add(piezoPanel_, "growy, wrap");
        rightPanel_.add(beamSheetPanel_, "span 2, growx, wrap");

        add(leftPanel_, "");
        add(rightPanel_, "aligny top");
    }

    public LightSheetPanel getLightSheetPanel() {
        return beamSheetPanel_;
    }

    public int getPathNum() {
        return pathNum_;
    }

    @Override
    public void selected() {
        scannerPanel_.selected();
    }

    @Override
    public void unselected() {

    }
}
