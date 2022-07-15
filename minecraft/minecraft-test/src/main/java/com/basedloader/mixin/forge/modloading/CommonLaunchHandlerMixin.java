package com.basedloader.mixin.forge.modloading;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.loading.targets.CommonLaunchHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommonLaunchHandler.class)
public class CommonLaunchHandlerMixin {

	/**
	 * For some reason forge adds a broken mod with the path "". Let's fix that
	 */
	@Inject(method = "getModClasses", at = @At("RETURN"), cancellable = true)
	private void postProcessModClasses(CallbackInfoReturnable<Map<String, List<Path>>> cir) {
		List<String> toRemoveMods = new ArrayList<>();
		Map<String, List<Path>> classes = cir.getReturnValue();
		classes.forEach((mod, paths) -> {
			for (Path path : paths) {
				if (path.toString().equals("")) {
					toRemoveMods.add(mod);
				}
			}
		});

		for (String toRemoveMod : toRemoveMods) {
			classes.remove(toRemoveMod);
		}
		cir.setReturnValue(classes);
	}
}
