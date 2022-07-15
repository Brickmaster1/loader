package com.basedloader;

import cpw.mods.modlauncher.api.IModuleLayerManager;

import java.util.Optional;

public class DummyModuleLayerManager implements IModuleLayerManager {

	@Override
	public Optional<ModuleLayer> getLayer(Layer layer) {
		return Optional.of(ModuleLayer.empty());
	}
}
