package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.LightSheetManager;

import java.util.Objects;

// Note: changes based on camera trigger mode

/**
 * A setup panel for diSPIM.
 */
public class SetupPanel extends Panel implements ListeningPanel {

    private PiezoCalibrationPanel piezoPanel_;
    private BeamSheetControlPanel beamSheetPanel_;

    private PositionPanel positionPanel_;

    private JoystickPanel joystickPanel_;
    private ExcitationPanel excitationPanel_;
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

        piezoPanel_ = new PiezoCalibrationPanel(model_, pathNum);
        beamSheetPanel_ = new BeamSheetControlPanel(model_, pathNum);
        positionPanel_ = new PositionPanel(model_, pathNum);

        joystickPanel_ = new JoystickPanel(model_);
        excitationPanel_ = new ExcitationPanel(model_);
        cameraPanel_ = new CameraPanel(model_);

        // TODO: add joystick panel back in
        leftPanel_.add(excitationPanel_, "growx, wrap");
        //leftPanel_.add(joystickPanel_, "growx, wrap");
        if (model_.devices().getDeviceAdapter().getMicroscopeGeometry() == GeometryType.SCAPE) {
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

    public BeamSheetControlPanel getBeamSheetPanel() {
        return beamSheetPanel_;
    }

    public int getPathNum() {
        return pathNum_;
    }

    @Override
    public void selected() {
        excitationPanel_.selected();
    }

    @Override
    public void unselected() {

    }
}
