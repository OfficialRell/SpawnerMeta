package mc.rellox.spawnermeta.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.events.EventExecutor;
import mc.rellox.spawnermeta.api.events.IEvent;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.SpawnerBuilder;
import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.events.EventRegistry;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

@SuppressWarnings("removal")
public final class APIRegistry implements APIInstance {
	
	private boolean registered;
	
	private final List<WrappedExecutor<?>> executors;
	
	public APIRegistry() {
		this.executors = new ArrayList<>();
	}

	public boolean registered() {
		return registered;
	}
	
	@Override
	public <E extends IEvent> void register(Class<E> c, EventExecutor<E> executor) {
		Objects.requireNonNull(c, "Event class cannot be null");
		Objects.requireNonNull(executor, "Event executor cannot be null");
		WrappedExecutor<?> wrapper = new WrappedExecutor<>(c, executor);
		int j = 0;
		for(int i = 0; i < executors.size(); i++) {
			if(executors.get(i).subclass(c) == true) {
				j = i + 1;
				break;
			}
		}
		executors.add(j, wrapper);
		registered = true;
	}
	
	@Override
	public boolean breakSpawner(Block block, boolean drop, boolean particles) {
		Objects.requireNonNull(block, "Block cannot be null");
		return EventRegistry.destroy(block, drop, particles);
	}
	
	@Override
	public boolean placeSpawner(Block block, Player player, IVirtual spawner) {
		Objects.requireNonNull(block, "Block cannot be null");
		Objects.requireNonNull(spawner, "Virtual spawner cannot be null");
		return EventRegistry.place(block, player, spawner);
	}
	
	@Override
	public IVirtual getVirtual(ItemStack item) {
		Objects.requireNonNull(item, "Item cannot be null");
		return IVirtual.of(item);
	}
	
	@Override
	public IVirtual getVirtual(Block block) {
		Objects.requireNonNull(block, "Block cannot be null");
		return IVirtual.of(block);
	}
	
	@Override
	public ISpawner getSpawner(Block block) {
		Objects.requireNonNull(block, "Block cannot be null");
		return ISpawner.of(block);
	}
	
	@Override
	public IGenerator getGenerator(Block block) {
		Objects.requireNonNull(block, "Block cannot be null");
		return GeneratorRegistry.get(block);
	}
	
	@Override
	public SpawnerBuilder buildSpawner(SpawnerType type) {
		Objects.requireNonNull(type, "Spawner type cannot be null");
		return new SpawnerBuilder(type);
	}
	
	public void execute(IEvent event) {
		executors.forEach(wrapper -> {
			if(wrapper.castable(event) == false) return;
			wrapper.execute(event);
		});
	}
	
	private static class WrappedExecutor<E> {

		private final Class<E> c;
		private final EventExecutor<E> e;
		
		public WrappedExecutor(Class<E> c, EventExecutor<E> e) {
			this.c = c;
			this.e = e;
		}
		
		public boolean subclass(Class<?> a) {
			return c.isAssignableFrom(a);
		}
		
		public boolean castable(Object o) {
			return c.isInstance(o);
		}
		
		@SuppressWarnings("unchecked")
		public void execute(Object o) {
			e.execute((E) o);
		}
		
	}

}
