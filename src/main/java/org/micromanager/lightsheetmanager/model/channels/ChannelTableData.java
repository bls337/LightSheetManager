package org.micromanager.lightsheetmanager.model.channels;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The data model's internal representation of the channel table.
 */
public class ChannelTableData {

    private final ArrayList<ChannelSpec> channels_;
    private String channelGroup_;

    public ChannelTableData() {
        channels_ = new ArrayList<>();
        channelGroup_ = "";
    }

    public ChannelTableData(final ChannelSpec[] channels, final String channelGroup) {
        channels_ = new ArrayList<>();
        channelGroup_ = channelGroup;
        Collections.addAll(channels_, channels);
    }

    public ChannelSpec getChannelByIndex(final int index) {
        return channels_.get(index);
    }

    public ChannelSpec[] getChannels() {
        return channels_.toArray(new ChannelSpec[0]);
    }

    public ChannelSpec[] getUsedChannels() {
        return channels_.stream()
                        .filter(ChannelSpec::isUsed)
                        .toArray(ChannelSpec[]::new);
    }

    public int getNumChannels() {
        return channels_.size();
    }

    public void addEmptyChannel() {
        channels_.add(new ChannelSpec(channelGroup_));
    }

    public void addChannel(final ChannelSpec channel) {
        channels_.add(channel);
    }

    public void removeChannel(final int index) {
        channels_.remove(index);
    }

    public void removeAllChannels() {
        channels_.clear();
    }

    public void setChannelGroup(final String channelGroup) {
        channelGroup_ = channelGroup;
    }

    public String getChannelGroup() {
        return channelGroup_;
    }

    public double getChannelOffset(final String channelName) {
        for (ChannelSpec channel : channels_) {
            if (channel.getName().equals(channelName)) {
                return channel.getOffset();
            }
        }
        return 0.0;
    }

    public void printChannelData() {
        System.out.println("[ChannelTableData]");
        for (ChannelSpec channel : channels_) {
            System.out.println(channel);
        }
    }
}
