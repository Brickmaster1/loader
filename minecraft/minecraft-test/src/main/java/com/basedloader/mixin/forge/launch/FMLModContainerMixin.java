package com.basedloader.mixin.forge.launch;

import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(FMLModContainer.class)
public class FMLModContainerMixin {

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElseThrow()Ljava/lang/Object;"))
	private Object emptyModule(Optional<Module> instance) {
		return null;
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Class;forName(Ljava/lang/Module;Ljava/lang/String;)Ljava/lang/Class;"))
	private Class<?> classForNameNoModule(Module caller, String pa) throws ClassNotFoundException {
		return Class.forName(pa);
	}
}
