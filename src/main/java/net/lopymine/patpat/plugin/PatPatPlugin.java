package net.lopymine.patpat.plugin;

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import ru.nik51.patpat.plugin.api.PatPatPluginAPI;

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
	@SuppressWarnings("java:S2696") // Plugins system
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

		Bukkit.getServicesManager().register(PatPatPluginAPI.class, new PatPatPluginAPIImpl(), this, ServicePriority.Normal);

		PatLogger.info("Plugin started");
	}

	@Override
	@SuppressWarnings("java:S2696") // Plugins system
	public void onDisable() {
		if (adventure != null) {
			adventure.close();
			adventure = null;
		}
		PatTranslator.unregister();
	}
}
