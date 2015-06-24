package gradle.plugins.mqsicreatebar

class Debug {

	static debug = false
	static firstTime = true
	
	static debug(def string) {
		if (firstTime) {
			if (System.properties['debug'] != null) {
				debug = true
				firstTime = false
			}
		}
		if (debug) {
			println string
		}
	}
}
