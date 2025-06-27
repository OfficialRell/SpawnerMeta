package mc.rellox.spawnermeta.holograms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.hologram.IHologram;
import mc.rellox.spawnermeta.api.region.IBox;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.text.content.Content;

public abstract class AbstractHologram implements IHologram {
	
	protected final IGenerator generator;
	private final Object hologram;
	private final IBox box;
	private final Set<Player> players;
	
	public AbstractHologram(IGenerator generator, boolean above, int radius) {
		this.generator = generator;
		String title = title().text();
		Block block = generator.block();
		this.hologram = modifier
				.create(block.getLocation()
						.add(0.5, 1 + (above ? 0.25 : 0)
								+ Settings.settings.holograms_height, 0.5), title);
		this.box = IBox.cube(block, radius);
		this.players = new HashSet<>();
	}
	
	@Override
	public IGenerator generator() {
		return generator;
	}
	
	@Override
	public Set<Player> viewers() {
		return players;
	}
	
	@Override
	public void update() {
		List<Player> list = generator.world().getPlayers();
		for(Player player : list) {
			if(box.in(player) == true) {
				if(players.add(player) == true) show(player);
			} else if(players.remove(player) == true) hide(player);
		}
	}
	
	@Override
	public void rewrite() {
		String title = title().text();
		modifier.update(players, hologram, title);
	}

	@Override
	public void show(Player player) {
		modifier.spawn(player, hologram);
	}
	
	@Override
	public void hide(Player player) {
		modifier.destroy(player, hologram);
	}

	@Override
	public void clear() {
		players.forEach(player -> modifier.destroy(player, hologram));
	}
	
	public abstract Content title();

}
