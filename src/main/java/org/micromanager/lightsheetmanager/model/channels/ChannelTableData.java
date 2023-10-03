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
        channelGroup_ = "None";
    }

    public ChannelTableData(final ChannelSpec[] channels, final String channelGroup) {
        channels_ = new ArrayList<>();
        if (channels != null) {
            Collections.addAll(channels_, channels);
        }
        channelGroup_ = channelGroup;
    }

    public ArrayList<ChannelSpec> getChannels() {
        return channels_;
    }

    public ChannelSpec[] getChannelArray() {
        return channels_.toArray(new ChannelSpec[0]);
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

    public void printChannelData() {
        System.out.println("[ChannelTableData]");
        for (ChannelSpec channel : channels_) {
            System.out.println(channel);
        }
    }
}
