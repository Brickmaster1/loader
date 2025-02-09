package io.github.astrarre.amalg.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.fabricmc.loader.impl.util.log.Log;

import net.fabricmc.loader.impl.util.log.LogCategory;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.fabricmc.tinyremapper.api.TrClass;
import net.fabricmc.tinyremapper.api.TrEnvironment;
import net.fabricmc.tinyremapper.api.TrMethod;
import net.fabricmc.tinyremapper.api.TrRemapper;

public class SecondPassMixinVisitor extends ClassVisitor {
	static final Set<String> HARD = Set.of("Lorg/spongepowered/asm/mixin/Shadow;", "Lorg/spongepowered/asm/mixin/Overwrite;");
	static final Set<String> INJECTORS = Set.of(
			"Lorg/spongepowered/asm/mixin/injection/ModifyVariable;",
			"Lorg/spongepowered/asm/mixin/injection/Inject;",
			"Lorg/spongepowered/asm/mixin/injection/ModifyArg;",
			"Lorg/spongepowered/asm/mixin/injection/ModifyArgs;",
			"Lorg/spongepowered/asm/mixin/injection/ModifyConstant;",
			"Lorg/spongepowered/asm/mixin/injection/Redirect;"
	);

	public final TrClass type;
	public final MixinClass state;

