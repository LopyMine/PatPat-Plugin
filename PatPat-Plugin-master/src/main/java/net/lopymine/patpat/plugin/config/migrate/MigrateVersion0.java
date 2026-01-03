package net.lopymine.patpat.plugin.config.migrate;

import com.google.gson.*;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PlayerListConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class MigrateVersion0 implements MigrateHandler {

	private static final String OLD_CONFIG_FILENAME = "config.yml";

	private boolean createBackup(File file) {
		String filename = file.getName();
        File backupFolder = MigrateManager.getConfigFolder().toPath().resolve("backup").toFile();		if ((!backupFolder.exists() || !backupFolder.isDirectory()) && !backupFolder.mkdir()) {
			PatLogger.error("Failed to create PatPat Plugin backup folder");
			return false;
		}
		try {
			Files.copy(file.toPath(), backupFolder.toPath().resolve(filename + ".bkp"), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			PatLogger.error("Failed to create backup for old config:", e);
			return false;
		}
		return true;
	}

	@Nullable
	public PlayerListConfig transformPlayerList(File oldFile) {
		PlayerListConfig playerListConfig = new PlayerListConfig();
		try (FileReader reader = new FileReader(oldFile)) {
			JsonObject rootObj = JsonParser.parseReader(reader).getAsJsonObject();
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

	@Override
	public boolean needMigrate() {
        File oldPlayerList = new File(MigrateManager.getConfigFolder(), "player-list.json");
        File oldConfig = new File(MigrateManager.getConfigFolder(), OLD_CONFIG_FILENAME);
		return oldPlayerList.exists() || oldConfig.exists();
	}

	@Override
	public boolean migrate() {
        File oldConfig = new File(MigrateManager.getConfigFolder(), OLD_CONFIG_FILENAME);
		if (oldConfig.exists() && createBackup(oldConfig)) {
			try {
				Files.delete(oldConfig.toPath());
			} catch (Exception e) {
				PatLogger.error("Failed to delete old PatPatConfig:", e);
				return false;
			}
		}
		PatPatConfig config = new PatPatConfig();
		config.save();

        File oldPlayerList = new File(MigrateManager.getConfigFolder(), "player-list.json");
		if (!oldPlayerList.exists()) {
			new PlayerListConfig().save();
			return true;
		}

		if (!createBackup(oldPlayerList)) {
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

	@Override
	public String getVersion() {
		return "0";
	}

}
