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
public class PermissionEnableCommand implements ICommand {

	private static final TranslatableComponent COMPONENT = Component.translatable("patpat.command.permission.enable.success")
			.color(NamedTextColor.GREEN);

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		PatPatConfig config = PatPatConfig.getInstance();
		PermissionConfig permissionConfig = config.getPermissionRestrictions();
		if (permissionConfig.isEnabled()) {
			sender.sendMsg("patpat.command.permission.enable.already");
			return;
		}
		permissionConfig.setEnabled(true);
		config.save();

		sender.sendMsg(COMPONENT.arguments(Component.text(permissionConfig.getPermissionForPat()).color(NamedTextColor.GOLD)));
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("permission.toggle");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat permission enable";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.permission.enable.description");
	}
}
