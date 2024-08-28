package net.lopymine.patpat.plugin.patPat;

import lombok.*;
import org.bukkit.plugin.java.JavaPlugin;

import net.lopymine.patpat.plugin.patPat.command.PatPatCommandManager;
import net.lopymine.patpat.plugin.patPat.config.*;
import net.lopymine.patpat.plugin.patPat.packet.PatPatPacketManager;

import java.util.logging.Level;

public final class PatPatPlugin extends JavaPlugin {

	public static final String MOD_ID = "patpat";
	@Setter
	@Getter
	private static PatPatPlugin instance;
	@Setter
	@Getter
	private PatPatConfig patPatConfig;
	@Setter
	@Getter
	private PlayerListConfig playerListConfig;

	public static String id(String path) {
		return "%s:%s".formatted(MOD_ID, path);
	}

	public static String permission(String permission) {
		return "%s.%s".formatted(MOD_ID, permission);
	}

	@Override
	public void onEnable() {
		PatPatPlugin.setInstance(this);

		if (!this.getDataFolder().mkdirs()) {
			this.getLogger().log(Level.WARNING, "Failed to create data folder for PatPat Plugin!");
		}
		this.setPatPatConfig(PatPatConfig.getInstance());
		this.setPlayerListConfig(PlayerListConfig.getInstance());

		PatPatPacketManager.register();
		PatPatCommandManager.register();
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
