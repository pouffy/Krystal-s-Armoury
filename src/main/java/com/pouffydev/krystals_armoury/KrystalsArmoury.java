package com.pouffydev.krystals_armoury;

import com.pouffydev.krystals_armoury.init.KAItems;
import net.fabricmc.loader.api.FabricLoader;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrystalsArmoury implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Krystal's Armoury");
	public static final String ID = "krystals_armoury";
	@Override
	public void onInitialize(ModContainer mod) {
		KAItems.registerModItems();
		devLogger("Assembling Krystal's Armoury");
	}

	public static void devLogger(String text) {
		if (!QuiltLoader.isDevelopmentEnvironment()) return;
		KrystalsArmoury.LOGGER.info("DEV - [" + text + "]");
	}
}
