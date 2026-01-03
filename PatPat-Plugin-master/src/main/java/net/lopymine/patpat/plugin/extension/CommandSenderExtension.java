package net.lopymine.patpat.plugin.extension;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.command.PatPatCommandManager;
import net.lopymine.patpat.plugin.util.ComponentUtils;

public class CommandSenderExtension {

	private static final Component PREFIX_COMPONENT = ComponentUtils.wrapInBrackets(
			Component.text("PatPat").color(NamedTextColor.GREEN)
	).appendSpace();

	private CommandSenderExtension() {
		throw new IllegalStateException("Extension class");
	}

	public static void sendMsg(CommandSender sender, String message, Object... args) {
		sender.sendMessage(PatPatCommandManager.getPluginMessage(message.formatted(args)));
	}

	public static void sendMsg(CommandSender sender, ComponentLike message) {
		PatPatPlugin.getAdventure().sender(sender).sendMessage(PREFIX_COMPONENT.append(message));
	}

	public static void sendMsg(CommandSender sender, String key, ComponentLike... args) {
		PatPatPlugin.getAdventure().sender(sender).sendMessage(PREFIX_COMPONENT.append(Component.translatable(key).args(args)));
	}
}
