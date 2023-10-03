package mc.rellox.spawnermeta.api.view.layout;

import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.text.content.Content;

public interface ILayout {
	
	/**
	 * @param field - layout field
	 * @param slot - slot
	 */
	
	void set(SlotField field, ISlot slot);
	
	/**
	 * @param slot - inventory slot
	 * @param field - layout field
	 * @return {@code true} if this slot is defined by this layout field
	 */
	
	boolean is(int slot, SlotField field);
	
	/**
	 * @return All defined slots
	 */
	
	List<ISlot> all();
	
	/**
	 * @param field - layout field
	 * @return Slot of this layout field or {@code null}
	 */
	
	ISlot get(SlotField field);
	
	/**
	 * @return Rows of this layout
	 */
	
	int rows();
	
	/**
	 * @param field - layout field
	 * @return First slot of this layout field or {@code -1}
	 */
	
	int slot(SlotField field);
	
	/**
	 * @param field - layout field
	 * @return All slots occupied by this layout field or {@code null}
	 */
	
	int[] slots(SlotField field);
	
	/**
	 * Fills all slots with this item stack.
	 * 
	 * @param item - item stack
	 * @param field - layout field
	 */
	
	void fill(Inventory inventory, ItemStack item, SlotField field);
	
	/**
	 * @param field - layout field
	 * @return {@code true} if this layout field is defined
	 */
	
	boolean defined(SlotField field);
	
	/**
	 * @return Slot data of the backgroun
	 */
	
	IBackground background();
	
	/**
	 * Fills in all background slots.
	 */
	
	void fill(Inventory inventory);
	
	/**
	 * @param name - name of the inventory
	 * @return Newly created inventory
	 */
	
	Inventory create(Content name);

}
