package mc.rellox.spawnermeta.prices;

import java.util.stream.IntStream;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.hook.ICurrency;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.utility.Utility;

public abstract class Price {
	
	public static Price of(Group group, int value) {
		return PriceManager.price(group, value);
	}
	
	public final PriceType type;
	public final int value;
	
	public Price(PriceType type, int value) {
		this.type = type;
		this.value = value;
	}
	
	/**
	 * @param player - player
	 * @return Balance the player has
	 */
	
	public abstract int balance(Player player);
	
	/**
	 * Checks if the player has this price.
	 * 
	 * @param player - player
	 * @return {@code true} if player has this price
	 */
	
	public abstract boolean has(Player player);
	
	/**
	 * Removed this price from the player.
	 * 
	 * @param player - player
	 */
	
	public abstract void remove(Player player);
	
	/**
	 * @return Text of insufficient funds
	 */
	
	public abstract Content insufficient();
	
	/**
	 * @return Formatted price text
	 */
	
	public abstract Content text();
	
	/**
	 * @param player - player
	 * @return Formatted requirement price text
	 */
	
	public abstract Content requires(Player player);
	
	/**
	 * Refunds this price to the player.
	 * 
	 * @param player - player
	 */
	
	public abstract void refund(Player player);
	
	public static class PriceExperience extends Price {
		
		public PriceExperience(int value) {
			super(PriceType.EXPERIENCE, value);
		}

		@Override
		public boolean has(Player player) {
			return Utility.op(player) == true || Utility.getExp(player) >= value;
		}
		
		@Override
		public void remove(Player player) {
			if(Utility.op(player) == true) return;
			Utility.changeExp(player, -value);
		}
		
		@Override
		public Content insufficient() {
			return Language.get("Prices.experience.insufficient");
		}
		
		@Override
		public Content text() {
			return Language.get("Prices.type.experience.amount",
					"amount", Settings.settings.price(value));
		}
		
		@Override
		public Content requires(Player player) {
			int require = value - balance(player);
			return Language.get("Prices.type.experience.amount",
					"amount", Settings.settings.price(require));
		}

		@Override
		public int balance(Player player) {
			return Utility.getExp(player);
		}
		
		@Override
		public void refund(Player player) {
			Utility.changeExp(player, value);
		}
		
	}
	
	public static class PriceLevels extends Price {
		
		public PriceLevels(int value) {
			super(PriceType.LEVELS, value);
		}

		@Override
		public boolean has(Player player) {
			return Utility.op(player) == true || player.getLevel() >= value;
		}
		
		@Override
		public void remove(Player player) {
			if(Utility.op(player) == true) return;
			player.setLevel(player.getLevel() - value);
		}
		
		@Override
		public Content insufficient() {
			return Language.get("Prices.type.levels.insufficient");
		}
		
		@Override
		public Content text() {
			return Language.get("Prices.type.levels.amount",
					"amount", Settings.settings.price(value));
		}
		
		@Override
		public Content requires(Player player) {
			int require = value - player.getLevel();
			return Language.get("Prices.type.levels.amount",
					"amount", Settings.settings.price(require));
		}

		@Override
		public int balance(Player player) {
			return player.getLevel();
		}
		
		@Override
		public void refund(Player player) {
			int level = player.getLevel() + value;
			player.setLevel(level);
		}
		
	}
	
	public static class PriceMaterial extends Price {
		
		private final ItemMatcher matcher;
		
		public PriceMaterial(int value, ItemMatcher matcher) {
			super(PriceType.MATERIAL, value);
			this.matcher = matcher;
		}

		@Override
		public boolean has(Player player) {
			if(Utility.op(player) == true) return true;
			Inventory v = player.getInventory();
			int h = IntStream.range(0, 36).map(i -> {
				ItemStack item = v.getItem(i);
				return matcher.match(item) ? item.getAmount() : 0;
			}).sum();
			return h >= value;
		}
		
		@Override
		public void remove(Player player) {
			if(Utility.op(player) == true) return;
			Inventory v = player.getInventory();
			int[] f = {value};
			IntStream.range(0, 36).forEach(i -> {
				if(f[0] <= 0) return;
				ItemStack item = v.getItem(i);
				if(matcher.match(item) == false) return;
				int a = item.getAmount();
				if(a > f[0]) {
					item.setAmount(a - f[0]);
					f[0] = 0;
				} else {
					f[0] -= a;
					v.setItem(i, null);
				}
			});
		}
		
		@Override
		public Content insufficient() {
			return Language.get("Prices.type.material.insufficient");
		}
		
		@Override
		public Content text() {
			return Language.get("Prices.type.material.amount",
					"amount", value,
					"material", Utility.displayName(matcher.refund()));
		}
		
		@Override
		public Content requires(Player player) {
			return Language.get("Prices.type.material.amount",
					"amount", value - balance(player),
					"material", Utility.displayName(matcher.refund()));
		}

		@Override
		public int balance(Player player) {
			Inventory v = player.getInventory();
			return IntStream.range(0, 36).map(i -> {
				ItemStack slot = v.getItem(i);
				return matcher.match(slot) == true ? slot.getAmount() : 0;
			}).sum();
		}
		
		@Override
		public void refund(Player player) {
			for(int i = 0; i < value; i++)
				ItemMatcher.add(player, matcher.refund());
		}
		
	}
	
	public static class PriceCurrency extends Price {
		
		private final ICurrency currency;
		
		public PriceCurrency(PriceType type, int value, ICurrency currency) {
			super(type, value);
			this.currency = currency;
		}

		@Override
		public boolean has(Player player) {
			return Utility.op(player) == true || currency.has(player, value) == true;
		}
		
		@Override
		public void remove(Player player) {
			if(Utility.op(player) == true) return;
			currency.remove(player, value);
		}
		
		@Override
		public Content insufficient() {
			return Language.get("Prices.type." + type.key() + ".insufficient");
		}
		
		@Override
		public Content text() {
			return Language.get("Prices.type." + type.key() + ".amount",
					"amount", Settings.settings.price(value));
		}
		
		@Override
		public Content requires(Player player) {
			int require = value - balance(player);
			return Language.get("Prices.type." + type.key() + ".amount",
					"amount", Settings.settings.price(require));
		}

		@Override
		public int balance(Player player) {
			return currency.get(player);
		}
		
		@Override
		public void refund(Player player) {
			currency.add(player, value);
		}
		
	}

}
