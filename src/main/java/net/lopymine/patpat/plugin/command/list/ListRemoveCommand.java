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

@ExtensionMethod(CommandSenderExtension.class)
public class ListRemoveCommand implements ICommand {

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		if (strings.length != 1) {
			return Collections.emptyList();
		}

		String prefix = strings[0].toLowerCase();
		return PlayerListConfig.getInstance().getNicknameByUuid().entrySet().stream()
				.map(entry -> {
					if(!Objects.equals(entry.getValue(), "?")){
						return entry.getValue();
					}
					return entry.getKey().toString();
				})
				.filter(s -> s.toLowerCase().startsWith(prefix))
				.toList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		if (strings.length == 0) {
			sender.sendMsg(this.getExampleOfUsage());
			return;
		}

		String value = strings[0];
		try {
			UUID uuid = UUID.fromString(value);
			removeUuid(sender, uuid);
		} catch (IllegalArgumentException ignored) {
			removeName(sender, value);
		}
	}

	private void removeUuid(CommandSender sender, UUID uuid) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		String nickname = offlinePlayer.getName();
		if (nickname == null) {
			PlayerListConfig config = PlayerListConfig.getInstance();
			nickname = config.getNicknameByUuid().getOrDefault(uuid, "?");
		}

		TextComponent nicknameComponent = Component
				.text(nickname)
				.color(NamedTextColor.GOLD)
				.hoverEvent(HoverEvent.showText(Component.text(uuid.toString())))
				.clickEvent(ClickEvent.clickEvent(Action.COPY_TO_CLIPBOARD, uuid.toString()));

		PlayerListConfig config = PlayerListConfig.getInstance();
		if (config.remove(offlinePlayer.getUniqueId())) {
			TranslatableComponent component = Component.translatable("patpat.command.list.remove.success")
					.color(NamedTextColor.RED)
					.arguments(nicknameComponent);

			sender.sendMsg(component);
			config.save();
		} else {
			sender.sendMsg("patpat.command.list.remove.already", nicknameComponent);
		}
	}

	private void removeName(CommandSender sender, String nickname) {
		PlayerListConfig config = PlayerListConfig.getInstance();

		TextComponent nicknameComponent = Component
				.text(nickname)
				.color(NamedTextColor.GOLD);
		if (config.remove(nickname)) {
			TranslatableComponent component = Component.translatable("patpat.command.list.remove.success")
					.color(NamedTextColor.RED)
					.arguments(nicknameComponent);

			sender.sendMsg(component);
			config.save();
		} else {
			sender.sendMsg("patpat.command.list.remove.already", nicknameComponent);
		}
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("list.remove");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat list remove (<UUID> | <NICKNAME>)";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.list.remove.description");
	}
}
