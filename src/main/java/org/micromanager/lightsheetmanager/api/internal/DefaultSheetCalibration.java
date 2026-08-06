package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.SheetCalibration;

import java.util.Objects;

public class DefaultSheetCalibration implements SheetCalibration {

    private final double imagingCenter;
    private final double sheetWidth;
    private final double sheetOffset;
    private final boolean autoSheetWidthEnabled;
    private final double autoSheetWidthPerPixel;
    private final double scanSpeed;
    private final double scanOffset;

    private DefaultSheetCalibration(Builder builder) {
        imagingCenter = builder.imagingCenter;
        sheetWidth = builder.sheetWidth;
        sheetOffset = builder.sheetOffset;
        autoSheetWidthEnabled = builder.autoSheetWidthEnabled;
        autoSheetWidthPerPixel = builder.autoSheetWidthPerPixel;
        scanSpeed = builder.scanSpeed;
        scanOffset = builder.scanOffset;
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
        return imagingCenter;
    }

    // standard camera modes

    @Override
    public double sheetWidth() {
        return sheetWidth;
    }

    @Override
    public double sheetOffset() {
        return sheetOffset;
    }

    @Override
    public boolean autoSheetWidthEnabled() {
        return autoSheetWidthEnabled;
    }

    @Override
    public double autoSheetWidthPerPixel() {
        return autoSheetWidthPerPixel;
    }

    // virtual slit camera mode

    @Override
    public double scanSpeed() {
        return scanSpeed;
    }

    @Override
    public double scanOffset() {
        return scanOffset;
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
        return Double.compare(imagingCenter, other.imagingCenter) == 0 &&
                Double.compare(sheetWidth, other.sheetWidth) == 0 &&
                Double.compare(sheetOffset, other.sheetOffset) == 0 &&
                autoSheetWidthEnabled == other.autoSheetWidthEnabled &&
                Double.compare(autoSheetWidthPerPixel, other.autoSheetWidthPerPixel) == 0 &&
                Double.compare(scanSpeed, other.scanSpeed) == 0 &&
                Double.compare(scanOffset, other.scanOffset) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imagingCenter, sheetWidth, sheetOffset,
                autoSheetWidthEnabled, autoSheetWidthPerPixel, scanSpeed, scanOffset);
    }

    @Override
    public String toString() {
        return String.format("%s[imagingCenter=%s, sheetWidth=%s, sheetOffset=%s, " +
                        "autoSheetWidthEnabled=%s, autoSheetWidthPerPixel=%s, scanSpeed=%s, scanOffset=%s]",
                getClass().getSimpleName(),
                imagingCenter, sheetWidth, sheetOffset,
                autoSheetWidthEnabled, autoSheetWidthPerPixel, scanSpeed, scanOffset);
    }

    public static class Builder implements SheetCalibration.Builder {

        private double imagingCenter = 0.0;
        private double sheetWidth = 0.0;
        private double sheetOffset = 0.0;
        private boolean autoSheetWidthEnabled = false;
        private double autoSheetWidthPerPixel = 0.0;
        private double scanSpeed = 0.0;
        private double scanOffset = 0.0;

        private Builder() {
        }

        private Builder(final SheetCalibration settings) {
            imagingCenter = settings.imagingCenter();
            sheetWidth = settings.sheetWidth();
            sheetOffset = settings.sheetOffset();
            autoSheetWidthEnabled = settings.autoSheetWidthEnabled();
            autoSheetWidthPerPixel = settings.autoSheetWidthPerPixel();
            scanSpeed = settings.scanSpeed();
            scanOffset = settings.scanOffset();
        }

        // normal camera modes

        @Override
        public Builder imagingCenter(double center) {
            imagingCenter = center;
            return this;
        }

        @Override
        public Builder sheetWidth(double width) {
            sheetWidth = width;
            return this;
        }

        @Override
        public Builder sheetOffset(double offset) {
            sheetOffset = offset;
            return this;
        }

        @Override
        public Builder autoSheetWidthEnabled(boolean state) {
            autoSheetWidthEnabled = state;
            return this;
        }

        @Override
        public Builder autoSheetWidthPerPixel(double widthPerPixel) {
            autoSheetWidthPerPixel = widthPerPixel;
            return this;
        }

        // virtual slit camera mode

        @Override
        public Builder scanSpeed(double speed) {
            scanSpeed = speed;
            return this;
        }

        @Override
        public Builder scanOffset(double offset) {
            scanOffset = offset;
            return this;
        }

        @Override
        public DefaultSheetCalibration build() {
            return new DefaultSheetCalibration(this);
        }

    }

}
