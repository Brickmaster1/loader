package com.basedloader.mixin.forge.launch;

import net.minecraftforge.fml.loading.JarVersionLookupHandler;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

/**
 * TODO: make this find forge version through Fabric API
 */
@Mixin(ForgeVersion.class)
public class ForgeVersionMixin {

	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/loading/JarVersionLookupHandler;getSpecificationVersion(Ljava/lang/Class;)Ljava/util/Optional;"))
	private static Optional<String> conc(Class<?> clazz) {
		if(clazz.getSimpleName().contains("ForgeVersion")) {
			return Optional.of("38.0.17");
		}
		return JarVersionLookupHandler.getSpecificationVersion(clazz);
	}
}
