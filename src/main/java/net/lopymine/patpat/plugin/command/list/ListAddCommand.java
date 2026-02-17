package net.lopymine.patpat.plugin.command.list;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

@ExtensionMethod(CommandSenderExtension.class)
public class ListAddCommand implements ICommand {

	@Override
	public List<String> getSuggestions(CommandSender sender, String[] strings) {
		if (strings.length != 1) {
			return Collections.emptyList();
		}

		String prefix = strings[0].toLowerCase();
		return Bukkit.getOnlinePlayers().stream()
				.flatMap(player -> Stream.of(
						player.getName(),
						player.getUniqueId().toString()
				))
				.filter(s -> s.toLowerCase().startsWith(prefix))
				.toList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		if (strings.length == 0) {
			sender.sendMsg(this.getExampleOfUsage());
		}

		String value = strings[0];
		try {
			UUID uuid = UUID.fromString(value);
			addUuid(sender, uuid);
		} catch (IllegalArgumentException ignored) {
			addName(sender, value);
		}
	}

	private void addUuid(CommandSender sender, UUID uuid) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		String nickname = offlinePlayer.getName();
		if (nickname == null) {
			nickname = "?";
		}
		addPlayer(sender, uuid, nickname);
	}

	private void addName(CommandSender sender, String nickname) {
		OfflinePlayer offlinePlayer = null;
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (nickname.equals(player.getName())) {
				offlinePlayer = player;
				break;
			}
		}

		if (offlinePlayer == null) {
			sender.sendMsg("patpat.command.error.player_not_exist", Component.text(nickname).color(NamedTextColor.GOLD));
			return;
		}
		addPlayer(sender, offlinePlayer.getUniqueId(), nickname);
	}

	private void addPlayer(CommandSender sender, UUID uuid, String nickname) {
		PlayerListConfig config = PlayerListConfig.getInstance();
		TextComponent nicknameComponent = Component
				.text(nickname)
				.color(NamedTextColor.GOLD)
				.hoverEvent(HoverEvent.showText(Component.text(uuid.toString())))
				.clickEvent(ClickEvent.clickEvent(Action.COPY_TO_CLIPBOARD, uuid.toString()));

		if (config.add(uuid, nickname)) {
			TranslatableComponent component = Component.translatable("patpat.command.list.add.success")
					.color(NamedTextColor.GREEN)
					.arguments(nicknameComponent);

			sender.sendMsg(component);
			config.save();
		} else {
			sender.sendMsg("patpat.command.list.add.already", nicknameComponent);
		}
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("list.add");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat list add (<UUID> | <NICKNAME>)";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.list.add.description");
	}
}
