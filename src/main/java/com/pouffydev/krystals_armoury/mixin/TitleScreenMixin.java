package com.pouffydev.krystals_armoury.mixin;

import com.pouffydev.krystals_armoury.KrystalsArmoury;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Inject(method = "init", at = @At("TAIL"))
	public void exampleMod$onInit(CallbackInfo ci) {
		KrystalsArmoury.LOGGER.info("This line is printed by an example mod mixin!");
	}
}
