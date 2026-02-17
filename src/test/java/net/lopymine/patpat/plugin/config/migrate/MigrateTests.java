package net.lopymine.patpat.plugin.config.migrate;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.MockBukkit;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.*;
import net.lopymine.patpat.plugin.config.migrate.JsonConfigReader.ReadStatus;
import net.lopymine.patpat.plugin.config.option.ListMode;
import net.lopymine.patpat.plugin.util.TestingFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

class MigrateTests {

	@BeforeAll
	static void setUp() {
		MockBukkit.mock();
		MockBukkit.load(PatPatPlugin.class);
		PatPatConfig.getInstance().setDebug(true);
	}

	@AfterAll
	static void tearDown() {
		MockBukkit.unmock();
	}


	@Nested
	class V0 {

		@Test
		void test1() {
			File file = TestingFileUtils.getResource("migrate/v0/case1.json");
			Assertions.assertTrue(file.exists());

			PlayerListConfig playerListConfig = new MigrateVersion0().transformPlayerList(file);
			Assertions.assertNotNull(playerListConfig);
			Assertions.assertTrue(playerListConfig.getUuids().contains(UUID.fromString("7b829ed5-9b74-428f-9b4d-ede06975fbc1")));
		}

		@Test
		void test2() {
			File file = TestingFileUtils.getResource("migrate/v0/case2.json");
			Assertions.assertTrue(file.exists());

			PlayerListConfig playerListConfig = new MigrateVersion0().transformPlayerList(file);
			Assertions.assertNotNull(playerListConfig);
			Assertions.assertTrue(playerListConfig.getUuids().isEmpty());
		}

		@Test
		void test3() {
			File file = TestingFileUtils.getResource("migrate/v0/case3.json");
			Assertions.assertTrue(file.exists());

			PlayerListConfig playerListConfig = new MigrateVersion0().transformPlayerList(file);
			Assertions.assertNotNull(playerListConfig);
			Assertions.assertTrue(playerListConfig.getUuids().contains(UUID.fromString("7b829ed5-9b74-428f-9b4d-ede06975fbc1")));
			Assertions.assertTrue(playerListConfig.getUuids().contains(UUID.fromString("192e3748-12d5-4573-a8a5-479cd394a1dc")));
		}
	}

	@Nested
	class V100 {

		@TempDir
		File tempDir;

		@Test
		void test1() throws IOException {
			File source = TestingFileUtils.getResource("migrate/v100/case1.json");
			Assertions.assertTrue(source.exists());
			File file = new File(tempDir, source.getName());
			Files.copy(source.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Assertions.assertTrue(file.exists());



			JsonConfigReader jsonConfigReader = new JsonConfigReader(file);
			ReadStatus status = jsonConfigReader.readConfig();
			Assertions.assertEquals(ReadStatus.SUCCESS, status);

			MigrateVersion100 migration = new MigrateVersion100();
			Assertions.assertTrue(migration.needMigrate(jsonConfigReader));
			migration.migrate(jsonConfigReader);
			PatPatConfig config = PatPatConfig.readFile(jsonConfigReader.getConfig());
			Assertions.assertNotNull(config);
			Assertions.assertTrue(config.getInfo().getVersion().is(Version.of("1.0.1")));
			Assertions.assertTrue(config.isDebug());
			Assertions.assertTrue(config.isApi());
			Assertions.assertEquals(ListMode.BLACKLIST, config.getListMode());
		}
	}
}
