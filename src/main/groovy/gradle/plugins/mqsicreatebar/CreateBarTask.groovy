package gradle.plugins.mqsicreatebar

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

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
		
		// TODO 
		def projectFile = new File(project.projectDir.absolutePath + "/.project")
		
		if (projectFile.exists() == false) {
			throw new FileNotFoundException(".project", ".project file does not exist")
		}
		
		def projectDescription = new XmlSlurper().parse(projectFile)
		def isApplicationProject = false
		def isMessageBrokerProject = false
		
		// check the .project file
		// if the project is an Application then
		// if it is a regular integration project then user needs to define what flows should be included
		projectDescription.natures?.nature?.each { it ->
			def text = it.text()
			if (text == 'com.ibm.etools.msgbroker.tooling.applicationNature') {
				isApplicationProject = true
			}
			if (text == 'com.ibm.etools.msgbroker.tooling.messageBrokerProjectNature') {
				isMessageBrokerProject = true
			}
		}
		
		if (isMessageBrokerProject == false) {
			throw new Exception("The project does not appear to be a Message Broker project")
		}
		
		if (isApplicationProject) {
			println "Building application project ${project.name}"
		} else {
			println "Building integration project ${project.name}"
		}
		
		
		if (isApplicationProject) {
			
			def barFileName = "build/" + project.name.replaceAll(" ", "_") + "-" + project.version + ".bar"
				
			if (new File(barFileName).exists() == false) {
				println "Creating $barFileName"
			
				def cmd ="mqsicreatebar -data ../ -b $barFileName -a \"${project.name}\" -version ${project.version} -cleanBuild -deployAsSource -trace"
				executeCommand(cmd)
			}
			
		} else {
		
			// when we have an integration project the user must explicitly define which files should be included in the bar file
			def config = new ConfigSlurper().parse(new File(project.projectDir.absolutePath + "/build.config").toURL())
			
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
				
					println "Creating $barFileName"
				
					def cmd = "mqsicreatebar -data ../ -b $barFileName -p \"${project.name}\" -o $o -cleanBuild -deployAsSource -trace"
					
					executeCommand(cmd)
				}
			}
		}
	}
	
	def executeCommand(String cmd) {
		
		println cmd
		
		def process = cmd.execute()
		
		process.consumeProcessOutput(System.out, System.err)
		process.waitForOrKill(60000L)
		
		if (process.exitValue() != 0) {
			throw new Exception("mqsicreatebar failed. Error code " + process.exitValue() + ".")
		}
		
	}
}