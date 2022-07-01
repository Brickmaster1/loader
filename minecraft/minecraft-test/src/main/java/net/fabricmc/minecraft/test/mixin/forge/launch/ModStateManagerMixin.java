package net.fabricmc.minecraft.test.mixin.forge.launch;

import me.hydos.mald.wrapper.AdvanceLoader;
import net.minecraftforge.fml.ModStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ServiceLoader;

@Mixin(ModStateManager.class)
public class ModStateManagerMixin {

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/ServiceLoader;load(Ljava/lang/ModuleLayer;Ljava/lang/Class;)Ljava/util/ServiceLoader;"))
	private <S> ServiceLoader<S> iHaveDoneThisSoManyTimesFML(ModuleLayer layer, Class<S> service) {
		return ServiceLoader.load(service, AdvanceLoader.KNOT);
	}
}
