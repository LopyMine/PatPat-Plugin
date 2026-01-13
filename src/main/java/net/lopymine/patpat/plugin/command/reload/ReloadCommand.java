package net.lopymine.patpat.plugin.command.reload;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.PatTranslator;
import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.Collections;
import java.util.List;

@ExtensionMethod(CommandSenderExtension.class)
public class ReloadCommand implements ICommand {

	@Override
	public List<String> getSuggestions(CommandSender sender, String[] strings) {
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		PatPatConfig.reload();
		PlayerListConfig.reload();
		PatTranslator.unregister();
		PatTranslator.register();
		sender.sendMsg("patpat.command.reload");
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("reload");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat reload";
	}


	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.reload.description");
	}
}
