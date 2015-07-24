package com.gbi.commons.util.file;

import java.io.File;

public class ResourceUtil {
	public static File getTestFile(Class<?> clazz, String filename) {
		return new File(getTestFileAbstractName(clazz, filename));
	}

	public static String getTestFileAbstractName(Class<?> clazz, String filename) {
		System.out.println(System.getProperty("user.dir") + "/src/test/resources/" + clazz.getName().replace('.', '/') + "_test/" + filename);
		return System.getProperty("user.dir") + "/src/test/resources/" + clazz.getName().replace('.', '/') + "_test/" + filename;
	}
}