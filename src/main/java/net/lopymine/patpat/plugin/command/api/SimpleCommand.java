package net.lopymine.patpat.plugin.command.api;

import org.bukkit.command.*;

import net.lopymine.patpat.plugin.command.PatPatCommandManager;

import java.util.*;
import org.jetbrains.annotations.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Makes command creating a little bit easier and with support Tab Completer
 * <p>
 * By nikita51
 */
@SuppressWarnings("unused")
public final class SimpleCommand implements TabExecutor {

	private final String description;
	private final String msgNoPermission;
	private final String usage;
	private final String permission;
	private final ICommand command;
	private final Map<String, SimpleCommand> child;

	private SimpleCommand(@Nullable ICommand command,
	                      @Nullable String description,
	                      @Nullable String msgNoPermission,
	                      @Nullable String usage,
	                      @Nullable String permission,
	                      @Nullable Map<String, SimpleCommand> child) {
		this.command         = command;
		this.usage           = usage;
		this.permission      = permission;
		this.description     = description;
		this.msgNoPermission = msgNoPermission;
		this.child           = Objects.requireNonNullElseGet(child, HashMap::new);
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Sends user command description
	 *
	 * @param sender is to whom will be sent description
	 */
	public void printDescription(@NotNull CommandSender sender) {
		if (permission == null || sender.hasPermission(permission)) {
			if (description != null) {
				sender.sendMessage(description);
			}
			child.values().forEach(simpleCommand -> simpleCommand.printDescription(sender));
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		try {
			if (permission == null || sender.hasPermission(permission)) {
				if (args.length == 0) {
					accept(sender, args);
					return true;
				}
				String[] cropArgs = new String[args.length - 1];
				if (cropArgs.length > 0) {
					System.arraycopy(args, 1, cropArgs, 0, cropArgs.length);
				}
				SimpleCommand simpleCommand = child.get(args[0].toLowerCase(Locale.ROOT));
				if (simpleCommand != null) {
					simpleCommand.onCommand(sender, command, label, cropArgs);
					return true;
				}

				accept(sender, args);
				return true;
			}
			if (msgNoPermission != null) {
				sender.sendMessage(msgNoPermission);
			}
			return true;
		} catch (Exception ignore) {
			return false;
		}
	}

	private void accept(@NotNull CommandSender sender, @NotNull String[] args) {
		if (command != null) {
			command.execute(sender, args);
		} else if (usage != null) {
			PatPatCommandManager.sendMessage(sender, PatPatCommandManager.getWrongMessage("command"));
			PatPatCommandManager.sendMessage(sender, usage);
		}
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (permission != null && !sender.hasPermission(permission)) {
			return Collections.emptyList();
		}
		String cmd = args[0].toLowerCase(Locale.ROOT);
		SimpleCommand simpleCommand = child.get(cmd);
		if (simpleCommand != null) {
			if (args.length > 1) {
				String[] cropArgs = new String[args.length - 1];
				System.arraycopy(args, 1, cropArgs, 0, cropArgs.length);
				return simpleCommand.onTabComplete(sender, command, label, cropArgs);
			}
			return Collections.emptyList();
		} else if (this.command != null) {
			return this.command.getSuggestions(sender, args);
		}
		return child.entrySet().stream()
				.filter(e -> e.getKey().startsWith(cmd))
				.filter(e -> {
					String perm = e.getValue().permission;
					return perm == null || sender.hasPermission(perm);
				})
				.map(Map.Entry::getKey)
				.toList();
	}

	public static final class Builder {

		@Nullable
		private String description;
		@Nullable
		private String msgNoPermission;
		@Nullable
		private String usage;
		@Nullable
		private String permission;
		@Nullable
		private ICommand command;
		@Nullable
		private Map<String, SimpleCommand> childCommandMap;

		private Builder() {
		}

		/**
		 * Sets description which was printed in {@link SimpleCommand#printDescription}
		 *
		 * @param description the command description
		 * @return {@link Builder}
		 */
		public Builder description(@Nullable String description) {
			this.description = description;
			return this;
		}

		/**
		 * Sets message which will be sent to user when user doesn't have permissions to execute that command
		 *
		 * @param msgNoPermission the message
		 * @return {@link Builder}
		 */
		public Builder msgNoPermission(@Nullable String msgNoPermission) {
			this.msgNoPermission = msgNoPermission;
			return this;
		}

		/**
		 * Sets the message if user typed wrong command
		 *
		 * @param usage the message
		 * @return {@link Builder}
		 */
		public Builder usage(@Nullable String usage) {
			this.usage = usage;
			return this;
		}

		/**
		 * Sets specific permission tag
		 *
		 * @param permission the permission tag, example: "mute.*"
		 * @return {@link Builder}
		 */
		public Builder permission(@Nullable String permission) {
			this.permission = permission;
			return this;
		}

		/**
		 * Sets command and tab completer executor
		 *
		 * @param executor the command executor
		 * @return {@link Builder}
		 */
		public Builder executor(@Nullable ICommand executor) {
			this.command = executor;
			return this;
		}

		/**
		 * Sets sub commands
		 *
		 * @param child   the child command
		 * @param name    the child command name
		 * @param aliases the command aliases
		 * @return {@link Builder}
		 */
		public Builder child(@NotNull SimpleCommand child, @NotNull String name, @NotNull String... aliases) {
			if (this.childCommandMap == null) {
				this.childCommandMap = new HashMap<>();
			}
			this.childCommandMap.put(name.toLowerCase(Locale.ROOT), child);
			for (String alias : aliases) {
				this.childCommandMap.put(alias.toLowerCase(Locale.ROOT), child);
			}
			return this;
		}

		/**
		 * Builds command
		 *
		 * @return {@link SimpleCommand}
		 */
		public @NotNull SimpleCommand build() {
			if (this.childCommandMap == null || this.childCommandMap.isEmpty()) {
				checkNotNull(this.command, "An executor is required");
			}
			return new SimpleCommand(this.command, this.description, this.msgNoPermission, this.usage, this.permission, this.childCommandMap);
		}
	}
}

