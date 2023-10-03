package mc.rellox.spawnermeta.view.layout;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.view.layout.IBackground;
import mc.rellox.spawnermeta.api.view.layout.ILayout;
import mc.rellox.spawnermeta.api.view.layout.ISlot;
import mc.rellox.spawnermeta.api.view.layout.SlotField;
import mc.rellox.spawnermeta.text.content.Content;

public class ActiveLayout implements ILayout {
	
	private final IBackground background;
	private final int rows;
	private final Map<SlotField, ISlot> slots;
	
	public ActiveLayout(IBackground background, int rows) {
		this.background = background;
		this.rows = rows;
		this.slots = new EnumMap<>(SlotField.class);
	}
	
	@Override
	public void set(SlotField field, ISlot slot) {
		slots.put(field, slot);
	}

	@Override
	public boolean is(int slot, SlotField field) {
		var s = slots.get(field);
		return s == null ? false : s.is(slot);
	}
	
	@Override
	public List<ISlot> all() {
		return new ArrayList<>(slots.values());
	}
	
	@Override
	public ISlot get(SlotField field) {
		return slots.get(field);
	}
	
	@Override
	public int rows() {
		return rows;
	}

	@Override
	public int slot(SlotField field) {
		var s = slots.get(field);
		return s == null ? -1 : s.slot();
	}

	@Override
	public int[] slots(SlotField field) {
		var s = slots.get(field);
		return s == null ? null : s.slots();
	}

	@Override
	public void fill(Inventory inventory, ItemStack item, SlotField field) {
		var s = slots.get(field);
		if(s == null) return;
		int m = inventory.getSize();
		for(int i : s.slots()) {
			if(i >= m) continue;
			inventory.setItem(i, item);
		}
	}

	@Override
	public boolean defined(SlotField field) {
		return slots.containsKey(field);
	}

	@Override
	public IBackground background() {
		return background;
	}

	@Override
	public void fill(Inventory inventory) {
		var item = background.toItem();
		if(item == null) return;
		int m = inventory.getSize();
		for(int i = 0; i < m; i++)
			inventory.setItem(i, item);
	}
	
	@Override
	public Inventory create(Content name) {
		return Bukkit.createInventory(null, rows * 9, name.text());
	}

}
