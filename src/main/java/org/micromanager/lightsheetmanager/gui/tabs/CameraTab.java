package org.micromanager.lightsheetmanager.gui.tabs;

import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.gui.components.Button;
import org.micromanager.lightsheetmanager.gui.components.ListeningPanel;
import org.micromanager.lightsheetmanager.gui.components.Panel;
import org.micromanager.lightsheetmanager.gui.components.Spinner;
import org.micromanager.lightsheetmanager.model.devices.cameras.CameraBase;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CameraTab extends Panel implements ListeningPanel {

    private Button btnUnchangedROI_;
    private Button btnFullROI_;
    private Button btnHalfROI_;
    private Button btnQuarterROI_;
    private Button btnEigthROI_;
    private Button btnCustomROI_;
    private Button btnCurrentROI_;

    private Spinner spnOffsetX_;
    private Spinner spnOffsetY_;
    private Spinner spnWidth_;
    private Spinner spnHeight_;

    private final LightSheetManager model_;

    public CameraTab(final LightSheetManager model) {;
        model_ = Objects.requireNonNull(model);
        createUserInterface();
        createEventHandlers();
    }

    private void createUserInterface() {
        final JLabel lblTitle = new JLabel("Camera Settings");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        final Panel pnlROI = new Panel("Imaging ROI");

        final JLabel lblOffsetX = new JLabel("Offset X:");
        final JLabel lblOffsetY = new JLabel("Offset Y:");
        final JLabel lblWidth = new JLabel("Width:");
        final JLabel lblHeight = new JLabel("Height:");

        btnUnchangedROI_ = new Button("Unchanged", 140, 30);
        btnFullROI_ = new Button("Full", 70, 30);
        btnHalfROI_ = new Button("1/2", 70, 30);
        btnQuarterROI_ = new Button("1/4", 70, 30);
        btnEigthROI_ = new Button("1/8", 70, 30);
        btnCustomROI_ = new Button("Custom", 140, 30);
        btnCurrentROI_ = new Button("Get Current ROI", 140, 30);

        spnOffsetX_ = Spinner.createIntegerSpinner(0, 0, Integer.MAX_VALUE, 1, 6);
        spnOffsetY_ = Spinner.createIntegerSpinner(0, 0, Integer.MAX_VALUE, 1, 6);
        spnWidth_ = Spinner.createIntegerSpinner(0, 0, Integer.MAX_VALUE, 1, 6);
        spnHeight_ = Spinner.createIntegerSpinner(0, 0, Integer.MAX_VALUE, 1, 6);

        pnlROI.add(btnUnchangedROI_, "span 2, wrap");
        pnlROI.add(btnFullROI_, "");
        pnlROI.add(btnHalfROI_, "wrap");
        pnlROI.add(btnQuarterROI_, "");
        pnlROI.add(btnEigthROI_, "wrap");
        pnlROI.add(btnCustomROI_, "span 2, wrap");
        pnlROI.add(lblOffsetX, "");
        pnlROI.add(spnOffsetX_, "wrap");
        pnlROI.add(lblOffsetY, "");
        pnlROI.add(spnOffsetY_, "wrap");
        pnlROI.add(lblWidth, "");
        pnlROI.add(spnWidth_, "wrap");
        pnlROI.add(lblHeight, "");
        pnlROI.add(spnHeight_, "wrap");
        pnlROI.add(btnCurrentROI_, "span 2");

        add(lblTitle, "wrap");
        add(pnlROI, "wrap");
    }

    // TODO: should change roi for all cameras?
    private void createEventHandlers() {
        // roi is full camera
        btnFullROI_.registerListener(() -> applyROI(this::binnedSensor));

        // roi 1/2
        btnHalfROI_.registerListener(() -> applyROI(c -> computeCenterRectangle(binnedSensor(c), 2)));

        // roi 1/4
        btnQuarterROI_.registerListener(() -> applyROI(c -> computeCenterRectangle(binnedSensor(c), 4)));

        // roi 1/8
        btnEigthROI_.registerListener(() -> applyROI(c -> computeCenterRectangle(binnedSensor(c), 8)));

        // set custom roi
        btnCustomROI_.registerListener(() -> applyROI(c -> customROI()));

        // populate spinner with current roi
        btnCurrentROI_.registerListener(() -> {
            try {
                final Rectangle roi = model_.devices().firstImagingCamera().getROI();
                spnOffsetX_.setValue(roi.x);
                spnOffsetY_.setValue(roi.y);
                spnWidth_.setValue(roi.width);
                spnHeight_.setValue(roi.height);
            } catch (Exception e) {
                model_.studio().logs().showError(
                        "No imaging camera available; check that a camera is assigned in the hardware "
                        + "configuration and set as Active on the Acquisition tab.");
            }
        });
    }

    /**
     * Applies a per-camera ROI to every imaging camera, then checks that they still agree.
     *
     * <p>The cameras must end up with the same frame size or the next acquisition kills the JVM, so
     * a partial apply is reported rather than swallowed: the vendor adapter rejects an out-of-range
     * ROI per camera, which is exactly how two cameras end up at different sizes. Failures are
     * collected and shown once, after every camera has been tried; the previous per-camera modal
     * dialog inside the loop froze the UI for seconds at a time.
     *
     * @param target computes the ROI to apply to a given camera, in binned pixels
     */
    private void applyROI(final Function<CameraBase, Rectangle> target) {
        final CameraBase[] cameras = model_.devices().imagingCameras();
        if (cameras.length == 0) {
            model_.studio().logs().showError("No imaging camera available; check that a camera is "
                    + "assigned in the hardware configuration and set as Active on the "
                    + "Acquisition tab.");
            return;
        }

        final List<String> rejected = new ArrayList<>();
        for (final CameraBase camera : cameras) {
            if (!camera.setROI(target.apply(camera))) {
                rejected.add(camera.getDeviceName());
            }
        }

        final String mismatch = CameraBase.describeFrameSizeMismatch(cameras);
        if (rejected.isEmpty() && mismatch == null) {
            return; // every camera accepted the roi and they all agree
        }

        final StringBuilder message = new StringBuilder();
        if (!rejected.isEmpty()) {
            message.append("The requested ROI was rejected by: ")
                    .append(String.join(", ", rejected))
                    .append(".\n\nROI coordinates are in binned pixels, so the largest usable "
                            + "offset and size shrink as binning grows.\n\n");
        }
        if (mismatch != null) {
            message.append("The imaging cameras no longer agree on frame size: ")
                    .append(mismatch)
                    .append(".\n\nAcquisitions are blocked until they match, because cameras with "
                            + "different frame sizes crash Micro-Manager outright.");
        }
        model_.studio().logs().showError(message.toString().trim());
    }

    /**
     * Returns this camera's sensor size in binned pixels, i.e. the largest ROI it can be given.
     *
     * <p>{@code core.setROI()} takes binned coordinates while
     * {@link CameraBase#getResolution()} deliberately reports unbinned pixels, so the sensor has to
     * be scaled down before it is used as an ROI. Skipping this made every preset out of range at
     * binning above 1: a 2400 px sensor at 2x2 binning only addresses 1200.
     */
    private Rectangle binnedSensor(final CameraBase camera) {
        final Rectangle sensor = camera.getResolution();
        final int binning = Math.max(1, camera.getBinning());
        return new Rectangle(0, 0, sensor.width / binning, sensor.height / binning);
    }

    // Returns the custom ROI set by the spinners.
    private Rectangle customROI() {
        return new Rectangle(
                spnOffsetX_.getInt(), spnOffsetY_.getInt(),
                spnWidth_.getInt(), spnHeight_.getInt()
        );
    }

    // Computes the ROI based on a scaling factor.
    private Rectangle computeCenterRectangle(final Rectangle rect, final int scale) {
        if (scale < 1) {
            model_.studio().logs().showError("scale must be > 1; could not compute ROI!");
            return rect;
        }
        if (rect.x != 0 || rect.y != 0) {
            model_.studio().logs().showError("position must be (0, 0); could not compute ROI!");
            return rect;
        }
        final int width = rect.width / scale;
        final int height = rect.height / scale;
        final int x = (rect.width - width) / 2;
        final int y = (rect.height - height) / 2;
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }
}
