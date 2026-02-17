package net.lopymine.patpat.plugin.util;

import com.google.gson.*;
import lombok.experimental.UtilityClass;

import java.io.Reader;

@UtilityClass
public class JsonUtils {

	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	@SuppressWarnings( "deprecation" ) // Use JsonParser.parse for older versions
	public static JsonElement parseReader(Reader json) {
		try {
			return JsonParser.parseReader(json);
		} catch (NoSuchMethodError e){
			return new JsonParser().parse(json);
		}
	}

}
