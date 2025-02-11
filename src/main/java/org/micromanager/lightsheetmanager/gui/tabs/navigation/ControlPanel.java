package org.micromanager.lightsheetmanager.gui.tabs.navigation;

import mmcorej.CMMCore;
import mmcorej.DeviceType;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.TextField;
import org.micromanager.lightsheetmanager.gui.utils.DialogUtils;
import org.micromanager.lightsheetmanager.model.positions.Subscriber;

import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.geom.Point2D;
import java.util.Objects;

// TODO: add tooltips for the buttons!

public class ControlPanel extends Panel implements Subscriber {

    public enum Axis {
        NONE,
        X,
        Y
    }

    public enum Units {
        MICRONS("µm"),
        DEGREES("°");

        private final String text_;

        Units(final String text) {
           text_ = text;
        }

        @Override
        public String toString() {
           return text_;
        }
    }

    private final CMMCore core_;

    // data
    private String propertyName_; // property name in device adapter
    private String deviceName_;
    private DeviceType deviceType_;
    private Axis axis_;
    private Units units_;

    // ui
    private JLabel lblPropertyName_;
    private JLabel lblPosition_;
    private TextField txtAbsoluteMove_;
    private TextField txtRelMoveStepSize_;
    private Button btnRelMovePlus_;
    private Button btnRelMoveMinus_;
    private Button btnMoveToZero_;
    private Button btnSetZero_;

    private UpdateMethod updateMethod_;
    private final LightSheetManager model_;

    public ControlPanel(final LightSheetManager model, final String propertyName, final String deviceName, final DeviceType deviceType, final Axis axis, final Units units) {
        model_ = Objects.requireNonNull(model);
        core_ = model_.studio().core();
        propertyName_ = Objects.requireNonNull(propertyName);
        deviceName_ = Objects.requireNonNull(deviceName);
        deviceType_ = Objects.requireNonNull(deviceType);
        axis_ = axis;
        units_ = units;
        setUpdateMethod();
        createUserInterface();
        createEventHandlers();
    }

    public String getPropertyName() {
        return propertyName_;
    }

    public DeviceType getDeviceType() {
        return deviceType_;
    }

    public void createUserInterface() {
        setMigLayout("", "[]5[]", "");

        // select step size start value
        String startValue = "10";
        if (units_ == Units.DEGREES) {
            startValue = "0.2";
        }

        txtAbsoluteMove_ = new TextField("0", 5);
        txtRelMoveStepSize_ = new TextField(startValue, 3);

        btnRelMovePlus_ = new Button("+", 40, 26);
        btnRelMoveMinus_ = new Button("-", 40, 26);

        btnMoveToZero_ = new Button("Go to 0", 68, 26);
        if (isStageDevice()) {
            btnSetZero_ = new Button("Set 0", 58, 26);
            btnSetZero_.setToolTipText("");
        }

        // select axis names based on device
        String propName = propertyName_;
        if (axis_ != Axis.NONE) {
            if (deviceType_ == DeviceType.GalvoDevice) {
                final String axisLetter = (axis_ == Axis.X) ? "A" : "B";
                propName = propertyName_ + ", " + axisLetter;
            } else {
                propName = propertyName_ + ", " + axis_;
            }
        }

        lblPropertyName_ = new JLabel(propName + ":");
        lblPropertyName_.setMinimumSize(new Dimension(90, 20));

        lblPosition_ = new JLabel();
        lblPosition_.setMinimumSize(new Dimension(80, 20));
        lblPosition_.setText("0.000 " + units_);

        // tooltips
        txtAbsoluteMove_.setToolTipText("Sends an absolute move command to the device.");
        txtRelMoveStepSize_.setToolTipText("The relative move step size used by the +/- buttons.");
        btnRelMovePlus_.setToolTipText("Sends a relative move command to the device.");
        btnRelMoveMinus_.setToolTipText("Sends a relative move command to the device.");
        btnMoveToZero_.setToolTipText("Move to position 0.");

        add(lblPropertyName_, "");
        add(lblPosition_, "");
        add(txtAbsoluteMove_, "");
        add(btnRelMoveMinus_, "gapleft 5");
        add(txtRelMoveStepSize_, "");
        add(btnRelMovePlus_, "");
        add(btnMoveToZero_, "gapleft 5");
        if (isStageDevice()) {
            // check for imaging prefix to exclude piezo
            if (!propertyName_.startsWith("Imaging")) {
                add(btnSetZero_, "gapleft 40");
            }
        }
    }

