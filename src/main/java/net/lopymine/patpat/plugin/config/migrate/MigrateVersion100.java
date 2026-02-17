package net.lopymine.patpat.plugin.config.migrate;

import com.google.gson.JsonObject;

import net.lopymine.patpat.plugin.config.*;
import net.lopymine.patpat.plugin.util.JsonUtils;

public class MigrateVersion100 implements MigrateHandler {

	private static final Version MIGRATION_VERSION = Version.of("1.0.0");

	@Override
	public String getVersion() {
		return MIGRATION_VERSION.toString();
	}

	@Override
	public boolean needMigrate(JsonConfigReader jsonConfigReader) {
		return Version.of(jsonConfigReader.getJson().getAsJsonObject("_info").get("version").getAsString()).is(MIGRATION_VERSION);
	}

	@Override
	public boolean migrate(JsonConfigReader jsonConfigReader) {
		JsonObject json = jsonConfigReader.getJson();
		json.add("permissionRestrictions", JsonUtils.GSON.toJsonTree(new PermissionConfig()));
		json.addProperty("api", true);
		json.getAsJsonObject("_info").addProperty("version", "1.0.1");
		return jsonConfigReader.saveConfig();
	}

}
