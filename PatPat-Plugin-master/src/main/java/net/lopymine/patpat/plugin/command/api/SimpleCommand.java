package net.lopymine.patpat.plugin.command.api;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.command.PatPatCommandManager;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unused")
@ExtensionMethod(CommandSenderExtension.class)
public final class SimpleCommand implements TabExecutor {

    private final Component description;
    private final String msgOnlyForPlayer;
    private final Component msgNoPermission;
    private final String usage;
    private final String permission;
    private final ICommand command;
    private final Map<String, SimpleCommand> child;
    private final boolean onlyForPlayer;

    private SimpleCommand(@Nullable ICommand command,
                          @Nullable Component description,
                          boolean onlyForPlayer,
                          @Nullable String msgOnlyForPlayer,
                          @Nullable Component msgNoPermission,
                          @Nullable String usage,
                          @Nullable String permission,
                          @Nullable Map<String, SimpleCommand> child) {
        this.command = command;
        this.usage = usage;
        this.permission = permission;
        this.description = description;
        this.onlyForPlayer = onlyForPlayer;
        this.msgOnlyForPlayer = msgOnlyForPlayer;
        this.msgNoPermission = msgNoPermission;
        // Freeze map to prevent accidental concurrent mutation
        this.child = child == null ? Map.of() : Map.copyOf(child);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void printDescription(@NotNull CommandSender sender) {
        if (permission == null || sender.hasPermission(permission)) {
            if (description != null) {
                sender.sendMsg(description);
            }
            child.values().forEach(simpleCommand -> simpleCommand.printDescription(sender));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (onlyForPlayer && !(sender instanceof Player)) {
                if (msgOnlyForPlayer != null) {
                    sender.sendMessage(msgOnlyForPlayer);
                }
                return false;
            }

            if (permission == null || sender.hasPermission(permission)) {
                if (args.length == 0) {
                    accept(sender, args);
                    return true;
                }

                String sub = args[0].toLowerCase(Locale.ROOT);
                SimpleCommand subCommand = child.get(sub);

                if (subCommand != null) {
                    String[] cropArgs = Arrays.copyOfRange(args, 1, args.length);
                    subCommand.onCommand(sender, command, label, cropArgs);
                    return true;
                }

                accept(sender, args);
                return true;
            }

            if (msgNoPermission != null) {
                sender.sendMsg(msgNoPermission);
            }
            return true;
        } catch (Exception e) {
            // Не глотаем: иначе Folia thread-check и реальные ошибки будут скрыты
            PatLogger.error("Command execution error for /%s".formatted(label), e);
            return false;
        }
    }

    private void accept(@NotNull CommandSender sender, @NotNull String[] args) {
        if (this.command != null) {
            this.command.execute(sender, args);
        } else if (usage != null) {
            PatPatCommandManager.sendMessage(sender, usage);
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            return Collections.emptyList();
        }
        String cmd = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);

        SimpleCommand simpleCommand = child.get(cmd);
        if (simpleCommand != null) {
            if (args.length > 1) {
                String[] cropArgs = Arrays.copyOfRange(args, 1, args.length);
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

        @Nullable private Component description;
        private boolean onlyForPlayer = false;
        @Nullable private String msgOnlyForPlayer;
        @Nullable private Component msgNoPermission;
        @Nullable private String usage;
        @Nullable private String permission;
        @Nullable private ICommand command;
        @Nullable private Map<String, SimpleCommand> childCommandMap;

        private Builder() {}

        public Builder description(@Nullable Component description) {
            this.description = description;
            return this;
        }

        public Builder onlyForPlayer() {
            this.onlyForPlayer = true;
            return this;
        }

        public Builder msgOnlyForPlayer(@Nullable String msgOnlyForPlayer) {
            this.msgOnlyForPlayer = msgOnlyForPlayer;
            return this;
        }

        public Builder msgNoPermission(@Nullable Component msgNoPermission) {
            this.msgNoPermission = msgNoPermission;
            return this;
        }

        public Builder usage(@Nullable String usage) {
            this.usage = usage;
            return this;
        }

        public Builder permission(@Nullable String permission) {
            this.permission = permission;
            return this;
        }

        public Builder executor(@Nullable ICommand executor) {
            this.command = executor;
            return this;
        }

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

        public @NotNull SimpleCommand build() {
            if (this.childCommandMap == null || this.childCommandMap.isEmpty()) {
                checkNotNull(this.command, "An executor is required");
            }
            return new SimpleCommand(
                    this.command, this.description, this.onlyForPlayer,
                    this.msgOnlyForPlayer, this.msgNoPermission, this.usage,
                    this.permission, this.childCommandMap
            );
        }
    }
}
