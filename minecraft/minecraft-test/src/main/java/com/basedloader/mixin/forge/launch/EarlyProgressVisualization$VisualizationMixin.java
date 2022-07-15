package com.basedloader.mixin.forge.launch;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import net.minecraftforge.client.loading.NoVizFallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "net/minecraftforge/fml/loading/progress/EarlyProgressVisualization$Visualization")
public interface EarlyProgressVisualization$VisualizationMixin {

	/**
	 * @author hYdos
	 * @reason modules are cringe.
	 */
	@Overwrite
	default long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, LongSupplier monitorSupplier) {
		return NoVizFallback.fallback(width, height, title, monitorSupplier).getAsLong();
	}
}
