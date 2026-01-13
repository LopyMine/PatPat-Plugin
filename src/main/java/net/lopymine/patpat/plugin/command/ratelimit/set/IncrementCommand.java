package net.lopymine.patpat.plugin.command.ratelimit.set;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.RateLimitConfig;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.Collections;
import java.util.List;

@ExtensionMethod(CommandSenderExtension.class)
public class IncrementCommand implements ICommand {

	private static final Component ONE_COMPONENT = Component.text(1).color(NamedTextColor.GOLD);

	@Override
	public List<String> getSuggestions(CommandSender sender, String[] strings) {
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		PatPatConfig config = PatPatConfig.getInstance();
		RateLimitConfig rateLimitConfig = config.getRateLimit();
		if (strings.length == 0) {
			sender.sendMsg(
					"patpat.command.ratelimit.info.increment",
					Component.text(rateLimitConfig.getTokenIncrement()).color(NamedTextColor.GOLD)
			);
			return;
		}
		if (strings.length > 1) {
			sender.sendMsg(this.getExampleOfUsage());
			return;
		}
		try {
			int value = Integer.parseInt(strings[0]);
			if (value < 1) {
				sender.sendMsg(
						"patpat.command.error.number_less_than",
						Component.text(value).color(NamedTextColor.GOLD),
						ONE_COMPONENT
				);
				return;
			}
			rateLimitConfig.setTokenIncrement(value);
			config.save();

			sender.sendMsg(
					"patpat.command.ratelimit.set.increment",
					Component.text(value).color(NamedTextColor.GOLD)
			);
		} catch (NumberFormatException ignored) {
			sender.sendMsg(
					"patpat.command.error.not_number",
					Component.text(strings[0]).color(NamedTextColor.GOLD)
			);
		}
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.commandPermission("ratelimit.set.increment");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat ratelimit set increment [value]";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.ratelimit.set.increment.description");
	}

}
