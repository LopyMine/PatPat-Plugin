package net.lopymine.patpat.plugin.command;

import org.bukkit.command.*;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.command.api.*;
import net.lopymine.patpat.plugin.command.list.*;

import java.util.Objects;

public class PatPatCommandManager {

	public static void register() {
		SimpleCommand setModeCommand = getSimpleCommand(new PatPatListSetCommand());
		SimpleCommand addToListCommand = getSimpleCommand(new PatPatListAddCommand());
		SimpleCommand removeFromListCommand = getSimpleCommand(new PatPatListRemoveCommand());

		SimpleCommand listCommand = SimpleCommand.builder()
				.permission(PatPatPlugin.permission("list"))
				.usage(PatPatCommandManager.getPluginMessage("/patpat list [<set> | <add> | <remove>]"))
				.child(setModeCommand, "set")
				.child(addToListCommand, "add")
				.child(removeFromListCommand, "remove")
				.build();

		SimpleCommand rootCommand = SimpleCommand.builder()
				.usage(PatPatCommandManager.getPluginMessage("/patpat [<list>]"))
				.child(listCommand, "list")
				.build();

		PatPatPlugin plugin = PatPatPlugin.getInstance();
		PluginCommand command = plugin.getCommand("patpat");
		Objects.requireNonNull(command);
		command.setExecutor(rootCommand);
	}

	public static SimpleCommand getSimpleCommand(ICommand command) {
		return SimpleCommand.builder()
				.permission(command.getPermissionKey())
				.usage(command.getExampleOfUsage())
				.msgNoPermission(PatPatCommandManager.getNoPermissionMessage())
				.description(command.getDescription())
				.executor(command)
				.build();
	}

	public static void sendMessage(CommandSender sender, String message, Object... args) {
		sender.sendMessage(getPluginMessage(message.formatted(args)));
	}

	public static String getNoPermissionMessage() {
		return getPluginMessage("You don't have permissions to execute this command!");
	}

	public static String getWrongMessage(String valueWhichIsWrong) {
		return "Was entered wrong %s, please follow this example of usage:".formatted(valueWhichIsWrong);
	}

	public static String getPluginMessage(String message) {
		return "[§lPatPat Plugin§r] " + message;
	}
}
