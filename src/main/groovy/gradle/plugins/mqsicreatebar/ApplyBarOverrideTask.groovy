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
		def barFileNameStage = "build/" + "$prefix-stage$ext"
		def barFileNameProd = "build/" + "$prefix-prod$ext"
		
		doApplyBarOverride(barFileName, barFileNameStage, "build-stage.properties")
		doApplyBarOverride(barFileName, barFileNameProd, "build-prod.properties")
	}
	
	def doApplyBarOverride(def barFileNameSrc, def barFileNameTgt, def propsFileName) {
	
		println "Creating $barFileNameTgt"
		
		// copy the original bar file and apply the overrides
		new File(barFileNameTgt).bytes = new File(barFileNameSrc).bytes
		
		// command to apply the bar override with stage/prod deployment descriptors
		def cmd = "mqsiapplybaroverride -b $barFileNameTgt -k ${project.name} -p $propsFileName"
		println cmd
	
		def process = cmd.execute()
		println process.text
		if (process.exitValue() != 0) {
			throw new Exception("mqsiapplybaroverride failed for file $barFileNameTgt. Error code " + process.exitValue())
		}
	}
}
