package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.ChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

public interface ChannelSettings {

    boolean enabled();

    int count();

    int numGroups();

    String group();

    ChannelMode mode();

    String[] groupNames();

    ChannelSpec[] used();

    ChannelSpec[] data();

    interface Builder {

        Builder enabled(final boolean state);

        Builder group(final String group);

        Builder mode(final ChannelMode mode);

        Builder data(final ChannelSpec[] channels);

        ChannelSettings build();
    }

}
