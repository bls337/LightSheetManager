package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.Font;

public class CheckBox extends JCheckBox {

    public static final int LEFT = SwingConstants.LEFT;
    public static final int RIGHT = SwingConstants.RIGHT;

    private boolean isLocked_ = false;

    public CheckBox(final String text, final boolean defaultState) {
        super(text, defaultState);
        setHorizontalTextPosition(RIGHT);
        setFocusPainted(false);
    }

    public CheckBox(final String text, final boolean defaultState, final int constant) {
        super(text, defaultState);
        setHorizontalTextPosition(constant);
        setFocusPainted(false);
    }

    public CheckBox(final String text, final int fontSize, final boolean defaultState, final int constant) {
        super(text, defaultState);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        setHorizontalTextPosition(constant);
        setFocusPainted(false);
    }

    /**
     * Set the locked state, in the locked state the {@code CheckBox}
     * will not respond to button presses or mouse clicks.
     *
     * @param locked the locked state
     */
    public void setLocked(final boolean locked) {
        isLocked_ = locked;
        if (locked) {
            setFont(getFont().deriveFont(java.awt.Font.BOLD));
        } else {
            setFont(getFont().deriveFont(java.awt.Font.PLAIN));
        }
    }

    /**
     * Sets the selected state of the {@code CheckBox}.
     *
     * @param state the new selection state
     * @param shouldFire {@code true} fires events, {@code false} updates silently
     */
    public void setSelected(boolean state, boolean shouldFire) {
        if (shouldFire) {
            // Standard behavior: updates state and triggers events
            super.setSelected(state);
        } else {
            // "Silent" behavior: updates state via model without firing ActionEvents
            getModel().setSelected(state);
        }
    }

    public void registerListener(final Method method) {
        addActionListener(method::run);
    }

    @Override
    public void setSelected(boolean state) {
        // default to true, always fires events
        setSelected(state, true);
    }

    @Override
    protected void processMouseEvent(java.awt.event.MouseEvent e) {
        if (!isLocked_) {
            super.processMouseEvent(e);
        }
    }

    @Override
    protected void processKeyEvent(java.awt.event.KeyEvent e) {
        if (!isLocked_) {
            super.processKeyEvent(e);
        }
    }

}
