package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.ChannelSettings;
import org.micromanager.lightsheetmanager.api.data.MultiChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

import java.util.Arrays;
import java.util.HashMap;

public class DefaultChannelSettings implements ChannelSettings {

    public static class Builder implements ChannelSettings.Builder {

        private String channelGroup_ = "";
        private MultiChannelMode channelMode_ = MultiChannelMode.NONE;
        private HashMap<String, ChannelSpec[]> groups_ = new HashMap<>();

        public Builder() {
        }

        public Builder(final DefaultChannelSettings channelSettings) {
            channelGroup_ = channelSettings.channelGroup_;
            channelMode_ = channelSettings.channelMode_;
            groups_ = channelSettings.groups_;
        }

        @Override
        public ChannelSettings.Builder channelGroup(final String group) {
            channelGroup_ = group;
            return this;
        }

        @Override
        public ChannelSettings.Builder channelMode(final MultiChannelMode mode) {
            channelMode_ = mode;
            return this;
        }

        @Override
        public ChannelSettings.Builder channels(final ChannelSpec[] channels) {
            groups_.put(channelGroup_, channels);
            return this;
        }

        @Override
        public DefaultChannelSettings build() {
            return new DefaultChannelSettings(this);
        }
    }

    private final String channelGroup_;
    private final MultiChannelMode channelMode_;
    private final HashMap<String, ChannelSpec[]> groups_;

    // default value for when the channel group key is not found
    private static final ChannelSpec[] EMPTY_CHANNELS = new ChannelSpec[0];

    private DefaultChannelSettings(Builder builder) {
        channelGroup_ = builder.channelGroup_;
        channelMode_ = builder.channelMode_;
        groups_ = builder.groups_;
    }

    public Builder copyBuilder() {
        return new Builder(this);
    }

   /**
    * Returns the number of used channels for the selected channel group,
    * and returns 0 if the channel group does not exist.
    *
    * @return the number of channels in the channel group
    */
    @Override
    public int numChannels() {
        return Arrays.stream(groups_.getOrDefault(channelGroup_, EMPTY_CHANNELS))
              .filter(ChannelSpec::useChannel)
              .toArray(ChannelSpec[]::new).length;
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
        return channelGroup_;
    }

   /**
    * Returns the channel mode.
    *
    * @return the channel mode
    */
    @Override
    public MultiChannelMode channelMode() {
        return channelMode_;
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
        return Arrays.stream(groups_.getOrDefault(channelGroup_, EMPTY_CHANNELS))
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
        return groups_.getOrDefault(channelGroup_, EMPTY_CHANNELS);
    }

}
