package net.lopymine.patpat.plugin.entity;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.util.FoliaUtils;

import java.lang.reflect.InvocationTargetException;

public class PatPlayerFactory {

	public static IPatPlayer of(Player player) {
		if(FoliaUtils.IS_FOLIA){
			try {
				return (IPatPlayer) Class.forName("net.lopymine.patpat.plugin.folia.FoliaPatPlayer").getMethod("of", Player.class).invoke(null, player);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
			         ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			return PatPlayer.of(player);
		}
	}

	@CanIgnoreReturnValue
	public static IPatPlayer register(Player player) {
		if(FoliaUtils.IS_FOLIA){
			try {
				return (IPatPlayer) Class.forName("net.lopymine.patpat.plugin.folia.FoliaPatPlayer").getMethod("register", Player.class).invoke(null, player);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
			         ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			return PatPlayer.register(player);
		}
	}

	public static void unregister(Player player) {
		if(FoliaUtils.IS_FOLIA){
			try {
				Class.forName("net.lopymine.patpat.plugin.folia.FoliaPatPlayer").getMethod("unregister", Player.class).invoke(null, player);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
			         ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			PatPlayer.unregister(player);
		}
	}
}
