//package org.micromanager.lightsheetmanager.api.internal;
//
//
//import org.micromanager.lightsheetmanager.api.AutofocusSettings;
//import org.micromanager.lightsheetmanager.api.LightSheetManagerAPI;
//import org.micromanager.lightsheetmanager.api.TimingSettings;
//import org.micromanager.lightsheetmanager.api.VolumeSettings;
//
//public class DefaultLightSheetManager implements LightSheetManagerAPI {
//
//   /**
//    * Access to a AutofocusSettingsBuilder.
//    *
//    * @return AutofocusSettings.Builder with default settings.
//    */
//   @Override
//   public AutofocusSettings.Builder autofocusSettingsBuilder() {
//      return new DefaultAutofocusSettings.Builder();
//   }
//
//   /**
//    * Access to a VolumeSettingsBuilder.
//    *
//    * @return VolumeSettings.Builder with default settings.
//    */
//   @Override
//   public VolumeSettings.Builder volumeSettingsBuilder() {
//      return new DefaultVolumeSettings.Builder();
//   }
//
//   /**
//    * Access to a TimingSettingsBuilder.
//    *
//    * @return TimingSettings.Builder with default settings.
//    */
//   @Override
//   public TimingSettings.Builder timingSettingsBuilder() {
//      return new DefaultTimingSettings.Builder();
//   }
//
//}
