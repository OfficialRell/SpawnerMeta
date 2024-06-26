package mc.rellox.spawnermeta.view.layout;

import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.view.layout.ISlot;
import mc.rellox.spawnermeta.api.view.layout.SlotField;
import mc.rellox.spawnermeta.utility.Utility;

public record ActiveSlot(SlotField field, int[] slots, Material material, Material denier,
		boolean glint, int model) implements ISlot {
	
	public ActiveSlot(SlotField field, int slot, Material material, Material denier, boolean glint, int model) {
		this(field, new int[] {slot}, material, denier, glint, model);
	}
	
	public ActiveSlot {
		if(field.background() == true)
			throw new IllegalArgumentException("This slot is not for background");
	}

	public void save(MemorySection file, String path) {
		String p = path + "." + field.defines();
		file.addDefault(p + ".slots", IntStream.of(slots).boxed().toList());
		file.addDefault(p + ".material", material == null ? "EMPTY" : material.name());
		file.addDefault(p + ".glint", glint);
		file.addDefault(p + ".model", model);
		if(denied() == true) file.addDefault(p + ".deny-material", denier.name());
	}

	@Override
	public int slot() {
		return slots[0];
	}

	@Override
	public boolean is(int slot) {
		return IntStream.of(slots).anyMatch(s -> s == slot);
	}

	@Override
	public ItemStack toItem(boolean denied) {
		Material m = denied ? denier : material;
		if(m == null) return null;
		var item = new ItemStack(m);
		var meta = item.getItemMeta();
		if(glint == true) {
			meta.addEnchant(Utility.enchantment_power, 1, true);
			meta.addItemFlags(ItemFlag.values());
			Utility.hideCustomFlags(meta);
		}
		if(model > 0) meta.setCustomModelData(model);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public boolean denied() {
		return denier != null;
	}

}
