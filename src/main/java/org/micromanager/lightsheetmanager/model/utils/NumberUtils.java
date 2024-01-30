package org.micromanager.lightsheetmanager.model.utils;

import org.apache.commons.math3.util.Precision;

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
     *
     * @param f
     * @param place number of places after decimal point, between 0 and 9
     * @return
     */
    public static float roundFloatToPlace(float f, int place) {
        if (place < 0) throw new IllegalArgumentException();
        if (place > 9) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, place);
        return ((float) Math.round(f * factor)) / factor;
    }

    /**
     * Return the value rounded to the specified decimal places.
     *
     * @param value the value to round
     * @param place number of places after decimal point, between 0 and 9
     * @return the rounded value
     */
    public static double roundDoubleToPlace(final double value, final int place) {
        if (place < 0 || place > 9) {
            throw new IllegalArgumentException();
        }
        long factor = (long) Math.pow(10, place);
        return ((double) Math.round(value * factor)) / factor;
    }

    /**
     * "rounds up" to nearest increment of 0.25, e.g. 0.0 goes to 0.0 but 0.01 goes to 0.25
     * @param f
     * @return
     */
    public static float ceilToQuarterMs(float f) {
        return (float) (Math.ceil(f*4)/4);
    }

    /**
     * rounds to nearest increment of 0.25
     * @param f
     * @return
     */
    public static float roundToQuarterMs(float f) {
        return ((float) Math.round(f*4))/4;
    }

    /**
     * Tests whether a float is outside the range set by two others.  Don't need to know
     *   which of the two range-specifying numbers is minimum and which is maximum.
     * @param num
     * @param end1
     * @param end2
     * @return
     */
    public static boolean outsideRange(float num, float end1, float end2) {
        return (num > Math.max(end1, end2) || num < Math.min(end1, end2));
    }

    /**
     * Tests whether a float is outside the range set by two others.  Don't need to know
     *   which of the two range-specifying numbers is minimum and which is maximum.
     * @param num
     * @param end1
     * @param end2
     * @return
     */
    public static boolean outsideRange(double num, double end1, double end2) {
        return (num > Math.max(end1, end2) || num < Math.min(end1, end2));
    }

    /**
     * Tests whether a float is outside the range set by two others.  Don't need to know
     *   which of the two range-specifying numbers is minimum and which is maximum.
     * @param num
     * @param end1
     * @param end2
     * @return
     */
    public static boolean outsideRange(int num, int end1, int end2) {
        return (num > Math.max(end1, end2) || num < Math.min(end1, end2));
    }
}
