package com.basedloader.mixin.launch;

import java.util.ServiceLoader;
import java.util.stream.Stream;

import cpw.mods.cl.ModularURLHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModularURLHandler.class, remap = false)
public class ModularURLHandlerMixin {

	@Redirect(method = "initFrom", at = @At(value = "INVOKE", target = "Ljava/util/ServiceLoader;load(Ljava/lang/ModuleLayer;Ljava/lang/Class;)Ljava/util/ServiceLoader;"))
	private static <S> ServiceLoader<S> replaceServiceLoader(ModuleLayer layer, Class<S> service) {
		return null;
	}

	@Redirect(method = "initFrom", at = @At(value = "INVOKE", target = "Ljava/util/ServiceLoader;stream()Ljava/util/stream/Stream;"))
	private static Stream<ServiceLoader.Provider<?>> emptyList(ServiceLoader<?> instance) {
		return Stream.empty();
	}
}
