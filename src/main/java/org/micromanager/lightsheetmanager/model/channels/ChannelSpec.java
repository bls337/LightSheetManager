package org.micromanager.lightsheetmanager.model.channels;

/**
 * An entry in the channel table.
 */
public class ChannelSpec {

    private boolean useChannel_; // use this channel?
    private String group_;       // configuration group
    private String name_;        // configuration setting name
    private double offset_;      // channel-specific offset (used in some acquisition modes)

    public ChannelSpec() {
        useChannel_ = false;
        group_ = "";
        name_ = "";
        offset_ = 0;
    }

    public ChannelSpec(final boolean useChannel, final String group, final String name, final double offset) {
        useChannel_ = useChannel;
        group_ = group;
        name_ = name;
        offset_ = offset;
    }

    public ChannelSpec(final ChannelSpec channel) {
        this.useChannel_ = channel.useChannel_;
        this.group_ = channel.group_;
        this.name_ = channel.name_;
        this.offset_ = channel.offset_;
    }

    public String getName() {
        return name_;
    }

    public void setName(final String name) {
        name_ = name;
    }

    public double getOffset() {
        return offset_;
    }

    public void setOffset(final double offset) {
        offset_ = offset;
    }

    public boolean isUsed() {
        return useChannel_;
    }

    public void setUsed(final boolean state) {
        useChannel_ = state;
    }

    public String getGroup() {
        return group_;
    }

    public void setGroup(final String group) {
        group_ = group;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[useChannel_=%s, group_=%s, config_=%s, offset_=%s]",
                getClass().getSimpleName(), useChannel_, group_, name_, offset_
        );
    }

}
