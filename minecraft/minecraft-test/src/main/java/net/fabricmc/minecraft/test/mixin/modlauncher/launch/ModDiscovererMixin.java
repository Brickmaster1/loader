package net.fabricmc.minecraft.test.mixin.modlauncher.launch;

import java.util.ServiceLoader;

import me.hydos.mald.wrapper.AdvanceLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModDiscoverer.class)
public class ModDiscovererMixin {

	@Redirect(method = "<init>(Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Ljava/util/ServiceLoader;load(Ljava/lang/ModuleLayer;Ljava/lang/Class;)Ljava/util/ServiceLoader;"))
	private <S> ServiceLoader<S> loadNonModuleService(ModuleLayer layer, Class<S> service) {
		return ServiceLoader.load(service, AdvanceLoader.KNOT);
	}
}
