package org.micromanager.lightsheetmanager.model.positions;

import mmcorej.DeviceType;
import org.micromanager.lightsheetmanager.LightSheetManager;

import javax.swing.SwingWorker;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class PositionUpdater implements Publisher {

   // polling
   private int pollingDelayMs_;
   private final AtomicBoolean isPolling_;
   private SwingWorker<Void, Void> worker_;

   // data
   private final HashMap<String, Object> positions_;
   private final HashMap<String, ArrayList<Subscriber>> topics_;

   private final LightSheetManager model_;

   public PositionUpdater(final LightSheetManager model) {
       model_ = Objects.requireNonNull(model);
       isPolling_ = new AtomicBoolean(false);
       positions_ = new HashMap<>();
       topics_ = new HashMap<>();
       pollingDelayMs_ = 500;
   }

   // call this after the devices are found
   public void setup() {
      final String[] devices = model_.devices()
            .getDeviceAdapter().getPositionDevices();
      for (String device : devices) {
         positions_.put(device, 0.0); // TODO: how to init?
         topics_.put(device, new ArrayList<>());
         System.out.println(device);
      }
   }

   private void createPollingTask() {
      worker_ = new SwingWorker<Void, Void>() {
         @Override
         protected Void doInBackground() {
            while (isPolling_.get()) {
               updatePositions();
               updateSubscribers();
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

   public void startPolling() {
      isPolling_.set(true);
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

   @Override
   public void register(Subscriber subscriber, String topic) {
      topics_.get(topic).add(subscriber);
   }

   /**
    * Update the position map.
    */
   public void updatePositions() {
      for (String device : positions_.keySet()) {
         final String deviceName = model_.devices().getDevice(device).getDeviceName();
         final DeviceType deviceType = model_.devices().getDevice(device).getDeviceType(deviceName);
         try {
            if (deviceType == DeviceType.XYStageDevice) {
               positions_.put(device, new Point2D.Double(
                     model_.core().getXPosition(deviceName),
                     model_.core().getYPosition(deviceName)));
            } else if (deviceType == DeviceType.StageDevice) {
               positions_.put(device, model_.core().getPosition(deviceName));
            } else if (deviceType == DeviceType.GalvoDevice) {
               positions_.put(device, model_.core().getGalvoPosition(deviceName));
            }
         } catch (Exception e) {
            // TODO:
         }
      }
   }

   /**
    * Send messages to all subscribers.
    */
   private void updateSubscribers() {
      for (String topic : topics_.keySet()) {
         for (Subscriber sub : topics_.get(topic)) {
            sub.update(topic, positions_.get(topic));
         }
      }
   }

}
