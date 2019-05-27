package org.openmrs.module.initializer;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openmrs.util.OpenmrsUtil;

import java.io.IOException;

public class InitializerLogFactory {
	
	static Logger logger = null;
	
	static String targetLog = OpenmrsUtil.getApplicationDataDirectory() + "initializer.log";
	
	static FileAppender inizAppender = null;
	
	private static Logger setUpLogger() throws IOException {
		logger = Logger.getLogger("org.openmrs.module.initializer");
		inizAppender = new FileAppender(new PatternLayout("%d %-5p [%c{1}] %m%n"), targetLog, true);
		logger.addAppender(inizAppender);
		logger.setLevel((Level) Level.ALL);
		return logger;
	}
	
	public static Logger getLog() {
		if (logger == null) {
			try {
				logger = setUpLogger();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logger;
	}
	
}
