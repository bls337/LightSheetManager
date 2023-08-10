package org.micromanager.lightsheetmanager.model;

import mmcorej.CMMCore;
import mmcorej.Configuration;
import org.micromanager.Studio;
import org.micromanager.lightsheetmanager.api.AcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.data.CameraModes;
import org.micromanager.lightsheetmanager.api.data.DISPIMDevice;
import org.micromanager.lightsheetmanager.api.internal.DefaultAcquisitionSettingsDISPIM;
import org.micromanager.lightsheetmanager.api.internal.DefaultTimingSettings;
import org.micromanager.lightsheetmanager.model.channels.ChannelSpec;
import org.micromanager.lightsheetmanager.model.data.AcquisitionModes;
import org.micromanager.lightsheetmanager.model.data.MultiChannelModes;
import org.micromanager.lightsheetmanager.model.devices.cameras.AndorCamera;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPLogic;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIPiezo;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIScanner;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIXYStage;
import org.micromanager.lightsheetmanager.model.devices.vendor.ASIZStage;
import org.micromanager.lightsheetmanager.model.devices.vendor.SingleAxis;
import org.micromanager.lightsheetmanager.model.utils.NumberUtils;

import java.awt.geom.Point2D;
import java.util.Objects;

// Replacement for ControllerUtils.java

public class PLogicDISPIM {

    private Studio studio_;
    private CMMCore core_;

    private DeviceManager devices_;

    // ASI Devices
    private ASIScanner scanner1_;
    private ASIScanner scanner2_;
    private ASIPiezo piezo1_;
    private ASIPiezo piezo2_;
    private ASIXYStage xyStage_;
    private ASIZStage zStage_;
    private ASIPLogic plcCamera_;
    private ASIPLogic plcLaser_;

    double scanDistance_;      // in microns; cached value from last call to prepareControllerForAcquisition()
    double actualStepSizeUm_;  // cached value from last call to prepareControllerForAcquisition()
    boolean zSpeedZero_;       // cached value from last call to prepareStageScanForAcquisition()
    String lastDistanceStr_;   // cached value from last call to prepareControllerForAcquisition()
    String lastPosStr_;        // cached value from last call to prepareControllerForAcquisition()

    // PLC
    static final int triggerStepDurationTics = 10;  // 2.5ms with 0.25ms tics
    static final int acquisitionFlagAddr = 1;
    static final int counterLSBAddr = 3;
    static final int counterMSBAddr = 4;
    static final int triggerStepEdgeAddr = 6;
    static final int triggerStepPulseAddr = 7;
    static final int triggerStepOutputAddr = 40;  // BNC #8
    static final int triggerInAddr = 35;  // BNC #3
    static final int triggerSPIMAddr = 46;  // backplane signal, same as XY card's TTL output
    static final int laserTriggerAddress = 10;  // this should be set to (42 || 8) = (TTL1 || manual laser on)

    private final DefaultAcquisitionSettingsDISPIM.Builder asb_;

    public PLogicDISPIM(final Studio studio, final DeviceManager devices, final DefaultAcquisitionSettingsDISPIM.Builder asb) {
        studio_ = Objects.requireNonNull(studio);
        devices_ = Objects.requireNonNull(devices);
        core_ = studio_.core();

        asb_ = asb;

        scanDistance_ = 0;
        actualStepSizeUm_ = 0;
        zSpeedZero_ = true;
        lastDistanceStr_ = "";
        lastPosStr_ = "";

        // populate devices
        scanner1_ = devices_.getDevice(DISPIMDevice.getIllumBeam(1));
        scanner2_ = devices_.getDevice(DISPIMDevice.getIllumBeam(2));
        piezo1_ = devices_.getDevice(DISPIMDevice.getImagingFocus(1));
        piezo2_ = devices_.getDevice(DISPIMDevice.getImagingFocus(2));
        plcCamera_ = devices_.getDevice(DISPIMDevice.TRIGGER_CAMERA);
        plcLaser_ = devices_.getDevice(DISPIMDevice.TRIGGER_LASER);
        xyStage_ = devices_.getDevice(DISPIMDevice.SAMPLE_XY);
        zStage_ = devices_.getDevice(DISPIMDevice.SAMPLE_Z);
    }

