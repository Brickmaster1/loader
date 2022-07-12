package io.github.astrarre.amalg.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.fabricmc.tinyremapper.api.TrEnvironment;

public class MixinClass {
	public final List<String> targets = new ArrayList<>();
	public final List<Implements> prefixes = new ArrayList<>();
	public final List<Consumer<TrEnvironment>> remapper;
	public final Map<Member, List<String>> aliases = new HashMap<>();
	public final int mrjVersion;
	public final String internalName;
	public boolean remap = true, isMixin;

	public MixinClass(List<Consumer<TrEnvironment>> remapper, int version, String name) {
		this.remapper = remapper;
		this.mrjVersion = version;
		this.internalName = name;
	}

	public void applyEnvironment(Consumer<TrEnvironment> remapper) {
		this.remapper.add(remapper);
	}

	public record Implements(String interfaceInternalName, String prefix) {}
	public record Member(String name, String desc) {}
}
