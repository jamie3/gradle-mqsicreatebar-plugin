package gradle.plugins.mqsicreatebar

class ProjectUtil {

	private ProjectUtil() {
		
	}
	
	static File getProjectFile(def project) {
		def projectFile = new File(project.projectDir.absolutePath + "/.project")
		
		if (projectFile.exists() == false) {
			throw new FileNotFoundException(".project", ".project file does not exist")
		}
		
		return projectFile
	}
	
	static ConfigObject getConfigFile(def project) {
		return new ConfigSlurper().parse(new File(project.projectDir.absolutePath + "/build.config").toURL())
	}
	
	static boolean isApplicationProject(def project) {
		
		def projectFile = getProjectFile(project)
		
		def projectDescription = new XmlSlurper().parse(projectFile)
		def isApplicationProject = false
		
		// check the .project file
		// if the project is an Application then
		projectDescription.natures?.nature?.each { it ->
			def text = it.text()
			if (text == 'com.ibm.etools.msgbroker.tooling.applicationNature') {
				isApplicationProject = true
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
