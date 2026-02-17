package net.lopymine.patpat.plugin.command.pat;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.packet.handler.PatPacketHandler;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.*;

@ExtensionMethod(CommandSenderExtension.class)
public class PatCommand implements ICommand {

	private static final int MAX_DISTANCE_SUGGESTION_ENTITY = 16;
	private static final String API_NULL_PLACEHOLDER = "console";

	@Override
	public void execute(CommandSender sender, String[] strings) {
		if (strings.length == 0 || strings.length > 2) {
			sender.sendMsg(Component.text(getExampleOfUsage()));
			return;
		}

		String pattedEntityValue = strings[0];
		LivingEntity pattedEntity;
		try {
			UUID uuid = UUID.fromString(pattedEntityValue);
			Entity entity = Bukkit.getEntity(uuid);
			if (entity == null) {
				sender.sendMsg("patpat.command.error.entity_not_exist", Component.text(pattedEntityValue).color(NamedTextColor.GOLD));
				return;
			}
			if (!(entity instanceof LivingEntity livingEntity)) {
				sender.sendMsg("patpat.command.error.entity_not_living_entity", Component.text(pattedEntityValue).color(NamedTextColor.GOLD));
				return;
			}
			pattedEntity = livingEntity;

		} catch (IllegalArgumentException ignored) {
			Player player = Bukkit.getPlayerExact(pattedEntityValue);
			if (player == null) {
				sender.sendMsg("patpat.command.error.player_not_exist", Component.text(pattedEntityValue).color(NamedTextColor.GOLD));
				return;
			}
			if (!player.isOnline()) {
				sender.sendMsg("patpat.command.error.player_not_online", Component.text(pattedEntityValue).color(NamedTextColor.GOLD));
				return;
			}
			pattedEntity = player;
		}
		Player whoPatted = sender instanceof Player player ? player : null;
		if (strings.length == 2) {
			if (Objects.equals(strings[1], API_NULL_PLACEHOLDER)) {
				whoPatted = null;
			} else {
				Player player = Bukkit.getPlayerExact(strings[1]);
				if (player == null) {
					sender.sendMsg("patpat.command.error.player_not_exist", Component.text(strings[1]).color(NamedTextColor.GOLD));
					return;
				}
				if (!player.isOnline()) {
					sender.sendMsg("patpat.command.error.player_not_online", Component.text(strings[1]).color(NamedTextColor.GOLD));
					return;
				}
				whoPatted = player;
			}
		}
		PatPacketHandler.showPatPacket(pattedEntity, whoPatted, true);
		String patSuccessKey = pattedEntity instanceof Player ? "patpat.command.pat.success.player" : "patpat.command.pat.success.mob";
		sender.sendMsg(patSuccessKey, Component.text(pattedEntityValue).color(NamedTextColor.GOLD));
	}

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		if (strings.length == 0 || strings.length > 2) {
			return Collections.emptyList();
		}

		String value = strings[strings.length - 1].toLowerCase();
		List<String> suggestions = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			String name = player.getName();
			if (name.toLowerCase().startsWith(value)) {
				suggestions.add(name);
			}
		}
		if (strings.length == 2) {
			if (API_NULL_PLACEHOLDER.toLowerCase().startsWith(strings[1].toLowerCase())) {
				suggestions.add(API_NULL_PLACEHOLDER);
			}
			return suggestions;
		}
		if (!(commandSender instanceof Player player)) {
			return suggestions;
		}
		for (Entity entity : player.getNearbyEntities(MAX_DISTANCE_SUGGESTION_ENTITY, MAX_DISTANCE_SUGGESTION_ENTITY, MAX_DISTANCE_SUGGESTION_ENTITY)) {
			if (!(entity instanceof LivingEntity) || entity instanceof Player) {
				continue;
			}
			String uuid = entity.getUniqueId().toString();
			if (uuid.startsWith(value)) {
				suggestions.add(uuid);
			}
		}
		return suggestions;
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("pat");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat pat (<UUID> | <NICKNAME>) [<NICKNAME>]";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.pat.description");
	}
}
