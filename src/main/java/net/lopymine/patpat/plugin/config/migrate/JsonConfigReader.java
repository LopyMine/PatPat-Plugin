package net.lopymine.patpat.plugin.config.migrate;

import com.google.gson.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.util.JsonUtils;

import java.io.*;

@Getter
@RequiredArgsConstructor
public class JsonConfigReader {

	private final File config;
	private JsonObject json;

	public ReadStatus readConfig() {
		try (FileReader fileReader = new FileReader(config)) {
			json = JsonUtils
					.parseReader(fileReader)
					.getAsJsonObject();
		} catch (FileNotFoundException e) {
			return ReadStatus.FILE_NOT_FOUND;
		} catch (IOException e) {
			return ReadStatus.FILE_READ_ERROR;
		}
		return ReadStatus.SUCCESS;
	}

	public boolean saveConfig() {
		try (FileWriter writer = new FileWriter(config)) {
			JsonUtils.GSON.toJson(json, writer);
			return true;
		} catch (IOException e) {
			PatLogger.error("Error saving config!", e);
			return false;
		}
	}

	public enum ReadStatus {
		SUCCESS,
		FILE_NOT_FOUND,
		FILE_READ_ERROR
	}

}
