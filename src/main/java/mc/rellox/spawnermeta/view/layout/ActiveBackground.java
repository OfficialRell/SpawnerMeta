package mc.rellox.spawnermeta.view.layout;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.view.layout.IBackground;
import mc.rellox.spawnermeta.api.view.layout.SlotField;
import mc.rellox.spawnermeta.utility.Utility;

public record ActiveBackground(SlotField field, Material material, boolean glint, int model) implements IBackground {

	public void save(MemorySection file, String path) {
		String p = path + "." + field.defines();
		file.addDefault(p + ".material", material == null ? "EMPTY" : material.name());
		file.addDefault(p + ".glint", glint);
		file.addDefault(p + ".model", model);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack toItem(boolean denied) {
		if(material == null) return null;
		var item = new ItemStack(material);
		var meta = item.getItemMeta();
		if(glint == true) meta.addEnchant(Utility.enchantment_power, 1, true);
		if(model > 0) meta.setCustomModelData(model);
		meta.addItemFlags(Utility.ITEM_FLAGS);
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
}
