package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.model.LightSheetManagerModel;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;

import javax.swing.JLabel;
import java.util.Objects;

public class PositionPanel extends Panel {

    private JLabel lblImagingCenterValue_;

    private JLabel lblSlicePositionValue_;
    private JLabel lblImagingPositionValue_;
    private JLabel lblIllumPositionValue_;


    private Button btnImagingCenterGo_;
    private Button btnImagingCenterSet_;

    private TextField txtSlicePosition_;
    private TextField txtImagingPosition_;
    private TextField txtIllumPosition_;

    private Button btnSliceZero_;
    private Button btnImagingZero_;

    private Button btnIllumGoHome_;
    private Button btnIllumSetHome_;

    private Button btnTestAcq_;

    private int pathNum_;

    private LightSheetManagerModel model_;

    public PositionPanel(final LightSheetManagerModel model, final int pathNum) {
        super("Positions");
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        final JLabel lblImagingCenter = new JLabel("Imaging Center:");
        final double imagingCenter = model_.acquisitions().settings()
                .sheetCalibration(pathNum_).imagingCenter();
        lblImagingCenterValue_ = new JLabel(imagingCenter + " μm");

        setMigLayout(
                "",
                "[]5[]",
                "[]5[]"
        );

        Button.setDefaultSize(50, 26);
        btnImagingCenterGo_ = new Button("Go");
        btnImagingCenterSet_ = new Button("Set");

        btnTestAcq_ = new Button("Test Acquisition", 120, 26);

        final JLabel lblSlicePosition = new JLabel("Slice Position:");
        final JLabel lblImagingPosition = new JLabel("Imaging Piezo:");
        txtSlicePosition_ = new TextField();
        txtImagingPosition_ = new TextField();

        Button.setDefaultSize(80, 26);
        btnSliceZero_ = new Button("Go to 0");
        btnImagingZero_ = new Button("Go to 0");

        final JLabel lblIllumPosition = new JLabel("Illumination Piezo:");
        txtIllumPosition_ = new TextField();

        btnIllumGoHome_ = new Button("Go Home");
        btnIllumSetHome_ = new Button("Set Home");

        lblSlicePositionValue_ = new JLabel("0.0 °");
        lblImagingPositionValue_ = new JLabel("0.0 μm");
        lblIllumPositionValue_ = new JLabel("0.0 μm");

        final ASIPiezo piezo = model_.devices().getDevice("ImagingFocus");
        final ASIScanner scanner = model_.devices().getDevice("IllumSlice");

        if (piezo != null && scanner != null) {
            final double piezoPosition = piezo.getPosition();
            final double scannerPosition = scanner.getPosition().y;
            lblImagingPositionValue_.setText(piezoPosition + " μm");
            lblSlicePositionValue_.setText(scannerPosition + " °");
        }

        txtSlicePosition_.setText("0");
        txtImagingPosition_.setText("0");
        txtIllumPosition_.setText("0");

        switch (geometryType) {
            case DISPIM:
                add(lblImagingCenter, "");
                add(lblImagingCenterValue_, "");
                add(btnImagingCenterGo_, "");
                add(btnImagingCenterSet_, "split 2");
                add(btnTestAcq_, "wrap");

                add(lblSlicePosition, "");
                add(lblSlicePositionValue_, "");
                add(txtSlicePosition_, "");
                add(btnSliceZero_, "wrap");

                add(lblImagingPosition, "");
                add(lblImagingPositionValue_, "");
                add(txtImagingPosition_, "");
                add(btnImagingZero_, "wrap");

                add(lblIllumPosition, "");
                add(lblIllumPositionValue_, "");
                add(txtIllumPosition_, "");
                add(btnIllumGoHome_, "split 2");
                add(btnIllumSetHome_, "");
                break;
            case SCAPE:
                add(lblImagingCenter, "");
                add(lblImagingCenterValue_, "");
                add(btnImagingCenterGo_, "split 2");
                add(btnImagingCenterSet_, "wrap");

                add(lblSlicePosition, "");
                add(lblSlicePositionValue_, "");
                add(txtSlicePosition_, "split 2");
                add(btnSliceZero_, "wrap");

                add(lblImagingPosition, "");
                add(lblImagingPositionValue_, "");
                add(txtImagingPosition_, "split 2");
                add(btnImagingZero_, "wrap");

                // TODO: enable when feature exists
                btnTestAcq_.setEnabled(false);
                add(btnTestAcq_, "wrap");
                break;
            default:
                break;
        }
    }

    // TODO: prevent errors if there is no piezo/scanner selected in config (null values)
    // TODO: currently set up for SCAPE geometry, compare to original diSPIM plugin
    private void createEventHandlers() {
        final ASIPiezo piezo = model_.devices().getDevice("ImagingFocus");
        final ASIScanner scanner = model_.devices().getDevice("IllumSlice");
//        System.out.println("piezo: " + piezo);
//        System.out.println("scanner: " + scanner);

        btnImagingCenterGo_.registerListener(e -> {
            // FIXME: make sure this is the same as original plugin, diSPIM also moves Scanner with computeGalvoFromPiezo
            final double imagingCenter = model_.acquisitions().settings()
                    .sheetCalibration(pathNum_).imagingCenter();
            piezo.setPosition(imagingCenter);
        });

        btnImagingCenterSet_.registerListener(e -> {
            // FIXME: check for piezo limits!
            final double piezoPosition = piezo.getPosition();
            model_.acquisitions().settingsBuilder()
                    .sheetCalibrationBuilder(pathNum_).imagingCenter(piezoPosition);
            lblImagingCenterValue_.setText(Double.toString(piezoPosition));
        });

        btnImagingZero_.registerListener(e -> {
            piezo.setPosition(0.0);
            lblSlicePositionValue_.setText(piezo.getPosition() + " μm");
        });

        btnSliceZero_.registerListener(e -> {
            final double xValue = scanner.getPosition().x;
            scanner.setPosition(xValue, 0.0);
            lblSlicePositionValue_.setText(scanner.getPosition().y + " °");
        });

        // FIXME: find a better way to check for devices existing
        if (scanner != null && piezo != null) {
            txtImagingPosition_.registerListener(e -> {
                System.out.println("imaging position update: " + txtImagingPosition_.getText());
                piezo.setPosition(Double.parseDouble(txtImagingPosition_.getText()));
                lblImagingPositionValue_.setText(piezo.getPosition() + " μm");
                //lblImagingPositionValue_.setText(txtImagingPosition_.getText() + " μm");
            });

            txtSlicePosition_.registerListener(e -> {
                System.out.println("slice y position update: " + txtSlicePosition_.getText());
                final double xValue = scanner.getPosition().x;
                scanner.setPosition(xValue, Double.parseDouble(txtSlicePosition_.getText()));
                lblSlicePositionValue_.setText(scanner.getPosition().y + " °");
                //lblSlicePositionValue_.setText(txtSlicePosition_.getText() + " °");
            });
        }
    }
}
