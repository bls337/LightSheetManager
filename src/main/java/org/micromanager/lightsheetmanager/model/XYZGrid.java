package org.micromanager.lightsheetmanager.model;

import org.micromanager.MultiStagePosition;
import org.micromanager.PositionList;
import org.micromanager.StagePosition;
import org.micromanager.lightsheetmanager.LightSheetManager;
import org.micromanager.lightsheetmanager.model.devices.Stage;
import org.micromanager.lightsheetmanager.model.devices.XYStage;
import org.micromanager.lightsheetmanager.model.utils.GeometryUtils;
import org.micromanager.lightsheetmanager.model.utils.NumberUtils;

/**
 * Creates an XYZ grid and puts it in a Micro-Manager {@code PositionList}.
 * <p>
 * This adapts functionality from the Micro-Manager 1.4 ASI diSPIM plugin.
 */
public class XYZGrid {

    private boolean useX;
    private boolean useY;
    private boolean useZ;

    private double startX;
    private double startY;
    private double startZ;

    private double stopX;
    private double stopY;
    private double stopZ;

    private double deltaX;
    private double deltaY;
    private double deltaZ;

    private int overlapPercentYZ;
    private boolean clearPositions;

    public XYZGrid() {
    }

    private int updateGridXCount() {
        double delta = deltaX;
        final double range = startX - stopX;
        if (Math.signum(range) != Math.signum(delta) &&
                !NumberUtils.doublesEqual(Math.abs(range), 0.0)) {
            delta *= -1;
            // gridXDeltaField_.setValue(delta); // TODO: set value
        }
        final int count = (int)Math.ceil(range/delta) + 1;
        // gridXCount_.setText(Integer.toString(count));
        return count;
    }

    private int updateGridYCount() {
        double delta = deltaY;
        final double range = startY - stopY;
        if (Math.signum(range) != Math.signum(delta) &&
                !NumberUtils.doublesEqual(Math.abs(range), 0.0)) {
            delta *= -1;
            // gridYDeltaField_.setValue(delta); // TODO: set value
        }
        final int count = (int)Math.ceil(range/delta) + 1;
        // gridYCount_.setText(Integer.toString(count));
        return count;
    }

    private int updateGridZCount() {
        double delta = deltaZ;
        final double range = startX - stopX;
        if (Math.signum(range) != Math.signum(delta) &&
                !NumberUtils.doublesEqual(Math.abs(range), 0.0)) {
            delta *= -1;
            // gridZDeltaField_.setValue(delta); // TODO: set value
        }
        final int count = (int)Math.ceil(range/delta) + 1;
        // gridZCount_.setText(Integer.toString(count));
        return count;
    }

    /**
     * Computes grid (position list as well as slices/spacing) based on current settings
     */
    public void computeGrid(final LightSheetManager model) {

        XYStage xyStage = model.devices().device("SampleXY");
        Stage zStage = model.devices().device("SampleZ");

        final int numX = useX ? updateGridXCount() : 1;
        final int numY = useY ? updateGridYCount() : 1;
        final int numZ = useZ ? updateGridZCount() : 1;

        // computer the center of each range
        double centerX = (startX + stopX) / 2;
        double centerY = (startY + stopY) / 2;
        double centerZ = (startZ + stopZ) / 2;

        double startY = centerY - deltaY * (numY-1) / 2;
        double startZ = centerZ - deltaZ * (numZ-1) / 2;

        if (useX) {
            // TODO: update GUI with values, aliases for asb and vsb?
            final double speedFactor = GeometryUtils.getStageGeometricSpeedFactor(
                    model.acquisitions().settings().stageScan().firstViewAngle(),true);
            model.acquisitions().settingsBuilder().volumeBuilder().sliceStepSize(Math.abs(deltaX)/speedFactor);
            model.acquisitions().settingsBuilder().volumeBuilder().slicesPerView(numX);
            // move to X center if we aren't generating a position list with it
            if (!useY && !useZ) {
                xyStage.setXYPosition(centerX, xyStage.getXYPosition().y); // TODO: make convenience method?
                xyStage.waitForDevice();
            }
        } else {
            // use current X value as center; this was original behavior
            centerX = xyStage.getXYPosition().x;
        }

        // if we aren't using one axis, use the current position instead of GUI position
        if (useY && !useZ) {
            startZ = zStage.getPosition();
        }
        if (useZ && !useY) {
            startY = xyStage.getXYPosition().y; // Note: only the Y coordinate
        }

        if (!useY && !useZ && !clearPositions) {
            return; // early exit => YZ unused
        }

        // TODO: where to put prompt?
//        PositionList positionList = model_.studio().positions().getPositionList();
//        final boolean isPositionListEmpty = positionList.getNumberOfPositions() == 0;
//        if (!isPositionListEmpty) {
//            final boolean overwrite = false;
//            if (!overwrite) {
//                return; // early exit => nothing to do
//            }
//        }
        PositionList positionList = new PositionList();
        if (useY || useZ) {
            for (int iz = 0; iz < numZ; ++iz) {
                for (int iy = 0; iy < numY; ++iy) {
                    MultiStagePosition msp = new MultiStagePosition();
                    if (useY) {
                        msp.add(StagePosition.create2D(
                                xyStage.getDeviceName(),
                                centerX,
                                startY + iy * deltaY));
                    }
                    if (useZ) {
                        msp.add(StagePosition.create1D(
                                zStage.getDeviceName(),
                                startZ + iz * deltaZ));
                    }
                    msp.setLabel("Pos_" + iz + "_" + iy);
                    positionList.addPosition(msp);
                }
            }
        }
        model.studio().positions().setPositionList(positionList);
    }

    public boolean getUseX() {
        return useX;
    }

    public void setUseX(final boolean state) {
        useX = state;
    }

    public boolean getUseY() {
        return useY;
    }

    public void setUseY(final boolean state) {
        useY = state;
    }

    public boolean getUseZ() {
        return useZ;
    }

    public void setUseZ(final boolean state) {
        useZ = state;
    }

    public boolean getClearYZ() {
        return clearPositions;
    }

    public void setClearYZ(final boolean state) {
        clearPositions = state;
    }

    public void setOverlapYZ(final int value) {
        overlapPercentYZ = value;
    }

    public int getOverlapYZ() {
        return overlapPercentYZ;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(final double value) {
        startX = value;
    }

    public double getStopX() {
        return stopX;
    }

    public void setStopX(final double value) {
        stopX = value;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(final double value) {
        deltaX = value;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(final double value) {
        startY = value;
    }

    public double getStopY() {
        return stopY;
    }

    public void setStopY(final double value) {
        stopY = value;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(final double value) {
        deltaY = value;
    }

    public double getStartZ() {
        return startZ;
    }

    public void setStartZ(final double value) {
        startZ = value;
    }

    public double getStopZ() {
        return stopZ;
    }

    public void setStopZ(final double value) {
        stopZ = value;
    }

    public double getDeltaZ() {
        return deltaZ;
    }

    public void setDeltaZ(final double value) {
        deltaZ = value;
    }

}
