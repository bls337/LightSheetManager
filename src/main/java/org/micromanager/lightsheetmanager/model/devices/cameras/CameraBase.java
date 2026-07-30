package org.micromanager.lightsheetmanager.model.devices.cameras;

import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.LightSheetCamera;
import org.micromanager.lightsheetmanager.api.data.CameraMode;
import org.micromanager.lightsheetmanager.model.devices.DeviceBase;

import java.awt.Rectangle;

/**
 * This is the base camera class.
 *
 * <p>Methods that need per-vendor knowledge are abstract on purpose: a camera class that
 * forgets one fails to compile. Cameras whose device library resolves to
 * {@code CameraLibrary.UNKNOWN} use {@link UnknownCamera}.
 */
public abstract class CameraBase extends DeviceBase implements LightSheetCamera {

    protected CameraMode mode_;

    public CameraBase(final Studio studio, final String deviceName) {
        super(studio, deviceName);
        mode_ = CameraMode.EDGE;
    }

    public void setExposure(final double milliseconds) {
        try {
            core_.setExposure(deviceName_, milliseconds);
        } catch (Exception e) {
            studio_.logs().logError("could not set camera exposure");
        }
    }

    /**
     * Returns this camera's exposure in milliseconds.
     */
    public double getExposure() {
        double exposure = 0.0;
        try {
            exposure = core_.getExposure(deviceName_);
        } catch (Exception e) {
            studio_.logs().logError("could not get camera exposure");
        }
        return exposure;
    }

    /**
     * Returns this camera's ROI in binned pixels, the unit {@code core.setROI()} uses.
     *
     * <p>Device-scoped: the no-argument {@code core_.getROI()} reads whichever camera the Core is
     * pointed at, which is the wrong camera on any dual-camera rig.
     */
    // TODO: take binning into account
    public Rectangle getROI() {
        Rectangle roi = new Rectangle();
        try {
            roi = core_.getROI(deviceName_);
        } catch (Exception e) {
            studio_.logs().showError("could not get camera roi");
        }
        return roi;
    }

