package com.basedloader.mixin.launch;

import net.minecraftforge.fml.loading.targets.CommonUserdevLaunchHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommonUserdevLaunchHandler.class)
public class CommonUserdevLaunchHandlerMixin {

	@Redirect(method = "getMinecraftPaths", at = @At(value = "INVOKE", target = "Ljava/lang/System;getProperty(Ljava/lang/String;)Ljava/lang/String;"))
	private String getClasspath(String key) {
		if(key.equals("legacyClassPath")) {
			key = "java.class.path";
		}

		return System.getProperty(key);
	}
}
