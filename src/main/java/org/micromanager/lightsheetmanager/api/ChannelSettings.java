package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.api.data.MultiChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

public interface ChannelSettings {

    interface Builder {

        Builder channelGroup(final String group);

        Builder channelMode(final MultiChannelMode mode);

        Builder channels(final ChannelSpec[] channels);

        ChannelSettings build();
    }

    int numChannels();

    int numGroups();

    String channelGroup();

    MultiChannelMode channelMode();

    String[] channelGroups();

    ChannelSpec[] channels();

    ChannelSpec[] usedChannels();

}
