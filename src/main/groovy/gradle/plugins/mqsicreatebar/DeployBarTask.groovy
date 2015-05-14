package gradle.plugins.mqsicreatebar

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Executes the IBM mqsideploy command
 * 
 * @author Jamie Archibald
 *
 */
class DeployBarTask extends DefaultTask {

	@TaskAction
	def deploy() {
		println "Deploying ${project.name}"
		
		if (project.application.env == null) {
			throw new Exception("Missing project.application.env")
		}
	}
}
