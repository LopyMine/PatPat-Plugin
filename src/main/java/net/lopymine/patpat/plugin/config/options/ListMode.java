package net.lopymine.patpat.plugin.config.options;

public enum ListMode {
	WHITELIST,
	BLACKLIST,
	DISABLED;

	public static ListMode getOrDisabled(String modeId) {
		try {
			return ListMode.valueOf(modeId);
		} catch (IllegalArgumentException ignored) {
			return DISABLED;
		}
	}
}
