package mc.rellox.spawnermeta.holograms;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.Spawner;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.text.content.Content;

public class Hologram {
	
	protected final Block block;
	private final Object hologram;
	
	public Hologram(Block block) {
		this.block = block;
		String title = title(Spawner.of(block)).text();
		this.hologram = HologramRegistry.modifier.create(block.getLocation().add(0.5, 1.25, 0.5), title);
		HologramRegistry.modifier.spawn(hologram);
	}
	
	public boolean is(Block block) {
		return this.block.equals(block);
	}
	
	public void update() {
		String title = title(Spawner.of(block)).text();
		HologramRegistry.modifier.update(hologram, title);
	}
	
	public void spawn(Player player) {
		HologramRegistry.modifier.spawn(player, hologram);
	}
	
	public void destroy() {
		HologramRegistry.modifier.destroy(hologram);
	}
	
	private Content title(Spawner spawner) {
		SpawnerType type = spawner.getType();
		String r = type == SpawnerType.EMPTY ? "empty" : "regular";
		int stack = spawner.getStack();
		return stack > 1 ? Language.get("Spawners.hologram." + r + ".multiple",
				"name", type, "stack", stack)
				: Language.get("Spawners.hologram." + r + ".single",
						"name", type);
	}

}
