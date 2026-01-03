package net.lopymine.patpat.plugin.config.migrate;

public interface MigrateHandler {

	String getVersion();

	boolean needMigrate();

	boolean migrate();

}