package com.basedloader.wrapper;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

import net.fabricmc.loader.api.ModContainer;

public record FabricFMLModFile(ModContainer fabricMod,
							   FabricFMLModContainer.IModInfoWrapper wrapper) implements IModFile {

	@Override
	public List<IModLanguageProvider> getLoaders() {
		return List.of();
	}

	@Override
	public Path findResource(String... pathName) {
		return fabricMod.findPath(String.join(File.separator, pathName)).orElse(null);
	}

	@Override
	public Supplier<Map<String, Object>> getSubstitutionMap() {
		return Map::of;
	}

	@Override
	public Type getType() {
		return Type.MOD;
	}

	@Override
	public Path getFilePath() {
		return fabricMod.getOrigin().getPaths().get(0);
	}

	@Override
	public SecureJar getSecureJar() {
		return null;
	}

	@Override
	public void setSecurityStatus(SecureJar.Status status) {

	}

	@Override
	public List<IModInfo> getModInfos() {
		return List.of(wrapper);
	}

	@Override
	public ModFileScanData getScanResult() {
		return null;
	}

	@Override
	public String getFileName() {
		return "FABRIC MOD";
	}

	@Override
	public IModLocator getLocator() {
		return null;
	}

	@Override
	public IModFileInfo getModFileInfo() {
		return wrapper;
	}
}
