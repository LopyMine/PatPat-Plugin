package net.lopymine.patpat.plugin.command.permission.set;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.command.permission.PermissionCommand;
import net.lopymine.patpat.plugin.config.*;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@ExtensionMethod(CommandSenderExtension.class)
public class PermissionSetCommand implements ICommand {

	private static final Pattern PERMISSION_RULES = Pattern.compile("^[a-z0-9_]+(\\.[a-z0-9_]+)*(\\.\\*)?$");

	@Override
	public List<String> getSuggestions(CommandSender sender, String[] strings) {
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		PatPatConfig config = PatPatConfig.getInstance();
		PermissionConfig permissionConfig = config.getPermissionRestrictions();
		if(strings.length == 0) {
			sender.sendMsg(PermissionCommand.PERMISSION_INFO, Component.text(permissionConfig.getPermissionForPat()).color(NamedTextColor.GOLD));
			return;
		}
		String permission = strings[0];
		if(permission.contains(" ")){
			sender.sendMsg("patpat.command.error.permission_contain_space", Component.text(permission).color(NamedTextColor.GOLD));
			return;
		}
		if(!permission.equals(permission.toLowerCase())) {
			sender.sendMsg("patpat.command.error.permission_only_lowercase", Component.text(permission).color(NamedTextColor.GOLD));
			return;
		}
		if(!PERMISSION_RULES.matcher(permission).find()){
			sender.sendMsg("patpat.command.error.permission_unknown_character", Component.text(permission).color(NamedTextColor.GOLD));
			return;
		}
		permissionConfig.setPermissionForPat(permission);
		config.save();

		sender.sendMsg("patpat.command.permission.set.success", Component.text(permission).color(NamedTextColor.GOLD));
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("permission.set");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat permission set [<permission>]";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.permission.set.description");
	}

}
