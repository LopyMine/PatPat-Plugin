package net.lopymine.patpat.plugin.command.list;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

@ExtensionMethod(CommandSenderExtension.class)
public class ListAddCommand implements ICommand {

    @Override
    public List<String> getSuggestions(CommandSender sender, String[] strings) {
        if (strings.length != 1) {
            return Collections.emptyList();
        }

        String prefix = strings[0].toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .flatMap(player -> Stream.of(player.getName(), player.getUniqueId().toString()))
                .filter(s -> s.toLowerCase().startsWith(prefix))
                .toList();
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (strings.length == 0) {
            sender.sendMsg(this.getExampleOfUsage());
            return;
        }

        String value = strings[0];

        try {
            UUID uuid = UUID.fromString(value);
            addByUuid(sender, uuid);
            return;
        } catch (IllegalArgumentException ignored) {
        }

        addByName(sender, value);
    }

    private void addByUuid(CommandSender sender, UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String nickname = offlinePlayer.getName();
        if (nickname == null) nickname = "?";
        addPlayer(sender, uuid, nickname);
    }

    private void addByName(CommandSender sender, String nickname) {
        // 1) онлайн (быстро)
        Player online = Bukkit.getPlayerExact(nickname);
        if (online != null) {
            addPlayer(sender, online.getUniqueId(), online.getName());
            return;
        }

        // 2) cached offline (быстро, без полного перебора)
        OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(nickname);
        if (cached == null) {
            sender.sendMsg(
                    "patpat.command.error.player_not_exist",
                    Component.text(nickname).color(NamedTextColor.GOLD)
            );
            sender.sendMsg(Component.text("Use UUID if player is not cached.").color(NamedTextColor.GRAY));
            return;
        }

        addPlayer(sender, cached.getUniqueId(), cached.getName() == null ? nickname : cached.getName());
    }

    private void addPlayer(CommandSender sender, UUID uuid, String nickname) {
        PlayerListConfig config = PlayerListConfig.getInstance();
        TextComponent nicknameComponent = Component.text(nickname)
                .color(NamedTextColor.GOLD)
                .hoverEvent(HoverEvent.showText(Component.text(uuid.toString())))
                .clickEvent(ClickEvent.clickEvent(Action.COPY_TO_CLIPBOARD, uuid.toString()));

        if (config.add(uuid, nickname)) {
            TranslatableComponent component = Component.translatable("patpat.command.list.add.success")
                    .color(NamedTextColor.GREEN)
                    .args(nicknameComponent);
            sender.sendMsg(component);
            config.save();
        } else {
            sender.sendMsg("patpat.command.list.add.already", nicknameComponent);
        }
    }

    @Override
    public String getPermissionKey() {
        return StringUtils.permission("list.add");
    }

    @Override
    public String getExampleOfUsage() {
        return "/patpat list add (<UUID> | <NICKNAME>)";
    }

    @Override
    public Component getDescription() {
        return Component.translatable("patpat.command.list.add.description");
    }
}
