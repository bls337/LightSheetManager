package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JComboBox;

import java.awt.Dimension;
import java.util.Arrays;

public class ComboBox<T> extends JComboBox<T> {

    public ComboBox(final T[] labels, final T selected, final int width, final int height) {
        super(labels);
        setAbsoluteSize(width, height);
        setSelectedItem(selected);
        setFocusable(false); // removes the focus highlight
    }

    @SuppressWarnings("unchecked")
    public T getSelected() {
        return (T) getSelectedItem();
    }

    public void setSelected(final T item) {
        setSelectedItem(item);
    }

    public void setAbsoluteSize(final int width, final int height) {
        final Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

    public void registerListener(final Method method) {
        addActionListener(method::run);
    }

}