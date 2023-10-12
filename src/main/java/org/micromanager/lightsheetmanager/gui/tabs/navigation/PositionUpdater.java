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

    private PositionPanel positionPanel_; // TODO: update multiple setup panels

    private final NavigationPanel navPanel_;

    private final AtomicBoolean isPolling_;

    public PositionUpdater(final NavigationPanel navPanel) {
        navPanel_ = Objects.requireNonNull(navPanel);
        isPolling_ = new AtomicBoolean(true);
        pollingDelayMs_ = 500;
    }

    private void createPollingTask() {
        isPolling_.set(true);
        worker_ = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                while (isPolling_.get()) {
                    //System.out.println("updater tick");
                    try {
                        navPanel_.updatePositions();
                        positionPanel_.updatePositions();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!isPolling_.get()) {
                        System.out.println("break!");
                        break;
                    }
                    try {
                        Thread.sleep(pollingDelayMs_);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        };
    }

    public void startPolling() {
        createPollingTask();
        worker_.execute();
    }

    public void stopPolling() {
        isPolling_.set(false);
    }

    public boolean isPolling() {
        return isPolling_.get();
    }

    public void setPollingDelayMs(final int delayMs) {
        pollingDelayMs_ = delayMs;
    }

    public int getPollingDelayMs() {
        return pollingDelayMs_;
    }

    public void setPositionPanel(final PositionPanel panel) {
        positionPanel_ = panel;
    }

}
