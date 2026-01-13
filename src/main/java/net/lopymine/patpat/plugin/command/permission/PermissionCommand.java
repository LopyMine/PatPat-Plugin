package net.lopymine.patpat.plugin.command.permission;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PermissionConfig;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.Collections;
import java.util.List;

@ExtensionMethod(CommandSenderExtension.class)
public class PermissionCommand implements ICommand {

	public static final Component PERMISSION_ENABLED = Component
			.translatable("patpat.command.permission.info.status")
			.arguments(Component
					.translatable("patpat.formatter.enabled_or_disabled.true")
					.color(NamedTextColor.GREEN)
			);

	public static final Component PERMISSION_DISABLED = Component
			.translatable("patpat.command.permission.info.status")
			.arguments(Component
					.translatable("patpat.formatter.enabled_or_disabled.false")
					.color(NamedTextColor.RED)
			);
	public static final String PERMISSION_INFO = "patpat.command.permission.info.permission";

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		PatPatConfig config = PatPatConfig.getInstance();
		PermissionConfig permissionConfig = config.getPermissionRestrictions();
		sender.sendMsg(permissionConfig.isEnabled() ? PERMISSION_ENABLED : PERMISSION_DISABLED);
		sender.sendMsg(PERMISSION_INFO, Component.text(permissionConfig.getPermissionForPat()).color(NamedTextColor.GOLD));
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("permission.info");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat permission [enable | disable | set]";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.permission.info.description");
	}
}
