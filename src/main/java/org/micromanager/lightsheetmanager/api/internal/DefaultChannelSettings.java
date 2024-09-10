package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.ChannelSettings;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

import java.util.Arrays;
import java.util.HashMap;

public class DefaultChannelSettings implements ChannelSettings {

    public static class Builder implements ChannelSettings.Builder {

        private String channelGroup_ = "";
        private HashMap<String, ChannelSpec[]> groups_ = new HashMap<>();

        public Builder() {
        }

        public Builder(final DefaultChannelSettings channelSettings) {
            channelGroup_ = channelSettings.channelGroup_;
            groups_ = channelSettings.groups_;
        }

        @Override
        public ChannelSettings.Builder channelGroup(final String channelGroup) {
            channelGroup_ = channelGroup;
            return this;
        }

        @Override
        public ChannelSettings.Builder channels() {
            //groups_ = ;
            return this;
        }

        @Override
        public DefaultChannelSettings build() {
            return new DefaultChannelSettings(this);
        }
    }

    private final String channelGroup_;
    private final HashMap<String, ChannelSpec[]> groups_;

    private DefaultChannelSettings(Builder builder) {
        channelGroup_ = builder.channelGroup_;
        groups_ = builder.groups_;
    }

    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public int numChannels() {
        return groups_.get(channelGroup_).length;
    }

    @Override
    public int numGroups() {
        return groups_.size();
    }

    @Override
    public String channelGroup() {
        return channelGroup_;
    }

    @Override
    public String[] channelGroups() {
        return groups_.keySet().toArray(new String[0]);
    }

    @Override
    public ChannelSpec[] channels() {
        return groups_.get(channelGroup_);
    }

    @Override
    public ChannelSpec[] usedChannels() {
        return Arrays.stream(groups_.get(channelGroup_))
                .filter(ChannelSpec::isUsed)
                .toArray(ChannelSpec[]::new);
    }
}
