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
	
	def prefix
	def ext
	def barFileName
	
	@TaskAction
	def applyBarOverride() {
		
		if (ProjectUtil.isApplicationProject(project)) {
			
			prefix = project.name.replaceAll(" ", "_") + "-" + project.version
			ext = ".bar"
			barFileName = "build/" + prefix + ext
			
			// if the user has supplied a properties file at the command line
			// then we will apply the properties using mqsiapplybaroverride command
			// otherwise we will leave the bar file alone
			println barFileName
			
			def overridesFile = System.properties['overridesFile']
			
			if (overridesFile) { 
				
				// user has specified an overrides file
				applyBarOverrideFromProperties(barFileName, overridesFile)
			}
			
		} else if (ProjectUtil.isMessageBrokerProject(project)) {
				
			// apply bar override for integration project
			
			println "Applying bar override for ${project.name}"
			
			def config = ProjectUtil.getConfigFile(project)
			
			// extract the key/values from the "environment" for each bar file to override
			config.barFile?.each { it ->
				def barName = it.getKey()
				applyBarOverrideFromConfig(config.barFile.getAt(barName))
			}
		}
	}
	
	def applyBarOverrideFromConfig(def barFileConfig) {
		
		// for each environment we will apply bar overrides and create a bar file
		barFileConfig.environment?.each { it ->
			
			def envName = it.getKey()
			debug "Environment $envName"
			
			def envProps = extractConfig(barFileConfig.environment.getAt(envName))
			
			def manualOverrides = new StringBuilder()
			
			envProps.each { key ->
				manualOverrides.append(envProps.getAt(key) + "=" + envProps[key] + " ")
			}
			
			debug "-m $manualOverrides"
		}
	}
	
	def extractConfig(ConfigObject envConfig) {
		def overrides = [:]
		
		debug "Extracting config for $envConfig"
		
		// IIB requires the property as "resourceName#propertyName"
		// since groovy config files don't like # then we need to
		// iterate over each resource defined in the environment
		// and fetch the property name
		envConfig.each { it ->
			def resourceName = it.getKey()
			debug "Found resource $resourceName"
			
			def props = extractPropertiesAndValues(resourceName, envConfig.getAt(resourceName))
					
			overrides << props
		}
		
		debug overrides
		
		return overrides
	}
	
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
	
	private applyBarOverrideFromProperties(String propsFileName) {
	
		println "Applying bar override for $barFileName"
		
		def newFileName
		
		int index = propsFileName.indexOf(".properties")
		if (index > 0) {
			def classifier = propsFileName.substring(0, index)
			newFileName = "build/" + prefix + "-" + project.version + "-" + classifier + ".bar"
		} else {
			throw new Exception("Property overridesFile must be .properties file")
		}
		
		// copy the original bar file and apply the overrides
		new File(newFileName).bytes = new File(barFileName).bytes
		
		// command to apply the bar override with stage/prod deployment descriptors
		def cmd = "mqsiapplybaroverride -b $newFileName -k $barFileName -p $propsFileName"
		println cmd
	
		def process = cmd.execute()
		println process.text
		if (process.exitValue() != 0) {
			throw new Exception("mqsiapplybaroverride failed for file $barFileName. Error code " + process.exitValue())
		}
	}
}
