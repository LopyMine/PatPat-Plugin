package net.lopymine.patpat.plugin.config.adapter;

import com.google.gson.*;

import net.lopymine.patpat.plugin.config.Version;

import java.lang.reflect.Type;

public class VersionAdapter implements JsonSerializer<Version>, JsonDeserializer<Version> {

	@Override
	public Version deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String value = json.getAsString();
		return Version.of(value);
	}

	@Override
	public JsonElement serialize(Version src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
