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
			
		} else {
			//if (ProjectUtil.isMessageBrokerProject(project)) {
		
			def config = ProjectUtil.getConfigFile(project)
			
			def workspace = config.workspace
			
			def projects = config.projects instanceof java.util.List ? config.projects?.join(' ') : []
			
			def libraries = config.libraries instanceof java.util.List ? config.libraries?.join(' ') : []
			
			// when we have an integration project the user must explicitly define which files should be included in the bar file
			def o = config.files?.join(' ')
			
			/*each { it ->
				
				def key = it.getKey()
				
				def workspace = config.workspace
				def include = config.barFile.getAt(key).include
				
				// prepend the project directory name to the file path
				for (int i=0; i<include.size(); i++) {
					include[i] = "\"${project.name}/" + include[i] + "\""
				}
				
				o = include.join(' ')
				
			}*/
			
			def barFileName = "build/" + config.barFileName.replaceAll(" ", "_") + "-" + project.version + ".bar"
			if (new File(barFileName).exists() == false) {
			
				def cmd = ""
				
				// create library
				if (libraries.size() > 0) {
					
					println "Building library ${project.name}"
		
					cmd = "mqsicreatebar -data $workspace -b $barFileName -l \"$libraries\" -cleanBuild -trace"
					
				} else if (projects.size > 0) {
					
					println "Building integration project ${project.name}"
		
					cmd = "mqsicreatebar -data $workspace -b $barFileName -p \"$projects\" -o $o -cleanBuild -trace"
					
				} else {
					throw new Exception("One of 'projects' or 'libraries' must be defined in the build.config");
				}
				
				executeCommand(cmd)
				
				println "Created $barFileName"
			}
		}
		/*} else {
			throw new Exception("The project does not appear to be a Message Broker project")
		}*/
	}
	
	def executeCommand(String cmd) {
		
		debug cmd
		
		def workingDir = project.projectDir.absolutePath
		def process = cmd.execute(null, new File(workingDir))
		
		//process.consumeProcessOutput(System.out, System.err)
		//process.text.eachLine {println it}
		process.waitFor()

		if (process.exitValue() != 0) {
			throw new Exception("Command failed. Error code " + process.exitValue() + ". " + cmd)
		}
		
	}
}