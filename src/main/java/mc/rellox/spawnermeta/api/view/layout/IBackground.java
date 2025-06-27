package mc.rellox.spawnermeta.api.view.layout;

import org.bukkit.Material;

public interface IBackground extends IItem {
	
	/**
	 * @return Layout field of this background slot
	 */
	
	SlotField field();
	
	/**
	 * @return Material of this background slot
	 */
	
	Material material();
	
	/**
	 * @return {@code true} if this background slot has glint
	 */
	
	boolean glint();
	
	/**
	 * @return Model data of this background slot
	 */
	
	int model();

}
