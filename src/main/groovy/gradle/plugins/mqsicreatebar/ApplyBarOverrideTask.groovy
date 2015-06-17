package gradle.plugins.mqsicreatebar

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task which executes the IBM mqsiapplybaroverride command
 * 
 * @author Jamie Archibald
 *
 */
class ApplyBarOverrideTask extends DefaultTask {
	
	@TaskAction
	def applyBarOverride() {
		
		def prefix = project.name + "-" + project.version
		def ext = ".bar"
		def barFileName = "build/" + prefix + ext
		
		// if the user has supplied a properties file at the command line
		// then we will apply the properties using mqsiapplybaroverride command
		// otherwise we will leave the bar file alone
		
		def overridesFile = System.properties['overridesFile']
		
		if (overridesFile) { 
			doApplyBarOverride(barFileName, overridesFile)
		}
	}
	
	def doApplyBarOverride(def barFileName, String propsFileName) {
	
		println "Applying bar override for $barFileName"
		
		def newFileName
		
		int index = propsFileName.indexOf(".properties")
		if (index > 0) {
			newFileName = "build/" + project.name + "-" + project.version + "-" + propsFileName.substring(0, index) + ".bar"
		} else {
			throw new Exception("Property overridesFile must be .properties file")
		}
		
		// copy the original bar file and apply the overrides
		new File(newFileName).bytes = new File(barFileName).bytes
		
		// command to apply the bar override with stage/prod deployment descriptors
		def cmd = "mqsiapplybaroverride -b $newFileName -k ${project.name} -p $propsFileName"
		println cmd
	
		def process = cmd.execute()
		println process.text
		if (process.exitValue() != 0) {
			throw new Exception("mqsiapplybaroverride failed for file $barFileName. Error code " + process.exitValue())
		}
	}
}
