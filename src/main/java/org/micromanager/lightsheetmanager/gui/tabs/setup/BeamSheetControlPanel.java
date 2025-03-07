package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsSCAPE;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.CheckBox;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Slider;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.LightSheetManager;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.util.Objects;

public class BeamSheetControlPanel extends Panel {

    // first panel
    private TextField txtSlope_;
    private TextField txtOffset_;
    private Button btnPlotProfile_;
    private JLabel lblSlopeUnits2_;

    // second panel
    private CheckBox cbxAutoSheetWidth_;
    private TextField txtSheetWidth_;
    private Button btnCenterOffset_;

    private Button btnSheetWidthMinus_;
    private Button btnSheetWidthPlus_;

    private Button btnSheetOffsetMinus_;
    private Button btnSheetOffsetPlus_;

    private Slider sldSheetWidth_;
    private Slider sldSheetOffset_;

    // layout panels
    private Panel pnlFirst_;  // virtual slit camera trigger mode active
    private Panel pnlSecond_; // all other camera trigger modes

    // SCAPE only
    private JLabel lblSlopeOffset_;
    private TextField txtSheetOffset_;

    private final int pathNum_;

    // TODO: this is temporary solution until, update this var so we don't have to rebuild the acqSettings
    private double currentOffset_;

    private final LightSheetManager model_;

    public BeamSheetControlPanel(final LightSheetManager model, final int pathNum) {
        super("Light Sheet Synchronization");
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;
        createUserInterface();
        createEventHandlers();

        currentOffset_ = model_.acquisitions().settings()
                .sheetCalibration(pathNum_).sheetOffset();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        final DefaultAcquisitionSettingsSCAPE acqSettings = model_.acquisitions()
                .settings();

        //setMigLayout("", "[]10[]", "");

        pnlFirst_ = new Panel();
        pnlSecond_ = new Panel();

        // first panel for virtual slit mode
        final JLabel lblSlope = new JLabel("Speed / slope:");
        final JLabel lblOffset = new JLabel("Start / offset:");
        final JLabel lblSlopeUnits = new JLabel("μ°/px");
        final JLabel lblOffsetUnits = new JLabel("m°");

        btnPlotProfile_ = new Button("Plot Profile", 100, 26);
        txtSlope_ = new TextField();
        txtOffset_ = new TextField();

        // second panel for other camera trigger modes
        final JLabel lblSheetWidth = new JLabel("Sheet width:");
        //final JLabel lblSheetOffset = new JLabel("Sheet offset:");
        final JLabel lblSheetOffset = new JLabel("Galvo offset * 1000:");
        lblSlopeUnits2_ = new JLabel("μ°/px"); // TODO: reuse labels like this?

        cbxAutoSheetWidth_ = new CheckBox("Automatic", false);
        txtSheetWidth_ = new TextField();
        btnCenterOffset_ = new Button("Center", 100, 26);

        Button.setDefaultSize(40, 30);
        btnSheetWidthMinus_ = new Button("-");
        btnSheetWidthPlus_ = new Button("+");
        btnSheetOffsetMinus_ = new Button("-");
        btnSheetOffsetPlus_ = new Button("+");

        // TODO: set the ranges of these sliders to the micro-mirror's min and max deflection
        sldSheetWidth_ = new Slider(0, 8, 1000);
        sldSheetOffset_ = new Slider(-1, 1, 1000);

        sldSheetWidth_.setDouble(acqSettings.sheetCalibration(pathNum_).sheetWidth());
        sldSheetOffset_.setDouble(acqSettings.sheetCalibration(pathNum_).sheetOffset());

        // virtual slit trigger mode
        pnlFirst_.add(lblSlope, "");
        pnlFirst_.add(txtSlope_, "");
        pnlFirst_.add(lblSlopeUnits, "wrap");
        pnlFirst_.add(lblOffset, "");
        pnlFirst_.add(txtOffset_, "");
        pnlFirst_.add(lblOffsetUnits, "");
        pnlFirst_.add(btnPlotProfile_, "gapleft 100");

        // regular trigger modes
        switch (geometryType) {
            case DISPIM:
                pnlSecond_.add(lblSheetWidth, "");
                pnlSecond_.add(cbxAutoSheetWidth_, "");
                pnlSecond_.add(txtSheetWidth_, "");
                pnlSecond_.add(lblSlopeUnits2_, "");
                pnlSecond_.add(btnSheetWidthMinus_, "");
                pnlSecond_.add(btnSheetWidthPlus_, "");
                pnlSecond_.add(sldSheetWidth_, "wrap");
                pnlSecond_.add(lblSheetOffset, "span 3");
                pnlSecond_.add(btnCenterOffset_, "");
                pnlSecond_.add(btnSheetOffsetMinus_, "");
                pnlSecond_.add(btnSheetOffsetPlus_, "");
                pnlSecond_.add(sldSheetOffset_, "");
                break;
            case SCAPE:
                txtSheetOffset_ = new TextField(6);
                final double value = model_.acquisitions().settings()
                        .sheetCalibration(pathNum_).sheetOffset();
                txtSheetOffset_.setText(String.valueOf(value));
                //lblSlopeOffset_ = new JLabel(strValue);
                pnlSecond_.add(lblSheetOffset, "");
                //pnlSecond_.add(lblSlopeOffset_, "");
                pnlSecond_.add(txtSheetOffset_, "gapleft 20");
//                pnlSecond_.add(btnCenterOffset_, "");
//                pnlSecond_.add(btnSheetOffsetMinus_, "split 2");
//                pnlSecond_.add(btnSheetOffsetPlus_, "");
//                pnlSecond_.add(sldSheetOffset_, "");
                break;
            default:
                break;
        }

        // add panel based on camera trigger mode
        final CameraMode cameraMode = model_.acquisitions().settings().cameraMode();
        if (cameraMode == CameraMode.VIRTUAL_SLIT) {
            add(pnlFirst_, "");
        } else {
            add(pnlSecond_, "");
        }
    }

