package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.ChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

public interface ChannelSettings {

    interface Builder {

        Builder channelGroup(final String group);

        Builder channelMode(final ChannelMode mode);

        Builder channels(final ChannelSpec[] channels);

        ChannelSettings build();
    }

    int numChannels();

    int numGroups();

    String channelGroup();

    ChannelMode channelMode();

    String[] channelGroups();

    ChannelSpec[] channels();

    ChannelSpec[] allChannels();

}
