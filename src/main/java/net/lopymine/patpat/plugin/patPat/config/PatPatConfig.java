package net.lopymine.patpat.plugin.patPat.config;

import lombok.*;
import org.bukkit.configuration.file.FileConfiguration;

import net.lopymine.patpat.plugin.patPat.PatPatPlugin;
import net.lopymine.patpat.plugin.patPat.config.options.ListMode;

@Getter
@Setter
public class PatPatConfig {

	private ListMode listMode;
	private double patVisibilityRadius;

	public PatPatConfig(ListMode listMode, double patVisibilityRadius) {
		this.listMode            = listMode;
		this.patVisibilityRadius = patVisibilityRadius;
	}

	public void save() {
		PatPatPlugin.getInstance().saveConfig();
	}

	public static PatPatConfig getInstance() {
		PatPatPlugin plugin = PatPatPlugin.getInstance();
		FileConfiguration config = plugin.getConfig();
		String listModeId = config.get("listMode", ListMode.DISABLED.name()).toString().toLowerCase();
		double patVisibilityRadius = (Integer) config.get("patVisibilityRadius", 10);
		ListMode listMode = ListMode.getOrDisabled(listModeId);

		return new PatPatConfig(listMode, patVisibilityRadius);
	}
}
