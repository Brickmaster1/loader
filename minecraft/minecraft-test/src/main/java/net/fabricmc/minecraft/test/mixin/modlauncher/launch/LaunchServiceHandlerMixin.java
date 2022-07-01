package net.fabricmc.minecraft.test.mixin.modlauncher.launch;

import java.util.ServiceLoader;

import me.hydos.mald.wrapper.AdvanceLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "cpw/mods/modlauncher/LaunchServiceHandler")
public class LaunchServiceHandlerMixin {

	@Redirect(method = "lambda$new$0", at = @At(value = "INVOKE", target = "Ljava/util/ServiceLoader;load(Ljava/lang/ModuleLayer;Ljava/lang/Class;)Ljava/util/ServiceLoader;"))
	private static <S> ServiceLoader<S> getServicesWithoutModules(ModuleLayer layer, Class<S> service) {
		return ServiceLoader.load(service, AdvanceLoader.KNOT);
	}
}
