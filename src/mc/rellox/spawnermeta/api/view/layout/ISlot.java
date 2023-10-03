package mc.rellox.spawnermeta.api.view.layout;

import org.bukkit.Material;

public interface ISlot extends IItem {
	
	/**
	 * @return Layout field of this slot
	 */
	
	SlotField field();
	
	/**
	 * @return First index of this slot
	 */
	
	int slot();
	
	/**
	 * @return Indecees of this slot
	 */
	
	int[] slots();
	
	/**
	 * @return Material of this slot
	 */
	
	Material material();
	
	/**
	 * @return Denied material or {@code null}
	 */
	
	Material denier();
	
	/**
	 * @return {@code true} if this slot can be denied
	 */
	
	boolean denied();
	
	/**
	 * @return {@code true} if this slot has glint
	 */
	
	boolean glint();
	
	/**
	 * @return Model data of this slot
	 */
	
	int model();
	
	/**
	 * @param slot - slot index
	 * @return {@code true} if this slot has this index
	 */
	
	boolean is(int slot);

}
