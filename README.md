# gradle-mqsicreatebar-plugin
IBM Integration Bus MQSI Create Bar Gradle Plugin

Overview
--------

A plugin that allows you to execute the mqsicreatebar and mqsibaroverride command from gradle.

Build
-----

./gradle uploadArchives

Usage
-----

To use the plugin in your project add the following into your build.gradle file

```groovy
buildscript {
    repositories {
        maven {
            url uri('../repo')    // path to the maven repository containing the plugin
        }
    }
    dependencies {
        classpath 'gradle.plugins.mqsicreatebar:gradle-mqsicreatebar-plugin:1.0'
    }
}

apply plugin: 'gradle.plugins.mqsicreatebar'
```
