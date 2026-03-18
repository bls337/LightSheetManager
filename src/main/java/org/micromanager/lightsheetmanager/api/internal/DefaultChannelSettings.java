package org.micromanager.lightsheetmanager.api.internal;

import org.micromanager.lightsheetmanager.api.ChannelSettings;
import org.micromanager.lightsheetmanager.api.data.ChannelMode;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultChannelSettings implements ChannelSettings {

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

    // Note: used by GSON library for deserialization
    private DefaultChannelSettings() {
        this(new Builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ChannelSettings settings) {
        Objects.requireNonNull(settings, "Cannot copy from null settings");
        return new Builder(settings);
    }

    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    /**
     * Return true if channels are enabled.
     *
     * @return true if channels are enabled
     */
    @Override
    public boolean enabled() {
        return enabled_;
    }

    /**
     * Returns the number of used channels in the channel group.
     *
     * @return the number of used channels in the channel group
     */
    @Override
    public int count() {
        if (enabled_) {
            return used().length;
        } else {
            return 1;
        }
    }

    /**
     * Returns the number of channel groups.
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
    public String group() {
        return group_;
    }

    /**
     * Returns the channel mode.
     *
     * @return the channel mode
     */
    @Override
    public ChannelMode mode() {
        return mode_;
    }

    /**
     * Returns an array of all channel group names.
     *
     * @return an array of channel group names
     */
    @Override
    public String[] groupNames() {
        return groups_.keySet().toArray(String[]::new);
    }

    /**
     * Returns the used channels in the channel group.
     *
     * @return the used channels in the channel group
     */
    @Override
    public ChannelSpec[] used() {
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
    public ChannelSpec[] data() {
        return groups_.getOrDefault(group_, EMPTY_CHANNELS);
    }

    @Override
    public Map<String, ChannelSpec[]> groups() {
        return Collections.unmodifiableMap(groups_);
    }

    // TODO: add groups_ to toString, equals, and hashCode methods

    @Override
    public String toString() {
        return String.format("%s[enabled=%s, group=%s, mode=%s]",
                getClass().getSimpleName(), enabled_, group_, mode_);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DefaultChannelSettings other = (DefaultChannelSettings) obj;
        return enabled_ == other.enabled_
                && group_.equals(other.group_)
                && mode_ == other.mode_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled_, group_, mode_);
    }

    public static class Builder implements ChannelSettings.Builder {

        private boolean enabled_ = false;
        private String group_ = "";
        private ChannelMode mode_ = ChannelMode.VOLUME;
        private HashMap<String, ChannelSpec[]> groups_ = new HashMap<>();

        private Builder() {
        }

        public Builder(final ChannelSettings settings) {
            enabled_ = settings.enabled();
            group_ = settings.group();
            mode_ = settings.mode();
            groups_ = new HashMap<>();
            // deep copy
            settings.groups().forEach((name, channels) -> {
                ChannelSpec[] array = new ChannelSpec[channels.length];
                for (int i = 0; i < channels.length; i++) {
                    array[i] = new ChannelSpec(channels[i]);
                }
                groups_.put(name, array);
            });
        }

        @Override
        public Builder enabled(final boolean state) {
            enabled_ = state;
            return this;
        }

        @Override
        public Builder group(final String group) {
            group_ = group;
            return this;
        }

        @Override
        public Builder mode(final ChannelMode mode) {
            mode_ = mode;
            return this;
        }

        @Override
        public Builder data(final ChannelSpec[] channels) {
            groups_.put(group_, channels);
            return this;
        }

        @Override
        public DefaultChannelSettings build() {
            return new DefaultChannelSettings(this);
        }

    }

}
