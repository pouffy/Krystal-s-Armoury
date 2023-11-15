package com.pouffydev.krystals_armoury.init;

import com.google.common.collect.Maps;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class KAModelPredicateProviders {
	private static final Map<Identifier, ModelPredicateProvider> GLOBAL = Maps.newHashMap();
	private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.newHashMap();
	private static final Identifier DAMAGED_ID = new Identifier("damaged");
	private static final Identifier DAMAGE_ID = new Identifier("damage");
	private static final UnclampedModelPredicateProvider DAMAGED_PROVIDER = (stack, world, entity, seed) -> {
		return stack.isDamaged() ? 1.0F : 0.0F;
	};
	private static final UnclampedModelPredicateProvider DAMAGE_PROVIDER = (stack, world, entity, seed) -> {
		return MathHelper.clamp((float)stack.getDamage() / (float)stack.getMaxDamage(), 0.0F, 1.0F);
	};
	public static UnclampedModelPredicateProvider register(Identifier id, UnclampedModelPredicateProvider provider) {
		GLOBAL.put(id, provider);
		return provider;
	}

	private static void registerCustomModelData(ModelPredicateProvider provider) {
		GLOBAL.put(new Identifier("custom_model_data"), provider);
	}

	public static void register(Item item, Identifier id, UnclampedModelPredicateProvider provider) {
		((Map)ITEM_SPECIFIC.computeIfAbsent(item, (key) -> {
			return Maps.newHashMap();
		})).put(id, provider);
	}
	@Nullable
	public static ModelPredicateProvider get(Item item, Identifier id) {
		if (item.getMaxDamage() > 0) {
			if (DAMAGE_ID.equals(id)) {
				return DAMAGE_PROVIDER;
			}

			if (DAMAGED_ID.equals(id)) {
				return DAMAGED_PROVIDER;
			}
		}

		ModelPredicateProvider modelPredicateProvider = GLOBAL.get(id);
		if (modelPredicateProvider != null) {
			return modelPredicateProvider;
		} else {
			Map<Identifier, ModelPredicateProvider> map = ITEM_SPECIFIC.get(item);
			return map == null ? null : map.get(id);
		}
	}
	static {
		register(KAItems.anglersCleaver, new Identifier("cast"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				boolean bl = entity.getMainHandStack() == stack;
				boolean bl2 = entity.getOffHandStack() == stack;
				if (entity.getMainHandStack().getItem() instanceof FishingRodItem) {
					bl2 = false;
				}

				return (bl || bl2) && entity instanceof PlayerEntity && ((PlayerEntity) entity).fishHook != null ? 1.0F : 0.0F;
			}
		});
	}
}
