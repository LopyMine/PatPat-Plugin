package net.lopymine.patpat.plugin.command.info;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.Collections;
import java.util.List;

@ExtensionMethod(CommandSenderExtension.class)
public class InfoCommand implements ICommand {

    @Override
    public List<String> getSuggestions(CommandSender commandSender, String[] strings) {
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String pluginVersion = PatPatPlugin.getInstance().getDescription().getVersion();
        String platformVersion = Bukkit.getServer().getName();
        String minecraftVersion = Bukkit.getServer().getVersion();

        String debugInformation = "Platform: %s%nMinecraft: %s%nVersion: %s"
                .formatted(platformVersion, minecraftVersion, pluginVersion);

        ClickEvent clickEvent = ClickEvent.clickEvent(Action.COPY_TO_CLIPBOARD, debugInformation);
        HoverEvent<Component> hoverEvent = HoverEvent.showText(Component.translatable("patpat.command.info.copy"));
        Component style = Component.empty().clickEvent(clickEvent).hoverEvent(hoverEvent);

        Component platformComponent = Component.translatable("patpat.command.info.platform")
                .args(Component.text(platformVersion).color(NamedTextColor.GOLD))
                .mergeStyle(style);

        Component pluginVersionComponent = Component.translatable("patpat.command.info.version")
                .args(Component.text(pluginVersion).color(NamedTextColor.GOLD))
                .mergeStyle(style);

        Component minecraftVersionComponent = Component.translatable("patpat.command.info.minecraft_version")
                .args(Component.text(minecraftVersion).color(NamedTextColor.GOLD))
                .mergeStyle(style);

        sender.sendMsg(platformComponent);
        sender.sendMsg(minecraftVersionComponent);
        sender.sendMsg(pluginVersionComponent);
    }

    @Override
    public String getPermissionKey() {
        return StringUtils.permission("info");
    }

    @Override
    public String getExampleOfUsage() {
        return "/patpat info";
    }

    @Override
    public Component getDescription() {
        return Component.translatable("patpat.command.info.description");
    }
}
