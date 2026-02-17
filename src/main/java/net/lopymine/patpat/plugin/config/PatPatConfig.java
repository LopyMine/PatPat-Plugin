package net.lopymine.patpat.plugin.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import net.lopymine.patpat.plugin.*;
import net.lopymine.patpat.plugin.ratelimit.RateLimitManager;
import net.lopymine.patpat.plugin.config.option.ListMode;
import net.lopymine.patpat.plugin.util.JsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class PatPatConfig {

	public static final String FILENAME = "config.json";

	@Getter
	private static PatPatConfig instance;

	@SerializedName("_info")
	private InfoConfig info;
	private boolean debug;
	private boolean api;

	private ListMode listMode;
	private RateLimitConfig rateLimit;
	private PermissionConfig permissionRestrictions;

	public PatPatConfig() {
		this.listMode               = ListMode.DISABLED;
		this.rateLimit              = new RateLimitConfig();
		this.info                   = new InfoConfig();
		this.permissionRestrictions = new PermissionConfig();
		this.debug                  = false;
		this.api                    = true;
	}

	public static void reload() {
		File configPath = getConfigPath();
		if (!configPath.exists()) {
			instance = create();
		}

		instance = readFile(configPath);
		if (instance == null) {
			PatLogger.error("Failed to reload PatPatConfig: instance is null");
			return;
		}
		RateLimitManager.reloadTask();
	}

	@Nullable
	public static PatPatConfig readFile(File file) {
		try (FileReader reader = new FileReader(file)) {
			return JsonUtils.GSON.fromJson(reader, PatPatConfig.class);
		} catch (Exception e) {
			PatLogger.error("Failed to read PatPatConfig:", e);
		}
		return null;
	}

	private static File getConfigPath() {
		return new File(PatPatPlugin.getInstance().getDataFolder(), FILENAME);
	}

	public void save() {
		info.reset();
		String json = JsonUtils.GSON.toJson(this, PatPatConfig.class);
		try (FileWriter writer = new FileWriter(getConfigPath())) {
			writer.write(json);
		} catch (Exception e) {
			PatLogger.error("Failed to save PatPatConfig:", e);
		}
	}

	@Nullable
	private static String loadConfigFromJar() {
		try (InputStream inputStream = PatPatPlugin.getInstance().getClass().getClassLoader().getResourceAsStream(FILENAME)) {
			assert (inputStream != null);
			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			PatLogger.error("Failed to load PatPatConfig from jar!");
		}
		return null;
	}

	private static PatPatConfig create() {
		String json = loadConfigFromJar();
		if (json == null) {
			return null;
		}
		PatPatConfig config = JsonUtils.GSON.fromJson(json, PatPatConfig.class);
		try (FileWriter writer = new FileWriter(getConfigPath())) {
			writer.write(json);
		} catch (Exception e) {
			PatLogger.error("Failed to create PatPatConfig:", e);
		}
		return config;
	}
}
