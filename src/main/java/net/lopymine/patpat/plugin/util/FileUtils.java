package net.lopymine.patpat.plugin.util;

import lombok.experimental.UtilityClass;

import net.lopymine.patpat.plugin.PatLogger;
import net.lopymine.patpat.plugin.PatPatPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class FileUtils {

	public static final File CONFIG_FOLDER = PatPatPlugin.getInstance().getDataFolder();
	public static final File BACKUP_FOLDER = new File(CONFIG_FOLDER, "backups");

	public boolean backupFile(@NotNull File file) {
		return backupFile(file, false);
	}

	public boolean backupFile(@NotNull File file, boolean deleteOriginal) {
		try {
			if (!file.exists()) {
				PatLogger.error("File for backup if not exists");
				return false;
			}
			if (!BACKUP_FOLDER.exists() && !BACKUP_FOLDER.mkdirs()) {
				PatLogger.error("Failed creating backup folder");
				return false;
			}

			String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
			File backup = new File(BACKUP_FOLDER, file.getName() + "." + time + ".bak");
			Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			PatLogger.error("Failed creating config backup", e);
			return false;
		}
		if (deleteOriginal) {
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				PatLogger.error("Failed delete file after backup", e);
			}
		}
		return true;
	}

}
