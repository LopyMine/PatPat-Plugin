package net.lopymine.patpat.plugin.extension;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;

public class PlayerExtension {

	private PlayerExtension() {
		throw new IllegalStateException("Extension class");
	}

	public static void sendPatPatMessage(Player sender, String message, Object... args) {
		CommandSenderExtension.sendMsg(sender, message, args);
	}

	public static void sendPatPatMessage(Player sender, ComponentLike message) {
		CommandSenderExtension.sendMsg(sender, message);
	}

}
