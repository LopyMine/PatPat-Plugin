package net.lopymine.patpat.plugin.config;

import com.google.gson.Gson;
import lombok.Getter;

import net.lopymine.patpat.plugin.PatPatPlugin;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

@Getter
public class PlayerListConfig {

	private static final Gson GSON = new Gson();

	private final Set<UUID> uuids;

	public PlayerListConfig() {
		this.uuids = new HashSet<>();
	}

	public static PlayerListConfig getInstance() {
		return PlayerListConfig.read();
	}

	public static PlayerListConfig create() {
		PlayerListConfig playerListConfig = new PlayerListConfig();

		String json = GSON.toJson(playerListConfig);

		try (FileWriter writer = new FileWriter(getConfigPath())) {
			writer.write(json);
		} catch (Exception e) {
			PatPatPlugin.getInstance().getLogger().log(Level.WARNING, "Failed to create player list config!", e);
		}

		return playerListConfig;
	}

	private static PlayerListConfig read() {
		File configPath = getConfigPath();
		if (!configPath.exists()) {
			return create();
		}

		try (FileReader reader = new FileReader(configPath)) {
			return GSON.fromJson(reader, PlayerListConfig.class);
		} catch (Exception e) {
			PatPatPlugin.getInstance().getLogger().log(Level.WARNING, "Failed to read player list config!", e);
		}

		return create();
	}

	private static File getConfigPath() {
		return new File(PatPatPlugin.getInstance().getDataFolder(), "player-list.json");
	}

	public void save() {
		String json = GSON.toJson(this, PlayerListConfig.class);
		try (FileWriter writer = new FileWriter(getConfigPath())) {
			writer.write(json);
		} catch (Exception e) {
			PatPatPlugin.getInstance().getLogger().log(Level.WARNING, "Failed to save player list config!", e);
		}
	}
}
