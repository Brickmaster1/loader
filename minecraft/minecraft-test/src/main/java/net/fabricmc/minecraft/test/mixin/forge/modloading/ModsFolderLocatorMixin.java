package net.fabricmc.minecraft.test.mixin.forge.modloading;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModsFolderLocator;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.uncheck;

@Mixin(ModsFolderLocator.class)
public abstract class ModsFolderLocatorMixin extends AbstractJarFileLocator {

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	private static String SUFFIX;

	@Override
	public Stream<Path> scanCandidates() {
		LOGGER.info(LogMarkers.SCAN, "(FML) Scanning FabricLoader mods dir for mods");
		var excluded = ModDirTransformerDiscoverer.allExcluded();

		return FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getMetadata().getType().equals("forge")).map(modContainer -> modContainer.getOrigin().getPaths().get(0));
	}
}
