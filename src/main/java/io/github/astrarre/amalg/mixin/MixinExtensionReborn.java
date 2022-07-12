package io.github.astrarre.amalg.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.api.TrEnvironment;

public class MixinExtensionReborn implements TinyRemapper.Extension {
	final Map<Integer, MrjState> listeners = new HashMap<>();

	@Override
	public void attach(TinyRemapper.Builder builder) {
		Lock lock = new ReentrantLock();
		builder.extraAnalyzeVisitor((mrjVersion, className, next) -> {
			MrjState state;
			try {
				lock.lock();
				state = listeners.computeIfAbsent(mrjVersion, m -> new MrjState());
			} finally {
				lock.unlock();
			}

			FirstPassMixinVisitor visitor = new FirstPassMixinVisitor(next, mrjVersion, className, state.listeners);
			state.map.put(className, visitor.state);
			return visitor;
		});

		builder.extraStateProcessor(env -> {
			MrjState state = this.listeners.get(env.getMrjVersion());
			if(state != null) {
				for(Consumer<TrEnvironment> listener : state.listeners) {
					listener.accept(env);
				}
				state.listeners.clear();
			}
		});

		builder.extraPreApplyVisitor((cls, next) -> {
			MrjState state;
			try {
				lock.lock();
				state = listeners.get(cls.getEnvironment().getMrjVersion());
			} finally {
				lock.unlock();
			}
			if(state == null) {
				System.out.println(cls.getName());
				return next;
			}
			MixinClass type = state.map.get(cls.getName());
			if(type.isMixin) {
				return new SecondPassMixinVisitor(next, cls, type);
			} else {
				return next;
			}
		});
	}

	record MrjState(List<Consumer<TrEnvironment>> listeners, Map<String, MixinClass> map) {
		public MrjState() {
			this(new Vector<>(), new ConcurrentHashMap<>());
		}
	}
}
