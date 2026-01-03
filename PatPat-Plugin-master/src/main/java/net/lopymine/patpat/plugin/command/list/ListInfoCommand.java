package net.lopymine.patpat.plugin.command.list;

import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.*;
import org.bukkit.command.CommandSender;

import net.lopymine.patpat.plugin.command.api.ICommand;
import net.lopymine.patpat.plugin.config.*;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.*;

@ExtensionMethod(CommandSenderExtension.class)
public class ListInfoCommand implements ICommand {

	public static final Component EMPTY_TEXT = Component.translatable("patpat.command.list.empty")
			.color(NamedTextColor.GRAY)
			.decorate(TextDecoration.ITALIC);

	@Override
	public List<String> getSuggestions(CommandSender sender, String[] strings) {
		return Collections.emptyList();
	}

	@Override
	public void execute(CommandSender sender, String[] strings) {
		PlayerListConfig playerListConfig = PlayerListConfig.getInstance();
		PatPatConfig config = PatPatConfig.getInstance();
		Collection<String> values = playerListConfig.getNicknameByUuid().values();
		Iterator<String> iterator = values.iterator();

		Component playersText = Component.empty();

		if (!iterator.hasNext()) {
			playersText = EMPTY_TEXT;
		}

		while (iterator.hasNext()) {
			String nickname = iterator.next();
			playersText = playersText.append(Component.text(nickname + (iterator.hasNext() ? ", " : "")));
		}

		Component contentComponent = Component.text(values.size())
				.color(NamedTextColor.GOLD);

		sender.sendMsg("patpat.command.list.info.mode", config.getListMode().getText());
		sender.sendMsg("patpat.command.list.info.content", contentComponent);
		sender.sendMsg(playersText);
	}

	@Override
	public String getPermissionKey() {
		return StringUtils.permission("list.info");
	}

	@Override
	public String getExampleOfUsage() {
		return "/patpat list info";
	}

	@Override
	public Component getDescription() {
		return Component.translatable("patpat.command.list.info.description");
	}
}
