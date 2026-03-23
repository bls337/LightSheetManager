package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SheetCalibration;

import java.util.Objects;

public class DefaultSheetCalibration implements SheetCalibration {

    private final double imagingCenter_;
    private final double sheetWidth_;
    private final double sheetOffset_;
    private final boolean autoSheetWidthEnabled_;
    private final double autoSheetWidthPerPixel_;
    private final double scanSpeed_;
    private final double scanOffset_;

    private DefaultSheetCalibration(Builder builder) {
        imagingCenter_ = builder.imagingCenter_;
        sheetWidth_ = builder.sheetWidth_;
        sheetOffset_ = builder.sheetOffset_;
        autoSheetWidthEnabled_ = builder.autoSheetWidthEnabled_;
        autoSheetWidthPerPixel_ = builder.autoSheetWidthPerPixel_;
        scanSpeed_ = builder.scanSpeed_;
        scanOffset_ = builder.scanOffset_;
    }

    // Note: used by GSON library for deserialization
    private DefaultSheetCalibration() {
        this(new Builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SheetCalibration settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public double imagingCenter() {
        return imagingCenter_;
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
    public boolean autoSheetWidthEnabled() {
        return autoSheetWidthEnabled_;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultSheetCalibration other = (DefaultSheetCalibration) obj;
        return Double.compare(imagingCenter_, other.imagingCenter_) == 0 &&
                Double.compare(sheetWidth_, other.sheetWidth_) == 0 &&
                Double.compare(sheetOffset_, other.sheetOffset_) == 0 &&
                autoSheetWidthEnabled_ == other.autoSheetWidthEnabled_ &&
                Double.compare(autoSheetWidthPerPixel_, other.autoSheetWidthPerPixel_) == 0 &&
                Double.compare(scanSpeed_, other.scanSpeed_) == 0 &&
                Double.compare(scanOffset_, other.scanOffset_) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imagingCenter_, sheetWidth_, sheetOffset_,
                autoSheetWidthEnabled_, autoSheetWidthPerPixel_, scanSpeed_, scanOffset_);
    }

    @Override
    public String toString() {
        return String.format("%s[imagingCenter=%s, sheetWidth=%s, sheetOffset=%s, " +
                        "autoSheetWidthEnabled=%s, autoSheetWidthPerPixel=%s, scanSpeed=%s, scanOffset=%s]",
                getClass().getSimpleName(),
                imagingCenter_, sheetWidth_, sheetOffset_,
                autoSheetWidthEnabled_, autoSheetWidthPerPixel_, scanSpeed_, scanOffset_);
    }

    public static class Builder implements SheetCalibration.Builder {

        private double imagingCenter_ = 0.0;
        private double sheetWidth_ = 0.0;
        private double sheetOffset_ = 0.0;
        private boolean autoSheetWidthEnabled_ = false;
        private double autoSheetWidthPerPixel_ = 0.0;
        private double scanSpeed_ = 0.0;
        private double scanOffset_ = 0.0;

        private Builder() {
        }

        private Builder(final SheetCalibration settings) {
            imagingCenter_ = settings.imagingCenter();
            sheetWidth_ = settings.sheetWidth();
            sheetOffset_ = settings.sheetOffset();
            autoSheetWidthEnabled_ = settings.autoSheetWidthEnabled();
            autoSheetWidthPerPixel_ = settings.autoSheetWidthPerPixel();
            scanSpeed_ = settings.scanSpeed();
            scanOffset_ = settings.scanOffset();
        }

        // normal camera modes

        @Override
        public Builder imagingCenter(double center) {
            imagingCenter_ = center;
            return this;
        }

        @Override
        public Builder sheetWidth(double width) {
            sheetWidth_ = width;
            return this;
        }

        @Override
        public Builder sheetOffset(double offset) {
            sheetOffset_ = offset;
            return this;
        }

        @Override
        public Builder autoSheetWidthEnabled(boolean state) {
            autoSheetWidthEnabled_ = state;
            return this;
        }

        @Override
        public Builder autoSheetWidthPerPixel(double widthPerPixel) {
            autoSheetWidthPerPixel_ = widthPerPixel;
            return this;
        }

        // virtual slit camera mode

        @Override
        public Builder scanSpeed(double speed) {
            scanSpeed_ = speed;
            return this;
        }

        @Override
        public Builder scanOffset(double offset) {
            scanOffset_ = offset;
            return this;
        }

        @Override
        public DefaultSheetCalibration build() {
            return new DefaultSheetCalibration(this);
        }

    }

}
