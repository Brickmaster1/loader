package com.basedloader.mixin.forge.launch;

import static net.minecraftforge.fml.Logging.LOADING;

import java.lang.reflect.InvocationTargetException;

import net.minecraftforge.fml.mclanguageprovider.MinecraftModLanguageProvider;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MinecraftModLanguageProvider.MinecraftModTarget.class)
public class MinecraftModLanguageProvider$MinecraftModTargetMixin implements IModLanguageProvider.IModLanguageLoader {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * @author hYdos
	 * @reason too many mixins to make it worth redirecting. Just Modules
	 */
	@Overwrite
	public <T> T loadMod(final IModInfo info, final ModFileScanData modFileScanResults, final ModuleLayer gameLayer) {
		try {
			final Class<?> mcModClass = Class.forName("net.minecraftforge.fml.mclanguageprovider.MinecraftModContainer");
			return (T)mcModClass.getConstructor(IModInfo.class).newInstance(info);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
				 ClassNotFoundException e) {
			LOGGER.fatal(LOADING,"Unable to load MinecraftModContainer, wut?", e);
			throw new RuntimeException(e);
		}
	}
}
