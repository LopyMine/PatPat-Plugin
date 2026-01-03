package net.lopymine.patpat.plugin.config.option;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum ListMode {
	WHITELIST(NamedTextColor.GREEN),
	BLACKLIST(NamedTextColor.GREEN),
	DISABLED(NamedTextColor.RED);

	private final NamedTextColor formatting;

	ListMode(NamedTextColor formatting) {
		this.formatting = formatting;
	}

	public Component getText() {
		return Component.text(this.name()).color(this.formatting);
	}
}
