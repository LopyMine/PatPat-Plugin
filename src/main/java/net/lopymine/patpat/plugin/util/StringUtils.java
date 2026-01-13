package net.lopymine.patpat.plugin.util;

import lombok.experimental.UtilityClass;

import static net.lopymine.patpat.plugin.PatPatPlugin.MOD_ID;

@UtilityClass
public class StringUtils {

	public static String modId(String path) {
		return "%s:%s".formatted(MOD_ID, path);
	}

	public static String permission(String permission) {
		return "%s.%s".formatted(MOD_ID, permission);
	}

	public static String commandPermission(String permission) {
		return "%s.command.%s".formatted(MOD_ID, permission);
	}

}
