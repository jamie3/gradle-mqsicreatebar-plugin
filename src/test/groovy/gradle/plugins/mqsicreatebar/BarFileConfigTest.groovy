package gradle.plugins.mqsicreatebar;

import static org.junit.Assert.*

class BarFileConfigTest {

	@org.junit.Test
	def void barFileConfig_test() {
		
		def config = new ConfigSlurper().parse(new File('src/test/resources/barFile.config').toURL())
		
		assertNotNull config.barFile
		
		config.barFile.each { it ->
			def barName = it.getKey()
			println barName
			def include = it.getAt(barName).include
		}
	}
}