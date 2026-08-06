LightSheetManager can be built with maven

To do so, `MMJ_.jar`, which contains the code for the Micro-Manager java layer, must be copied into `LightSheetManager/lib/MMJ_.jar`

Copy it from your Micro-Manager 2.0 installation, where it ships as part of the application:

```
C:\Program Files\Micro-Manager-2.0\plugins\Micro-Manager\MMJ_.jar
```

The jar is not tracked by this repository, so it is not updated for you. Copy it again after upgrading Micro-Manager, or the plugin will be built against an older java layer than the one it runs in. A Micro-Manager 1.4 installation also contains an `MMJ_.jar`; it is a different artifact and will not work here.

To build, run `mvn clean package` from the `LightSheetManager`. The resultant jar will be available in the `LightSheetManager/target` folder, and then must be manually copied into the `Micro-manager/mmplugins`. Currently, the automated tests are throwing an error. In the case the tests can be skipped adnt the plugin built with `mvn clean package -Dmaven.test.skip`. 

Doing this requires having Maven installed. Most IDEs have a Maven plugin that can accomplish the same thing as the manual way above.