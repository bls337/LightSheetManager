package org.micromanager.lightsheetmanager.gui.tabs.navigation;

import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import mmcorej.CMMCore;
import mmcorej.DeviceType;
import org.micromanager.Studio;

import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.geom.Point2D;
import java.util.Objects;

// TODO: add tooltips for the buttons!

public class ControlPanel extends Panel {

    enum Axis {
        NONE,
        X,
        Y
    }

    private final Studio studio_;
    private final CMMCore core_;

    private String propertyName_;
    private String deviceName_;
    private DeviceType deviceType_;
    private Axis deviceAxis_;
    private String units_;

    private JLabel lblPropertyName_;
    private JLabel lblPosition_;
    private Spinner spnRelativeMove_;
    private Spinner spnAbsoluteMove_;
    private Button btnRelMovePlus_;
    private Button btnRelMoveMinus_;
    private Button btnAbsoluteMove_;
    private Button btnMoveToZero_;
    private Button btnSetZero_;

    private UpdateMethod updateMethod_;

    public ControlPanel(final Studio studio, final String propertyName, final String deviceName, final DeviceType deviceType, final Axis axis) {
        propertyName_ = Objects.requireNonNull(propertyName);
        deviceName_ = Objects.requireNonNull(deviceName);
        deviceType_ = Objects.requireNonNull(deviceType);
        deviceAxis_ = Objects.requireNonNull(axis);
        studio_ = Objects.requireNonNull(studio);
        core_ = studio.core();
        units_ = "µm"; // microns
        setUpdateMethod();
        createUserInterface();
        createEventHandlers();
    }

    public String getPropertyName() {
        return propertyName_;
    }

    public void setUnits(final String units) {
        units_ = units;
    }

    public DeviceType getDeviceType() {
        return deviceType_;
    }

    public void createUserInterface() {

        Button.setDefaultSize(120, 20);
        spnRelativeMove_ = Spinner.createDoubleSpinner(100.0, 0.0, Double.MAX_VALUE, 1.0);
        spnAbsoluteMove_ = Spinner.createDoubleSpinner(100.0, 0.0, Double.MAX_VALUE, 1.0);
        spnRelativeMove_.setColumnSize(8);
        spnAbsoluteMove_.setColumnSize(8);

        btnAbsoluteMove_ = new Button("Absolute Move", 110, 20);

        Button.setDefaultSize(40, 20);
        btnRelMovePlus_ = new Button("+");
        btnRelMoveMinus_ = new Button("-");

        Button.setDefaultSize(100, 20);

        btnMoveToZero_ = new Button("Zero", 60, 20);
        if (isStageDevice()) {
            btnSetZero_ = new Button("Set Zero", 80, 20);
        }

        lblPropertyName_ = new JLabel(propertyName_);
        lblPropertyName_.setMinimumSize(new Dimension(95, 20));

        lblPosition_ = new JLabel();
        lblPosition_.setMinimumSize(new Dimension(80, 20));
        if (isStageDevice()) {
            lblPosition_.setText("0.000 µm");
        } else {
            lblPosition_.setText("0.000");
        }

        add(lblPosition_, "");
        add(lblPropertyName_, "");
        add(spnRelativeMove_, "");
        add(btnRelMoveMinus_, "");
        add(btnRelMovePlus_, "");
        add(spnAbsoluteMove_, "gapleft 20");
        add(btnAbsoluteMove_, "");
        add(btnMoveToZero_, "gapleft 20");
        if (isStageDevice()) {
            add(btnSetZero_, "");
        }
    }

