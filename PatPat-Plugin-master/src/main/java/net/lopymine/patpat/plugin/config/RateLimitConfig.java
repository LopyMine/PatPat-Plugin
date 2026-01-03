package net.lopymine.patpat.plugin.config;

import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;
import lombok.Setter;

import net.lopymine.patpat.plugin.command.ratelimit.Time;
import net.lopymine.patpat.plugin.config.adapter.TimeAdapter;

@Getter
@Setter
public class RateLimitConfig {

	private boolean enabled;
	private int tokenLimit = 20;
	private int tokenIncrement = 1;
	@JsonAdapter(TimeAdapter.class)
	private Time tokenInterval = Time.of("1s");
	private String permissionBypass = "patpat.ratelimit.bypass";

}
