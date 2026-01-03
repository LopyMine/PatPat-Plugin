package net.lopymine.patpat.plugin.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

@UtilityClass
public class ComponentUtils {

	public static TextComponent wrapInBrackets(Component component) {
		return Component
				.text("[")
				.append(component)
				.append(Component.text("]"));
	}

}
