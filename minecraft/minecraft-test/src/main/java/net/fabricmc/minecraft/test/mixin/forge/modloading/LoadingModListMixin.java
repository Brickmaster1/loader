package net.fabricmc.minecraft.test.mixin.forge.modloading;

import net.minecraftforge.fml.loading.LoadingModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LoadingModList.class)
public class LoadingModListMixin {

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void addCoreMods() {}
}
