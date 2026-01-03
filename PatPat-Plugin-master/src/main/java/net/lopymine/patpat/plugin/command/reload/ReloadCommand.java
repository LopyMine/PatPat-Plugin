package net.lopymine.patpat.plugin.command.reload;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.PatTranslator;
import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtensionMethod(CommandSenderExtension.class)
public class ReloadCommand implements ICommand {

    @Override
    public List<String> getSuggestions(CommandSender sender, String[] strings) {
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        PatPatPlugin plugin = PatPatPlugin.getInstance();

        // Фон: I/O и пересборка переводов
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
            PatPatConfig.reload();
            PlayerListConfig.reload();
            PatTranslator.unregister();
            PatTranslator.register();

            // Ответ: безопасно в контексте отправителя
            if (sender instanceof Player player) {
                player.getScheduler().run(plugin, st -> sender.sendMsg("patpat.command.reload"), () -> {});
            } else {
                sender.sendMsg("patpat.command.reload");
            }
        });
    }

    @Override
    public String getPermissionKey() {
        return StringUtils.permission("reload");
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
