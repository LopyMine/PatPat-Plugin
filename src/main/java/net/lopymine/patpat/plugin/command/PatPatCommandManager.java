package net.lopymine.patpat.plugin.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.PluginCommand;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.command.api.*;
import net.lopymine.patpat.plugin.command.info.InfoCommand;
import net.lopymine.patpat.plugin.command.list.*;
import net.lopymine.patpat.plugin.command.pat.PatCommand;
import net.lopymine.patpat.plugin.command.permission.*;
import net.lopymine.patpat.plugin.command.permission.set.PermissionSetCommand;
import net.lopymine.patpat.plugin.command.ratelimit.*;
import net.lopymine.patpat.plugin.command.ratelimit.set.*;
import net.lopymine.patpat.plugin.command.reload.ReloadCommand;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.Objects;

public class PatPatCommandManager {

	private static final Component NO_PERMISSION_MESSAGE = Component
			.translatable("patpat.command.no_permission")
			.color(NamedTextColor.RED);

	private PatPatCommandManager() {
		throw new IllegalStateException("Manager class");
	}

	public static void register() {
		SimpleCommand listCommand = registerListCommand();
		SimpleCommand rateLimitCommand = registerRateLimitCommand();
		SimpleCommand permissionCommand = registerPermissionCommand();
		SimpleCommand reloadCommand = getSimpleCommand(new ReloadCommand());
		SimpleCommand infoCommand = getSimpleCommand(new InfoCommand());
		SimpleCommand patCommand = getSimpleCommand(new PatCommand());

		SimpleCommand rootCommand = SimpleCommand.builder()
				.usage("/patpat (info | list | ratelimit | reload | pat | permission)")
				.child(listCommand, "list")
				.child(rateLimitCommand, "ratelimit")
				.child(reloadCommand, "reload")
				.child(infoCommand, "info")
				.child(patCommand, "pat")
				.child(permissionCommand, "permission")
				.build();

		PatPatPlugin plugin = PatPatPlugin.getInstance();
		PluginCommand command = plugin.getCommand("patpat");
		Objects.requireNonNull(command, "Command `patpat` is not registered");
		command.setExecutor(rootCommand);
	}

	private static SimpleCommand registerListCommand() {
		SimpleCommand infoCommand = getSimpleCommand(new ListInfoCommand());
		SimpleCommand setModeCommand = getSimpleCommand(new ListSetCommand());
		SimpleCommand addToListCommand = getSimpleCommand(new ListAddCommand());
		SimpleCommand removeFromListCommand = getSimpleCommand(new ListRemoveCommand());

		return SimpleCommand.builder()
				.permission(StringUtils.commandPermission("list"))
				.usage("/patpat list (info | set | add | remove)")
				.child(infoCommand, "info")
				.child(setModeCommand, "set")
				.child(addToListCommand, "add")
				.child(removeFromListCommand, "remove")
				.msgNoPermission(NO_PERMISSION_MESSAGE)
				.build();
	}

	private static SimpleCommand registerRateLimitCommand() {
		SimpleCommand infoCommand = getSimpleCommand(new RateLimitInfoCommand());
		SimpleCommand enableCommand = getSimpleCommand(new RateLimitEnableCommand());
		SimpleCommand disableCommand = getSimpleCommand(new RateLimitDisableCommand());

		SimpleCommand incrementCommand = getSimpleCommand(new IncrementCommand());
		SimpleCommand intervalCommand = getSimpleCommand(new IntervalCommand());
		SimpleCommand limitCommand = getSimpleCommand(new LimitCommand());

		SimpleCommand setCommand = SimpleCommand.builder()
				.permission(StringUtils.commandPermission("ratelimit.set"))
				.usage("/patpat ratelimit set (increment | interval | limit)")
				.child(incrementCommand, "increment")
				.child(intervalCommand, "interval")
				.child(limitCommand, "limit")
				.msgNoPermission(NO_PERMISSION_MESSAGE)
				.build();

		return SimpleCommand.builder()
				.permission(StringUtils.commandPermission("ratelimit"))
				.usage("/patpat ratelimit (enable | disable | set | info)")
				.child(enableCommand, "enable")
				.child(disableCommand, "disable")
				.child(infoCommand, "info")
				.child(setCommand, "set")
				.msgNoPermission(NO_PERMISSION_MESSAGE)
				.build();
	}

	private static SimpleCommand registerPermissionCommand() {
		SimpleCommand setCommand = getSimpleCommand(new PermissionSetCommand());
		SimpleCommand enableCommand = getSimpleCommand(new PermissionEnableCommand());
		SimpleCommand disableCommand = getSimpleCommand(new PermissionDisableCommand());

		return SimpleCommand.builder()
				.permission(StringUtils.commandPermission("permission"))
				.usage("/patpat permission [enable | disable | set]")
				.child(enableCommand, "enable")
				.child(disableCommand, "disable")
				.child(setCommand, "set")
				.msgNoPermission(NO_PERMISSION_MESSAGE)
				.executor(new PermissionCommand())
				.build();
	}

	public static SimpleCommand getSimpleCommand(ICommand command) {
		return SimpleCommand.builder()
				.permission(command.getPermissionKey())
				.usage(command.getExampleOfUsage())
				.msgNoPermission(NO_PERMISSION_MESSAGE)
				.description(command.getDescription())
				.executor(command)
				.build();
	}

	public static SimpleCommand getSimpleCommand(ICommand command, ChildCommand... childCommands) {
		SimpleCommand.Builder simpleCommandBuilder = SimpleCommand.builder()
				.permission(command.getPermissionKey())
				.usage(command.getExampleOfUsage())
				.msgNoPermission(NO_PERMISSION_MESSAGE)
				.description(command.getDescription())
				.executor(command);

		for (ChildCommand childCommand : childCommands) {
			simpleCommandBuilder.child(childCommand.getCommand(), childCommand.getName(), childCommand.getAliases());
		}
		return simpleCommandBuilder.build();
	}


}
