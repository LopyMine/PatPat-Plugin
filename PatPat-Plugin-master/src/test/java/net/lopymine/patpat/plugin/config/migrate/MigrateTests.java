package net.lopymine.patpat.plugin.config.migrate;

import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;

import net.lopymine.patpat.plugin.PatPatPlugin;
import net.lopymine.patpat.plugin.config.PatPatConfig;
import net.lopymine.patpat.plugin.config.PlayerListConfig;
import net.lopymine.patpat.plugin.util.FileUtils;

import java.io.File;
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
			File file = FileUtils.getResource("migrate/v0/case1.json");
			Assertions.assertTrue(file.exists());

			PlayerListConfig playerListConfig = new MigrateVersion0().transformPlayerList(file);
			Assertions.assertNotNull(playerListConfig);
			Assertions.assertTrue(playerListConfig.getUuids().contains(UUID.fromString("7b829ed5-9b74-428f-9b4d-ede06975fbc1")));
		}

		@Test
		void test2() {
			File file = FileUtils.getResource("migrate/v0/case2.json");
			Assertions.assertTrue(file.exists());

			PlayerListConfig playerListConfig = new MigrateVersion0().transformPlayerList(file);
			Assertions.assertNotNull(playerListConfig);
			Assertions.assertTrue(playerListConfig.getUuids().isEmpty());
		}

		@Test
		void test3() {
			File file = FileUtils.getResource("migrate/v0/case3.json");
			Assertions.assertTrue(file.exists());

			PlayerListConfig playerListConfig = new MigrateVersion0().transformPlayerList(file);
			Assertions.assertNotNull(playerListConfig);
			Assertions.assertTrue(playerListConfig.getUuids().contains(UUID.fromString("7b829ed5-9b74-428f-9b4d-ede06975fbc1")));
			Assertions.assertTrue(playerListConfig.getUuids().contains(UUID.fromString("192e3748-12d5-4573-a8a5-479cd394a1dc")));
		}
	}
}
