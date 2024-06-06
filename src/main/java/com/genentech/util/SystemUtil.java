package com.genentech.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class SystemUtil {
	static public Properties resource = null;

	/****
	 * load the test data in properties file
	 * 
	 * @param fileName
	 *            : The file name to load
	 * 
	 * 
	 */
	static public Properties loadPropertiesResources(String fileName) {
		resource = new Properties();
		try {
			File file = new File("./testdata/" + fileName);
			InputStream data_input = new FileInputStream(file);
			resource.load(data_input);
		} catch (Exception e) {
			System.out.println("Error:Not found properties file" + fileName);
			e.printStackTrace();
		}
		return resource;
	}

	public static void driverKiller() throws Exception {
		final String KILL = "taskkill /IM ";
		// IE process
		String processName = "IEDriverServer.exe";
		String[] completeName = new String[] {KILL + processName};
		Runtime.getRuntime().exec(completeName);
		Thread.sleep(1000);
		// chrome process
		String processName2 = "chromedriver.exe";
		completeName[0] = KILL + processName2;
		Runtime.getRuntime().exec(completeName);
		Thread.sleep(1000);
	}
}
