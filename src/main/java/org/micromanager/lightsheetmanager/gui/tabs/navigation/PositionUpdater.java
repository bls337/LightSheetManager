package org.micromanager.lightsheetmanager.gui.tabs.navigation;

import org.micromanager.lightsheetmanager.gui.tabs.setup.PositionPanel;

import javax.swing.SwingWorker;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Updates the positions on the navigation panel periodically using a separate thread.
 * <p>
 * The updater starts updating after the UI is created.
 */
public class PositionUpdater {

    private int pollingDelayMs_;

    private SwingWorker<Void, Void> worker_;

    private PositionPanel positionPanel_; // TODO: update multiple setup panels for diSPIM

    private final NavigationPanel navPanel_;

    private final AtomicBoolean isPolling_;

    public PositionUpdater(final NavigationPanel navPanel) {
        navPanel_ = Objects.requireNonNull(navPanel);
        isPolling_ = new AtomicBoolean(false);
        pollingDelayMs_ = 500;
    }

    /**
     * Create the polling thread without starting it.
     */
    private void createPollingTask() {
        worker_ = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                while (isPolling_.get()) {
                    //System.out.println("updater tick");
                    try {
                        navPanel_.updatePositions();
                        positionPanel_.updatePositions();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    try {
                        Thread.sleep(pollingDelayMs_);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //System.out.println("done!");
                return null;
            }
        };
    }

    /**
     * Create the polling task and start the thread.
     */
    public void startPolling() {
        isPolling_.set(true);
        createPollingTask();
        worker_.execute();
    }

    /**
     * Stops the position polling thread.
     */
    public void stopPolling() {
        isPolling_.set(false);
    }

    /**
     * Return true if polling positions.
     *
     * @return true if polling positions
     */
    public boolean isPolling() {
        return isPolling_.get();
    }

    /**
     * Set the polling delay in milliseconds.
     *
     * @param delayMs delay in milliseconds
     */
    public void setPollingDelayMs(final int delayMs) {
        pollingDelayMs_ = delayMs;
    }

    /**
     * Return the polling delay in milliseconds.
     *
     * @return the polling delay in milliseconds
     */
    public int getPollingDelayMs() {
        return pollingDelayMs_;
    }

    // TODO: better way to connect this?
    /**
     * Set the position panel to update in addition to the {@code NavigationPanel}.
     *
     * @param panel the reference to the panel
     */
    public void setPositionPanel(final PositionPanel panel) {
        positionPanel_ = panel;
    }

}
