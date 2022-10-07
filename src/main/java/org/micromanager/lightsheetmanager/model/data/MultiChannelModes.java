package org.micromanager.lightsheetmanager.model.data;

public enum MultiChannelModes {
    NONE("None"),
    VOLUME("Every Volume"),
    VOLUME_HW("Every Volume (PLogic)"),
    SLICE_HW("Every Slice (PLogic)");

    private final String text_;

    MultiChannelModes(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }
}
