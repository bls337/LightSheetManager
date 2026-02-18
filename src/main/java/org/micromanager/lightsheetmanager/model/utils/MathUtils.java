package org.micromanager.lightsheetmanager.model.utils;

/**
 * Math utilities.
 */
public final class MathUtils {

    /** This class should not be instantiated. */
    private MathUtils() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    /**
     * Returns the value constrained to the range min to max.
     *
     * @param value the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return the clamped value
     */
    public static int clamp(final int value, final int min, final int max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Returns the value constrained to the range min to max.
     *
     * @param value the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return the clamped value
     */
    public static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(value, max));
    }

}
