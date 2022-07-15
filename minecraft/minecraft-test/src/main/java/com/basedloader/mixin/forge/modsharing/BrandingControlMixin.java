package com.basedloader.mixin.forge.modsharing;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.internal.BrandingControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

@Mixin(BrandingControl.class)
public class BrandingControlMixin {

	@Redirect(method = "computeBranding", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 2))
	private static <E> ImmutableList.Builder<E> replaceMcpWithFabricBranding(ImmutableList.Builder<E> instance, E element) {
		ModContainer loaderMod = FabricLoader.getInstance().getModContainer("fabricloader").orElseThrow();
		return instance.add((E) ("Fabric " + loaderMod.getMetadata().getVersion()));
	}
}
