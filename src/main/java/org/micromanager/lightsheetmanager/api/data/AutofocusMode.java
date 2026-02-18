package org.micromanager.lightsheetmanager.api.data;

/**
 * The autofocus mode for the general autofocus settings.
 */
public enum AutofocusMode {
    FIXED_PIEZO_SWEEP_SLICE("Fixed piezo, sweep slice"),
    FIXED_SLICE_SWEEP_PIEZO("Fixed slice, sweep piezo");

    private final String name_;

    AutofocusMode(final String name) {
        name_ = name;
    }

    @Override
    public String toString() {
        return name_;
    }

}