    /**
     * Attaches button functions to ui elements.
     */
    private void createEventHandlers() {
        if (deviceType_ == DeviceType.XYStageDevice) { // TODO: isMultiAxisDevice method?
            switch (deviceAxis_) {
                case X:
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeXPosition(spnRelativeMove_.getDouble()));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeXPosition(-spnRelativeMove_.getDouble()));
                    btnAbsoluteMove_.registerListener(e ->
                            setXPosition(spnAbsoluteMove_.getDouble()));
                    btnMoveToZero_.registerListener(e -> setXPosition(0.0));
                    btnSetZero_.registerListener(e -> setOriginX());
                    break;
                case Y:
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeYPosition(spnRelativeMove_.getDouble()));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeYPosition(-spnRelativeMove_.getDouble()));
                    btnAbsoluteMove_.registerListener(e ->
                            setYPosition(spnAbsoluteMove_.getDouble()));
                    btnMoveToZero_.registerListener(e -> setYPosition(0.0));
                    btnSetZero_.registerListener(e -> setOriginY());
                default:
                    break;
            }

        } else if (deviceType_ == DeviceType.StageDevice) {
            // single axis device
            btnRelMovePlus_.registerListener(e ->
                    setRelativePosition(spnRelativeMove_.getDouble()));

            btnRelMoveMinus_.registerListener(e ->
                    setRelativePosition(-spnRelativeMove_.getDouble()));

            btnAbsoluteMove_.registerListener(e ->
                    setPosition(spnAbsoluteMove_.getDouble()));

            btnMoveToZero_.registerListener(e -> setPosition(0.0));

            //if (isStageDevice()) {
            btnSetZero_.registerListener(e -> setOrigin());
            //}
        } else if (deviceType_ == DeviceType.GalvoDevice) {
            switch (deviceAxis_) {
                case X:
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeGalvoPositionX(spnRelativeMove_.getDouble()));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeGalvoPositionY(-spnRelativeMove_.getDouble()));
                    btnAbsoluteMove_.registerListener(e ->
                            setPositionGalvoX(spnAbsoluteMove_.getDouble()));
                    btnMoveToZero_.registerListener(e -> setPositionGalvoX(0.0));
                    //btnSetZero_.registerListener(e -> setOriginX());
                    break;
                case Y:
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeGalvoPositionY(spnRelativeMove_.getDouble()));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeGalvoPositionY(-spnRelativeMove_.getDouble()));
                    btnAbsoluteMove_.registerListener(e ->
                            setPositionGalvoY(spnAbsoluteMove_.getDouble()));
                    btnMoveToZero_.registerListener(e -> setPositionGalvoX(0.0));
                    //btnSetZero_.registerListener(e -> setOriginY());
                default:
                    break;
            }
        } else {
            // TODO: !!!
            //studio_.logs().logError("error!");
        }
    }

    /**
     * Sets the update method for this ControlPanel based on DeviceType.
     */
    private void setUpdateMethod() {
        if (deviceType_ == DeviceType.XYStageDevice) {
            switch (deviceAxis_) {
                case X:
                    updateMethod_ = this::getXPosition;
                    break;
                case Y:
                    updateMethod_ = this::getYPosition;
                    break;
                default:
                    //studio_.logs().showError("No update method set!");
                    break;
            }
        } else if (deviceType_ == DeviceType.GalvoDevice) {
            switch (deviceAxis_) {
                case X:
                    updateMethod_ = this::getGalvoPositionX;
                    break;
                case Y:
                    updateMethod_ = this::getGalvoPositionY;
                    break;
                default:
                    //studio_.logs().showError("No update method set!");
                    break;
            }
        } else {
            updateMethod_ = this::getPosition;
        }
    }

    private boolean isStageDevice() {
        return deviceType_ == DeviceType.XYStageDevice || deviceType_ == DeviceType.StageDevice;
    }

    private void setRelativeXYPosition(final double dx, final double dy) {
        try {
            core_.setRelativeXYPosition(deviceName_, dx, dy);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    private void setXYPosition(final double dx, final double dy) {
        try {
            core_.setXYPosition(deviceName_, dx, dy);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    /////////////////////////

    private void setRelativeXPosition(final double dx) {
        try {
            core_.setRelativeXYPosition(deviceName_, dx, 0.0);
        } catch (Exception e) {
            studio_.logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }

    private void setRelativeYPosition(final double dy) {
        try {
            core_.setRelativeXYPosition(deviceName_, 0.0, dy);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    private void setXPosition(final double dx) {
        try {
            core_.setXYPosition(deviceName_, dx, getYPosition());
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    private void setYPosition(final double dy) {
        try {
            core_.setXYPosition(deviceName_, getXPosition(), dy);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    private void setRelativePosition(final double d) {
        try {
            core_.setRelativePosition(deviceName_, d);
        } catch (Exception e) {
            studio_.logs().showError("failed to set relative position!");
        }
    }

    private void setPosition(final double d) {
        try {
            core_.setPosition(deviceName_, d);
        } catch (Exception e) {
            studio_.logs().showError("failed to set position!");
        }
    }

    //////////////////////////////

    // TODO: return 0.0 on failure?

    private double getXPosition() {
        try {
            return core_.getXPosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
            return 0.0;
        }
    }

    private double getYPosition() {
        try {
            return core_.getYPosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
            return 0.0;
        }
    }

    private double getPosition() {
        try {
            return core_.getPosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
            return 0.0;
        }
    }

    // Zeroing Methods

    private void setOriginY() {
        try {
            System.out.println("deviceName_: " + deviceName_);
            core_.setOriginY(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed to set y origin!");
        }
    }

    private void setOriginX() {
        try {
            System.out.println("deviceName_: " + deviceName_);
            core_.setOriginX(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed to set x origin!");
        }
    }

    private void setOrigin() {
        try {
            core_.setOrigin(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed to set origin!");
        }
    }

    // Home and Stop

    public void stop() {
        try {
            core_.stop(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError(deviceName_  + " stop() failed!");
        }
    }

    // TODO: needed?
    private void home() {
        try {
            core_.home(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    //// Galvo

    private void setGalvoPosition(final double dx, final double dy) {
        try {
            core_.setGalvoPosition(deviceName_, dx, dy);
        } catch (Exception e) {
            studio_.logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }

    private void setRelativeGalvoPositionX(final double dx) {
        try {
            core_.setGalvoPosition(deviceName_, dx, 0.0);
        } catch (Exception e) {
            studio_.logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }

    private void setRelativeGalvoPositionY(final double dy) {
        try {
            core_.setGalvoPosition(deviceName_, 0.0, dy);
        } catch (Exception e) {
            studio_.logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }


    private void setPositionGalvoX(final double dx) {
        try {
            core_.setGalvoPosition(deviceName_, dx, getGalvoPositionY());
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    private void setPositionGalvoY(final double dy) {
        try {
            core_.setGalvoPosition(deviceName_, getGalvoPositionX(), dy);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
    }

    private Point2D.Double getGalvoPosition() {
        Point2D.Double result = new Point2D.Double();
        try {
            result = core_.getGalvoPosition(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
        return result;
    }

    private double getGalvoPositionX() {
        double result = 0.0;
        try {
            result = core_.getGalvoPosition(deviceName_).x;
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
        return result;
    }

    private double getGalvoPositionY() {
        double result = 0.0;
        try {
            result = core_.getGalvoPosition(deviceName_).y;
        } catch (Exception e) {
            studio_.logs().showError("failed!");
        }
        return result;
    }

    /////////////
    // FIXME: support all types of devices!
    // TODO: deal with "-0.0000", and check updateMethod_ for null??
    public void updatePosition() {
        //lblPosition_.setText(String.format("%.3f ", getXPosition()) + units_);
        final String value = String.format("%.3f ", updateMethod_.update());
        EventQueue.invokeLater(() -> {
            //System.out.println("isEDT = " + SwingUtilities.isEventDispatchThread());
            lblPosition_.setText(value + units_);
        });
    }

}
