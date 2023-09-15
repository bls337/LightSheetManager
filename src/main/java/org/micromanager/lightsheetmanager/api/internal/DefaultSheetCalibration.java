package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SheetCalibration;

// TODO: put liveScanPeriod_ into main settings? it is not a per-view item
public class DefaultSheetCalibration implements SheetCalibration {

    public static class Builder implements SheetCalibration.Builder {

        private double sheetWidth_ = 0.0;
        private double sheetOffset_ = 0.0;
        private boolean useAutoSheetWidth_ = false;
        private double autoSheetWidthPerPixel_ = 0.0;
        private double scanSpeed_ = 0.0;
        private double scanOffset_ = 0.0;

        public Builder() {
        }

        private Builder(final DefaultSheetCalibration sheetCalibration) {
            sheetWidth_ = sheetCalibration.sheetWidth_;
            sheetOffset_ = sheetCalibration.sheetOffset_;
            useAutoSheetWidth_ = sheetCalibration.useAutoSheetWidth_;
            autoSheetWidthPerPixel_ = sheetCalibration.autoSheetWidthPerPixel_;
            scanSpeed_ = sheetCalibration.scanSpeed_;
            scanOffset_ = sheetCalibration.scanOffset_;
        }

        // normal camera modes

        @Override
        public SheetCalibration.Builder sheetWidth(double width) {
            sheetWidth_ = width;
            return this;
        }

        @Override
        public SheetCalibration.Builder sheetOffset(double offset) {
            sheetOffset_ = offset;
            return this;
        }

        @Override
        public SheetCalibration.Builder useAutoSheetWidth(boolean state) {
            useAutoSheetWidth_ = state;
            return this;
        }

        @Override
        public SheetCalibration.Builder autoSheetWidthPerPixel(double widthPerPixel) {
            autoSheetWidthPerPixel_ = widthPerPixel;
            return this;
        }

        // virtual slit camera mode

        @Override
        public SheetCalibration.Builder scanSpeed(double speed) {
            scanSpeed_ = speed;
            return this;
        }

        @Override
        public SheetCalibration.Builder scanOffset(double offset) {
            scanOffset_ = offset;
            return this;
        }

        @Override
        public DefaultSheetCalibration build() {
            return new DefaultSheetCalibration(this);
        }
    }

    private final double sheetWidth_;
    private final double sheetOffset_;
    private final boolean useAutoSheetWidth_;
    private final double autoSheetWidthPerPixel_;
    private final double scanSpeed_;
    private final double scanOffset_;

    private DefaultSheetCalibration(Builder builder) {
        sheetWidth_ = builder.sheetWidth_;
        sheetOffset_ = builder.sheetOffset_;
        useAutoSheetWidth_ = builder.useAutoSheetWidth_;
        autoSheetWidthPerPixel_ = builder.autoSheetWidthPerPixel_;
        scanSpeed_ = builder.scanSpeed_;
        scanOffset_ = builder.scanOffset_;
    }

    @Override
    public DefaultSheetCalibration.Builder copyBuilder() {
        return new Builder(this);
    }

    // standard camera modes

    @Override
    public double sheetWidth() {
        return sheetWidth_;
    }

    @Override
    public double sheetOffset() {
        return sheetOffset_;
    }

    @Override
    public boolean isUsingAutoSheetWidth() {
        return useAutoSheetWidth_;
    }

    @Override
    public double autoSheetWidthPerPixel() {
        return autoSheetWidthPerPixel_;
    }

    // virtual slit camera mode

    @Override
    public double scanSpeed() {
        return scanSpeed_;
    }

    @Override
    public double scanOffset() {
        return scanOffset_;
    }

}
