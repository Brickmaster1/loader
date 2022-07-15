package com.basedloader.mixin.client.forge.interop;

import net.minecraftforge.client.gui.ModListScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModListScreen.class)
public class ModListScreenMixin {
/*
	FIXME: this pulls from forge's resource pack. Bad!!!
	@Redirect(method = "lambda$updateCache$12", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/resource/PathResourcePack;openRoot(Ljava/lang/String;)Ljava/io/InputStream;"))
	private InputStream handleSomeFabricModLogos(PathResourcePack instance, String s) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		try {
			return instance.openRoot(s);
		} catch (IllegalArgumentException e) {
			Method openFile = instance.getClass().getMethod("openFile", String.class);
			openFile.setAccessible(true);
			return (InputStream) openFile.invoke(instance, s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}*/
}
