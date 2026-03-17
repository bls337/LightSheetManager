package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.ChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

import java.util.Map;

public interface ChannelSettings {

    Builder copyBuilder();

    boolean enabled();

    int count();

    int numGroups();

    String group();

    ChannelMode mode();

    String[] groupNames();

    ChannelSpec[] used();

    ChannelSpec[] data();

    /**
     * Returns an immutable shallow copy of the map.
     *
     * @return an immutable shallow copy of the map
     */
    Map<String, ChannelSpec[]> groups();

    interface Builder {

        Builder enabled(final boolean state);

        Builder group(final String group);

        Builder mode(final ChannelMode mode);

        Builder data(final ChannelSpec[] channels);

        ChannelSettings build();
    }

}
