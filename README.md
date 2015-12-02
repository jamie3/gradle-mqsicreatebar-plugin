# gradle-mqsicreatebar-plugin
IBM Integration Bus MQSI Create Bar Gradle Plugin

Overview
--------

A gradle plugin that makes it easier to create bar files used for deploying onto IBM Integration Bus v9.0. The plugin executes the mqsicreatebar and mqsibaroverride commands. The plugin is useful in CI tools such as Jenkins.

Pre-Requisites
--------------

The mqsi environment must be initialized before the plugin can be used. For example, on windows you must run the gradle tasks within the IBM Integration Console (e.g. mqsiprofile.cmd).

Build
-----

This will build and install the plugin to the local maven repo.

./gradle install

Usage
-----

To use the plugin in your IIB project you must create two files, build.gradle and build.config.

build.gradle example:

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
        classpath 'gradle.plugins.mqsicreatebar:gradle-mqsicreatebar-plugin:1.1'
    }
}

apply plugin: 'gradle.plugins.mqsicreatebar'
```

build.config contains the configuration settings for the project. For example the build.config can contain properties which mqsiapplybaroverride is performed. See Build with Overrides section below.

Project Structure
-----------------

Below is an example of the project structure for an application project. For each application project an equivalent bar file will be created. Support for packaging multiple applications in a single bar file is not supported.

The build.gradle file should be placed in the folder alongside your Application project (not the workspace folder). For example:

```
/workspace/
   /.metadata
   /ApplicationA
      build.gradle
      build.config
      /build/
         ApplicationA-1.0.bar
         ApplicationA-1.0-env1.bar
         ApplicationA-1.0-env2.bar
   /ApplicationB
     build.gradle
     build.config
      /build/
         ApplicationB-1.0.bar
         ApplicationB-1.0-env1.bar
         Applicationb-1.0-env2.bar
   /LIB1
   /LIB2
```   
   
In the above example you would need a build.gradle file for each Application project. When executing "gradle build" task in each project the following will be created. If you have environment defined in the build.config file an equivalent bar file will be created. This essentially

```
/workspace/
   /ApplicationA
      build.gradle
      /build/
         ApplicationA-1.0.bar
         ApplicationA-1.0-env1.bar
         ApplicationA-1.0-env2.bar
   /ApplicationB
     build.gradle
      /build/
         ApplicationB-1.0.bar
         ApplicationB-1.0-env1.bar
         Applicationb-1.0-env2.bar
   /IntegrationProjectA
      /build/
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

### Build Application Project ###
Creates a bar file from the build.gradle using the default properties from the project

```
gradle build
```

### Build Integration Project ###
Creates bar file(s) from an integration project. For an integration project you must create a build.config file alongside the build.gradle file. An example of the build.config file is shown below.

```
# The location of the workspace
workspace = '../'

# Name of the bar file that the plugin will create
barFileName = MyBarFile

# Integration projects to include
projects = [
	"MyProject"
]

# Files to include in the bar file
files = [
	"MyProject/myFlow.msgflow",
	"MessageSet/myMessageSet.mset"
]
```

Afterwards you can execute the build command

```
gradle build
```

This will produce the bar file: 

```
build/MyBarFile-1.0.bar
```

### Build Library ###
Below is an example config file for building a library bar file:

```
workspace = '../'

barFileName = 'RoutingRulesTypeB'

libraries = [
	"RoutingRulesTypeB"	
]
```

### Build with Overrides ###
When the bar file is build using the "gradle build" command a broker.xml is created within the bar files META-INF folder. IBM contains an mqsibaroverride command that overrides the properties in the broker.xml file. This plugin allows you to specify these properties within the build.config file.

Example broker.xml file:

```
<Broker>
	<CompiledMessageFlow>
		<ConfigurableProperty override="/abc" uri="myFlow#exampleProperty"/>
		<ConfigurableProperty override="/def" uri="myFlow#exampleProperty2"/>
	</CompiledMessageFlow>
</Broker>
```

To override the configurable property you must define the following within the build.config file.

```
environment {
	stage {
		myFlow {
			exampleProperty = "/tmp"
			exampleProperty2 = "/tmp2"
		}
	}
	prod {
		myFlow {
			exampleProperty = "/var/log"
			exampleProperty2 = "/var/log2"
		}
	}
}
```

This will create two bar files:

```
build/MyBarFile-1.0-stage.bar
build/MyBarFile-1.0-prod.bar
```

Each bar file will contain the properties pertaining to environment defined in the build.config file.

### Debugging ###
Debugging can be enabled in the plugin as follows:


```
gradle <task> -Ddebug

```

