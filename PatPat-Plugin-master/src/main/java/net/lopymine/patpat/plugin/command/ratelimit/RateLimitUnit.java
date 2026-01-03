package net.lopymine.patpat.plugin.command.ratelimit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitUnit {
	SECONDS("sec", 1),
	MINUTES("min", 60 * SECONDS.multiplier),
	HOURS("hour", 60 * MINUTES.multiplier),
	DAYS("day", 24 * HOURS.multiplier);

	private final String name;
	private final int multiplier;
}
