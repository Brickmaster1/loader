package com.basedloader.mixin.launch;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ModuleLayerHandler;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Launcher.class)
public class LauncherMixin {

	@Redirect(method = "run", at = @At(value = "INVOKE", target = "Lcpw/mods/modlauncher/ModuleLayerHandler;buildLayer(Lcpw/mods/modlauncher/api/IModuleLayerManager$Layer;)Lcpw/mods/modlauncher/ModuleLayerHandler$LayerInfo;"))
	private @Coerce Object e(ModuleLayerHandler instance, IModuleLayerManager.Layer layer) {
		return null;
	}
}
