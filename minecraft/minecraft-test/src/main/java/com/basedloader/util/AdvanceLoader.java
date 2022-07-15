package com.basedloader.util;

import net.fabricmc.loader.impl.launch.knot.KnotClassLoader;

import java.io.IOException;
import java.util.Objects;

public class AdvanceLoader {
	public static final KnotClassLoader KNOT = (KnotClassLoader) AdvanceLoader.class.getClassLoader();

	public static void advanceDefineClass(String className) {
		try {
			byte[] cls = Objects.requireNonNull(KNOT.getResourceAsStream(className.replace(".", "/") + ".class"), "Unable to locate class " + className).readAllBytes();
			KNOT.defineClassFwd(className, cls, 0, cls.length, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}