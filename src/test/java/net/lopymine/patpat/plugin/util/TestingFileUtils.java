package net.lopymine.patpat.plugin.util;

import java.io.File;

public class TestingFileUtils {

	private static final String RESOURCE_PATH = "build/resources/test";

	private TestingFileUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static File getResource(String test) {
		return new File(RESOURCE_PATH, test);
	}
}
