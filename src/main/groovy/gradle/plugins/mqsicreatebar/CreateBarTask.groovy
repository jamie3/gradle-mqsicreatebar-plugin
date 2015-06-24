package gradle.plugins.mqsicreatebar

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static gradle.plugins.mqsicreatebar.Debug.*

/**
 * Task which executes the IBM mqsicreatebar command
 * 
 * @author Jamie Archibald
 *
 */
class CreateBarTask extends DefaultTask {

	@TaskAction
	def createBar() {
		
		if (project.version?.toString().isEmpty()) {
			throw new Exception("Missing project version")
		}
		
		def projectFile = ProjectUtil.getProjectFile(project)
		
		if (ProjectUtil.isApplicationProject(project)) {
			
			println "Building application project ${project.name}"
			
			def barFileName = "build/" + project.name.replaceAll(" ", "_") + "-" + project.version + ".bar"
				
			if (new File(barFileName).exists() == false) {
			
				def cmd ="mqsicreatebar -data ../ -b $barFileName -a \"${project.name}\" -version ${project.version} -cleanBuild -deployAsSource -trace"
				executeCommand(cmd)
				
				debug "Created $barFileName"
			}
			
		} else if (ProjectUtil.isMessageBrokerProject(project)) {
		
			println "Building integration project ${project.name}"
		
			// when we have an integration project the user must explicitly define which files should be included in the bar file
			def config = ProjectUtil.getConfigFile(project)
			
			config.barFile?.each { it ->
				
				def key = it.getKey()
				def include = config.barFile.getAt(key).include
				
				// prepend the project directory name to the file path
				for (int i=0; i<include.size(); i++) {
					include[i] = "\"${project.name}/" + include[i] + "\""
				}
				
				def o = include.join(' ')
				def barFileName = "build/" + project.name.replaceAll(" ", "_") + "-" + project.version + "-$key" + ".bar"
				
				if (new File(barFileName).exists() == false) {
				
					def cmd = "mqsicreatebar -data ../ -b $barFileName -p \"${project.name}\" -o $o -cleanBuild -deployAsSource -trace"
					
					executeCommand(cmd)
					
					println "Created $barFileName"
				}
			}
		} else {
			throw new Exception("The project does not appear to be a Message Broker project")
		}
	}
	
	def executeCommand(String cmd) {
		
		debug cmd
		
		def process = cmd.execute()
		
		process.consumeProcessOutput(System.out, System.err)
		process.waitForOrKill(60000L)
		
		if (process.exitValue() != 0) {
			throw new Exception("mqsicreatebar failed. Error code " + process.exitValue() + ".")
		}
		
	}
}