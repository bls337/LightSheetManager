package org.micromanager.lightsheetmanager.model.positions;

public interface Publisher {
    void register(Subscriber subscriber, String topic);
}
