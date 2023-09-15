package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.internal.DefaultSheetCalibration;

/**
 * Light Sheet Synchronization on "Setup Path #" tabs.
 */
public interface SheetCalibration {

    interface Builder {

        /**
         * Sets the width of the light sheet in normal camera trigger modes.
         *
         * @param width the width of the light sheet
         */
        Builder sheetWidth(final double width);

        /**
         * Sets the offset of the light sheet in normal camera trigger modes.
         *
         * @param offset the offset
         */
        Builder sheetOffset(final double offset);

        /**
         * Automatically compute the width of the light sheet.
         *
         * @param state true to automatically set sheet width
         */
        Builder useAutoSheetWidth(final boolean state);

        /**
         * Sets the width per pixel when isUsingAutoSheetWidth is true.
         *
         * @param widthPerPixel TODO: units?
         */
        Builder autoSheetWidthPerPixel(final double widthPerPixel);

        /**
         * Sets the speed of the light sheet in the virtual slit trigger mode.
         *
         * @param speed the speed
         */
        Builder scanSpeed(final double speed);

        /**
         * Sets the offset of the light sheet in the virtual slit trigger mode.
         *
         * @param offset the offset
         */
        Builder scanOffset(final double offset);

        /**
         * Creates an immutable instance of SheetCalibration
         *
         * @return Immutable version of SheetCalibration
         */
        SheetCalibration build();

    }

    Builder copyBuilder();

    // normal camera trigger modes
    double sheetWidth();
    double sheetOffset();
    boolean isUsingAutoSheetWidth();
    double autoSheetWidthPerPixel();

    // virtual slit camera trigger mode
    double scanSpeed();
    double scanOffset();

}
