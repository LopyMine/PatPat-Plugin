package net.lopymine.patpat.plugin.config;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import net.lopymine.patpat.plugin.config.adapter.VersionAdapter;

@Getter
public class InfoConfig {

	@SerializedName("_comment")
	private String comment;
	@SerializedName("_doc")
	private String doc;
	@JsonAdapter(VersionAdapter.class)
	private Version version;

	public InfoConfig() {
		reset();
	}

	public void reset() {
		this.comment = "DON'T CHANGE ANYTHING IN THIS SECTOR!";
		this.doc     = "Documentation: https://github.com/LopyMine/PatPat-Plugin/blob/main/doc/en/config.md";
		this.version = Version.SERVER_CONFIG_VERSION;
	}

}
