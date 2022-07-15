package com.basedloader.mixin.launch;

import java.nio.file.spi.FileSystemProvider;
import java.util.Optional;
import java.util.function.Supplier;

import cpw.mods.jarhandling.impl.Jar;

import net.fabricmc.loader.impl.game.minecraft.FsProvider0;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Jar.class)
public class JarMixin {

	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElseThrow(Ljava/util/function/Supplier;)Ljava/lang/Object;"))
	private static <X extends Throwable, T extends FileSystemProvider> Object parentFileSystem(Optional<T> instance, Supplier<? extends X> exceptionSupplier) throws Throwable {
		return ((FsProvider0) instance.orElseThrow(exceptionSupplier)).getParent();
	}
}
