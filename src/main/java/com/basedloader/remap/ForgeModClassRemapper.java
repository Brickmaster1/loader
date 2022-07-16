package com.basedloader.remap;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.MethodDef;
import net.fabricmc.mapping.tree.TinyTree;

import org.objectweb.asm.tree.ClassNode;

public class ForgeModClassRemapper extends Remapper {

	private final Path minecraftJar;
	private final TinyTree mappings;
	private final Map<String, ClassDef> classCache = new HashMap<>();
	private Map<String, ClassNode> classNodeCache = new HashMap<>();
	private final String from;
	private final String to;

	public ForgeModClassRemapper(Path minecraftJar, TinyTree mappings, String from, String to) {
		this.minecraftJar = minecraftJar;
		this.mappings = mappings;
		this.from = from;
		this.to = to;
		for (ClassDef clazz : this.mappings.getClasses()) {
			classCache.put(clazz.getName(from), clazz);
		}
	}

	public void cache(String className, ClassNode node) {
		this.classNodeCache.put(className, node);
	}

	@Override
	public String mapType(String internalName) {
		if (this.classCache.containsKey(internalName)) {
			internalName = this.classCache.get(internalName).getName(this.to);
		}

		return super.mapType(internalName);
	}

	@Override
	public String[] mapTypes(String[] internalNames) {
		return Arrays.stream(internalNames).map(this::mapType).toArray(String[]::new);
	}

	@Override
	public String mapInvokeDynamicMethodName(String name, String descriptor) {
		if (name.equals("m_137492_")) {
			System.out.println("Kill. Me.");
		}
		return super.mapInvokeDynamicMethodName(name, descriptor);
	}

	@Override
	public String mapMethodName(String owner, String name, String descriptor) {
		ClassDef mapping = this.classCache.get(owner);
		if (mapping != null) {
			if (name.equals("m_137492_")) {
				System.out.println("Kill. Me.");
			}
			String finalName = name;
			List<MethodDef> result = mapping.getMethods().stream().filter(methodDef -> methodDef.getName(this.from).equals(finalName)).toList();
			if (result.size() > 0) {
				for (MethodDef methodDef : result) {
					if (methodDef.getDescriptor(this.from).equals(descriptor)) {
						name = methodDef.getName(this.to);
						descriptor = methodDef.getDescriptor(this.to);
					}
				}
			}
		} else {
			ClassNode classNode = this.classNodeCache.get(owner);
			if (classNode != null) {
				if (!classNode.superName.equals("java/lang/Object")) {
					if(owner.contains("Entity")) {
						System.out.println("test");
					}
				}

				if (classNode.interfaces.size() > 0) {
					System.out.println("Test2");
				}
			}
		}
		return super.mapMethodName(owner, name, descriptor);
	}

	@Override
	public String map(String internalName) {
		if (this.classCache.containsKey(internalName)) {
			internalName = this.classCache.get(internalName).getName(this.to);
		}

		return super.map(internalName);
	}

	public byte[] remap(byte[] classBytes) {
		ClassReader reader = new ClassReader(classBytes);
		ClassWriter writer = new ClassWriter(reader, 0);
		ClassRemapper remapper = new ClassRemapper(writer, this);
		reader.accept(remapper, 0);
		return writer.toByteArray();
	}
}
