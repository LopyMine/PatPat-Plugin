package net.lopymine.patpat.plugin.util;

import lombok.experimental.UtilityClass;

import net.lopymine.patpat.plugin.PatLogger;

@UtilityClass
public class FoliaUtils {

	public static final boolean IS_FOLIA = isFolia();

	private static boolean isFolia() {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			PatLogger.debug("Folia server detected");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
