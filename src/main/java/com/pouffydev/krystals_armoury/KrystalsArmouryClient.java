package com.pouffydev.krystals_armoury;

import com.pouffydev.krystals_armoury.content.item.anglers_cleaver.AnglersCleaverItem;
import com.pouffydev.krystals_armoury.init.KAItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class KrystalsArmouryClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		registerModelPredicates();
	}

	public void registerModelPredicates() {
		ModelPredicateProviderRegistry.register(KAItems.anglersCleaver, new Identifier("cast"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				boolean bl = entity.getMainHandStack() == stack;
				boolean bl2 = entity.getOffHandStack() == stack;
				if (entity.getMainHandStack().getItem() instanceof AnglersCleaverItem) {
					bl2 = false;
				}

				return (bl || bl2) && entity instanceof PlayerEntity && ((PlayerEntity) entity).fishHook != null ? 1.0F : 0.0F;
			}
		});
	}
}
