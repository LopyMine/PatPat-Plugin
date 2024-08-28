package net.lopymine.patpat.plugin.patPat.command.list;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.patPat.PatPatPlugin;
import net.lopymine.patpat.plugin.patPat.command.PatPatCommandManager;
import net.lopymine.patpat.plugin.patPat.command.api.ICommand;
import net.lopymine.patpat.plugin.patPat.config.PlayerListConfig;

import java.util.*;

public class PatPatListAddCommand implements ICommand {

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		if (strings.length == 1) {
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.startsWith(strings[0])).toList();
		}
		return List.of();
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (strings.length == 0) {
			PatPatCommandManager.sendMessage(commandSender, PatPatCommandManager.getWrongMessage("command"));
			PatPatCommandManager.sendMessage(commandSender, this.getExampleOfUsage());
		}

		String value = strings[0];
		OfflinePlayer offlinePlayer = null;
		try {
			UUID uuid = UUID.fromString(value);
			offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		} catch (IllegalArgumentException ignored) {
			for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				if (value.equals(player.getName())) {
					offlinePlayer = player;
					break;
				}
			}
		}

		if (offlinePlayer == null) {
			PatPatCommandManager.sendMessage(commandSender, "Failed to find player with \"§6%s§r\" uuid or nickname".formatted(value));
			return;
		}

		PlayerListConfig config = PatPatPlugin.getInstance().getPlayerListConfig();
		Set<UUID> uuids = config.getUuids();
		if (uuids.add(offlinePlayer.getUniqueId())) {
			PatPatCommandManager.sendMessage(commandSender, "Player §6%s§r has been added to list".formatted(offlinePlayer.getName()));
		} else {
			PatPatCommandManager.sendMessage(commandSender, "Player §6%s§r already added to list!".formatted(offlinePlayer.getName()));
		}
		config.save();
	}

	@Override
	public String getPermissionKey() {
		return PatPatPlugin.permission("list.add");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat list add [<UUID> | <NICKNAME>]";
	}

	@Override
	public String getDescription() {
		return "Adds player to the permission list";
	}
}
