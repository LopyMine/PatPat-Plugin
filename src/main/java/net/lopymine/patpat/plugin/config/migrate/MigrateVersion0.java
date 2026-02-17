package net.lopymine.patpat.plugin.config.migrate;

import com.google.gson.*;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.util.FileUtils;
import net.lopymine.patpat.plugin.util.JsonUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class MigrateVersion0 {

	private static final String OLD_CONFIG_FILENAME = "config.yml";

	@Nullable
	public PlayerListConfig transformPlayerList(File oldFile) {
		PlayerListConfig playerListConfig = new PlayerListConfig();
		try (FileReader reader = new FileReader(oldFile)) {
			JsonObject rootObj = JsonUtils.parseReader(reader).getAsJsonObject();
			JsonArray array = rootObj.get("uuids").getAsJsonArray();
			for (JsonElement element : array) {
				String uuid = element.getAsString();
				playerListConfig.add(UUID.fromString(uuid), "?");
			}
		} catch (FileNotFoundException e) {
			PatLogger.warn("Failed to find PlayerListConfig:", e);
			return null;
		} catch (Exception e) {
			PatLogger.error("Failed to read PlayerListConfig:", e);
			return null;
		}
		return playerListConfig;
	}

	public boolean needMigrate() {
		File oldPlayerList = new File(FileUtils.CONFIG_FOLDER, "player-list.json");
		File oldConfig = new File(FileUtils.CONFIG_FOLDER, OLD_CONFIG_FILENAME);
		return oldPlayerList.exists() || oldConfig.exists();
	}

	public boolean migrate() {
		File oldConfig = new File(FileUtils.CONFIG_FOLDER, OLD_CONFIG_FILENAME);
		if (oldConfig.exists() && FileUtils.backupFile(oldConfig)) {
			try {
				Files.delete(oldConfig.toPath());
			} catch (Exception e) {
				PatLogger.error("Failed to delete old PatPatConfig:", e);
				return false;
			}
		}
		PatPatConfig config = new PatPatConfig();
		config.save();

		File oldPlayerList = new File(FileUtils.CONFIG_FOLDER, "player-list.json");
		if (!oldPlayerList.exists()) {
			new PlayerListConfig().save();
			return true;
		}

		if (!FileUtils.backupFile(oldPlayerList)) {
			PatLogger.error("Failed to create backup for PlayerListConfig:");
			return false;
		}
		PlayerListConfig playerListConfig = transformPlayerList(oldPlayerList);
		if (playerListConfig == null) {
			return false;
		}
		try {
			Files.delete(oldPlayerList.toPath());
		} catch (Exception e) {
			PatLogger.error("Failed to delete old PlayerListConfig:", e);
			return false;
		}
		playerListConfig.save();
		return true;
	}

	public String getVersion() {
		return "0";
	}

}
