package com.pouffydev.krystals_armoury.mixin;

import com.pouffydev.krystals_armoury.content.item.anglers_cleaver.AnglersCleaverItem;
import com.pouffydev.krystals_armoury.init.KAItems;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberMixin extends ProjectileEntity {
	@Shadow
	public abstract PlayerEntity getPlayerOwner();
	@Shadow
	public abstract Entity getHookedEntity();

	private FishingBobberMixin(EntityType<ProjectileEntity> type, World world) {
		super(type, world);
	}
	@Unique
	private boolean handHasRod() {
		PlayerEntity player = this.getPlayerOwner();
		ItemStack itemStack = player.getMainHandStack();
		ItemStack itemStack2 = player.getOffHandStack();
		boolean mainHandHasRod = itemStack.isOf(KAItems.anglersCleaver);
		boolean offHandHasRod = itemStack2.isOf(KAItems.anglersCleaver);
		return mainHandHasRod || offHandHasRod;
	}

	@Inject(
		method = "removeIfInvalid",
		at = @At("HEAD"),
		cancellable = true
	)
	private void removeIfInvalid(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {
		if (!playerEntity.isRemoved() && playerEntity.isAlive() && handHasRod() && this.squaredDistanceTo(playerEntity) <= 1024.0D) {
			cir.setReturnValue(false);
		}
	}


	@Inject(
		method = "pullHookedEntity",
		at = @At("HEAD")
	)
	private void pullHookedEntity(Entity entity, CallbackInfo ci) {
		Entity entity2 = this.getHookedEntity();
		if (entity2 != null && handHasRod()) {
			Vec3d vec3d = (new Vec3d(entity2.getX() - this.getX(), entity2.getY() - this.getY(), entity2.getZ() - this.getZ())).multiply(0.3);
			entity.setVelocity(entity.getVelocity().add(vec3d));
		}
	}
}
