package net.lopymine.patpat.plugin.command.api;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {

	List<String> getSuggestions(CommandSender commandSender, String[] strings);

	void execute(CommandSender commandSender, String[] strings);

	String getPermissionKey();

	String getExampleOfUsage();

	Component getDescription();

}
