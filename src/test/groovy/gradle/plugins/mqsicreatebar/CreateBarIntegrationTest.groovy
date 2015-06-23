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
		
		Project project = ProjectBuilder.builder().withProjectDir(new File("src/test/resources/example-application")).withName("example-application").build()
		project.apply plugin: 'gradle.plugins.mqsicreatebar'
		
		project.buildDir = "src/test/resources/example-application"
		project.version = "1.0"
		
		/*project.
		project.buildDir = "src/test/resources/putAt("barFile", ["test":"test2"])
		println project.barFile
		*/
		
		project.tasks.clean.execute()
		project.tasks.createBar.execute()
		
		assertTrue new File("build/example-application-1.0.bar").exists()
	}
}
