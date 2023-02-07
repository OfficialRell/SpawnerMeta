package mc.rellox.spawnermeta.prices;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.prices.Price.PriceEconomy;
import mc.rellox.spawnermeta.prices.Price.PriceExperience;
import mc.rellox.spawnermeta.prices.Price.PriceLevels;
import mc.rellox.spawnermeta.prices.Price.PriceMaterial;

public final class PriceManager {

	private static final Map<Group, IPrice> PRICES = new HashMap<>();
	
	public static IPrice of(Group group) {
		return PRICES.get(group);
	}
	
	public static Price price(Group group, int i) {
		return of(group).of(i);
	}

	public static void reload() {
		PRICES.clear();
		Stream.of(Group.values()).forEach(group -> {
			String path = "Prices." + group.name();
			String type_name = CF.s.getString(path + ".price-type");
			PriceType type = PriceType.of(type_name);
			if(type == null) type = PriceType.EXPERIENCE;
			else if(type == PriceType.ECONOMY && SpawnerMeta.ECONOMY.get() == null) type = PriceType.EXPERIENCE;
			IPrice price;
			if(type == PriceType.EXPERIENCE) price = PriceExperience::new;
			else if(type == PriceType.LEVELS) price = PriceLevels::new;
			else if(type == PriceType.ECONOMY) price = PriceEconomy::new;
			else {
				ItemMatcher matcher = CF.s.getMatcher(path + ".item");
				price = i -> new PriceMaterial(i, matcher);
			}
			PRICES.put(group, price);
		});
	}
	
	public static interface IPrice {
		
		Price of(int i);
		
	}

}
