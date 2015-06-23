package gradle.plugins.mqsicreatebar

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.junit.Test;
import static org.junit.Assert.*

/**
 * Tests for the CreateBarTask. This requires running the tests using the IBM Integration Console environment.
 * 
 * @author Jamie Archibald
 *
 */
class CreateBarIntegrationTest {
	
	/**
	 * Creates a bar file from an IIB Application project
	 */
	@Test
	def void createApplicationBar_test() {
		
		def projectDir = "src/test/resources/iib-project/example-application"
		
		Project project = ProjectBuilder.builder()
			.withProjectDir(new File(projectDir))
			.withName("example-application")
			.build()
		project.apply plugin: 'gradle.plugins.mqsicreatebar'
		
		project.buildDir = projectDir
		project.version = "1.0"
		
		project.tasks.clean.execute()
		project.tasks.createBar.execute()
		
		assertTrue "Bar file not found", new File("build/example-application-1.0.bar").exists()
	}
	
	/**
	 * Creates a bar file from an IIB Application project
	 */
	@Test
	def void createIntegrationBar_test() {
		
		def projectDir = "src/test/resources/iib-project/example-integration"
		
		Project project = ProjectBuilder.builder()
			.withProjectDir(new File(projectDir))
			.withName("example-integration")
			.build()
		project.apply plugin: 'gradle.plugins.mqsicreatebar'
		
		project.buildDir = projectDir
		project.version = "1.0"
		
		project.tasks.clean.execute()
		project.tasks.createBar.execute()
		
		assertTrue "Bar file not found", new File("build/example-integration-1.0.bar").exists()
	}
}
