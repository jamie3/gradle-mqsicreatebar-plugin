package gradle.plugins.mqsicreatebar

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class MqsicreatebarPluginTest {
	
	Project project
	
	@Before
	def void setup() {
		project = ProjectBuilder.builder().build()
		project.apply plugin: 'gradle.plugins.mqsicreatebar'
	}
	
	@Test
	def void checkTasks_test() {
		assertTrue(project.tasks.createBar instanceof CreateBarTask)
		assertTrue(project.tasks.applyBarOverride instanceof ApplyBarOverrideTask)
	}
	
}