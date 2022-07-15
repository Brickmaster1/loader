package com.basedloader;

import java.lang.module.Configuration;
import java.util.List;

import cpw.mods.cl.ModuleClassLoader;

import net.fabricmc.loader.impl.launch.knot.KnotClassLoader;

/**
 * CPW can cry abt it.
 */
public class KnotModuleClassLoader extends ModuleClassLoader {

	public KnotModuleClassLoader(KnotClassLoader knot) {
		super(knot.getName(), Configuration.empty(), List.of());
	}
}
