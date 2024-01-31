package org.micromanager.lightsheetmanager.model.utils;

import org.apache.commons.math3.util.Precision;

import java.math.BigDecimal;

/**
 * Utilities for dealing with double precision floating point numbers.
 */
public class NumberUtils {

    /**
     * Return true if the two doubles are equal according to Apache commons-math3 library.
     *
     * <p>Note: Does "equality" test on floats using commons-math3 library and epsilon
     * of 100*maxUlps. (before r14315 used locally-defined epsilon of 1e-12,
     * then it changed to 10*maxUlps, then to 100*maxUlps in r15867
     * (this note is from the previous version of the plugin)
     *
     * @param a the first operand
     * @param b the second operand
     * @return true if close to equal
     */
    public static boolean doublesEqual(final double a, final double b) {
        return Precision.equals(a, b, 100);
    }

    /**
     * Return the double value rounded to the number of decimal places.
     *
     * @param value the value to round
     * @param place number of decimal places
     * @return the rounded value
     */
    public static double roundToPlace(final double value, final int place) {
        return Precision.round(value, place, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Return the double value rounded up to the nearest increment of 0.25.
     * <p>Example: 0.0 goes to 0.0 but 0.01 goes to 0.25
     *
     * @param value the value to round up
     * @return the value rounded up
     */
    public static double ceilToQuarterMs(final double value) {
        return Math.ceil(value * 4) / 4;
    }

    /**
     * Return the double value rounded to the nearest increment of 0.25.
     *
     * @param value the value to round
     * @return the rounded value
     */
    public static double roundToQuarterMs(final double value) {
        return (double) Math.round(value * 4) / 4;
    }

    /**
     * Return true if the value is outside the range set by the bounds end1 and end2.
     *
     * @param value the value to check the bounds of
     * @param end1 end of range
     * @param end2 end of range
     * @return true if value is outside of range
     */
    public static boolean outsideRange(double value, double end1, double end2) {
        return value > Math.max(end1, end2) || value < Math.min(end1, end2);
    }

}