    /**
     * Attaches button functions to ui elements.
     */
    private void createEventHandlers() {
        model_.positions().register(this, propertyName_);

        if (deviceType_ == DeviceType.XYStageDevice) {
            switch (axis_) {
                case X:
                    txtAbsoluteMove_.registerListener(e ->
                            setXPosition(Double.parseDouble(txtAbsoluteMove_.getText())));
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeXPosition(Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeXPosition(-Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnMoveToZero_.registerListener(e -> setXPosition(0.0));
                    btnSetZero_.registerListener(e -> {
                       final boolean result = DialogUtils.showYesNoDialog(btnSetZero_, "",
                             "This will change the coordinate system. Are you sure you would like to proceed?");
                       if (result) {
                          setOriginX();
                       }
                    });
                    break;
                case Y:
                    txtAbsoluteMove_.registerListener(e ->
                            setYPosition(Double.parseDouble(txtAbsoluteMove_.getText())));
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeYPosition(Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeYPosition(-Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnMoveToZero_.registerListener(e -> setYPosition(0.0));
                    btnSetZero_.registerListener(e -> {
                       final boolean result = DialogUtils.showYesNoDialog(btnSetZero_, "",
                             "This will change the coordinate system. Are you sure you would like to proceed?");
                       if (result) {
                          setOriginY();
                       }
                    });
                    break;
                default:
                    break;
            }

        } else if (deviceType_ == DeviceType.StageDevice) {
            // single axis device
            txtAbsoluteMove_.registerListener(e ->
                    setPosition(Double.parseDouble(txtAbsoluteMove_.getText())));
            btnRelMovePlus_.registerListener(e ->
                    setRelativePosition(Double.parseDouble(txtRelMoveStepSize_.getText())));
            btnRelMoveMinus_.registerListener(e ->
                    setRelativePosition(-Double.parseDouble(txtRelMoveStepSize_.getText())));
            btnMoveToZero_.registerListener(e -> setPosition(0.0));
            btnSetZero_.registerListener(e -> {
               final boolean result = DialogUtils.showYesNoDialog(btnSetZero_, "",
                     "This will change the coordinate system. Are you sure you would like to proceed?");
               if (result) {
                  setOrigin();
               }
            });
        } else if (deviceType_ == DeviceType.GalvoDevice) {
            // do not add btnSetZero_ to GalvoDevices
            switch (axis_) {
                case X:
                    txtAbsoluteMove_.registerListener(e ->
                            setPositionGalvoX(Double.parseDouble(txtAbsoluteMove_.getText())));
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeGalvoPositionX(Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeGalvoPositionX(-Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnMoveToZero_.registerListener(e -> setPositionGalvoX(0.0));
                    break;
                case Y:
                    txtAbsoluteMove_.registerListener(e ->
                            setPositionGalvoY(Double.parseDouble(txtAbsoluteMove_.getText())));
                    btnRelMovePlus_.registerListener(e ->
                            setRelativeGalvoPositionY(Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnRelMoveMinus_.registerListener(e ->
                            setRelativeGalvoPositionY(-Double.parseDouble(txtRelMoveStepSize_.getText())));
                    btnMoveToZero_.registerListener(e -> setPositionGalvoY(0.0));
                    break;
                default:
                    break;
            }
        } else {
            // TODO: !!!
            //model_.studio().logs().logError("error!");
        }
    }

    /**
     * Sets the update method for this ControlPanel based on DeviceType.
     */
    private void setUpdateMethod() {
        if (deviceType_ == DeviceType.XYStageDevice) {
            switch (axis_) {
                case X:
                    updateMethod_ = this::getXPosition;
                    break;
                case Y:
                    updateMethod_ = this::getYPosition;
                    break;
                default:
                    //model_.studio().logs().showError("No update method set!");
                    break;
            }
        } else if (deviceType_ == DeviceType.GalvoDevice) {
            switch (axis_) {
                case X:
                    updateMethod_ = this::getGalvoPositionX;
                    break;
                case Y:
                    updateMethod_ = this::getGalvoPositionY;
                    break;
                default:
                    //model_.studio().logs().showError("No update method set!");
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
            model_.studio().logs().showError("failed!");
        }
    }

    private void setXYPosition(final double dx, final double dy) {
        try {
            core_.setXYPosition(deviceName_, dx, dy);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
    }

    /////////////////////////

    private void setRelativeXPosition(final double dx) {
        try {
            core_.setRelativeXYPosition(deviceName_, dx, 0.0);
        } catch (Exception e) {
            model_.studio().logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }

    private void setRelativeYPosition(final double dy) {
        try {
            core_.setRelativeXYPosition(deviceName_, 0.0, dy);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
    }

    private void setXPosition(final double dx) {
        try {
            core_.setXYPosition(deviceName_, dx, getYPosition());
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
    }

    private void setYPosition(final double dy) {
        try {
            core_.setXYPosition(deviceName_, getXPosition(), dy);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
    }

    private void setRelativePosition(final double d) {
        try {
            core_.setRelativePosition(deviceName_, d);
        } catch (Exception e) {
            model_.studio().logs().showError("failed to set relative position!");
        }
    }

    private void setPosition(final double d) {
        try {
            core_.setPosition(deviceName_, d);
        } catch (Exception e) {
            model_.studio().logs().showError("failed to set position!");
        }
    }

    //////////////////////////////

    // TODO: return 0.0 on failure?

    private double getXPosition() {
        try {
            return core_.getXPosition(deviceName_);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
            return 0.0;
        }
    }

    private double getYPosition() {
        try {
            return core_.getYPosition(deviceName_);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
            return 0.0;
        }
    }

    private double getPosition() {
        try {
            return core_.getPosition(deviceName_);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
            return 0.0;
        }
    }

    // Zeroing Methods

    private void setOriginY() {
        try {
            //System.out.println("deviceName_: " + deviceName_);
            core_.setOriginY(deviceName_);
        } catch (Exception e) {
            model_.studio().logs().showError("failed to set y origin!");
        }
    }

    private void setOriginX() {
        try {
            //System.out.println("deviceName_: " + deviceName_);
            core_.setOriginX(deviceName_);
        } catch (Exception e) {
            model_.studio().logs().showError("failed to set x origin!");
        }
    }

    private void setOrigin() {
        try {
            core_.setOrigin(deviceName_);
        } catch (Exception e) {
            model_.studio().logs().showError("failed to set origin!");
        }
    }

    // Home and Stop

//    public void stop() {
//        try {
//            core_.stop(deviceName_);
//        } catch (Exception e) {
//            model_.studio().logs().showError(deviceName_  + " stop() failed!");
//        }
//    }

//    // TODO: needed?
//    private void home() {
//        try {
//            core_.home(deviceName_);
//        } catch (Exception e) {
//            model_.studio().logs().showError("failed!");
//        }
//    }

    //// Galvo

    private void setGalvoPosition(final double dx, final double dy) {
        try {
            core_.setGalvoPosition(deviceName_, dx, dy);
        } catch (Exception e) {
            model_.studio().logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }

    private void setRelativeGalvoPositionX(final double dx) {
        final double x = getGalvoPositionX();
        final double y = getGalvoPositionY();
        try {
            core_.setGalvoPosition(deviceName_, x + dx, y);
        } catch (Exception e) {
            model_.studio().logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }

    private void setRelativeGalvoPositionY(final double dy) {
       final double x = getGalvoPositionX();
       final double y = getGalvoPositionY();
        try {
            core_.setGalvoPosition(deviceName_, x, y + dy);
        } catch (Exception e) {
            model_.studio().logs().showError(propertyName_ + " " + deviceName_ + " failed!");
        }
    }

    private void setPositionGalvoX(final double dx) {
        try {
            core_.setGalvoPosition(deviceName_, dx, getGalvoPositionY());
        } catch (Exception e) {
           model_.studio().logs().showError("failed!");
        }
    }

    private void setPositionGalvoY(final double dy) {
        try {
            core_.setGalvoPosition(deviceName_, getGalvoPositionX(), dy);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
    }

    private Point2D.Double getGalvoPosition() {
        Point2D.Double result = new Point2D.Double();
        try {
            result = core_.getGalvoPosition(deviceName_);
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
        return result;
    }

    private double getGalvoPositionX() {
        double result = 0.0;
        try {
            result = core_.getGalvoPosition(deviceName_).x;
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
        return result;
    }

    private double getGalvoPositionY() {
        double result = 0.0;
        try {
            result = core_.getGalvoPosition(deviceName_).y;
        } catch (Exception e) {
            model_.studio().logs().showError("failed!");
        }
        return result;
    }

    @Override
    public void update(String topic, Object value) {
       //System.out.println("topic: " + topic + " obj:" + value);
       EventQueue.invokeLater(() -> {
          lblPosition_.setText(String.format("%.3f %s", updateMethod_.update(), units_));
       });
    }

}
