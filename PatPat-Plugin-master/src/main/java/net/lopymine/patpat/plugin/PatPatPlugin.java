package net.lopymine.patpat.plugin;

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

import net.lopymine.patpat.plugin.command.PatPatCommandManager;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.config.migrate.MigrateManager;
import net.lopymine.patpat.plugin.event.PatPatPlayerEventHandler;
import net.lopymine.patpat.plugin.packet.manager.PatPatPacketManager;

@Getter
public class PatPatPlugin extends JavaPlugin {

	public static final String PLUGIN_ID = "patpat-plugin";
	public static final String MOD_ID = "patpat";

	@Getter
	private static PatPatPlugin instance;

	@Getter
	private static BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance  = this;
        adventure = BukkitAudiences.create(this);

        if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
            PatLogger.warn("Failed to create config folder for PatPat Plugin!");
        }

        MigrateManager.migrate();
        PatPatConfig.reload();
        PlayerListConfig.reload();
        MigrateManager.checkVersion();

        PatPatPacketManager.register();
        PatPatCommandManager.register();
        PatPatPlayerEventHandler.register();
        PatTranslator.register();

        // Старт фоновой задачи лимитера (Folia-safe версия использует GlobalRegionScheduler)
        net.lopymine.patpat.plugin.command.ratelimit.RateLimitManager.reloadTask();

        PatLogger.info("Plugin started");
    }

    @Override
    public void onDisable() {
        // Останов фоновых задач
        net.lopymine.patpat.plugin.command.ratelimit.RateLimitManager.reloadTask(); // выключит, если enabled=false, но лучше иметь отдельный stop()
        PatTranslator.unregister();

        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

}
