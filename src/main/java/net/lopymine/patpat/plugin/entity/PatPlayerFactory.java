package net.lopymine.patpat.plugin.entity;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.entity.Player;

import net.lopymine.patpat.plugin.util.FoliaUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PatPlayerFactory {

	private static Method foliaOfMethod;
	private static Method foliaRegisterMethod;
	private static Method foliaUnregisterMethod;

	static {
		if (FoliaUtils.IS_FOLIA) {
			try {
				Class<?> foliaClass = Class.forName("net.lopymine.patpat.plugin.folia.FoliaPatPlayer");
				foliaOfMethod = foliaClass.getMethod("of", Player.class);
				foliaRegisterMethod = foliaClass.getMethod("register", Player.class);
				foliaUnregisterMethod = foliaClass.getMethod("unregister", Player.class);
			} catch (Exception e) {
				throw new RuntimeException("Failed to initialize FoliaPatPlayer methods via reflection", e);
			}
		}
	}

	public static IPatPlayer of(Player player) {
		if (FoliaUtils.IS_FOLIA) {
			try {
				return (IPatPlayer) foliaOfMethod.invoke(null, player);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			return PatPlayer.of(player);
		}
	}

	@CanIgnoreReturnValue
	public static IPatPlayer register(Player player) {
		if (FoliaUtils.IS_FOLIA) {
			try {
				return (IPatPlayer) foliaRegisterMethod.invoke(null, player);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			return PatPlayer.register(player);
		}
	}

	public static void unregister(Player player) {
		if (FoliaUtils.IS_FOLIA) {
			try {
				foliaUnregisterMethod.invoke(null, player);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			PatPlayer.unregister(player);
		}
	}
}
