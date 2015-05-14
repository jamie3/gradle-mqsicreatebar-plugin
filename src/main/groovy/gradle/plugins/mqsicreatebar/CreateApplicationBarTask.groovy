package gradle.plugins.mqsicreatebar

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task which executes the IBM mqsicreatebar command
 * 
 * @author Jamie Archibald
 *
 */
class CreateApplicationBarTask extends DefaultTask {

	@TaskAction
	def createBar() {
		
		def barFileName = "build/" + project.name + "-" + project.version + ".bar"
			
		if (new File(barFileName).exists() == false) {
		
			println "Creating $barFileName"
		
			def cmd = "mqsicreatebar -data ../ -b $barFileName -d ${project.name} -version ${project.version} -cleanBuild -deployAsSource"
			println cmd
			
			def process = cmd.execute()
			println process.text
			if (process.exitValue() != 0) {
				throw new Exception("mqsicreatebar failed. Error code " + process.exitValue())
			}
		}
	}
}