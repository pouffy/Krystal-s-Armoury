package com.pouffydev.krystals_armoury.content.item.anglers_cleaver;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class AnglersCleaverItem extends FishingRodItem implements Vanishable {
	private final float attackDamage;
	public static final Set<Enchantment> ALLOWED_ENCHANTMENTS = Set.of(Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT, Enchantments.LOOTING,
		Enchantments.MENDING, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.SHARPNESS, Enchantments.LURE, Enchantments.LUCK_OF_THE_SEA, Enchantments.SWEEPING);
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
	public AnglersCleaverItem(float attackSpeed, Item.Settings settings) {
		super(settings);
		this.attackDamage = 15;
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION));
		this.attributeModifiers = builder.build();
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		int i;
		if (user.fishHook != null) {
			if (!world.isClient) {
				i = user.fishHook.use(itemStack);
				itemStack.damage(i, user, (p) -> {
					p.sendToolBreakStatus(hand);
				});
			}

			world.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL,
				1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		} else {
			world.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL,
				0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			if (!world.isClient) {
				i = EnchantmentHelper.getLure(itemStack);
				int j = EnchantmentHelper.getLuckOfTheSea(itemStack);
				Entity testEntity = new FishingBobberEntity(user, world, j, i);
				itemStack.getOrCreateNbt().putUuid("bobberUUID", testEntity.getUuid());
				world.spawnEntity(testEntity);
			}
		}
		NbtCompound nbt = itemStack.getOrCreateNbt();
		if (world instanceof ServerWorld serverWorld) {
			if (serverWorld.getEntity(nbt.getUuid("bobberUUID")) != null) {
				nbt.putFloat("cast", 1.0f);
			} else {
				nbt.putFloat("cast", 0.0f);
			}
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack itemStack = super.getDefaultStack();
		itemStack.getOrCreateNbt().putFloat("cast", 0.0f);
		return itemStack;
	}

	public int getEnchantability() {
		return 1;
	}
	public float getAttackDamage() {
		return 15;
	}

	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB)) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();
			return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
		}
	}

	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, (e) -> {
			e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
		});
		return true;
	}

	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (state.getHardness(world, pos) != 0.0F) {
			stack.damage(2, miner, (e) -> {
				e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
			});
		}

		return true;
	}

	public boolean isSuitableFor(BlockState state) {
		return state.isOf(Blocks.COBWEB);
	}

	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}
}
