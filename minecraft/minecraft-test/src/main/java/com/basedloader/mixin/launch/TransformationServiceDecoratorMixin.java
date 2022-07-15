package com.basedloader.mixin.launch;

import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransformationServiceDecorator.class)
public class TransformationServiceDecoratorMixin {

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	private ITransformationService service;

	/**
	 * @author hydos
	 * @reason Bug With Classloaders. Mixin not supported on forge atm.
	 */
	@Overwrite
	void onInitialize(IEnvironment environment) {
		LOGGER.debug("Initializing transformation service {}", this.service::name);
		if (this.service.getClass().getName().contains("FMLServiceProvider")) {
			this.service.initialize(environment);
		}
		LOGGER.debug("Initialized transformation service {}", this.service::name);
	}
}
