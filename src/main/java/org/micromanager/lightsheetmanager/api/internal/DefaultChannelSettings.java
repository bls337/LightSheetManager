package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.ChannelSettings;
import org.micromanager.lightsheetmanager.api.data.ChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

import java.util.Arrays;
import java.util.HashMap;

public class DefaultChannelSettings implements ChannelSettings {

    public static class Builder implements ChannelSettings.Builder {

        private boolean enabled_ = false;
        private String group_ = "";
        private ChannelMode mode_ = ChannelMode.VOLUME;
        private HashMap<String, ChannelSpec[]> groups_ = new HashMap<>();

        public Builder() {
        }

        public Builder(final DefaultChannelSettings channelSettings) {
            group_ = channelSettings.group_;
            mode_ = channelSettings.mode_;
            groups_ = channelSettings.groups_;
        }

        @Override
        public ChannelSettings.Builder enabled(final boolean state) {
            enabled_ = state;
            return this;
        }

        @Override
        public ChannelSettings.Builder channelGroup(final String group) {
            group_ = group;
            return this;
        }

        @Override
        public ChannelSettings.Builder channelMode(final ChannelMode mode) {
            mode_ = mode;
            return this;
        }

        @Override
        public ChannelSettings.Builder channels(final ChannelSpec[] channels) {
            groups_.put(group_, channels);
            return this;
        }

        @Override
        public DefaultChannelSettings build() {
            return new DefaultChannelSettings(this);
        }
    }

    private final boolean enabled_;
    private final String group_;
    private final ChannelMode mode_;
    private final HashMap<String, ChannelSpec[]> groups_;

    // default value for when the channel group key is not found
    private static final ChannelSpec[] EMPTY_CHANNELS = new ChannelSpec[0];

    private DefaultChannelSettings(Builder builder) {
        enabled_ = builder.enabled_;
        group_ = builder.group_;
        mode_ = builder.mode_;
        groups_ = builder.groups_;
    }

    public Builder copyBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean enabled() {
        return enabled_;
    }

    /**
     * Returns the number of used channels for the selected channel group.
     * <p>
     * Always defaults to 1 channel.
     *
     * @return the number of channels in the channel group
     */
    @Override
    public int numChannels() {
        return (int) Arrays.stream(groups_.getOrDefault(group_, EMPTY_CHANNELS))
                .filter(ChannelSpec::useChannel)
                .count();
    }

    /**
     * Returns the number of channel groups in the channel settings.
     *
     * @return the number of channel groups
     */
    @Override
    public int numGroups() {
        return groups_.size();
    }

    /**
     * Returns the channel group.
     *
     * @return the channel group
     */
    @Override
    public String channelGroup() {
        return group_;
    }

    /**
     * Returns the channel mode.
     *
     * @return the channel mode
     */
    @Override
    public ChannelMode channelMode() {
        return mode_;
    }

    /**
     * Returns an array of all channel groups.
     *
     * @return an array of channel groups
     */
    @Override
    public String[] channelGroups() {
        return groups_.keySet().toArray(String[]::new);
    }

    /**
     * Returns the used channels for the selected channel group.
     *
     * @return the channels for the channel group
     */
    @Override
    public ChannelSpec[] channels() {
        return Arrays.stream(groups_.getOrDefault(group_, EMPTY_CHANNELS))
                .filter(ChannelSpec::useChannel)
                .toArray(ChannelSpec[]::new);
    }

    /**
     * Returns all channels for the selected channel group.
     *
     * @return all channels for the selected channel group
     */
    @Override
    public ChannelSpec[] allChannels() {
        return groups_.getOrDefault(group_, EMPTY_CHANNELS);
    }

}
