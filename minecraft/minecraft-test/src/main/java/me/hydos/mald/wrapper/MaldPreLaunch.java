package me.hydos.mald.wrapper;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class MaldPreLaunch implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		AdvanceLoader.advanceDefineClass("org.spongepowered.asm.launch.MixinLaunchPluginLegacy");
		AdvanceLoader.advanceDefineClass("org.spongepowered.asm.launch.MixinTransformationServiceAbstract");
		AdvanceLoader.advanceDefineClass("org.spongepowered.asm.launch.MixinTransformationServiceLegacy");
	}
}
