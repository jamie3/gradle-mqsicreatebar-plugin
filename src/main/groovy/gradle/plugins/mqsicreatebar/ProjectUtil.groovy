package gradle.plugins.mqsicreatebar

import org.gradle.api.Project

class ProjectUtil {

	private ProjectUtil() {
		
	}
	
	static File getProjectFile(Project project) {
		def projectFile = new File(project.projectDir.absolutePath + "/.project")
		
		if (projectFile.exists() == false) {
			return null
			//throw new FileNotFoundException(".project", "$projectFile file does not exist")
		}
		
		return projectFile
	}
	
	static ConfigObject getConfigFile(def project) {
		return new ConfigSlurper().parse(new File(project.projectDir.absolutePath + "/build.config").toURL())
	}
	
	static boolean isApplicationProject(def project) {
		
		def isApplicationProject = false
		
		def projectFile = getProjectFile(project)
		
		if (projectFile != null) {
			def projectDescription = new XmlSlurper().parse(projectFile)
			
			// check the .project file
			// if the project is an Application then
			projectDescription.natures?.nature?.each { it ->
				def text = it.text()
				if (text == 'com.ibm.etools.msgbroker.tooling.applicationNature') {
					isApplicationProject = true
				}
			}
		}
		
		return isApplicationProject
	}
	
	static boolean isIntegrationPRoject(def project) {
		return isMessageBrokerProject(project) && !isApplicationProject(project)
	}
	
	static boolean isMessageBrokerProject(def project) {
		
		def projectFile = getProjectFile(project)
		
		def projectDescription = new XmlSlurper().parse(projectFile)
		def isMessageBrokerProject = false
		
		// check the .project file
		// if it is a regular integration project then return
		projectDescription.natures?.nature?.each { it ->
			def text = it.text()
			if (text == 'com.ibm.etools.msgbroker.tooling.messageBrokerProjectNature') {
				isMessageBrokerProject = true
			}
		}
		
		return isMessageBrokerProject
	}
}
