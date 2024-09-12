package net.lopymine.patpat.plugin.config;

import lombok.*;
import org.bukkit.configuration.file.FileConfiguration;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.options.ListMode;

@Getter
@Setter
public class PatPatConfig {

	private ListMode listMode;

	public PatPatConfig(ListMode listMode) {
		this.listMode = listMode;
	}

	public static PatPatConfig getInstance() {
		PatPatPlugin plugin = PatPatPlugin.getInstance();
		FileConfiguration config = plugin.getConfig();
		String listModeId = config.get("listMode", ListMode.DISABLED.name()).toString().toLowerCase();
		ListMode listMode = ListMode.getOrDisabled(listModeId);

		return new PatPatConfig(listMode);
	}

	public void save() {
		PatPatPlugin.getInstance().saveConfig();
	}
}
