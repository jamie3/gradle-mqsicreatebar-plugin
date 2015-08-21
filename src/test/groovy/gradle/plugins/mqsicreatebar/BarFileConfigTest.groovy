package gradle.plugins.mqsicreatebar;

import static org.junit.Assert.*

class BarFileConfigTest {

	@org.junit.Test
	def void barFileConfig_test() {
		
		def config = new ConfigSlurper().parse(new File('src/test/resources/test.config').toURL())
		
		assertNotNull config.barFile
		assertNotNull config.workspace
		
		println config.files
		
		config.files.each { it ->
			def filePath = it.toString()
			println filePath
		}
	}
}