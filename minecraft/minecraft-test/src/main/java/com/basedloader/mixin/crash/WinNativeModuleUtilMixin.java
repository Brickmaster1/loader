package com.basedloader.mixin.crash;

import net.minecraft.util.NativeModuleLister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(NativeModuleLister.class)
public class WinNativeModuleUtilMixin {

	@Inject(method = "listModules", at = @At("HEAD"), cancellable = true)
	private static void thisIsntAnOverwriteISwear(CallbackInfoReturnable<List<NativeModuleLister.NativeModuleInfo>> cir) {
		cir.setReturnValue(List.of());
	}
}
