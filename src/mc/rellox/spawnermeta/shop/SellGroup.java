package mc.rellox.spawnermeta.shop;

import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

public record SellGroup(Material filler, Material sell, Material close, int rows, SellData... data) {
	
	public Inventory create() {
		return Bukkit.createInventory(null, rows * 9, Language.get("Shop-sell.name").text());
	}
	
	public SellData get(SpawnerType type) {
		return Stream.of(data)
				.filter(d -> d.type == type)
				.findFirst()
				.orElse(null);
	}
	
	public ItemStack x() {
		ItemStack item = new ItemStack(filler);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}

}
