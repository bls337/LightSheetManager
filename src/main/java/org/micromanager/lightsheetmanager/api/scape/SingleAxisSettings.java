package org.micromanager.lightsheetmanager.api.scape;


import org.micromanager.lightsheetmanager.model.devices.vendor.SingleAxis;

public interface SingleAxisSettings {

    interface Builder {

        Builder amplitude(final double amplitude);

        Builder period(final int period);

        Builder pattern(final SingleAxis.Pattern pattern);

        SingleAxisSettings build();
    }

    int period();

    double amplitude();

    SingleAxis.Pattern pattern();

}
