package net.fabricmc.minecraft.test.mixin.modlauncher.launch;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import cpw.mods.modlauncher.ClassTransformer;
import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.ModuleLayerHandler;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.TransformerAuditTrail;
import cpw.mods.modlauncher.api.ITransformationService;
import me.hydos.mald.wrapper.AdvanceLoader;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.loader.impl.launch.knot.KnotClassLoader;

@Mixin(targets = "cpw/mods/modlauncher/TransformationServicesHandler")
public class TransformationServicesHandlerMixin {

	@Shadow
	private Map<String, TransformationServiceDecorator> serviceLookup;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	private TransformStore transformStore;

	@Redirect(method = "lambda$discoverServices$18", at = @At(value = "INVOKE", target = "Ljava/util/ServiceLoader;load(Ljava/lang/ModuleLayer;Ljava/lang/Class;)Ljava/util/ServiceLoader;"))
	private static <S> ServiceLoader<S> loadServicesWithoutModules(ModuleLayer layer, Class<S> service) {
		return ServiceLoader.load(service, AdvanceLoader.KNOT);
	}

	@SuppressWarnings({"InvalidInjectorMethodSignature"})
	@Inject(method = "buildTransformingClassLoader", at = @At("HEAD"), cancellable = true)
	private void hereTakeKnotCl(final LaunchPluginHandler pluginHandler, final @Coerce Object builder, final Environment environment, final ModuleLayerHandler layerHandler, CallbackInfoReturnable<KnotClassLoader> cir) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		KnotClassLoader knot = (KnotClassLoader) this.getClass().getClassLoader();

		Constructor<ClassTransformer> cst = (Constructor<ClassTransformer>) ClassTransformer.class.getDeclaredConstructors()[1];
		cst.setAccessible(true);
		TransformerAuditTrail tat = new TransformerAuditTrail();
		ClassTransformer classTransformer = cst.newInstance(transformStore, pluginHandler, this.getClass().getClassLoader(), tat);

		// Slow but do we have a choice?
		knot.getDelegate().addPostMixinTransformer((className, bytes) -> {
			try {
				// Give Forge a heads-up and let it setup
				Method transform = classTransformer.getClass().getDeclaredMethod("transform", byte[].class, String.class, String.class);
				transform.setAccessible(true);
				return (byte[]) transform.invoke(classTransformer, bytes, className, "classloading");
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//				throw new RuntimeException(e);
				return bytes;
			}
		});
		cir.setReturnValue(knot);
	}

	/**
	 * @author hYdos
	 * @reason Remove problematic services. (mixin)
	 */
	@Overwrite
	private List<ITransformationService.Resource> runScanningTransformationServices(Environment environment) {
		LOGGER.debug("Transformation services begin scanning");

		return serviceLookup.values()
				.stream()
				.filter(s -> {
					try {
						Field service = s.getClass().getDeclaredField("service");
						service.setAccessible(true);
						for (String comparison : List.of("MixinTransformationServiceLegacy", "ForgeLoomMixinRemapperInjectorService")) {
							if (service.get(s).getClass().getSimpleName().contains(comparison)) {
								return false;
							}
						}
						return true;
					} catch (NoSuchFieldException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
				.map(s -> {
					try {
						return (List<ITransformationService.Resource>) s.getClass().getDeclaredMethod("runScan", Environment.class).invoke(s, environment);
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						throw new RuntimeException(e);
					}
				})
				.<ITransformationService.Resource>mapMulti(Iterable::forEach)
				.toList();
	}

}
