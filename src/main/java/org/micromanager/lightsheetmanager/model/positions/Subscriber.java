package org.micromanager.lightsheetmanager.model.positions;

public interface Subscriber {
    void update(String topic, Object value);
}
