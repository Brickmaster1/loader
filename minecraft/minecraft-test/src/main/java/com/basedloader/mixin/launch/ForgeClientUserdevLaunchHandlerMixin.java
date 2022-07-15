package com.basedloader.mixin.launch;

import java.util.Optional;

import net.minecraftforge.fml.loading.targets.ForgeClientUserdevLaunchHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ForgeClientUserdevLaunchHandler.class)
public class ForgeClientUserdevLaunchHandlerMixin {

	@Redirect(method = "lambda$launchService$0", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElseThrow()Ljava/lang/Object;"))
	private Object beSilentWeDontNeedIt(Optional<Object> instance) {
		return null;
	}

	@Redirect(method = "lambda$launchService$0", at = @At(value = "INVOKE", target = "Ljava/lang/Class;forName(Ljava/lang/Module;Ljava/lang/String;)Ljava/lang/Class;"))
	private Class<?> getClassNoModule(Module caller, String pa) throws ClassNotFoundException {
		return Class.forName(pa);
	}
}
