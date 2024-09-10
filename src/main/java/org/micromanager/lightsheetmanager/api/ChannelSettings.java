package org.micromanager.lightsheetmanager.api;

import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

public interface ChannelSettings {

    interface Builder {

        Builder channelGroup(final String channelGroup);

        Builder channels();

        ChannelSettings build();
    }

    int numChannels();

    int numGroups();

    String channelGroup();

    String[] channelGroups();

    ChannelSpec[] channels();

    ChannelSpec[] usedChannels();

}
