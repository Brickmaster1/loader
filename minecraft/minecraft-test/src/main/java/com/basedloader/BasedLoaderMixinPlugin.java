package com.basedloader;

import net.fabricmc.loader.api.FabricLoader;

import net.fabricmc.loader.api.ModContainer;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BasedLoaderMixinPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String s) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String s, String s1) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> set, Set<String> set1) {
	}

	@Override
	public List<String> getMixins() {
		return List.of();
	}

	@Override
	public void preApply(String name, ClassNode node, String s1, IMixinInfo mixinInfo) {
		List<ModContainer> forgeMods = FabricLoader.getInstance().getAllMods().stream()
				.filter(modContainer -> modContainer.getMetadata().getType().equals("forge"))
				.toList();

		for (ModContainer forgeMod : forgeMods) {
			Optional<Path> path = forgeMod.findPath("META-INF/accesstransformer.cfg");

			if (path.isPresent() && Files.exists(path.get())) {
				// TODO: apply accessTransformer
			}
		}
	}

	@Override
	public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

	}
}
