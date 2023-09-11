package org.micromanager.lightsheetmanager.gui.components;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import java.util.Hashtable;


/**
 * Create a slider that uses double values using a scale factor.
 * <p>
 * You can use the {@code getDouble} method instead of {@code getValue} to get the unscaled value.
 * You can use the {@code setDouble} method instead of {@code setValue} to set the value using the unscaled number.
 */
public class Slider extends JSlider {

    private final int scaleFactor_;

    public Slider(final int min, final int max, final int scaleFactor) {
        super(min*scaleFactor, max*scaleFactor, min*scaleFactor);

        UIManager.put("Slider.focus", UIManager.get("Slider.background")); // remove highlight when clicked

        scaleFactor_ = scaleFactor;

        final int scaledMin = min * scaleFactor_;
        final int scaledMax = max * scaleFactor_;
        setMajorTickSpacing(scaledMax-scaledMin);
        setMinorTickSpacing(scaleFactor_);

        // create label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(scaledMax, new JLabel(Double.toString(max)));
        labelTable.put(scaledMin, new JLabel(Double.toString(min)));
        setLabelTable(labelTable);

        setPaintTicks(true);
        setPaintLabels(true);
        setSnapToTicks(false);
    }

    public void registerListener(final Method method) {
        addChangeListener(e -> {
            if (!this.getValueIsAdjusting()) {
                method.run(e);
            }
        });
    }

    /**
     * Returns the actual value that the slider is meant to represent.
     *
     * @return the unscaled
     */
    public double getDouble() {
        return (double) getValue() / scaleFactor_;
    }

    public void setDouble(final double value) {
        setValue((int)(value*scaleFactor_));
    }
}
