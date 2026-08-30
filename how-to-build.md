LightSheetManager can be built with maven

To do so, `MMJ_.jar`, which contains the code for the Micro-Manager java layer, must be copied into `LightSheetManager/lib/MMJ_.jar`

Copy it from your Micro-Manager 2.0 installation, where it ships as part of the application:

```
C:\Program Files\Micro-Manager-2.0\plugins\Micro-Manager\MMJ_.jar
```

The jar is not tracked by this repository, so it is not updated for you. Copy it again after upgrading Micro-Manager, or the plugin will be built against an older java layer than the one it runs in. A Micro-Manager 1.4 installation also contains an `MMJ_.jar`; it is a different artifact and will not work here.

Building requires JDK 11: the pom's enforcer plugin fails the build on any other JDK, so a jar cannot silently be produced by the wrong toolchain. If the `java` on your PATH or your `JAVA_HOME` is newer, point `JAVA_HOME` at a JDK 11 for the build shell, for example on Windows PowerShell:

```
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-11.0.31.11-hotspot'
```

To build, run `mvn clean package` from the `LightSheetManager` directory. The resultant jar will be available in the `LightSheetManager/target` folder, and then must be manually copied into the `Micro-manager/mmplugins`. The build runs no automated tests. The first build on a machine must be run online so Maven can download the build plugins; after that `mvn -o clean package` works offline.

Doing this requires having Maven installed. Most IDEs have a Maven plugin that can accomplish the same thing as the manual way above.