	public SecondPassMixinVisitor(ClassVisitor classVisitor, TrClass type, MixinClass state) {
		super(Opcodes.ASM9, classVisitor);
		this.type = type;
		this.state = state;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		AnnotationVisitor visitor = super.visitAnnotation(descriptor, visible);
		if(descriptor.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
			return new MixinAnnotationReplacer(visitor);
		}
		return visitor;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
		FieldVisitor visitor = super.visitField(access, name, descriptor, signature, value);
		if(state.isMixin && state.remap) {
			if (state.aliases.isEmpty()) {
				Log.error(LogCategory.MOD_REMAP, "NO ALIASES WHILE REMAPPING!");
				return visitor;
			}

			MixinClass.Member member = new MixinClass.Member(name, descriptor);
			Collection<String> aliases = state.aliases.get(member);
			if(aliases.isEmpty()) {
				return visitor;
			} else {
				return new AliasRemapField(visitor, aliases);
			}
		}
		return visitor;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
		if(state.isMixin && state.remap) {
			if (state.aliases.isEmpty()) {
				Log.error(LogCategory.MOD_REMAP, "NO ALIASES WHILE REMAPPING!");
				return visitor;
			}

			MixinClass.Member member = new MixinClass.Member(name, descriptor);
			Collection<String> aliases = state.aliases.get(member);
			if(aliases.isEmpty()) {
				return new Method(visitor);
			} else {
				return new AliasRemapMethod(visitor, aliases);
			}
		}
		return visitor;
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

	TrEnvironment getEnvironment() {
		return this.type.getEnvironment();
	}

	TrRemapper getRemapper() {
		return getEnvironment().getRemapper();
	}

	class MixinAnnotationReplacer extends AnnotationVisitor {
		public MixinAnnotationReplacer(AnnotationVisitor annotationVisitor) {
			super(SecondPassMixinVisitor.this.api, annotationVisitor);
		}

		@Override
		public AnnotationVisitor visitArray(String name) {
			if(name.equals("value") || name.equals("targets")) {
				return null;
			}
			return super.visitArray(name);
		}

		@Override
		public void visitEnd() {
			AnnotationVisitor array = super.visitArray("value");
			for(String target : state.targets) {
				array.visit(null, Type.getType("L" + target + ";"));
			}
			array.visitEnd();
			super.visitEnd();
		}
	}

	class AliasRemapMethod extends MethodVisitor {
		final Collection<String> aliases;

		public AliasRemapMethod(MethodVisitor methodVisitor, Collection<String> aliases) {
			super(SecondPassMixinVisitor.this.api, methodVisitor);
			this.aliases = aliases;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			AnnotationVisitor visitor = super.visitAnnotation(descriptor, visible);
			if(HARD.contains(descriptor)) {
				return new AliasReplacingVistor(visitor, this.aliases);
			} else if(descriptor.equals("Lorg/spongepowered/asm/mixin/gen/Invoker;")) {
				return new ValueAliasReplacingVisitor(visitor, this.aliases);
			}
			return visitor;
		}
	}

	class AliasRemapField extends FieldVisitor {
		final Collection<String> aliases;

		public AliasRemapField(FieldVisitor fieldVisitor, Collection<String> aliases) {
			super(SecondPassMixinVisitor.this.api, fieldVisitor);
			this.aliases = aliases;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			AnnotationVisitor visitor = super.visitAnnotation(descriptor, visible);
			if(HARD.contains(descriptor)) {
				return new AliasReplacingVistor(visitor, this.aliases);
			} else if(descriptor.equals("Lorg/spongepowered/asm/mixin/gen/Accessor;")) {
				return new ValueAliasReplacingVisitor(visitor, this.aliases);
			}
			return visitor;
		}
	}

	class ValueAliasReplacingVisitor extends AnnotationVisitor {
		final Collection<String> aliases;

		public ValueAliasReplacingVisitor(AnnotationVisitor annotationVisitor, Collection<String> aliases) {
			super(SecondPassMixinVisitor.this.api, annotationVisitor);
			this.aliases = aliases;
		}

		@Override
		public void visit(String name, Object value) {
			if(!name.equals("value")) {
				super.visit(name, value);
			}
		}

		@Override
		public void visitEnd() {
			if(aliases.size() > 1) {
				Log.warn(LogCategory.MOD_REMAP, "[Error] multiple valid targets for invoker/accessor " + aliases);
			}
			super.visit("value", new ArrayList<>(this.aliases).get(0));
			super.visitEnd();
		}
	}

	class AliasReplacingVistor extends AnnotationVisitor {
		final Collection<String> aliases;

		public AliasReplacingVistor(AnnotationVisitor annotationVisitor, Collection<String> aliases) {
			super(SecondPassMixinVisitor.this.api, annotationVisitor);
			this.aliases = aliases;
		}

		@Override
		public AnnotationVisitor visitArray(String name) {
			if(name.equals("aliases")) {
				return null;
			} else {
				return super.visitArray(name);
			}
		}

		@Override
		public void visitEnd() {
			AnnotationVisitor visitor = super.visitArray("aliases");
			for(String alias : aliases) {
				visitor.visit("aliases", alias);
			}
			super.visitEnd();
		}
	}


	class Method extends MethodVisitor {

		public Method(MethodVisitor methodVisitor) {
			super(SecondPassMixinVisitor.this.api, methodVisitor);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			AnnotationVisitor visitor = super.visitAnnotation(descriptor, visible);
			if(INJECTORS.contains(descriptor)) {
				return new InjectorVisitor(visitor);
			}
			return visitor;
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
			return super.visitParameterAnnotation(parameter, descriptor, visible);
		}
	}

	class InjectorVisitor extends AnnotationVisitor { // todo add support for @Coerce once we actually pass the descriptor
		boolean remap = true;

		public InjectorVisitor(AnnotationVisitor annotationVisitor) {
			super(SecondPassMixinVisitor.this.api, annotationVisitor);
		}

		@Override
		public void visit(String name, Object value) {
			super.visit(name, value);
			if(name.equals("remap")) { // todo check in advance for this
				remap = (Boolean) value;
			}
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, String descriptor) {
			AnnotationVisitor visitor = super.visitAnnotation(name, descriptor);
			if(name.equals("at") && this.remap) { // add support for @Redirect, @ModifyArg, @ModifyArgs, @ModifyVariable
				return new AtVisitor(visitor);
			}
			return visitor;
		}

		@Override
		public AnnotationVisitor visitArray(String name) {
			AnnotationVisitor visitor = super.visitArray(name);
			if(name.equals("method") && remap) {
				return new MethodTargetVisitor(visitor, name);
			} else if(name.equals("at") && remap) {
				return new AnnotationVisitor(this.api, visitor) {
					@Override
					public AnnotationVisitor visitAnnotation(String name, String descriptor) {
						return new AtVisitor(super.visitAnnotation(name, descriptor));
					}
				};
			} else if(name.equals("slice")) {
				return new AnnotationVisitor(this.api, visitor) {
					@Override
					public AnnotationVisitor visitAnnotation(String name, String descriptor) {
						return new SliceVisitor(super.visitAnnotation(name, descriptor));
					}
				};
			}
			return visitor;
		}
	}

	class SliceVisitor extends AnnotationVisitor {
		public SliceVisitor(AnnotationVisitor annotationVisitor) {
			super(SecondPassMixinVisitor.this.api, annotationVisitor);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, String descriptor) {
			AnnotationVisitor visitor = super.visitAnnotation(name, descriptor);
			if(descriptor.equals("Lorg/spongepowered/asm/mixin/injection/At;")) {
				return new AtVisitor(visitor);
			}
			return visitor;
		}
	}

	class MethodTargetVisitor extends AnnotationVisitor {
		final List<String> values = new ArrayList<>();
		private final String name;

		public MethodTargetVisitor(AnnotationVisitor visitor, String name) {
			super(SecondPassMixinVisitor.this.api, visitor);
			this.name = name;
		}

		@Override
		public void visit(String name, Object value) {
			values.add((String) value);
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
			Set<String> destinationTargets = new HashSet<>();
			for(String target : state.targets) {
				TrClass type = getEnvironment().getClass(target);
				String mappedType = getRemapper().map(target);
				for(String selector : this.values) {
					if(selector.startsWith("/")) {
						MemberMatcher matcher = MemberMatcher.parse(selector);
						for(TrMethod method : type.getMethods()) {
							String name = method.getName();
							String desc = method.getDesc();
							if(matcher.match(target, name, desc)) {
								String mappedDesc = getRemapper().mapMethodDesc(desc);
								String mappedName = method.getNewName();
								if(mappedName == null) {
									mappedName = name;
								}
								destinationTargets.add("L" + mappedType + ";" + mappedName + mappedDesc);
							}
						}
					} else {
						String quantity;
						int start = selector.indexOf('{');
						int end = selector.lastIndexOf('}');
						if(start != -1 && end != -1) {
							quantity = selector.substring(start, end + 1);
							selector = selector.substring(0, start) + selector.substring(end + 1);
						} else {
							quantity = "";
						}

						int methodNameTerminator = selector.indexOf('(');
						int ownerNameTerminator = selector.indexOf(';');
						String owner, name, desc;
						if((ownerNameTerminator == -1 || ownerNameTerminator > methodNameTerminator) && methodNameTerminator != -1) {
							// present name, present descriptor
							owner = target;
							name = selector.substring(0, methodNameTerminator);
							desc = selector.substring(methodNameTerminator);
						} else if(methodNameTerminator != -1) {
							// present owner, present name, present descriptor
							owner = selector.substring(1, ownerNameTerminator);
							name = selector.substring(ownerNameTerminator + 1, methodNameTerminator);
							desc = selector.substring(methodNameTerminator);
						} else if(ownerNameTerminator != -1) {
							// present owner, present name
							owner = selector.substring(1, ownerNameTerminator);
							name = selector.substring(ownerNameTerminator + 1);
							desc = "";
						} else {
							// present name
							owner = target;
							name = selector;
							desc = "";
						}

						boolean matchFirst;
						if(name.endsWith("*")) {
							matchFirst = false;
							name = name.substring(0, name.length() - 1);
						} else {
							matchFirst = true;
						}
						if(owner.equals(target)) {
							Iterable<TrMethod> methods;
							if(matchFirst) {
								methods = matchOne(type, selector, name, desc);
							} else {
								String finalName = name;
								methods = type.getMethods()
								              .stream()
								              .filter(method -> method.getName().equals(finalName) && method.getDesc().startsWith(desc))
								              .collect(Collectors.toList());
							}
							for(TrMethod method : methods) {
								String mappedName = method.getNewName();
								if(mappedName == null) {
									mappedName = name;
								}
								destinationTargets.add("L" + mappedType + ";" + mappedName + quantity + getRemapper().mapDesc(method.getDesc()));
							}
						}
					}
				}
			}
			for(String target : destinationTargets) {
				super.visit(name, target);
			}
			super.visitEnd();
		}

		private Iterable<TrMethod> matchOne(TrClass type, String selector, String name, String desc) {
			Iterable<TrMethod> methods;
			// filters descriptors by number of arguments, it's not perfect, but it's not like the annotation processor is any
			// better so mald
			// only one of them *can* be correct, since u can't match to methods with a different number of arguments
			Map<Integer, TrMethod> descriptorByArgumentCount = new HashMap<>();
			for(TrMethod method : type.getMethods()) {
				String methodDesc = method.getDesc();
				if(method.getName().equals(name) && methodDesc.startsWith(desc)) {
					Type methodType = Type.getMethodType(methodDesc);
					TrMethod member = descriptorByArgumentCount.putIfAbsent(methodType.getArgumentTypes().length, method);
					if(member != null) {
						Log.error(LogCategory.MOD_REMAP, "[Error] Ambigious target " + selector + " with " + member.getName() + member.getDesc() + " &\n\t" +
						             method.getName() + method.getDesc() + " in " + state.internalName + "\n\tplease use the full descriptor of the method u want to target, or \""+member.getName()+"*\" to target both if they have the same name");
					}
				}
			}
			methods = descriptorByArgumentCount.values();
			return methods;
		}
	}

	class AtVisitor extends AnnotationVisitor {
		String injector, target;
		boolean remap = true;

		public AtVisitor(AnnotationVisitor annotationVisitor) {
			super(SecondPassMixinVisitor.this.api, annotationVisitor);
		}

		@Override
		public void visit(String name, Object value) {
			boolean visit = true;
			if(name.equals("value")) {
				this.injector = (String) value;
			} else if(name.equals("target")) {
				this.target = (String) value;
				visit = false;
			} else if(name.equals("desc")) {
				throw new UnsupportedOperationException("@Desc annotations aren't supported at the moment");
			} else if(name.equals("remap")) {
				this.remap = (Boolean) value;
			}
			if(visit) {
				super.visit(name, value);
			}
		}

		@Override
		public void visitEnd() {
			String target = this.target;
			if(this.remap) {
				switch(this.injector) {
					case "INVOKE", "INVOKE_ASSIGN", "INVOKE_STRING" -> {
						if(target == null) {
							Log.warn(LogCategory.MOD_REMAP, "INVOKE/INVOKE_ASSIGN/INVOKE_STRING not paired with target!");
						}
						// fully qualified descriptor
						int typeTerminator = target.indexOf(';');
						int nameTerminator = target.indexOf('(', typeTerminator);
						// todo better errors
						String type = this.target.substring(1, typeTerminator);
						String name = this.target.substring(typeTerminator + 1, nameTerminator);
						String desc = this.target.substring(nameTerminator);
						String mappedType = getRemapper().map(type);
						String mappedName = getRemapper().mapMethodName(type, name, desc);
						String mappedDesc = getRemapper().mapMethodDesc(desc);
						if(!type.equals(mappedType) || !name.equals(mappedName) || !desc.equals(mappedDesc)) {
							target = "L" + mappedType + ";" + mappedName + mappedDesc;
						}
					}
					case "NEW" -> {
						if(target == null) {
							Log.warn(LogCategory.MOD_REMAP, "NEW not paired with target!");
						}
						String type = target;
						String mappedType = getRemapper().mapDesc(type);
						if(!type.equals(mappedType)) {
							target = mappedType;
						}
					}
					case "FIELD" -> {
						if(target == null) {
							Log.warn(LogCategory.MOD_REMAP, "FIELD not paired with target!");
						}
						int typeTerminator = target.indexOf(';');
						int nameTerminator = target.indexOf(':', typeTerminator + 1);
						String type = target.substring(0, typeTerminator);
						String name = target.substring(typeTerminator + 1, nameTerminator);
						String desc = target.substring(nameTerminator + 1);
						String mappedType = getRemapper().map(type);
						String mappedName = getRemapper().mapFieldName(type, name, desc);
						String mappedDesc = getRemapper().mapDesc(desc);
						if(!type.equals(mappedType) || !name.equals(mappedName) || !desc.equals(mappedDesc)) {
							target = "L" + mappedType + ";" + mappedName + ":" + mappedDesc;
						}
					}
				}
			}

			if(target != null) {
				super.visit("target", target);
			}
			super.visitEnd();
		}
	}
}