    /**
     * Applies an ROI to this camera, in binned pixels.
     *
     * <p>Reports the outcome rather than showing it: callers apply ROIs to several cameras and must
     * be able to tell a partial apply from a clean one, because a partial apply leaves the cameras
     * disagreeing on frame size; see {@link #describeFrameSizeMismatch(CameraBase[])}. Showing a
     * dialog here also blocked the EDT once per camera.
     *
     * @param roi the ROI in binned pixels
     * @return true if the camera accepted the ROI
     */
    public boolean setROI(final Rectangle roi) {
        final boolean isLiveModeOn = studio_.live().isLiveModeOn();
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(false);
            // close the live mode window if it exists
            if (studio_.live().getDisplay() != null) {
                studio_.live().getDisplay().close();
            }
        }
        boolean accepted = true;
        try {
            core_.setROI(deviceName_, roi.x, roi.y, roi.width, roi.height);
        } catch (Exception e) {
            accepted = false;
            studio_.logs().logError("could not set roi " + roi.width + "x" + roi.height + " at ("
                    + roi.x + ", " + roi.y + ") on camera " + deviceName_ + ": " + e.getMessage());
        }
        if (isLiveModeOn) {
            studio_.live().setLiveModeOn(true);
        }
        return accepted;
    }

    /**
     * Describes how the given cameras disagree on frame size, or returns null when they agree.
     *
     * <p>Every camera's {@code StartSequenceAcquisition} re-initializes the <em>shared</em> Core
     * circular buffer to its own frame size, while the JNI image pop sizes its copy from the
     * Core-active camera's dimensions with no bounds check. Two cameras with different frame sizes
     * therefore read past the end of a buffer slot and kill the JVM outright with an
     * {@code EXCEPTION_ACCESS_VIOLATION}, not a Java exception, so nothing downstream can catch or
     * recover from it.
     *
     * <p>Compares dimensions only: MMCore exposes no per-device bytes-per-pixel accessor, so a
     * bit-depth mismatch between two cameras is not detected here.
     *
     * <p>A zero-area frame counts as a disagreement even if every camera reports one, because
     * {@link #getROI()} returns an empty rectangle when the read itself fails. Two unreadable
     * cameras would otherwise look like two matching ones and pass.
     *
     * <p>Each camera's binning is reported alongside its frame size, and unequal binning is called
     * out explicitly, because it is the usual cause and the one the ROI buttons cannot fix: the
     * presets give every camera the largest ROI valid for its own binning, which is the same
     * physical field of view but a different pixel count. No ROI makes two cameras match while
     * their binning differs, so a user who just pressed a preset needs to be told to look at
     * binning instead of pressing another one.
     *
     * @param cameras the cameras that will image together
     * @return a description of the disagreement with no trailing punctuation, so callers can embed
     *     it in a sentence, or null if every camera reports the same frame size
     */
    public static String describeFrameSizeMismatch(final CameraBase[] cameras) {
        if (cameras == null || cameras.length < 2) {
            return null; // a single camera cannot disagree with itself
        }
        final Rectangle first = cameras[0].getROI();
        final int firstBinning = binningOrUnknown(cameras[0]);
        boolean disagree = false;
        boolean binningDiffers = false;
        final StringBuilder sizes = new StringBuilder();
        for (final CameraBase camera : cameras) {
            final Rectangle roi = camera.getROI();
            final int binning = binningOrUnknown(camera);
            if (roi.width <= 0 || roi.height <= 0
                    || roi.width != first.width || roi.height != first.height) {
                disagree = true;
            }
            if (binning != UNKNOWN_BINNING && firstBinning != UNKNOWN_BINNING
                    && binning != firstBinning) {
                binningDiffers = true;
            }
            if (sizes.length() > 0) {
                sizes.append(", ");
            }
            sizes.append(camera.getDeviceName())
                    .append(" = ").append(roi.width).append("x").append(roi.height);
            if (binning != UNKNOWN_BINNING) {
                sizes.append(" at binning ").append(binning);
            }
        }
        if (!disagree) {
            return null;
        }
        if (binningDiffers) {
            sizes.append(" (the binning differs, which is the usual cause; no ROI will make them "
                    + "match until every camera uses the same binning)");
        }
        return sizes.toString();
    }

    private static final int UNKNOWN_BINNING = -1;

    /**
     * Reads a camera's binning for reporting, or returns {@link #UNKNOWN_BINNING} if it cannot.
     *
     * <p>Binning is diagnostic here, so a camera that cannot report it must not be able to break the
     * frame-size check that protects against a JVM kill. It can throw for ordinary reasons:
     * {@code UnknownCamera} throws deliberately, and every vendor's {@code getBinning()} takes
     * {@code substring(0, 1)} of a property read that yields {@code ""} when it fails.
     */
    private static int binningOrUnknown(final CameraBase camera) {
        try {
            return camera.getBinning();
        } catch (Exception e) {
            return UNKNOWN_BINNING;
        }
    }

    public void setROI() {
        // TODO: set custom roi from camera tab :: store in model
    }

    public int roiVerticalOffset(Rectangle roi, Rectangle sensor) {
        return (roi.y + roi.height / 2) - (sensor.height / 2);
    }

    public int roiReadoutRowsSplitReadout(Rectangle roi, Rectangle sensor) {
        return Math.min(
                Math.abs(roiVerticalOffset(roi, sensor)) + roi.height / 2,  // if ROI overlaps sensor mid-line
                roi.height);                                                // if ROI does not overlap mid-line
    }

    // needed for subclasses

    @Override
    public void setTriggerMode(CameraMode cameraMode) {
        mode_ = cameraMode;
    }

    @Override
    public CameraMode getTriggerMode() {
        return mode_;
    }

    @Override
    public abstract void setBinning();

    @Override
    public abstract int getBinning();

    /**
     * Returns the physical sensor size in unbinned pixels.
     *
     * <p>Binning is not applied here because readout and reset times depend on the number of
     * physical rows read, which does not change with binning.
     */
    @Override
    public abstract Rectangle getResolution();

    @Override
    public abstract double getRowReadoutTime();

    @Override
    public abstract double getReadoutTime(CameraMode cameraMode);

    @Override
    public abstract double getResetTime(CameraMode cameraMode);
}
