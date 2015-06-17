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
-----

Clean

```
gradle clean
```

Build

```
gradle build -DoverridesFile=my.properties
```