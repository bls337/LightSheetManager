package org.micromanager.lightsheetmanager.gui.tabs.setup;


import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
import org.micromanager.lightsheetmanager.gui.data.Icons;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;

import javax.swing.JLabel;
import java.awt.Dimension;
import java.util.Objects;

public class PiezoCalibrationPanel extends Panel {

    private Button btnTwoPoint_;
    private Button btnUpdate_;
    private Button btnRunAutofocus_;

    private JLabel lblSlopeValue_;
    private JLabel lblOffsetValue_;
    private TextField txtSlope_;
    private TextField txtOffset_;

    private Button btnStepUp_;
    private Button btnStepDown_;
    private TextField txtStepSize_;

    private final int pathNum_;

    private boolean isUsingPLogic_;

    private final LightSheetManager model_;

    public PiezoCalibrationPanel(final LightSheetManager model, final int pathNum) {
        super("Galvo Piezo Calibration");
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        isUsingPLogic_ = model_.devices().isUsingPLogic();

        final JLabel lblSlope = new JLabel("Galvo constant:");
        final JLabel lblOffset = new JLabel("Piezo offset:");
//        final JLabel lblSlope = new JLabel("Slope:");
//        final JLabel lblOffset = new JLabel("Offset:");
        final JLabel lblStepSize = new JLabel("Step Size:");

        // default values
        lblSlopeValue_ = new JLabel(String.format("%.3f μm/°", 0.0));
        lblOffsetValue_ = new JLabel(String.format("%.3f μm", 0.0));

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        // NOTE: was 80 width on dispim
        Button.setDefaultSize(100, 26);
        btnTwoPoint_ = new Button("2-point");

        if (geometryType == GeometryType.DISPIM) {
            btnUpdate_ = new Button("Update");
        } else {
            // SCAPE
            btnUpdate_ = new Button("Update", 120, 26);
        }
        btnRunAutofocus_ = new Button("Run Autofocus", 120, 26);

        final DefaultAcquisitionSettingsSCAPE acqSettings =
                model_.acquisitions().settings();

        txtSlope_ = new TextField(7);
        txtOffset_ = new TextField(7);
        txtStepSize_ = new TextField();

        final double sliceSlope = acqSettings.sliceCalibration(pathNum_).sliceSlope();
        final double sliceOffset = acqSettings.sliceCalibration(pathNum_).sliceOffset();

        txtSlope_.setText("0");
        txtOffset_.setText("0");

        lblSlopeValue_.setText(String.format("%.3f μm/°", sliceSlope));
        lblOffsetValue_.setText(String.format("%.3f μm", sliceOffset));

        Button.setDefaultSize(26, 26);
        btnStepUp_ = new Button(Icons.ARROW_UP);
        btnStepDown_ = new Button(Icons.ARROW_DOWN);

        btnRunAutofocus_.setEnabled(false);

        lblSlopeValue_.setMinimumSize(new Dimension(74, 20));
        lblOffsetValue_.setMinimumSize(new Dimension(74, 20));

        Panel pnlText = new Panel();
        pnlText.setMinimumSize(new Dimension(100, 30));
        pnlText.add(lblSlope, "");
        pnlText.add(lblSlopeValue_, "");
        pnlText.add(lblSlopeValue_, "");
        pnlText.add(txtSlope_, "wrap");
        pnlText.add(lblOffset, "");
        pnlText.add(lblOffsetValue_, "");

//        Panel pnlFields = new Panel();
//        pnlFields.setMinimumSize(new Dimension(20, 30));
       pnlText.add(txtOffset_, "");

        switch (geometryType) {
            case DISPIM:
                add(lblSlope, "");
                add(txtSlope_, "");
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
                add(pnlText, "wrap");
               // add(pnlFields, "wrap");
                add(btnUpdate_, "wrap, span 2, align center");
                add(btnRunAutofocus_, "span 2, align center");
                break;
            default:
                break;
        }
    }

    private void createEventHandlers() {

//        btnTwoPoint_.registerListener(e -> {
//
//        });

        if (isUsingPLogic_) {
            final ASIPiezo piezo = model_.devices().getDevice("ImagingFocus");
            final ASIScanner scanner = model_.devices().getDevice("IllumSlice");

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
                    txtOffset_.setText(String.format("%.3f μm", newOffset));
                    model_.studio().logs().logMessage("updated offset for view " + pathNum_ + "; new value is " +
                            newOffset + " (with channel offset of " + channelOffset + ")");
                }
            });
        }

//        btnStepUp_.registerListener(e -> {
//        });
//        btnStepDown_.registerListener(e -> {
//        });
//        txtStepSize_.registerListener(e -> {
//        });

        txtSlope_.registerListener(e -> {
            final double slope = Double.parseDouble(txtSlope_.getText());
            model_.acquisitions().settingsBuilder()
                    .sliceCalibrationBuilder(pathNum_).sliceSlope(slope);
            lblSlopeValue_.setText(String.format("%.3f μm/°", slope));
        });

        txtOffset_.registerListener(e -> {
            final double offset = Double.parseDouble(txtOffset_.getText());
            model_.acquisitions().settingsBuilder()
                    .sliceCalibrationBuilder(pathNum_).sliceOffset(offset);
            lblOffsetValue_.setText(String.format("%.3f μm", offset));
        });

        btnRunAutofocus_.registerListener(e -> {
            model_.acquisitions().autofocus().run();
        });
    }

}
