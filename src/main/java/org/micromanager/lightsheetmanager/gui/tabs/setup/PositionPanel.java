package org.micromanager.lightsheetmanager.gui.tabs.setup;

import org.micromanager.lightsheetmanager.api.data.GeometryType;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.positions.Subscriber;

import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.geom.Point2D;
import java.util.Objects;

public class PositionPanel extends Panel implements Subscriber {

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

    //private Button btnTestAcq_;

    private final int pathNum_;

    private boolean isUsingPLogic_;

    private final LightSheetManager model_;

    public PositionPanel(final LightSheetManager model, final int pathNum) {
        super("Positions");
        model_ = Objects.requireNonNull(model);
        pathNum_ = pathNum;
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final GeometryType geometryType = model_.devices()
                .getDeviceAdapter().getMicroscopeGeometry();

        isUsingPLogic_ = model_.devices().isUsingPLogic();

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

        //btnTestAcq_ = new Button("Test Acquisition", 120, 26);

        final JLabel lblSlicePosition = new JLabel("Slice Position:");
        final JLabel lblImagingPosition = new JLabel("Imaging Piezo:");
        txtSlicePosition_ = new TextField();
        txtImagingPosition_ = new TextField();

        Button.setDefaultSize(80, 26);
        btnSliceZero_ = new Button("Go to 0", 70, 26);
        btnImagingZero_ = new Button("Go to 0", 70, 26);

        final JLabel lblIllumPosition = new JLabel("Illumination Piezo:");
        txtIllumPosition_ = new TextField();

        btnIllumGoHome_ = new Button("Go Home");
        btnIllumSetHome_ = new Button("Set Home");

        lblSlicePositionValue_ = new JLabel("0.000 °");
        lblImagingPositionValue_ = new JLabel("0.000 μm");
        lblIllumPositionValue_ = new JLabel("0.000 μm");

        lblSlicePositionValue_.setMinimumSize(new Dimension(60, 20));
        lblImagingPositionValue_ .setMinimumSize(new Dimension(60, 20));
        lblSlicePositionValue_.setMinimumSize(new Dimension(60, 20));

        // tooltips
        btnImagingCenterGo_.setToolTipText("Move \"Imaging Piezo\" position to \"Imaging Center\" value.");
        //btnImagingCenterSet_.setToolTipText("Set \"Imaging Center\" value to \"Imaging Piezo\" position. ");
        btnSliceZero_.setToolTipText("Move \"Slice Position\" position to 0 degrees.");
        btnImagingZero_.setToolTipText("Move \"Imaging Piezo\" position to 0 μm.");
        //btnTestAcq_.setToolTipText("Run a test acquisition.");

        if (isUsingPLogic_) {
            final ASIPiezo piezo = model_.devices().getDevice("ImagingFocus");
            final ASIScanner scanner = model_.devices().getDevice("IllumSlice");

            final double piezoPosition = piezo.getPosition();
            final double scannerPosition = scanner.getPosition().y;
            final String piezoPositionStr = String.format("%.3f μm", piezoPosition);
            final String scannerPositionStr = String.format("%.3f °", scannerPosition);
            lblImagingPositionValue_.setText(piezoPositionStr);
            lblSlicePositionValue_.setText(scannerPositionStr);
        }

        // test fields
        txtSlicePosition_.setText("0");
        txtImagingPosition_.setText("0");
        txtIllumPosition_.setText("0");

        switch (geometryType) {
            case DISPIM:
                add(lblImagingCenter, "");
                add(lblImagingCenterValue_, "");
                add(btnImagingCenterGo_, "");
                add(btnImagingCenterSet_, "wrap");
                //add(btnTestAcq_, "wrap");

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
                add(btnImagingCenterGo_, "wrap");
                //add(btnImagingCenterSet_, "wrap");

                add(lblSlicePosition, "");
                add(lblSlicePositionValue_, "");
                add(txtSlicePosition_, "split 2");
                add(btnSliceZero_, "wrap");

                add(lblImagingPosition, "");
                add(lblImagingPositionValue_, "");
                add(txtImagingPosition_, "split 2");
                add(btnImagingZero_, "wrap");

                //btnTestAcq_.setEnabled(false);
                //add(btnTestAcq_, "span 2, wrap");
                break;
            default:
                break;
        }
    }

    // TODO: currently set up for SCAPE geometry, compare to original diSPIM plugin
    private void createEventHandlers() {
        // TODO: find a way to refresh these values
        if (model_.devices().getDeviceAdapter().hasDevice("ImagingFocus")) {
            model_.positions().register(this, "ImagingFocus");
        }
        if (model_.devices().getDeviceAdapter().hasDevice("IllumSlice")) {
            model_.positions().register(this, "IllumSlice");
        }

        if (isUsingPLogic_) {
            final ASIPiezo piezo = model_.devices().getDevice("ImagingFocus");
            final ASIScanner scanner = model_.devices().getDevice("IllumSlice");

            btnImagingCenterSet_.registerListener(e -> {
                // FIXME: check for piezo limits!
                final double piezoPosition = piezo.getPosition();
                model_.acquisitions().settingsBuilder()
                        .sheetCalibrationBuilder(pathNum_).imagingCenter(piezoPosition);
                lblImagingCenterValue_.setText(String.format("%.3f μm", piezoPosition));
            });

            btnImagingCenterGo_.registerListener(e -> {
                // FIXME: make sure this is the same as original plugin, diSPIM also moves Scanner with computeGalvoFromPiezo
                final double imagingCenter = model_.acquisitions().settingsBuilder().build()
                        .sheetCalibration(pathNum_).imagingCenter();
                piezo.setPosition(imagingCenter);
                lblImagingPositionValue_.setText(String.format("%.3f μm", piezo.getPosition()));
            });

            btnImagingZero_.registerListener(e -> {
                piezo.setPosition(0.0);
                lblImagingPositionValue_.setText(String.format("%.3f μm", piezo.getPosition()));
            });

            btnSliceZero_.registerListener(e -> {
                final double xValue = scanner.getPosition().x;
                scanner.setPosition(xValue, 0.0);
                lblSlicePositionValue_.setText(String.format("%.3f °", scanner.getPosition().y));
            });

            txtImagingPosition_.registerListener(e -> {
                piezo.setPosition(Double.parseDouble(txtImagingPosition_.getText()));
                lblImagingPositionValue_.setText(String.format("%.3f μm", piezo.getPosition()));
            });

            txtSlicePosition_.registerListener(e -> {
                final double xValue = scanner.getPosition().x;
                scanner.setPosition(xValue, Double.parseDouble(txtSlicePosition_.getText()));
                lblSlicePositionValue_.setText(String.format("%.3f °", scanner.getPosition().y));
            });
        }
    }

    @Override
    public void update(String topic, Object value) {
        EventQueue.invokeLater(() -> {
            if (topic.equals("ImagingFocus")) {
                lblImagingPositionValue_.setText(String.format("%.3f μm", (double)value));
            } else if (topic.equals("IllumSlice")) {
                final Point2D.Double point = (Point2D.Double)value;
                lblSlicePositionValue_.setText(String.format("%.3f °", point.y));
            }
        });
    }
}
