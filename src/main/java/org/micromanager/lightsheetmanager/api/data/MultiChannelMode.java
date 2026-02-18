package org.micromanager.lightsheetmanager.api.data;

public enum MultiChannelMode {
    VOLUME("Every Volume"),
    VOLUME_HW("Every Volume (PLogic)"),
    SLICE_HW("Every Slice (PLogic)");

    private final String text_;

    MultiChannelMode(final String text) {
        text_ = text;
    }

    @Override
    public String toString() {
        return text_;
    }

}
