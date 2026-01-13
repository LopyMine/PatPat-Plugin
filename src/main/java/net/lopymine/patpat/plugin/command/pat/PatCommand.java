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

	@Override
	public void execute(CommandSender sender, String[] strings) {
		if (strings.length != 1) {
			sender.sendMsg(Component.text(getExampleOfUsage()));
			return;
		}

		String value = strings[0];
		Player whoPatted = sender instanceof Player player ? player : null;
		LivingEntity pattedEntity = null;
		try {
			UUID uuid = UUID.fromString(value);
			Entity entity = Bukkit.getEntity(uuid);
			if (entity == null) {
				sender.sendMsg("patpat.command.error.entity_not_exist", Component.text(value).color(NamedTextColor.GOLD));
				return;
			}
			if (!(entity instanceof LivingEntity livingEntity)) {
				sender.sendMsg("patpat.command.error.entity_not_living_entity", Component.text(value).color(NamedTextColor.GOLD));
				return;
			}
			pattedEntity = livingEntity;

		} catch (IllegalArgumentException ignored) {
			Player player = Bukkit.getPlayerExact(value);
			if (player == null) {
				sender.sendMsg("patpat.command.error.player_not_exist", Component.text(value).color(NamedTextColor.GOLD));
				return;
			}
			if (!player.isOnline()) {
				sender.sendMsg("patpat.command.error.player_not_online", Component.text(value).color(NamedTextColor.GOLD));
				return;
			}
			pattedEntity = player;
		}
		PatPacketHandler.showPatPacket(pattedEntity, whoPatted, false);
		String patSuccessKey = pattedEntity instanceof Player ? "patpat.command.pat.success.player" : "patpat.command.pat.success.mob";
		sender.sendMsg(patSuccessKey, Component.text(value).color(NamedTextColor.GOLD));
	}

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		if (strings.length != 1) {
			return Collections.emptyList();
		}
		String value = strings[0];
		List<String> suggestions = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			String name = player.getName();
			if (name.startsWith(value)) {
				suggestions.add(name);
			}
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
		return "/patpat pat (<UUID> | <NICKNAME>)";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.pat.description");
	}
}
