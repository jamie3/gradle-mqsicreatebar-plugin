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
	String barFileName
	
	@TaskAction
	def applyBarOverride() {
		
		barName = project.name.replaceAll(" ", "_") + "-" + project.version
		
		if (ProjectUtil.isApplicationProject(project)) {
			
			// this is the bar that was created by the CreateBarTask
			barFileName = "build/$barName$ext"
			
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
			
			println "Applying bar override for ${project.name}"
			
			def config = ProjectUtil.getConfigFile(project)
			
			// extract the key/values from the "environment" for each bar file to override
			config.environment?.barFile?.each { it ->
				def barSubName = it.getKey()
				applyBarOverrideFromConfig(barSubName, config.barFile.getAt(barSubName))
			}
		}
	}
	
	def applyBarOverrideFromConfig(def barSubName, def barFileConfig) {
		
		// for each environment we will apply bar overrides and create a bar file
		project.environment?.each { it ->
			
			def envName = it.getKey()
			debug "Found environment $envName"
			
			def envProps = extractConfig(project.environment?.getAt(envName))
			
			// build the "-m <manualOverrides>"
			def manualOverrides = new StringBuilder()
			
			envProps.each { p ->
				manualOverrides.append("$p,")
			}
			
			def args = manualOverrides.toString()
			if (args.length() > 0) {
				args = args.substring(0, args.length()-1)
			}
			args = "-m \"$args\""
			
			// this is the bar that was created by the CreateBarTask
			barFileName = "build/$barName-$barSubName$ext"
			
			def newBarFileName = "build/$barName-$barSubName-$envName$ext"
			
			doApplyBarOverride(barFileName, newBarFileName, args)
		}
	}
	
	def extractConfig(ConfigObject envConfig) {
		
		def overrides = [:]
		
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
	
	def applyBarOverrideFromProperties(String propsFileName) {
	
		println "Applying bar override for $barFileName"
		
		def newBarFileName
		
		int index = propsFileName.indexOf(".properties")
		if (index > 0) {
			def classifier = propsFileName.substring(0, index)
			newBarFileName = "build/" + prefix + "-" + project.version + "-" + classifier + ".bar"
		} else {
			throw new Exception("Property overridesFile must be .properties file")
		}
		
		doApplyBarOverride(barFileName, newBarFileName, "-p $propsFileName")
	}
	
	def doApplyBarOverride(String barFileName, String newBarFileName, String args) {
		
		// command to apply the bar override with stage/prod deployment descriptors
		def cmd = "mqsiapplybaroverride -b $barFileName -o $newBarFileName $args"
		debug cmd
	
		def process = cmd.execute()
		
		process.consumeProcessOutput(System.out, System.err)
		process.waitForOrKill(60000L)
		
		if (process.exitValue() != 0) {
			throw new Exception("mqsiapplybaroverride failed for file $barFileName. Error code " + process.exitValue())
		}
		
		println "Created $newBarFileName"
	}
}
