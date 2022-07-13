package net.fabricmc.minecraft.test.mixin.forge.modsharing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.hydos.mald.wrapper.WrappedFgModContainer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.loader.api.FabricLoader;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModList.class)
public abstract class ModListMixin {

	@Shadow
	private List<ModContainer> mods;

	@Shadow
	private Map<String, ModContainer> indexedMods;

	@Shadow
	@Final
	@Mutable
	private List<IModInfo> sortedList;

	@Shadow
	private List<ModFileScanData> modFileScanData;

	@Shadow
	@Final
	@Mutable
	private List<IModFileInfo> modFiles;

	@Shadow
	protected abstract String fileToLine(IModFile mf);

	@Shadow
	public abstract <T> Stream<T> applyForEachModFile(Function<IModFile, T> function);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void append(final List<ModFile> modFiles, final List<ModInfo> sortedList, CallbackInfo ci) {
	}

	/**
	 * @author hYdos
	 * @reason To let FML know about Fabric mods.
	 */
	@Overwrite
	void setLoadedMods(final List<ModContainer> modContainers) {
		List<WrappedFgModContainer> fabricModContainers = FabricLoader.getInstance().getAllMods().stream().map(WrappedFgModContainer::new)
				.filter(mod -> modContainers.stream().map(ModContainer::getModId).noneMatch(s -> s.equals(mod.getModId())))
				.toList();
		List<ModContainer> mutableMods = new ArrayList<>(modContainers);
		mutableMods.addAll(fabricModContainers);

		this.mods = mutableMods;
		this.indexedMods = mutableMods.stream()
				.collect(Collectors.toMap(ModContainer::getModId, Function.identity()));
		this.sortedList = this.indexedMods.values().stream().map(ModContainer::getModInfo).toList();
		this.modFiles = this.sortedList.stream().map(IModInfo::getOwningFile).toList();
	}

	/**
	 * @author hYdos
	 * @reason Don't Process Fabric Mods
	 */
	@Overwrite
	public List<ModFileScanData> getAllScanData() {
		if (modFileScanData == null) {
			modFileScanData = this.sortedList.stream()
					.map(IModInfo::getOwningFile)
					.filter(Objects::nonNull)
					.filter(iModFileInfo -> !(iModFileInfo instanceof WrappedFgModContainer.IModInfoWrapper))
					.map(IModFileInfo::getFile)
					.distinct()
					.map(IModFile::getScanResult)
					.collect(Collectors.toList());
		}
		return modFileScanData;
	}

	/**
	 * @author hYdos
	 * @reason Come on forge, basic error handling smh
	 */
	@Overwrite
	private String crashReport() {
		if (this.indexedMods != null) {
			//return "\n" + applyForEachModFile(this::fileToLine).collect(Collectors.joining("\n\t\t", "\t\t", ""));
			return "";
		} else {
			return "";
		}
	}
}
