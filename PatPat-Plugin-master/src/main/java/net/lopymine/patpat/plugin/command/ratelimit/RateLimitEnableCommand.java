package net.lopymine.patpat.plugin.command.ratelimit;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import net.lopymine.patpat.plugin.extension.CommandSenderExtension;

@ExtensionMethod(CommandSenderExtension.class)
public class RateLimitEnableCommand extends RateLimitToggleCommand {

	private static final Component COMPONENT = Component.translatable("patpat.command.ratelimit.enable.success")
			.color(NamedTextColor.GREEN);

	@Override
	protected String getTranslationKey() {
		return "enable";
	}

	@Override
	protected boolean getValue() {
		return true;
	}

	@Override
	protected Component getComponent() {
		return COMPONENT;
	}

}
