# gradle-mqsicreatebar-plugin
IBM Integration Bus MQSI Create Bar Gradle Plugin

Overview
--------

A plugin that allows you to execute the mqsicreatebar and mqsibaroverride command from gradle.

Build
-----

This will build and install the plugin to the local maven repo.

./gradle install

This 

Usage
-----

To use the plugin in your project add the following into your build.gradle file

```groovy
buildscript {
    dependencies {
        classpath 'gradle.plugins.mqsicreatebar:gradle-mqsicreatebar-plugin:1.0'
    }
}

apply plugin: 'gradle.plugins.mqsicreatebar'
```

Tasks
=====

gradle clean - Cleans the build folder
gradle build - Builds the project and creates the bar file
    Parameters:
		-DoverridesFile=my.properties
		If defined will create a second bar file with the overridden properties