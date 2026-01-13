package net.lopymine.patpat.plugin.config.migrate;

import net.lopymine.patpat.plugin.*;
import net.lopymine.patpat.plugin.config.*;
import net.lopymine.patpat.plugin.config.migrate.JsonConfigReader.ReadStatus;
import net.lopymine.patpat.plugin.util.FileUtils;

import java.io.*;
import java.util.*;

public class MigrateManager {

	private static final String ISSUE_LINK = "https://github.com/LopyMine/PatPat-Plugin/issues";

	private static final Set<MigrateHandler> HANDLERS = new HashSet<>();
	private static final MigrateVersion0 OLD_CONFIG_MIGRATOR = new MigrateVersion0();

	private MigrateManager() {
		throw new IllegalStateException("Manager class");
	}

	public static void onInitialize() {
		HANDLERS.clear();
		addHandlers(
				new MigrateVersion100()
		);
	}

	public static void migrate() {
		onInitialize();

		if(OLD_CONFIG_MIGRATOR.needMigrate()){
			if (!OLD_CONFIG_MIGRATOR.migrate()) {
				PatLogger.error("-----------------------");
				PatLogger.error("Failed to migrate plugin config from version: %s", OLD_CONFIG_MIGRATOR.getVersion());
				PatLogger.error("Report the issue at github page: %s, attaching your config and specifying the mod and server versions.", ISSUE_LINK);
				PatLogger.error("-----------------------");
				return;
			}
			PatLogger.info("Config successful migrated from version: %s", OLD_CONFIG_MIGRATOR.getVersion());
		}

		File configFile = new File(FileUtils.CONFIG_FOLDER, "config.json");
		JsonConfigReader reader = new JsonConfigReader(new File(FileUtils.CONFIG_FOLDER, "config.json"));
		if(reader.readConfig() == ReadStatus.FILE_READ_ERROR) {
			PatLogger.error("Failed read config as json, try backup and create new");
			FileUtils.backupFile(configFile, true);
			return;
		}
		if(reader.readConfig() == ReadStatus.FILE_NOT_FOUND) {
			PatLogger.info("Config is not exists, will be created");
			return;
		}

		for (MigrateHandler handler : HANDLERS) {
			if (!handler.needMigrate(reader)) {
				continue;
			}
			String migrateVersion = handler.getVersion();
			if (!handler.migrate(reader)) {
				PatLogger.error("-----------------------");
				PatLogger.error("Failed to migrate plugin config from version: %s", migrateVersion);
				PatLogger.error("Report the issue at github page: %s, attaching your config and specifying the mod and server versions.", ISSUE_LINK);
				PatLogger.error("-----------------------");
				return;
			}
			PatLogger.info("Config successful migrated from version: %s", migrateVersion);
		}
	}

	public static void checkVersion() {
		Version version = PatPatConfig.getInstance().getInfo().getVersion();
		if (version.is(Version.SERVER_CONFIG_VERSION)) {
			return;
		}
		if (version.isMoreThan(Version.SERVER_CONFIG_VERSION)) {
			PatLogger.warn("Your config version is higher than the plugin's (%s > %s). This may cause errors!", version, Version.SERVER_CONFIG_VERSION);
			PatLogger.warn("Please update the plugin to avoid issues.");
			return;
		}
		PatLogger.warn("Your config version is lower than the plugin's (%s < %s). This may cause errors!", version, Version.SERVER_CONFIG_VERSION);
		PatLogger.warn("Back up your config and report the issue at github page: %s, attaching your config and specifying the mod and server versions.", ISSUE_LINK);
	}

	private static void addHandlers(MigrateHandler... handlers) {
		HANDLERS.addAll(List.of(handlers));
	}
}
