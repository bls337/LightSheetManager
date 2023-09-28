package org.micromanager.lightsheetmanager.gui.tabs.setup;


import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;

import javax.swing.JLabel;
import java.util.Objects;

public class PiezoCalibrationPanel extends Panel {

    private Button btnTwoPoint_;
    private Button btnUpdate_;
    private Button btnRunAutofocus_;

    private TextField txtSlope_;
    private TextField txtOffset_;

    private Button btnStepUp_;
    private Button btnStepDown_;
    private TextField txtStepSize_;

    private final int pathNum_;

    private final LightSheetManagerModel model_;

    public PiezoCalibrationPanel(final LightSheetManagerModel model, final int pathNum) {
        super("Piezo/Slice Calibration");
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        final JLabel lblSlope = new JLabel("Slope:");
        final JLabel lblOffset = new JLabel("Offset:");
        final JLabel lblStepSize = new JLabel("Step Size:");
        final JLabel lblMicronsPerDeg = new JLabel("μm/°");

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        Button.setDefaultSize(80, 26);
        btnTwoPoint_ = new Button("2-point");

        if (geometryType == GeometryType.DISPIM) {
            btnUpdate_ = new Button("Update");
        } else {
            // SCAPE
            btnUpdate_ = new Button("Update", 120, 26);
        }
        btnRunAutofocus_ = new Button("Run Autofocus", 120, 26);

        final DefaultAcquisitionSettingsDISPIM acqSettings =
                model_.acquisitions().settings();

        txtSlope_ = new TextField();
        txtOffset_ = new TextField();
        txtStepSize_ = new TextField();

        if (pathNum_ == 1) {
            txtSlope_.setText(String.valueOf(acqSettings.sliceCalibration(1).sliceSlope()));
            txtOffset_.setText(String.valueOf(acqSettings.sliceCalibration(1).sliceOffset()));
        } else {
            txtSlope_.setText(String.valueOf(acqSettings.sliceCalibration(2).sliceSlope()));
            txtOffset_.setText(String.valueOf(acqSettings.sliceCalibration(2).sliceOffset()));
        }

        Button.setDefaultSize(26, 26);
        btnStepUp_ = new Button(Icons.ARROW_UP);
        btnStepDown_ = new Button(Icons.ARROW_DOWN);

        btnRunAutofocus_.setEnabled(false);

        switch (geometryType) {
            case DISPIM:
                add(lblSlope, "");
                add(txtSlope_, "");
                add(lblMicronsPerDeg, "");
                add(btnTwoPoint_, "wrap");
                add(lblOffset, "");
                add(txtOffset_, "");
                add(new JLabel("μm"), "");
                add(btnUpdate_, "wrap");
                add(lblStepSize, "");
                add(txtStepSize_, "");
                add(new JLabel("μm"), "");
                add(btnStepDown_, "split 2");
                add(btnStepUp_, "wrap");
                add(btnRunAutofocus_, "span 3");
                break;
            case SCAPE:
                add(lblSlope, "");
                add(txtSlope_, "");
                add(lblMicronsPerDeg, "wrap");
                add(lblOffset, "");
                add(txtOffset_, "");
                add(new JLabel("μm"), "wrap");
                add(btnUpdate_, "wrap, span 3");
                add(btnRunAutofocus_, "span 3");
                break;
            default:
                break;
        }
    }

    private void createEventHandlers() {
        final DefaultAcquisitionSettingsDISPIM.Builder asb =
                model_.acquisitions().settingsBuilder();

        final ASIPiezo piezo = model_.devices().getDevice("ImagingFocus");
        final ASIScanner scanner = model_.devices().getDevice("IllumSlice");
//        System.out.println("piezo: " + piezo);
//        System.out.println("scanner: " + scanner);

        btnTwoPoint_.registerListener(e -> {

        });

        // FIXME: find a better way to check for devices existing
        if (scanner != null && piezo != null) {
            btnUpdate_.registerListener(e -> {
                if (scanner.isBeamOn()) {
                    final double rate = model_.acquisitions().settings()
                            .sliceCalibration(pathNum_).sliceSlope();
                    final double piezoPosition = piezo.getPosition();
                    final double scannerPosition = scanner.getPosition().y;
                    double channelOffset = 0.0;
                    // FIXME: update channelOffset
                    // was: channelOffset = ASIdiSPIM.getFrame().getAcquisitionPanel().getChannelOffset();
                    final double newOffset = piezoPosition - rate * scannerPosition - channelOffset;
                    txtOffset_.setText(Double.toString(newOffset));
                    model_.studio().logs().logMessage("updated offset for view " + pathNum_ + "; new value is " +
                            newOffset + " (with channel offset of " + channelOffset + ")");
                }
            });
        }

        btnStepUp_.registerListener(e -> {

        });

        btnStepDown_.registerListener(e -> {

        });

        txtSlope_.registerListener(e -> {
            asb.sliceCalibrationBuilder(pathNum_).sliceSlope(Double.parseDouble(txtSlope_.getText()));
        });

        txtOffset_.registerListener(e -> {
            asb.sliceCalibrationBuilder(pathNum_).sliceOffset(Double.parseDouble(txtOffset_.getText()));
        });

        txtStepSize_.registerListener(e -> {

        });

        btnRunAutofocus_.registerListener(e -> {
            model_.getAcquisitionEngine().getAutofocusRunner().runAutofocus();
        });
    }

}
