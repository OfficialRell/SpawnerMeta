package mc.rellox.spawnermeta.api.hologram;

import java.util.Set;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.holograms.Hologram;
import mc.rellox.spawnermeta.holograms.HologramModifier;
import mc.rellox.spawnermeta.holograms.Warning;

public interface IHologram {
	
	HologramModifier modifier = new HologramModifier();
	
	static IHologram hologram(IGenerator generator) {
		return new Hologram(generator);
	}
	
	static IHologram warning(IGenerator generator, boolean above) {
		return new Warning(generator, above);
	}
	
	/**
	 * @return Entity generator
	 */
	
	IGenerator generator();
	
	/**
	 * @return Set of players who sees this spawner hologram
	 */
	
	Set<Player> viewers();
	
	/**
	 * Updates spawner hologram players.
	 */
	
	void update();
	
	/**
	 * Updates spawner hologram text.
	 */
	
	void rewrite();
	
	/**
	 * Shows this hologram to the specified player.
	 * 
	 * @param player - player
	 */
	
	void show(Player player);
	
	/**
	 * Hides this hologram from the specified player.
	 * 
	 * @param player - player
	 */
	
	void hide(Player player);
	
	/**
	 * Hides this hologram from all players who sees it.
	 */
	
	void clear();

}