    private void createEventHandlers() {
        // first panel
        //btnPlotProfile_.registerListener(e -> {
            //System.out.println("do something here...");
        //});

        // second panel
//        cbxAutoSheetWidth_.registerListener(e -> {
//            final boolean state = cbxAutoSheetWidth_.isSelected();
//            model_.acquisitions().settingsBuilder()
//                    .sheetCalibrationBuilder(pathNum_).useAutoSheetWidth(state);
//            setEnabledSheetWidth(state);
//        });
//
//        sldSheetWidth_.registerListener(e -> {
//            final double value = sldSheetWidth_.getDouble();
//            model_.acquisitions().settingsBuilder()
//                    .sheetCalibrationBuilder(pathNum_).sheetWidth(value);
//            //System.out.println("sheetWidth value: " + value);
//        });
//
//        sldSheetOffset_.registerListener(e -> {
//            final double value = sldSheetOffset_.getDouble();
//            model_.acquisitions().settingsBuilder()
//                    .sheetCalibrationBuilder(pathNum_).sheetOffset(value);
//            final String strValue = String.format("%.3f", value);
//            currentOffset_ = value;
//            EventQueue.invokeLater(() -> {
//               txtSheetOffset_.setText(strValue);
//               lblSlopeOffset_.setText(strValue);
//            });
//            //System.out.println("sheetOffset value: " + strValue);
//        });

        txtSheetOffset_.registerListener(e -> {
            final double value = Double.parseDouble(txtSheetOffset_.getText());
            //final double total = Math.max(-1.0, Math.min(1.0, value));
            model_.acquisitions().settingsBuilder()
                    .sheetCalibrationBuilder(pathNum_).sheetOffset(value);
           // currentOffset_ = total;
//            EventQueue.invokeLater(() -> {
//                lblSlopeOffset_.setText(String.format("%.3f", total));
//                //sldSheetOffset_.setDouble(total);
//            });
        });

//        btnCenterOffset_.registerListener(e -> {
//            //System.out.println("center offset pressed");
//            model_.acquisitions().settingsBuilder()
//                    .sheetCalibrationBuilder(pathNum_).sheetOffset(0.0);
//            txtSheetOffset_.setText("0");
//            lblSlopeOffset_.setText("0");
//            sldSheetOffset_.setDouble(0);
//            currentOffset_ = 0;
//        });
//
//        // TODO: buttons
//        btnSheetOffsetMinus_.registerListener(e -> {
//            //final double value = model_.acquisitions().getAcquisitionSettings()
//            //        .sheetCalibration(pathNum_).sheetOffset() - 0.01;
//            currentOffset_ -= 0.01;
//            final double value = currentOffset_;
//            //System.out.println("value: " + value);
//            model_.acquisitions().settingsBuilder()
//                    .sheetCalibrationBuilder(pathNum_).sheetOffset(value);
//            txtSheetOffset_.setText(String.format("%.3f ", value));
//            sldSheetOffset_.setDouble(value);
//        });
//
//        btnSheetOffsetPlus_.registerListener(e -> {
////            final double value = model_.acquisitions().getAcquisitionSettings()
////                    .sheetCalibration(pathNum_).sheetOffset() + 0.01;
//            currentOffset_ += 0.01;
//            final double value = currentOffset_;
//            //System.out.println("value: " + value);
//            model_.acquisitions().settingsBuilder()
//                    .sheetCalibrationBuilder(pathNum_).sheetOffset(value);
//            txtSheetOffset_.setText(String.format("%.3f ", value));
//            sldSheetOffset_.setDouble(value);
//        });

//        btnSheetWidthMinus_.registerListener(e -> {
//        });
//
//        btnSheetWidthPlus_.registerListener(e -> {
//        });

    }

    /**
     * This is called when the camera trigger mode changes and updates the controls accordingly.
     */
    public void swapPanels(final CameraMode cameraMode) {
        if (cameraMode != CameraMode.VIRTUAL_SLIT) {
            remove(pnlFirst_);
            add(pnlSecond_, "");
        } else {
            remove(pnlSecond_);
            add(pnlFirst_, "");
        }
        revalidate();
        repaint();
    }

    /**
     * Enable or disable the sheet width controls based on the "Automatic" checkbox.
     *
     * @param state true to enable the ui components
     */
    private void setEnabledSheetWidth(final boolean state) {
        txtSheetWidth_.setEnabled(state);
        lblSlopeUnits2_.setEnabled(state);
        btnSheetWidthMinus_.setEnabled(!state);
        btnSheetWidthPlus_.setEnabled(!state);
        sldSheetWidth_.setEnabled(!state);
    }

}
