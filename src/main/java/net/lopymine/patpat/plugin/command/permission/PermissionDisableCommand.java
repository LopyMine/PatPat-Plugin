package net.lopymine.patpat.plugin.command.permission;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
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
public class PermissionDisableCommand implements ICommand {

	private static final TranslatableComponent COMPONENT = Component.translatable("patpat.command.permission.disable.success")
			.color(NamedTextColor.RED);

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		PatPatConfig config = PatPatConfig.getInstance();
		PermissionConfig permissionConfig = config.getPermissionRestrictions();
		if (!permissionConfig.isEnabled()) {
			sender.sendMsg("patpat.command.permission.disable.already");
			return;
		}
		permissionConfig.setEnabled(false);
		config.save();

		sender.sendMsg(COMPONENT);
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("permission.toggle");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat permission disable";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.permission.disable.description");
	}

}
