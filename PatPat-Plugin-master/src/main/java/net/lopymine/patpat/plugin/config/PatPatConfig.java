package net.lopymine.patpat.plugin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.command.ratelimit.RateLimitManager;
import net.lopymine.patpat.plugin.config.option.ListMode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class PatPatConfig {

	private static final String FILENAME = "config.json";
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	@Getter
	private static PatPatConfig instance;

	@SerializedName("_info")
	private InfoConfig info;
	private boolean debug;

	private ListMode listMode;
	private RateLimitConfig rateLimit;

	public PatPatConfig() {
		this.listMode  = ListMode.DISABLED;
		this.rateLimit = new RateLimitConfig();
		this.info      = new InfoConfig();
		this.debug     = false;
	}

	public static void reload() {
		File configPath = getConfigPath();
		if (!configPath.exists()) {
			instance = create();
		}

		try (FileReader reader = new FileReader(configPath)) {
			instance = GSON.fromJson(reader, PatPatConfig.class);
		} catch (Exception e) {
			PatLogger.error("Failed to read PatPatConfig:", e);
		}
		RateLimitManager.reloadTask();
	}

	private static File getConfigPath() {
		return new File(PatPatPlugin.getInstance().getDataFolder(), FILENAME);
	}

	public void save() {
		info.reset();
		String json = GSON.toJson(this, PatPatConfig.class);
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
		PatPatConfig config = GSON.fromJson(json, PatPatConfig.class);
		try (FileWriter writer = new FileWriter(getConfigPath())) {
			writer.write(json);
		} catch (Exception e) {
			PatLogger.error("Failed to create PatPatConfig:", e);
		}
		return config;
	}
}
