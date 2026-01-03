package net.lopymine.patpat.plugin.command.ratelimit;

import lombok.*;

import net.lopymine.patpat.plugin.util.TimeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@AllArgsConstructor
public class Time {

	private static final String PATTERN_STRING = "(\\d+)([a-z]*)";
	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);

	private RateLimitUnit unit;
	private int value;

	public static Time of(String value) {
		Matcher matcher = PATTERN.matcher(value);
		if (!matcher.find()) {
			throwExampleException(value);
		}
		int time = Integer.parseInt(matcher.group(1));
		RateLimitUnit unit = TimeUtils.getUnit(matcher.group(2).toLowerCase());
		return new Time(unit, time);
	}

	@Override
	public String toString() {
		return "%d%s".formatted(this.value, this.unit.getName());
	}


	private static void throwExampleException(String argument) {
		throw new IllegalArgumentException("String '%s' cannot parsed. Use a string like from the examples: 15, 30s, 20m, 1h, 2d".formatted(argument));
	}
}
