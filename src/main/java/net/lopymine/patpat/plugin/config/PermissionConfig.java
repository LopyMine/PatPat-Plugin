package net.lopymine.patpat.plugin.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PermissionConfig {

	private boolean enabled = false;
	private String permissionForPat = "patpat.pat";

}
