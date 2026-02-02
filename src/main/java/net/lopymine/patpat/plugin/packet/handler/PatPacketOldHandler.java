package net.lopymine.patpat.plugin.packet.handler;

import com.google.common.io.ByteArrayDataInput;
import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.format.NamedTextColor;

import net.lopymine.patpat.plugin.entity.IPatPlayer;
import net.lopymine.patpat.plugin.extension.CommandSenderExtension;
import net.lopymine.patpat.plugin.util.ComponentUtils;
import net.lopymine.patpat.plugin.util.StringUtils;

import java.util.HashSet;
import java.util.Set;


/**
 * @deprecated This class is no longer supporting
 * <p>use {@link PatPacketHandler} instead.</p>
 */
@Deprecated(since = "1.2.0")
@ExtensionMethod(CommandSenderExtension.class)
public class PatPacketOldHandler extends PatPacketHandler {

	public static final String PATPAT_C2S_PACKET_ID = StringUtils.modId("pat_entity_c2s_packet");
	public static final String PATPAT_S2C_PACKET_ID = StringUtils.modId("pat_entity_s2c_packet");

	private static final Set<IPatPlayer> WARNED_PLAYERS = new HashSet<>();

	private static final Component OLD_VERSION_WARN = Component
			.translatable("patpat.warn.old_version_mod")
			.color(NamedTextColor.RED)
			.appendSpace()
			.append(getButtons());

	private static Component getButtons() {
		String modrinthLink = "https://modrinth.com/mod/patpat";
		String curseForgeLink = "https://www.curseforge.com/minecraft/mc-mods/patpat";
		TranslatableComponent open = Component.translatable("patpat.link.open");
		Component modrinthText = Component.text("Modrinth").color(NamedTextColor.GREEN);
		Component curseForgeText = Component.text("CurseForge").color(NamedTextColor.GOLD);
		TextComponent modrinthButton = ComponentUtils.wrapInBrackets(open.args(modrinthText)).clickEvent(ClickEvent.clickEvent(Action.OPEN_URL, modrinthLink));
		TextComponent curseForgeButton = ComponentUtils.wrapInBrackets(open.args(curseForgeText)).clickEvent(ClickEvent.clickEvent(Action.OPEN_URL, curseForgeLink));
		return modrinthButton.color(NamedTextColor.WHITE).appendSpace().append(curseForgeButton);
	}

	@Override
	public void handle(IPatPlayer sender, ByteArrayDataInput buf) {
		super.handle(sender, buf);
		if (WARNED_PLAYERS.contains(sender)) {
			return;
		}
		WARNED_PLAYERS.add(sender);
		sender.getPlayer().sendMsg(OLD_VERSION_WARN);
	}

	@Override
	public String getIncomingPacketId() {
		return PATPAT_C2S_PACKET_ID;
	}

	@Override
	public String getOutgoingPacketId() {
		return PATPAT_S2C_PACKET_ID;
	}
}
