package com.basedloader.mixin.launch;

import com.basedloader.AdvanceLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ServiceLoader;

@Mixin(FMLLoader.class)
public class FMLLoaderMixin {

	@Redirect(method = "onInitialLoad", at = @At(value = "INVOKE", target = "Ljava/lang/Package;isCompatibleWith(Ljava/lang/String;)Z"))
	private static boolean assumeSo(Package instance, String i) {
		return true;
	}

	@Redirect(method = "onInitialLoad", at = @At(value = "INVOKE", target = "Ljava/util/ServiceLoader;load(Ljava/lang/ModuleLayer;Ljava/lang/Class;)Ljava/util/ServiceLoader;"))
	private static <S> ServiceLoader<S> loadServicesNoModules(ModuleLayer layer, Class<S> service) {
		return ServiceLoader.load(service, AdvanceLoader.KNOT);
	}

	@Redirect(method = "onInitialLoad", at = @At(value = "INVOKE", target = "Ljava/lang/Package;getSpecificationVersion()Ljava/lang/String;"))
	private static String getPackageSpec(Package instance) {
		String packageSpecVer = instance.getSpecificationVersion();
		if (packageSpecVer == null) {
			packageSpecVer = "3"; // Forgespi
		}
		return packageSpecVer;
	}
}
