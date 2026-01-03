package net.lopymine.patpat.plugin.command.ratelimit;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import net.lopymine.patpat.plugin.extension.CommandSenderExtension;

@ExtensionMethod(CommandSenderExtension.class)
public class RateLimitDisableCommand extends RateLimitToggleCommand {

	private static final Component COMPONENT = Component.translatable("patpat.command.ratelimit.disable.success")
			.color(NamedTextColor.RED);

	@Override
	protected String getTranslationKey() {
		return "disable";
	}

	@Override
	protected boolean getValue() {
		return false;
	}

	@Override
	protected Component getComponent() {
		return COMPONENT;
	}

}