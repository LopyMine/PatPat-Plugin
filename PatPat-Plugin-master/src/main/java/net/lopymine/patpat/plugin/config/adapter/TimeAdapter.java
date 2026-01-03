package net.lopymine.patpat.plugin.config.adapter;

import com.google.gson.*;

import net.lopymine.patpat.plugin.command.ratelimit.Time;

import java.lang.reflect.Type;

public class TimeAdapter implements JsonSerializer<Time>, JsonDeserializer<Time> {

	@Override
	public Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String value = json.getAsString();
		return Time.of(value);
	}

	@Override
	public JsonElement serialize(Time src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}
