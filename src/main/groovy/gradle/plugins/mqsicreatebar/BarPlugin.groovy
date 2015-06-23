package gradle.plugins.mqsicreatebar

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin for creating bar file from a message broker Application
 * 
 * @author Jamie Archibald
 *
 */
class BarPlugin implements Plugin<Project> {

	def barFileName
	def barFilePath

    void apply(Project project) {
	
        project.extensions.create("application", ApplicationBarPluginExtension)
		
		project.task('init') << {
			if (project.version == null) {
				throw new Exception("Missing project.version")
			}
			if (project.name == null) {
				throw new Exception("Missing project.name")
			}
			barFileName = project.name + "-" + project.version + ".bar"
		}
		
		// prints the version of the application
        project.task('version', dependsOn: 'init') << {
            println "${project.name} ${project.version}"
        }
		
		// cleans the project by removing all binary artifacts
		project.task('clean', dependsOn: 'init') << {
			println "Cleaning ${project.name}"
			if (new File("./build").deleteDir() == false) {
				throw new Exception("Failed to delete ./build directory")
			}
			println "Clean complete"
		}
		
		// add task classes
		project.task('createBar', type: CreateBarTask, dependsOn: 'init')
		project.task('applyBarOverride', type: ApplyBarOverrideTask, dependsOn: 'createBar')
		
		// builds the entire project
		project.task('build', dependsOn: 'applyBarOverride') << {
			println "Build ${project.name} complete"
		}
		
		project.task('deploy', type: DeployBarTask, dependsOn: 'init')
    }
}

class ApplicationBarPluginExtension {
    String name
}