# gradle-mqsicreatebar-plugin
IBM Integration Bus MQSI Create Bar Gradle Plugin

Overview
--------

A plugin that allows you to execute the mqsicreatebar and mqsibaroverride command from gradle.

Build
-----

This will build and install the plugin to the local maven repo.

./gradle install

Usage
-----

To use the plugin in your IIB project add the following into your build.gradle file

```groovy

// the version of your project
version=1.0

buildscript {
	repositories {
        maven {
			// location of where your maven repository resides
			url uri('file:/C:/dev/.m2/repository')	
        }
    }
    dependencies {
        classpath 'gradle.plugins.mqsicreatebar:gradle-mqsicreatebar-plugin:1.0'
    }
}

apply plugin: 'gradle.plugins.mqsicreatebar'
```

Currently this plugin only supports building IIB Application projects (e.g. mqsicreatebar -a <applicationName>). Support for Integration projects will be added in the near future. For each application project an equivalent bar file will be created. Support for packaging multiple applications in a single bar file is not supported.

The build.gradle file should be placed in the folder alongside your Application project (not the workspace folder). For example:

```
/workspace/
   /ApplicationA
      build.gradle
   /ApplicationB
     build.gradle
   /LIB1
   /LIB2
```   
   
In the above example you would need a build.gradle file for each Application project. When executing "gradle build" task in each project the following will be created.

```
/workspace/
   /ApplicationA
      build.gradle
      /build/
         ApplicationA-1.0.bar
   /ApplicationB
     build.gradle
      /build/
         ApplicationB-1.0.bar
   /LIB1
   /LIB2
```

Tasks
-----

### Clean ###
Deletes all bar files from the build folder

```
gradle clean
```

### Build ###
Creates a bar file from the build.gradle using the default properties from the project

```
gradle build
```

### Build with Overrides ###
Creats a bar file from the build.gradle using and overrides the default properties in the broker.xml file

```
gradle build -DoverridesFile=my.properties
```
