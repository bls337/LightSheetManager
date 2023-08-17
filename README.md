## LightSheetManager

LightSheetManager is a plugin for [micro-manager](https://micro-manager.org) 2.0 that aims to provide an easy interface to all light sheet microscopes, be they completely custom-built or assembled from commercial parts.

Our approach is to:
- Leverage the foundation of Micro-Manager, while addressing shortcomings specific to light sheet imaging.
- Have an API-centric architecture.  While we plan to implement a Micro-Manager GUI for the plugin, we want to ensure that the business logic of LightSheetManager is accessible elsewhere, e.g. that it's easy to create an alternative Python GUIs, script acquisitions, etc.
- Reduce duplication of effort across projects.  We want this to be a community effort with contributions from many individuals and institutions and useful for many different microscopes.


**High level overview of the design of this code:**

The main units are categorized into "Setting" and "Manager" interfaces. Settings are always immutable (i.e., constructed using Builders, and cannot be modified), which design pattern is prevalent in MM 2.0.  Settings can be handed to Managers which execute a task using those Settings.

The top level Manager is the "LightSheetManager".  Most likely there is never more than one instance. Getting access to LightSheetManager should enable one to execute everything that can be done with the API.

The AcquisitionManager runs acquisitions.  It is created using a Builder, each acquisition get its own AcquisitionManager. Once built, it can start an acquisition (only once), returns status, pause acquisition, unpause, change acquisition settings (only at certain points), and stop acquisition. It sends its data to a DataSink (see below). The AcquisitionBuilder will need AcquisitionSettings, a DataSink, and MMCore.  LightSheeManager ensures that only one AcquisitionManager can run at any one time.  This may involve a "baton" that an entity needs to hold to be allowed to do things with the hardware.

DataSink interface should be very thin, maybe only a "putImage()" function.  its implementation should be able to send images into a processing pipeline, and eventually into a Datastore. It would be good to keep the interface simple, so that it can easily be implemented outside of MM.  The goal is to be able to easily save to different image formats, which may require additions to Micro-Manager itself (e.g. [NGFF](https://ngff.openmicroscopy.org/latest/)).  Because metadata handling can limit image throughput in Micro-Manager (e.g. the MDA attaches lots of nearly-constant metadata to every image), LightSheetManager is expected to have semi-custom metadata handling and deal with untagged images for optimal performance.

A HardwareManager is used to set the hardware to a desired state during both live view and acquisitions.  It takes Settings that describe the "Calibration" of the system.

To solve the problem of hardware mapping, LightSheetManager will have a dedicated device adapter with a pre-init property called "MicroscopeGeometry" (or similar name).  Possible values for "MicroscopeGeometry" include diSPIM with piezos, iSPIM with piezo, ct-dSPIM, SOLS galvo-scan, SOLS stage-scan, mesoSPIM, OpenSPIM L, OpenSPIM T, OpenSPIM X, etc.  Depending on the value of this property other properties are created that connect logical and physical devices like is done with MultiCamera and other Utility devices.  It is possible to specify "None" for the physical device setting, but many values of "None" indicate that a new possible value for "MicroscopeGeometry" should be added.  The microscope configuration and device mapping is relayed to the LightSheetManager plugin by populating these properties using Micro-Manager’s normal mechanisms (which are amenable to scripting).

Some settings objects:
- AcquisitionSettings contains things like number of images, step size, channels used, whether multi-position, file name, etc.
- CalibrationSettings describe an imaging path e.g. cross-calibration light sheet with detection objective, scan width (for digital light sheets), center position, etc.
- AutofocusSettings contains information to run autofocus e.g. number of autofocus planes, algorithm for evaluating focus, etc.

Some manager objects:
- AutofocusManager takes AutofocusSettings and produces a CalibrationSettings object.
- HardwareManager takes at least one Calibration object.  Can be used by the GUI to find the right settings, and it can also be used by the AcquisitionManager.  

The plugin will have a GUI (not yet described!) in Micro-Manager 2.0 to facilitate interactive microscope alignment, locating the sample, and configuring acquisitions.  The GUI lives independent of the business logic so that it is straightforward to create customized GUIs in Java or Python or other languages.  This separation of GUI and business logic is key for code reuse.

Like Micro-Manager itself, we intend for LightSheetManager to be open source with relatively liberal license terms.

Development of this plugin was started by [ASI](https:////www.asiimaging.com) in collaboration with the Micro-Manager team.  National Institute of Health and Howard Hughes Medical Institute have provided valuable support.