    // TODO: numViews > 2
    /**
     * call special version which will only set the slice offset and not refresh everything else
     * @param settings
     * @param channelOffset
     * @return
     */
    public boolean prepareControllerForAquisitionOffsetOnly(
            final DefaultAcquisitionSettingsDISPIM settings,
            final double channelOffset) {

        final int numViews = settings.volumeSettings().numViews();
        final int firstView = settings.volumeSettings().firstView();

        if (numViews > 1 || firstView == 1) {
            final boolean success = prepareControllerForAcquisitionSide(settings, 1, channelOffset, true);
            if (!success) {
                return false;
            }
        }

        if (numViews > 1 || firstView != 1) {
            final boolean success = prepareControllerForAcquisitionSide(settings, 2, channelOffset, true);
            if (!success) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets all the controller's properties according to volume settings
     * and otherwise gets controller all ready for acquisition
     * (except for final trigger).
     *
     * @param settings
     * @param channelOffset
     * @return false if there was some error that should abort acquisition
     */
    public boolean prepareControllerForAquisition(
            final DefaultAcquisitionSettingsDISPIM settings,
            final double channelOffset) {
        // turn off beam and scan on both sides (they are turned off by SPIM state machine anyway)
        // also ensures that properties match reality at end of acquisition
        // SPIM state machine restores position of beam at end of SPIM state machine, now it
        // will be restored to blanking position
        scanner1_.setBeamOn(false);
        scanner2_.setBeamOn(false);
        scanner1_.sa().setModeX(SingleAxis.Mode.DISABLED);
        scanner2_.sa().setModeX(SingleAxis.Mode.DISABLED);

        final int numViews = settings.volumeSettings().numViews();
        final int firstView = settings.volumeSettings().firstView();

        // set up controller with appropriate SPIM parameters for each active side
        // some of these things only need to be done once if the same micro-mirror
        //   card is used (as is typical) but keeping code universal to handle
        //   case where MM devices reside on different controller cards
        // Note: firstView starts counting from 1...n views
        if (numViews > 1 || firstView == 1) {
            final boolean success = prepareControllerForAcquisitionSide(settings, 1, channelOffset, false);
            if (!success) {
                return false;
            }
        }
        if (numViews > 1 || firstView != 1) {
            final boolean success = prepareControllerForAcquisitionSide(settings, 2, channelOffset, false);
            if (!success) {
                return false;
            }
        }

        if (settings.isUsingStageScanning()
                && settings.acquisitionMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED) {
            if (numViews != 2) {
                studio_.logs().showError("Interleaved stage scan only possible for 2-sided acquisition.");
                return false;
            }
            if (settings.cameraMode() == CameraModes.OVERLAP) {
                studio_.logs().showError("Interleaved stage scan not compatible with overlap camera mode");
                return false;
            }
        }

        // make sure set to use TTL signal from backplane in case PLOGIC_LASER is set to PLogicMode different from diSPIM shutter
        plcCamera_.setPreset(12);
        plcLaser_.setPreset(12);

        // make sure shutter is set to the PLOGIC_LASER device
        try {
            core_.setShutterDevice(plcLaser_.getDeviceName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (settings.isUsingStageScanning()) {
            // scanning with ASI stage
            // algorithm is as follows:
            // use the # of slices and slice spacing that the user specifies
            // because the XY stage is 45 degrees from the objectives have to move it sqrt(2) * slice step size
            // for now use the current X position as the start of acquisition and always start in positive X direction
            // for now always do serpentine scan with 2 passes at the same Y location, one pass each direction over the sample
            // => total scan distance = # slices * slice step size * sqrt(2)
            //    scan start position = current X position
            //    scan stop position = scan start position + total distance
            //    slow axis start = current Y position
            //    slow axis stop = current Y position
            //    X motor speed = slice step size * sqrt(2) / slice duration
            //    number of scans = number of sides (1 or 2)
            //    scan mode = serpentine for 2-sided non-interleaved, raster otherwise (need to revisit for 2D stage scanning)
            //    X acceleration time = use whatever current setting is
            //    scan settling time = delay before side

            double actualMotorSpeed;

            // figure out the speed we should be going according to slice period, slice spacing, geometry, etc.
            final double requestedMotorSpeed = computeScanSpeed(settings, scanner1_.getSPIMNumScansPerSlice());  // in mm/sec

            final boolean isInterleaved = (settings.acquisitionMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED);

            final float maxSpeed = xyStage_.getMaxSpeedX();
            if (requestedMotorSpeed > maxSpeed * 0.8) {
                // trying to go near max speed smooth scanning will be compromised
                studio_.logs().showError("Required stage speed is too fast, please reduce step size or increase sample exposure.");
                return false;
            }
            if (requestedMotorSpeed < maxSpeed / 2000) {
                // 1/2000 of the max speed is approximate place where smooth scanning breaks down (speed quantum is ~1/12000 max speed);
                // this also prevents setting to 0 which the controller rejects
                studio_.logs().showError("Required stage speed is too slow, please increase step size or decrease sample exposure.");
                return false;
            }
            xyStage_.setSpeedX((float)requestedMotorSpeed);

            // ask for the actual speed to calculate the actual step size
            actualMotorSpeed = xyStage_.getSpeedXUm() / 1000;

            // set the acceleration to a reasonable value for the (usually very slow) scan speed
            xyStage_.setAccelerationX((float)computeScanAcceleration(actualMotorSpeed,
                    xyStage_.getMaxSpeedX(), settings.scanSettings().scanAccelerationFactor()));

            int numLines = settings.volumeSettings().numViews();
            if (isInterleaved) {
                numLines = 1;  // assure in acquisition code that we can't have single-sided interleaved
            }
            numLines *= ((double) settings.numChannels() / computeScanChannelsPerPass(settings));
            xyStage_.setScanNumLines(numLines);

            final boolean isStageScan2Sided = (settings.acquisitionMode() == AcquisitionModes.STAGE_SCAN) && settings.volumeSettings().numViews() == 2;
            xyStage_.setScanPattern(isStageScan2Sided ? ASIXYStage.ScanPattern.SERPENTINE : ASIXYStage.ScanPattern.RASTER);

            if (xyStage_.getAxisPolarityX() != ASIXYStage.AxisPolarity.NORMAL) {
                studio_.logs().showError(
                        "Stage scanning requires X axis polarity set to " + ASIXYStage.AxisPolarity.NORMAL);
                return false;
            }

            if (settings.isUsingMultiplePositions()) {
                // use current position as center position for stage scanning
                // multi-position situation is handled in position-switching code instead
                Point2D.Double p = xyStage_.getXYPosition();
                // TODO: error if getXYPosition fails (return null?)
                // TODO: prepareStageScanForAcquisition(p.x, p.y, settings.getSPIMMode())
                prepareStageScanForAcquisition(p.x, p.y, settings);
            }
        } else {
            scanDistance_ = 0;
        }

        // sets PLogic "acquisition running" flag
        plcCamera_.setPreset(3);
        plcLaser_.setPreset(3);

        studio_.logs().logMessage("Finished preparing controller for acquisition with offset " + channelOffset +
                " with mode " + settings.acquisitionMode() + " and settings:\n" + settings);
        return true;
    }

    // Compute appropriate motor speed in mm/s for the given stage scanning settings
    public double computeScanSpeed(DefaultAcquisitionSettingsDISPIM settings, final int numScansPerSlice) {
        //double sliceDuration = settings.timingSettings().sliceDuration();
        //double sliceDuration = 0.0; // TODO: get from SliceTiming
        double sliceDuration = getSliceDuration(settings.timingSettings(), numScansPerSlice); // TODO: ???
        if (settings.acquisitionMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED) {
            // pretend like our slice takes twice as long so that we move the correct speed
            // this has the effect of halving the motor speed
            // but keeping the scan distance the same
            sliceDuration *= 2;
        }
        final int channelsPerPass = computeScanChannelsPerPass(settings);
        //return settings.getStepSize() * du.getStageGeometricSpeedFactor(settings.firstSideIsA) / sliceDuration / channelsPerPass;
        return settings.volumeSettings().sliceStepSize() / sliceDuration / channelsPerPass; // TODO: add getStageGeometricSpeedFactor
    }

    // compute how many channels we do in each one-way scan
    private int computeScanChannelsPerPass(DefaultAcquisitionSettingsDISPIM settings) {
        return settings.channelMode() == MultiChannelModes.SLICE_HW ? settings.numChannels() : 1;
    }

    /**
     * Compute appropriate acceleration time in ms for the specified motor speed.
     * Set to be 10ms + 0-100ms depending on relative speed to max, all scaled by factor specified on the settings panel
     * @param motorSpeed
     * @return
     */
    public double computeScanAcceleration(final double motorSpeed, DefaultAcquisitionSettingsDISPIM settings) {
        return (10 + 100 * (motorSpeed / xyStage_.getMaxSpeedX())) * settings.scanSettings().scanAccelerationFactor();
    }

    // TODO: scanNum was part of SliceSettings (now TimingSettings)
    // scanNum was populated from numScansPerSlice_ which is the scanner SPIM_NUM_SCANSPERSLICE("SPIMNumScansPerSlice")
    // labeled "Lines scans per slice:" in advanced timing tab
    //    * gets the correct value for the slice timing's sliceDuration field based on other values of slice timing

    // slice duration is the max out of the scan time, laser time, and camera time
    public double getSliceDuration(final DefaultTimingSettings s, final int scanNum) {
        return Math.max(Math.max(
                s.delayBeforeScan() + (s.scanDuration() * scanNum), // scan time
                s.delayBeforeLaser() + s.laserTriggerDuration()     // laser time
                ),
                s.delayBeforeCamera() + s.cameraTriggerDuration()   // camera time
        );
    }

    /**
     * Compute appropriate acceleration time in ms for the specified motor speed.
     * Set to be 10ms + 0-100ms depending on relative speed to max, all scaled by factor specified on the settings panel
     *
     * @param motorSpeed
     * @return
     */
    public double computeScanAcceleration(final double motorSpeed, final double maxMotorSpeed, final double stageScanAccelFactor) {
//        final double maxMotorSpeed = props_.getPropValueFloat(Devices.Keys.XYSTAGE, Properties.Keys.STAGESCAN_MAX_MOTOR_SPEED_X);
//        final double accelFactor = props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_STAGESCAN_ACCEL_FACTOR);
//        return (10 + 100 * (motorSpeed / maxMotorSpeed) ) * accelFactor;
        return (10 + 100 * (motorSpeed / maxMotorSpeed)) * stageScanAccelFactor;
    }

    public boolean prepareStageScanForAcquisition(final double x, final double y, DefaultAcquisitionSettingsDISPIM settings) {
        final boolean scanFromCurrent = settings.scanSettings().scanFromCurrentPosition();
        final boolean scanNegative = settings.scanSettings().scanFromNegativeDirection();
        double xStartUm;
        double xStopUm;
        if (scanFromCurrent) {
            if (scanNegative) {
                xStartUm = x;
                xStopUm = x - scanDistance_;
            } else {
                xStartUm = x;
                xStopUm = x + scanDistance_;
            }
        } else {
            // centered
            if (scanNegative) {
                xStartUm = x + (scanDistance_ / 2);
                xStopUm = x - (scanDistance_ / 2);
            } else { // the original implementation
                xStartUm = x - (scanDistance_ / 2);
                xStopUm = x + (scanDistance_ / 2);
            }
        }

        xyStage_.setFastAxisStart((float) (xStartUm / 1000d));
        xyStage_.setFastAxisStop((float) (xStopUm / 1000d));
        xyStage_.setSlowAxisStart((float) (y / 1000d));
        xyStage_.setSlowAxisStop((float) (y / 1000d));

        zSpeedZero_ = true;  // will turn false if we are doing planar correction
        return false;
        // return preparePlanarCorrectionForAcquisition(); TODO: add planar correction later
    }

    // TODO: implement
//    public boolean preparePlanarCorrectionForAcquisition() {
//    }


//     * @param numSides number of Sides from which we take data (diSPIM: 1 or 2)
//     * @param firstSideIsA firstSide to take data from (A or B)
    /**
     * Returns the controller to "normal" state after an acquisition
     * @param centerPiezos true to move piezos to center position
     * @return false if there is a fatal error, true if successful
     */
    public boolean cleanUpControllerAfterAcquisition(final DefaultAcquisitionSettingsDISPIM settings, final boolean centerPiezos) {
        // clear "acquisition running" flag on PLC
        plcCamera_.setPreset(2);
        plcLaser_.setPreset(2);

        final int numViews = settings.volumeSettings().numViews();
        final int firstView = settings.volumeSettings().firstView();

        if (numViews > 1 || firstView == 1) {
            final boolean success = cleanUpControllerAfterAcquisitionSide(1, centerPiezos, 0.0);
            if (!success) {
                return false;
            }
        }
        if (numViews > 1 || firstView != 1) {
            final boolean success = cleanUpControllerAfterAcquisitionSide(2, centerPiezos, 0.0);
            if (!success) {
                return false;
            }
        }

        // clean up planar correction if needed
        if (!zSpeedZero_) {
            zStage_.setTTLInputMode("0 - none"); // TODO: make enum for TTL input mode
            zSpeedZero_ = true;
        }

        studio_.logs().logMessage("Finished controller cleanup after acquisition");
        return true;
    }

    public boolean prepareControllerForAcquisitionSide(
            final AcquisitionSettingsDISPIM settings,
            final int view,
            final double channelOffset,
            final boolean offsetOnly) {

        ASIScanner scanner = null;
        ASIPiezo piezo = null;

        switch (view) {
            case 1:
                scanner = scanner1_;
                piezo = piezo1_;
                break;
            case 2:
                scanner = scanner2_;
                piezo = piezo2_;
                break;
            default:
                studio_.logs().showError("does not support numViews > 2!");
                break;
        }

        // TODO: find a better way than != null
        if (scanner != null && piezo != null) {

            if (!offsetOnly) {
                // TODO: get and set light sheet props

                // if we are changing color slice by slice then set controller to do multiple slices per piezo move
                // otherwise just set to 1 slice per piezo move
                int numSlicesPerPiezo = 1;
                if (settings.isUsingChannels() && settings.channelMode() == MultiChannelModes.SLICE_HW) {
                    numSlicesPerPiezo = settings.numChannels();
                }
                scanner.setSPIMNumSlicesPerPiezo(numSlicesPerPiezo);

                // set controller to do multiple volumes per start trigger if we are doing
                //   multiple channels with  hardware switching of channel volume by volume
                // otherwise (no channels, software switching, slice by slice HW switching)
                //   just do one volume per start trigger
                int numVolumesPerTrigger = 1;
                if (settings.isUsingChannels() && settings.channelMode() == MultiChannelModes.VOLUME_HW) {
                    numVolumesPerTrigger = settings.numChannels();
                }

                // can either trigger controller once for all the time points and
                //  have the number of repeats pre-programmed (hardware timing)
                //  or let plugin send trigger for each time point (software timing)
                float delayRepeats = 0.0f;
                if (settings.isUsingHardwareTimePoints() && settings.isUsingTimePoints()) {
                    float volumeDurationMs = 1.0f;
                    float volumeIntervalMs = (float) settings.timePointInterval();
                    delayRepeats = volumeIntervalMs - volumeDurationMs;
                    numVolumesPerTrigger = settings.numTimePoints();
                }
                scanner.setSPIMDelayBeforeRepeat(delayRepeats);
                scanner.setSPIMNumRepeats(numVolumesPerTrigger);

                scanner.setSPIMDelayBeforeSide(
                        settings.isUsingStageScanning() ? 0  // minimal delay on micro-mirror card for stage scanning (can't actually be less than 2ms but this will get as small as possible)
                                : (float) settings.volumeSettings().delayBeforeView()); // this is the usual behavior
            }
            float piezoCenter = 0;
            if (settings.isUsingStageScanning()) {
                // for stage scanning we define the piezo position to be the home position (normally 0)
                // this is basically required for interleaved mode (otherwise piezo would be moving every slice)
                //    and by convention we'll do it for all stage scanning
                piezoCenter = piezo.getHomePosition();
            } else {
                // TODO: add centerAtCurrentZ to acqSettings
                final boolean centerAtCurrentZ = false;
                if (centerAtCurrentZ) {
                    piezoCenter = (float)piezo.getPosition(); //(float) positions_.getUpdatedPosition(piezoDevice, Joystick.Directions.NONE);
                } else {
                    piezoCenter = 0.0f; // FIXME: get from ui
                }
            }

            // if we set piezoAmplitude to 0 here then sliceAmplitude will also be 0
            float piezoAmplitude;
            if (settings.isUsingStageScanning() || settings.acquisitionMode() == AcquisitionModes.NO_SCAN) {
                piezoAmplitude = 0.0f;
            } else {
                piezoAmplitude = (float) ((settings.volumeSettings().slicesPerView() - 1) * settings.volumeSettings().sliceStepSize());
            }

            // use this instead of settings.numSlices from here on out because
            // we modify it if we are taking "extra slice" for synchronous/overlap
            int numSlicesHW = settings.volumeSettings().slicesPerView();

            // tweak the parameters if we are using synchronous/overlap mode
            // object is to get exact same piezo/scanner positions in first
            // N frames (piezo/scanner will move to N+1st position but no image taken)
            final CameraModes cameraMode = settings.cameraMode();
            if (cameraMode == CameraModes.OVERLAP) {
                piezoAmplitude *= ((float) numSlicesHW) / ((float) numSlicesHW - 1f);
                piezoCenter += piezoAmplitude / (2 * numSlicesHW);
                numSlicesHW += 1;
            }

            // FIXME: more light sheet setup
            final float slope1 = (float)settings.sliceCalibration(1).sliceSlope();
            final float slope2 = (float)settings.sliceCalibration(2).sliceSlope();
            float sliceRate = (view == 1) ? slope1 : slope2;
            if (NumberUtils.floatsEqual(sliceRate, 0.0f)) {
                studio_.logs().showError("Calibration slope for view " + view + " cannot be zero. Re-do calibration on Setup tab.");
                return false;
            }
            final float offset1 = (float)(settings.sliceCalibration(1).sliceOffset() + channelOffset);
            final float offset2 = (float)(settings.sliceCalibration(2).sliceOffset() + channelOffset);
            float sliceOffset = (view == 1) ? offset1 : offset2;
            float sliceAmplitude = piezoAmplitude / sliceRate;
            float sliceCenter = (piezoCenter - sliceOffset) / sliceRate;

            if (settings.acquisitionMode() == AcquisitionModes.PIEZO_SCAN_ONLY) {
                if (cameraMode == CameraModes.OVERLAP) {
                    float actualPiezoCenter = piezoCenter - piezoAmplitude / (2 * (numSlicesHW - 1));
                    sliceCenter = (actualPiezoCenter - sliceOffset) / sliceRate;
                }
                sliceAmplitude = 0.0f;
            }
            // round to nearest 0.0001 degrees, which is approximately the DAC resolution
            sliceAmplitude = NumberUtils.roundFloatToPlace(sliceAmplitude, 4);
            sliceCenter = NumberUtils.roundFloatToPlace(sliceCenter, 4);

            if (offsetOnly) {
                scanner.sa().setOffsetY(sliceCenter);
            } else { // normal case
                // only alternate scan directions if the user is using advanced timing
                //    and user has option enabled on the advanced timing panel
                final boolean oppositeDirections = false;

                scanner.setSPIMAlternateDirections(oppositeDirections);
                scanner.setSPIMScanDuration((float) settings.timingSettings().scanDuration());
                scanner.sa().setAmplitudeY(sliceAmplitude);
                scanner.sa().setOffsetY(sliceCenter);
                scanner.setSPIMNumSlices(numSlicesHW);
                scanner.setSPIMNumSides(settings.volumeSettings().numViews());

                if (settings.volumeSettings().firstView() == 1) {
                    scanner.setSPIMFirstSide(ASIScanner.SPIMSide.A);
                } else {
                    scanner.setSPIMFirstSide(ASIScanner.SPIMSide.B);
                }

                // get the piezo card ready
                // need to do this for stage scanning too, which makes sure the piezo amplitude is 0

                // if mode SLICE_SCAN_ONLY we have computed slice movement as if we
                //   were moving the piezo but now make piezo stay still
                if (settings.acquisitionMode() == AcquisitionModes.SLICE_SCAN_ONLY) {
                    // if we artificially shifted centers due to extra trigger and only moving piezo
                    // then move galvo center back to where it would have been
                    if (settings.cameraMode() == CameraModes.OVERLAP) {
                        piezoCenter -= piezoAmplitude / (2 * (numSlicesHW - 1));
                    }
                    piezoAmplitude = 0.0f;
                }

                float piezoMin = piezo.getLowerLimit() * 1000;
                float piezoMax = piezo.getUpperLimit() * 1000;

                if (NumberUtils.outsideRange(piezoCenter - piezoAmplitude / 2, piezoMin, piezoMax)
                        || NumberUtils.outsideRange(piezoCenter + piezoAmplitude / 2, piezoMin, piezoMax)) {
                    studio_.logs().showError("Imaging piezo for side " + view +
                            " would travel outside the piezo limits during acquisition.");
                    return false;
                }

                // round to nearest 0.001 micron, which is approximately the DAC resolution
                piezoAmplitude = NumberUtils.roundFloatToPlace(piezoAmplitude, 3);
                piezoCenter = NumberUtils.roundFloatToPlace(piezoCenter, 3);
                piezo.sa().setAmplitude(piezoAmplitude);
                piezo.sa().setOffset(piezoCenter);

                if (!settings.isUsingStageScanning()) {
                    piezo.setSPIMNumSlices(numSlicesHW);
                    piezo.setSPIMState(ASIPiezo.SPIMState.ARMED);
                }

                // TODO figure out what we should do with piezo illumination/center position during stage scan
                // set up stage scan parameters if necessary
                if (settings.isUsingStageScanning()) {
                    // TODO update UI to hide image center control for stage scanning
                    // for interleaved stage scanning there will never be "home" pulse and for normal stage scanning
                    //   the first side piezo will never get moved into position either so do both manually (for
                    //   simplicity ignore fact that one of two is unnecessary for two-sided normal stage scan acquisition)
                    piezo.home();
                }

                final boolean isInterleaved = (settings.isUsingStageScanning()
                        && settings.acquisitionMode() == AcquisitionModes.STAGE_SCAN_INTERLEAVED);

                // even though we have moved piezos to home position let's still tell firmware
                //    not to move piezos anywhere (i.e. maybe setting "home disable" to true doesn't have any really effect)
                scanner.setSPIMPiezoHomeDisable(isInterleaved);

                // set interleaved sides flag low unless we are doing interleaved stage scan
                scanner.setSPIMInterleaveSides(isInterleaved);

                // send sheet width/offset
                //float sheetWidth = getSheetWidth(asb_.cameraMode(), view);
                //float sheetOffset = getSheetOffset(asb_.cameraMode(), view);
                if (cameraMode == CameraModes.VIRTUAL_SLIT) {
                    // adjust sheet width and offset to account for settle time where scan is going but we aren't imaging yet
                    // FIXME: !!!
                    //final float settleTime = props_.getPropValueFloat(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_LS_SCAN_SETTLE);
                    // infer the main scan time (during imaging) from the laser duration
//                    final float readoutTime = (float) (settings.timingSettings().laserTriggerDuration() - 0.25f);  // -0.25 is for scanLaserBufferTime
//                    // offset should be decreased by half of the distance traveled during settle time (instead of re-extracting slope use existing sheetWidth/readoutTime)
//                    sheetOffset -= (sheetWidth * settleTime/readoutTime)/2;
//                    // width should be increased by ratio (1 + settle_fraction)
//                    sheetWidth += (sheetWidth * settleTime/readoutTime);
                }
                //scanner.sa().setAmplitudeX(sheetWidth);
                //scanner.sa().setOffsetX(sheetOffset);
            }
        }
        return true;
    }

    private boolean cleanUpControllerAfterAcquisitionSide(
            final int view,
            final boolean movePiezo,
            final double piezoPosition) {

        // TODO: skip scanner warnings?

        ASIScanner scanner = null;
        ASIPiezo piezo = null;
        switch (view) {
            case 1:
                scanner = scanner1_;
                piezo = piezo1_;
                break;
            case 2:
                scanner = scanner2_;
                piezo = piezo2_;
            default:
                break;
        }

        if (scanner != null && piezo != null) {
            // make sure SPIM state machine is stopped
            scanner.setSPIMState(ASIScanner.SPIMState.IDLE);

            // restore sheet width and offset in case they got clobbered by the code implementing light sheet mode
            // TODO: reset light sheet properties
            final double saAmplitudeXDegrees = scanner.sa().getAmplitudeX();
            final double saOffsetXDegrees = scanner.sa().getOffsetX();

            // move piezo back to desired position
            if (movePiezo) {
                piezo.setPosition(piezoPosition);
            }
        }
        // make sure we stop SPIM and SCAN state machines every time we trigger controller (in AcquisitionPanel code)
        return true;
    }

    public boolean setupHardwareChannelSwitching(final DefaultAcquisitionSettingsDISPIM settings) {

        MultiChannelModes channelMode = settings.channelMode();

        // PLogic can only handle up to 4 channels
        if ((settings.numChannels() > 4) &&
                (channelMode == MultiChannelModes.VOLUME_HW || channelMode == MultiChannelModes.SLICE_HW)) {
            studio_.logs().showError("PLogic card cannot handle more than 4 channels for hardware switching.");
            return false;
        }

        // set up clock for counters
        switch (channelMode) {
            case SLICE_HW:
                plcLaser_.setPreset(17);
                break;
            case VOLUME_HW:
                if (settings.volumeSettings().firstView() == 1) {
                    plcLaser_.setPreset(18); // A first
                } else {
                    plcLaser_.setPreset(26); // B first
                }
                break;
            default:
                studio_.logs().showError("Unknown multichannel mode for hardware switching.");
                return false;
        }

        // set up hardware counter
        switch (settings.numChannels()) {
            case 1:
                plcLaser_.setPreset(22); // no counter
                break;
            case 2:
                plcLaser_.setPreset(21); // mod2 counter
                break;
            case 3:
                plcLaser_.setPreset(16); // mod3 counter
                break;
            case 4:
                plcLaser_.setPreset(15); // mod4 counter
                break;
            default:
                studio_.logs().showError("Hardware channel switching only supports 1-4 channels");
                return false;
        }

        // speed things up by turning off updates, will restore value later
        final boolean editCellUpdates = plcLaser_.isAutoUpdateCellsOn();
        if (editCellUpdates) {
            plcLaser_.setAutoUpdateCells(false);
        }

        // make sure the counters get reset on the acquisition start flag
        // turns out we can only do this for 2-counter and 4-counter implemented with D-flops
        // TODO: figure out alternative for 3-position counter
        if (settings.numChannels() != 3) {
            plcLaser_.setPointerPosition(counterLSBAddr);
            plcLaser_.setCellInput(3, acquisitionFlagAddr + ASIPLogic.addrEdge);
            plcLaser_.setPointerPosition(counterMSBAddr);
            plcLaser_.setCellInput(3, acquisitionFlagAddr + ASIPLogic.addrEdge);
        }

        if (plcLaser_.getShutterMode() == ASIPLogic.ShutterMode.SEVEN_CHANNEL_SHUTTER) {
            // special 7-channel case
            if (plcLaser_.getNumCells() < 24) {
                // restore update setting
                plcLaser_.setAutoUpdateCells(editCellUpdates);
                studio_.logs().showError("Require 24-cell PLC firmware to use hardware channel switching with 7-channel shutter");
                return false;
            }

            // make sure cells 17-24 are controlling BNCs 1-8
            plcLaser_.setPreset(ASIPLogic.Preset.BNC1_8_ON_17_24);

            // now set cells 17-22 so they reflect the counter state used to track state as well as the global laser trigger
            // NB that this only uses 6 lasers (we need 2 free BNCs, BNC#7 for FW trigger and BNC#8 for supplemental X trigger
            for (int laserNum = 1; laserNum < 7; ++laserNum) {
                plcLaser_.setPointerPosition(laserNum + 16);
                plcLaser_.setCellType(ASIPLogic.CellType.LUT3);
                int lutValue = 0;
                // populate a 3-input lookup table with the combinations of lasers present
                // the LUT "MSB" is the laserTrigger, then the counter MSB, then the counter LSB
                for (int channelNum = 0; channelNum < settings.numChannels(); ++channelNum) {
                    if (doesPLogicChannelIncludeLaser(laserNum, settings.channels()[channelNum], settings.channelGroup())) {
                        lutValue += Math.pow(2, channelNum + 4);  // LUT adds 2^(code in decimal) for each setting, but trigger is MSB of this code
                    }
                }
                plcLaser_.setCellConfig(lutValue);
                plcLaser_.setCellInput(1, counterLSBAddr);
                plcLaser_.setCellInput(2, counterMSBAddr);
                plcLaser_.setCellInput(3, laserTriggerAddress);
            }

        } else {
            // original 4-channel mode
            // initialize cells 13-16 which control BNCs 5-8
            for (int cellNum = 13; cellNum <= 16; cellNum++) {
                plcLaser_.setPointerPosition(cellNum);
                plcLaser_.setCellType(ASIPLogic.CellType.AND4);
                plcLaser_.setCellInput(2, laserTriggerAddress);
                // note that PLC diSPIM assumes "laser + side" output mode is selected for micro-mirror card
            }

            // identify BNC from the preset and set counter inputs for 13-16 appropriately
            boolean[] hardwareChannelUsed = new boolean[4]; // initialized to all false
            for (int channelNum = 0; channelNum < settings.numChannels(); channelNum++) {
                // we already know there are between 1 and 4 channels
                int outputNum = getPLogicOutputFromChannel(settings.channels()[channelNum], settings.channelGroup());
                // TODO: handle case where we have multiple simultaneous outputs, e.g. outputs 6/7 together
                // Note: harsh recently asked about double triggering, but ended up needing to split 1-4
                if (outputNum < 5) {  // check for error in getPLogicOutputFromChannel()
                    // restore update setting
                    plcLaser_.setAutoUpdateCells(editCellUpdates);
                    return false;  // already displayed error
                }
                // make sure we don't have multiple Micro-Manager channels using same hardware channel
                if (hardwareChannelUsed[outputNum - 5]) {
                    // restore update setting
                    plcLaser_.setAutoUpdateCells(editCellUpdates);
                    studio_.logs().showError("Multiple channels cannot use same laser for PLogic triggering");
                    return false;
                } else {
                    hardwareChannelUsed[outputNum - 5] = true;
                }
                plcLaser_.setPointerPosition(outputNum + 8);
                plcLaser_.setCellInput(1, ASIPLogic.addrInvert); // enable this AND4
                // if we are doing per-volume switching with side B first then counter will start at 1 instead of 0
                // the following lines account for this by incrementing the channel number "match" by 1 in this special case
                int adjustedChannelNum = channelNum;
                if (channelMode == MultiChannelModes.VOLUME_HW && !(settings.volumeSettings().firstView() == 0)) {
                    adjustedChannelNum = (channelNum + 1) % settings.numChannels();
                }
                // map the channel number to the equivalent addresses for the AND4
                // inputs should be either 3 (for LSB high) or 67 (for LSB low)
                //                     and 4 (for MSB high) or 68 (for MSB low)
                final int in3 = (adjustedChannelNum & 0x01) > 0 ? counterLSBAddr : counterLSBAddr + ASIPLogic.addrInvert;
                final int in4 = (adjustedChannelNum & 0x02) > 0 ? counterMSBAddr : counterMSBAddr + ASIPLogic.addrInvert;
                plcLaser_.setCellInput(3, in3);
                plcLaser_.setCellInput(4, in4);

                // make sure cells 13-16 are controlling BNCs 5-8
                plcLaser_.setPreset(ASIPLogic.Preset.BNC5_8_ON_13_16);
            }
        }

        // restore update setting
        plcLaser_.setAutoUpdateCells(editCellUpdates);
        return true;
    }

    public boolean triggerControllerStartAcquisition(final AcquisitionModes acqMode, int side) {
        ASIScanner scanner;
        if (side == 1) {
            scanner = scanner1_;
        } else {
            scanner = scanner2_;
        }
        switch (acqMode) {
            case STAGE_SCAN:
            case STAGE_SCAN_INTERLEAVED:
            case STAGE_SCAN_UNIDIRECTIONAL:
                // for stage scan we send trigger to stage card, which sends
                // hardware trigger to the micro-mirror card
                scanner.setSPIMState(ASIScanner.SPIMState.ARMED);
                xyStage_.setScanState(ASIXYStage.ScanState.RUNNING);
                break;
            case PIEZO_SLICE_SCAN:
            case SLICE_SCAN_ONLY:
            case PIEZO_SCAN_ONLY:
            case NO_SCAN:
                // in actuality only matters which device we trigger if there are
                // two micro-mirror cards, which hasn't ever been done in practice yet
                scanner.setSPIMState(ASIScanner.SPIMState.RUNNING);
                break;
            default:
                studio_.logs().showError("Unknown acquisition mode");
                return false;
        }
        return true;
    }

    // TODO: "output 5 only" needs to be added to ASIPLogic
    private int getPLogicOutputFromChannel(final ChannelSpec channel, final String channelGroup) {
        try {
            Configuration configData = core_.getConfigData(channelGroup, channel.getName());
            if (!configData.isPropertyIncluded(plcLaser_.getDeviceName(), "OutputChannel")) {
                studio_.logs().showError("Must include PLogic \"OutputChannel\" in preset for hardware switching");
                return 0;
            }
            String setting = configData.getSetting(plcLaser_.getDeviceName(), "OutputChannel").getPropertyValue();
            if (setting.equals("output 5 only")) {
                return 5;
            } else if (setting.equals("output 6 only")) {
                return 6;
            } else if (setting.equals("output 7 only")) {
                return 7;
            } else if (setting.equals("output 8 only")) {
                return 8;
            } else {
                studio_.logs().showError("Channel preset setting must use PLogic \"OutputChannel\" and be set to one of outputs 5-8 only");
                return 0;
            }
        } catch (Exception e) {
            studio_.logs().showError(e, "Could not get PLogic output from channel");
            return 0;
        }
    }

    // TODO: make a variable for "OutputChannel", was in Properties before (this value is always the same)
    private boolean doesPLogicChannelIncludeLaser(final int laserNum, final ChannelSpec channel, final String channelGroup) {
        try {
            Configuration configData = core_.getConfigData(channelGroup, channel.getName());
            if (!configData.isPropertyIncluded(plcLaser_.getDeviceName(), "OutputChannel")) {
                studio_.logs().showError("Must include PLogic \"OutputChannel\" in preset for hardware switching");
                return false;
            }
            String setting = configData.getSetting(plcLaser_.getDeviceName(), "OutputChannel").getPropertyValue();
            return setting.contains(String.valueOf(laserNum));
        } catch (Exception e) {
            studio_.logs().showError(e, "Could not get PLogic output from channel");
            return false;
        }
    }

    // TODO: need sheet width settings in AcquisitionSettings
    /**
     * gets the sheet width for the specified settings in units of degrees
     * @param cameraMode
     * @param side
     * @return 0 if camera isn't assigned
     */
    public float getSheetWidth(CameraModes cameraMode, int side) {
        float sheetWidth;
        //final String cameraName = devices_.getMMDevice(cameraDevice);
        AndorCamera camera = devices_.getDevice("ImagingCamera1"); // TODO: find a way of adapting to different cameras
        String cameraName = camera.getDeviceName(); // TODO: put this on LSM camera?
//
//        // start by assuming the base value, then modify below if needed
//        final Properties.Keys widthProp = (side == Devices.Sides.A) ?
//                Properties.Keys.PLUGIN_SHEET_WIDTH_EDGE_A : Properties.Keys.PLUGIN_SHEET_WIDTH_EDGE_B;
//        sheetWidth = props_.getPropValueFloat(Devices.Keys.PLUGIN, widthProp);
        sheetWidth = 1; // TODO: get from properties

        if (cameraName == null || cameraName.equals("")) {
            studio_.logs().logDebugMessage("Could get sheet width for invalid device " + cameraName);
            return sheetWidth;
        }

//        if (cameraMode == CameraModes.VIRTUAL_SLIT) {
//            final float sheetSlope = prefs_.getFloat(
//                    MyStrings.PanelNames.SETUP.toString() + side.toString(),
//                    Properties.Keys.PLUGIN_LIGHTSHEET_SLOPE, 2000);
//            Rectangle roi = cameras_.getCameraROI(cameraDevice);  // get binning-adjusted ROI so value can stay the same regardless of binning
//            if (roi == null || roi.height == 0) {
//                studio_.logs().logDebugMessage("Could not get camera ROI for light sheet mode");
//            }
//            final float slopePolarity = (side == Devices.Sides.B) ? -1f : 1f;
//            sheetWidth = roi.height * sheetSlope * slopePolarity / 1e6f;  // in microdegrees per pixel, convert to degrees
//        } else {
//            final boolean autoSheet = prefs_.getBoolean(
//                    MyStrings.PanelNames.SETUP.toString() + side.toString(),
//                    Properties.Keys.PREFS_AUTO_SHEET_WIDTH, false);
//            if (autoSheet) {
//                Rectangle roi = cameras_.getCameraROI(cameraDevice);  // get binning-adjusted ROI so value can stay the same regardless of binning
//                if (roi == null || roi.height == 0) {
//                    studio_.logs().logDebugMessage("Could not get camera ROI for auto sheet mode");
//                }
//                final float sheetSlope = prefs_.getFloat(MyStrings.PanelNames.SETUP.toString() + side.toString(),
//                        Properties.Keys.PLUGIN_SLOPE_SHEET_WIDTH.toString(), 2);
//                sheetWidth = roi.height *  sheetSlope / 1000f;  // in millidegrees per pixel, convert to degrees
//                // TODO add extra width to compensate for filter depending on sweep rate and filter freq
//                // TODO calculation should account for sample exposure to make sure 0.25ms edges get appropriately compensated for
//                sheetWidth *= 1.1f;  // 10% extra width just to be sure
//            }
//        }
        return sheetWidth;
    }

    // TODO: needs properties
    public float getSheetOffset(CameraModes cameraMode, int side) {
        float sheetOffset;
        if (cameraMode == CameraModes.VIRTUAL_SLIT) {
            //sheetOffset = prefs_.getFloat(
                    //MyStrings.PanelNames.SETUP.toString() + side.toString(),
                    //Properties.Keys.PLUGIN_LIGHTSHEET_OFFSET, 0) / 1000f;  // in millidegrees, convert to degrees
        } else {
            //final Properties.Keys offsetProp = (side == Devices.Sides.A) ?
                   // Properties.Keys.PLUGIN_SHEET_OFFSET_EDGE_A : Properties.Keys.PLUGIN_SHEET_OFFSET_EDGE_B;
           // sheetOffset = props_.getPropValueFloat(Devices.Keys.PLUGIN, offsetProp);
        }
        sheetOffset = 1; // TODO: delete later
        return sheetOffset;
    }

    /**
     * Gets the actual step size for stage scanning acquisitions.
     * Only valid after call to prepareControllerForAcquisition().
     * @return
     */
    public double getActualStepSizeUm() {
        return actualStepSizeUm_;
    }

    // TODO: maybe make this work with any number of PathConfig variables...?
    /**
     * Sets the side-specific preset from the selected group.  Blocks until all involved devices are not busy.
     * Put in this class for convenience though it isn't necessarily about the controller.
     * @param side
     */
    public void setPathPreset(int side) {
        // set preset requested on Settings tab
        String sideKey = "PathConfig1";
        switch (side) {
            case 1:
                sideKey = "PathConfig1";
                break;
            case 2:
                sideKey = "PathConfig2";
                break;
            default:
                studio_.logs().showError("unknown side when setting up path presets");
                break;
        }
        final String preset = "PathConfig1";///props_.getPropValueString(Devices.Keys.PLUGIN, sideKey); // TODO: get from plugin!
        final String group = "PathGroup"; //props_.getPropValueString(Devices.Keys.PLUGIN, Properties.Keys.PLUGIN_PATH_GROUP); // TODO: get from plugin!
        try {
            if (!preset.equals("")) {
                core_.setConfig(group, preset);
                core_.waitForConfig(group, preset);
            }
        } catch (Exception e) {
            studio_.logs().showError("Couldn't set the path config " + preset + " of group " + group);
        }
    }
}


