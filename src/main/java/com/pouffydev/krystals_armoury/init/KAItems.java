package com.pouffydev.krystals_armoury.init;

import com.pouffydev.krystals_armoury.KrystalsArmoury;
import com.pouffydev.krystals_armoury.content.item.anglers_cleaver.AnglersCleaverItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class KAItems {
	public static final Item anglersCleaver = registerItem("anglers_cleaver",
		new AnglersCleaverItem(-3.1f, (new Item.Settings()).maxDamage(2500).group(ItemGroup.COMBAT)));

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registry.ITEM, new Identifier(KrystalsArmoury.ID, name), item);
	}

	public static void registerModItems() {
		KrystalsArmoury.LOGGER.info("Registering " + KrystalsArmoury.ID + " Mod items");
	}
}
