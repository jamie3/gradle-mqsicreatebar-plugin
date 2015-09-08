package gradle.plugins.mqsicreatebar

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static gradle.plugins.mqsicreatebar.Debug.*

/**
 * Task which executes the IBM mqsiapplybaroverride command
 * 
 * @author Jamie Archibald
 *
 */
class ApplyBarOverrideTask extends DefaultTask {
	
	String barName
	String ext = ".bar"
	
	// this is the bar that was created by the CreateBarTask
	String barFileName
	
	String projectDir
	
	@TaskAction
	def applyBarOverride() {
		
		def config = ProjectUtil.getConfigFile(project)
		def buildDir = project.getBuildDir()
		projectDir = project.getProjectDir()
		barName = config.barFileName.replaceAll(" ", "_") + "-" + project.version
		barFileName = projectDir + "/build/$barName$ext"
		
		if (new File(barFileName).exists() == false) {
			throw new FileNotFoundException(barFileName)
		}
		
		if (ProjectUtil.isApplicationProject(project)) {
			
			
			// if the user has supplied a properties file at the command line
			// then we will apply the properties using mqsiapplybaroverride command
			// otherwise we will leave the bar file alone
			println barFileName
			
			def overridesFile = System.properties['overridesFile']
			
			if (overridesFile) { 
				
				// user has specified an overrides file
				applyBarOverrideFromProperties(barFileName, overridesFile)
			}
			
		} else {
		
			// if (ProjectUtil.isMessageBrokerProject(project)) {
				
			// apply bar override for integration project
			
			// extract the key/values from the "environment" for each bar file to override
			config.environment?.each { it ->
				def envName = it.getKey()
				applyBarOverrideForEnvironment(config, envName)
			}
			
			if (config.environment == null || config.environment.size() == 0) {
				println "No environments found, skipping bar override"
			}
		}
	}
	
	def applyBarOverrideForEnvironment(ConfigObject config, String envName) {
		
		// for each environment we will apply bar overrides and create a bar file
		
		def envConfig = config.environment.getAt(envName)
		
		// hashmap containing our proerties to override
		def overridesMap = [:]
		
		/*
		 * iterate over each resource for the specified environment
		 * 
		 * environment {
		 * 		resourceName {
		 * 			key = "value"
		 * 		}
		 * }
		 * 
		 * 
		 */
		envConfig.each {
			def resourceName = it.getKey()
			def resourceConfig = envConfig.getAt(resourceName)
			println "Found resource $resourceName"	
			
			def props = extractPropertiesAndValues(resourceName, resourceConfig)
			
			println "Found override " + props
			
			overridesMap << props
		}
		
		if (overridesMap.size() > 0) {
			
			println "Applying bar override for environment $envName"
			
			String manualOverridesArg = ""
			
			overridesMap.each {
				def key = it.key
				def val = it.value
				manualOverridesArg += "$key=$val "
			}
			
			// this is the bar that was created by the CreateBarTask
			def newBarFileName = projectDir + "/build/$barName-$envName$ext"
			
			// command to apply the bar override with stage/prod deployment descriptors
			def cmd = "mqsiapplybaroverride -b $barFileName -o $newBarFileName -m \"$manualOverridesArg\""
			debug cmd
		
			def process = cmd.execute()
			
			process.consumeProcessOutput(System.out, System.err)
			process.waitForOrKill(60000L)
			
			if (process.exitValue() != 0) {
				throw new Exception("Apply bar override failed. Error code " + process.exitValue() + "\n$cmd")
			}
			
			println "Created $newBarFileName"
			
		} else {
		
			println "Found environment #envName but there were no properties to override"
		}
	}
	
	/**
	 * Extracts the key/value properties from the config object into IBM format 'resourceName#property'
	 * 
	 * @param resourceName
	 * @param resourceConfig
	 * @return
	 */
	def extractPropertiesAndValues(String resourceName, ConfigObject resourceConfig) {
		def props = [:]
		resourceConfig.each { it ->
			def key = it.getKey()
			def newKey = "$resourceName#$key"
			
			props[newKey] = resourceConfig.get(key)
			debug "   $newKey=" + props[newKey]
		}
		return props
	}
}
