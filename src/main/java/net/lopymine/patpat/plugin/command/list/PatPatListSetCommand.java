package net.lopymine.patpat.plugin.command.list;

import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.command.PatPatCommandManager;
import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.options.ListMode;

import java.util.List;

public class PatPatListSetCommand implements ICommand {

	private static final List<String> LIST_MODES = List.of("WHITELIST", "BLACKLIST", "DISABLED");

	@Override
	public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
		return LIST_MODES;
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (strings.length == 0) {
			PatPatCommandManager.sendMessage(commandSender, PatPatCommandManager.getWrongMessage("command"));
			PatPatCommandManager.sendMessage(commandSender, this.getExampleOfUsage());
			return;
		}

		String value = strings[0];
		try {
			ListMode listMode = ListMode.valueOf(value.toUpperCase());
			PatPatConfig config = PatPatPlugin.getInstance().getPatPatConfig();
			config.setListMode(listMode);
			config.save();
			PatPatCommandManager.sendMessage(commandSender, "List mode has been changed to §6%s§r", listMode.name());
		} catch (IllegalArgumentException e) {
			PatPatCommandManager.sendMessage(commandSender, PatPatCommandManager.getWrongMessage("list mode"));
			PatPatCommandManager.sendMessage(commandSender, this.getExampleOfUsage());
		}
	}

	@Override
	public String getPermissionKey() {
		return PatPatPlugin.permission("list.set");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat list set [<WHITELIST> | <BLACKLIST> | <DISABLED>]";
	}

	@Override
	public String getDescription() {
		return "Sets mode of the permission list";
	}
}
