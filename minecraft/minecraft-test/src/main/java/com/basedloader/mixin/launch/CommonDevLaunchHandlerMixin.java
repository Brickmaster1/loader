package com.basedloader.mixin.launch;

import net.minecraftforge.fml.loading.targets.CommonDevLaunchHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@Mixin(CommonDevLaunchHandler.class)
public abstract class CommonDevLaunchHandlerMixin {

	@Shadow
	protected abstract String[] getPackages();

	@Inject(method = "getMcFilter", at = @At(value = "HEAD"), cancellable = true)
	private void dontUseSecureJar(Path extra, List<Path> minecraft, Stream.Builder<List<Path>> mods, CallbackInfoReturnable<BiPredicate<String, String>> cir) {
		final var packages = getPackages();
		final var extraPath = extra.toString().replace('\\', '/');

		// We serve everything, except for things in the forge packages.
		BiPredicate<String, String> mcFilter = (path, base) -> {
			if (base.equals(extraPath) ||
					path.endsWith("/")) return true;
			for (var pkg : packages)
				if (path.startsWith(pkg)) return false;
			return true;
		};

		cir.setReturnValue(mcFilter);
	}
}
