package mc.rellox.spawnermeta.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.events.SpawnerChargeEvent;
import mc.rellox.spawnermeta.api.events.SpawnerSwitchEvent;
import mc.rellox.spawnermeta.api.events.SpawnerUpgradeEvent;
import mc.rellox.spawnermeta.api.spawner.Spawner;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.events.EventRegistry;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.spawner.UpgradeType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.order.IOrder;
import mc.rellox.spawnermeta.utils.Messagable;
import mc.rellox.spawnermeta.utils.Utils;
import mc.rellox.spawnermeta.views.SpawnerViewLayout.Slot;
import mc.rellox.spawnermeta.views.SpawnerViewLayout.SlotType;
import mc.rellox.spawnermeta.views.SpawnerViewLayout.WL;

public final class SpawnerUpgrade implements Listener {
	
	private static final Map<Block, SpawnerUpgrade> SPAWNERS = new HashMap<>();
	
	public static void newUpgrade(Player player, Block block) {
		if(block.getState() instanceof CreatureSpawner == false) return;
		SpawnerUpgrade su = SPAWNERS.get(block);
		if(su == null) SPAWNERS.put(block, su = new SpawnerUpgrade(block));
		su.open(player);
	}
	
	public static void removeUpgrade(Block block) {
		SPAWNERS.remove(block);
	}
	
	public static void close(Block block) {
		SpawnerUpgrade u = SPAWNERS.remove(block);
		if(u != null) u.close();
	}
	
	public static void update(Block block) {
		SpawnerUpgrade u = SPAWNERS.get(block);
		if(u != null) u.update();
	}
	
	public static void updateAll() {
		SPAWNERS.values().forEach(SpawnerUpgrade::update);
	}

	private final List<Player> players;
	private final Block block;
	private final Spawner spawner;
	private final Inventory v;
	private boolean t;
	private boolean enabled;

	private SpawnerUpgrade(Block block) {
		this.players = new ArrayList<>();
		this.block = block;
		this.spawner = Spawner.of(block);
		this.v = Bukkit.createInventory(null, 27, Language.get("Inventory.upgrades.name").text());
		this.t = true;
		this.enabled = spawner.isEnabled();
		Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
	}

	public void open(Player player) {
		players.add(player);
		player.openInventory(v);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
		update();
	}

	public void close() {
		players.forEach(Player::closeInventory);
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		if(event.getInventory().equals(v) == true) event.setCancelled(true);
		Inventory c = event.getClickedInventory();
		Player player = (Player) event.getWhoClicked();
		Messagable m = new Messagable(player);
		if(c == null || c.equals(v) == false || players.contains(player) == false) return;
		try {
			if(t == false) return;
			t = false;
			new BukkitRunnable() {
				@Override
				public void run() {
					t = true;
				}
			}.runTaskLater(SpawnerMeta.instance(), 5);
			int o = event.getSlot();
			UpgradeType upgrade;
			if(WL.is(SlotType.STATS, o) == true) {
				if(Settings.settings.spawner_switching == false) return;
				
				SpawnerSwitchEvent call = EventRegistry.call(new SpawnerSwitchEvent(player, spawner, !enabled));
				if(call.cancelled() == true) return;
				
				spawner.setEnabled(enabled = !enabled);
				player.playSound(player.getEyeLocation(), enabled
						? Sound.ENTITY_ITEM_FRAME_ADD_ITEM : Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 2f, 1f);
				spawner.setRotating(enabled);
				update();
				return;
			} else if(WL.is(SlotType.UPGRADE_RANGE, o) == true) upgrade = UpgradeType.RANGE;
			else if(WL.is(SlotType.UPGRADE_DELAY, o) == true) upgrade = UpgradeType.DELAY;
			else if(WL.is(SlotType.UPGRADE_AMOUNT, o) == true) upgrade = UpgradeType.AMOUNT;
			else {
				if(Settings.settings.charges_enabled == true && WL.is(SlotType.CHARGES, o) == true) {
					ClickType ct = event.getClick();
					SpawnerType type = spawner.getType();
					
					int r = Settings.settings.charges_price(type, block);
					int a;
					if(ct.isShiftClick() == true) {
						a = lowestCharges() / r;
						if(a <= 0) return;
					} else if(ct.isLeftClick() == true) {
						a = Settings.settings.charges_buy_first;
					} else if(ct.isRightClick() == true) {
						a = Settings.settings.charges_buy_second;
					} else return;
					Price price = Price.of(Group.charges, r * a);
					
					SpawnerChargeEvent call = EventRegistry.call(new SpawnerChargeEvent(player, spawner, price, a));
					if(call.cancelled() == true) return;
					if(call.withdraw(player) == false) return;
					
					int charges = spawner.getCharges() + call.charges;
					spawner.setCharges(charges);
					m.send(Language.list("Inventory.upgrades.charges.purchase", "charges", call.charges));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
					update();
				}
				return;
			}
			int i = upgrade.ordinal();
			if(player.hasPermission("spawnermeta.upgrades.buy") == false) {
				m.send(Language.list("Spawners.upgrades.permission.purchase"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
			if(allowed(i) == false) {
				m.send(Language.list("Inventory.upgrades.disabled-upgrade"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
			if(Settings.settings.natural_can_upgrade == false && spawner.isNatural() == true) {
				if(player.hasPermission("spawnermeta.ownership.bypass.upgrading") == false) {
					m.send(Language.list("Spawners.natural.upgrading.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
			if(Settings.settings.owned_can_upgrade == false && spawner.isOwner(player, true) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.upgrading") == false) {
					m.send(Language.list("Spawners.ownership.upgrading.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
			SpawnerType type = spawner.getType();
			int[] ls = spawner.getUpgradeLevels();
			int[] ms = Settings.settings.upgrades_levels.get(type);
			if(ls[i] < ms[i]) {
				Price price = price(type, ls[i], i);
				
				SpawnerUpgradeEvent call = EventRegistry.call(
						new SpawnerUpgradeEvent(player, spawner, upgrade, ls[i] + 1, ms[i], price));
				
				if(price.has(player) == false) {
					price = call.getUnsafePrice();
					m.send(Language.list("Prices.insufficient", 
							"insufficient", price.insufficient(), "price", price.requires(player)));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
				if(call.cancelled() == true) return;
				if(call.withdraw(player) == false) return;
				
				int u = call.upgrade_level;
				ls[i] = u < 1 ? 1 : u > ms[i] ? ms[i] : u;
				
				spawner.setUpgradeLevels(ls);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
				UpgradeType ut = UpgradeType.of(i);
				m.send(Language.list("Inventory.upgrades.purchase." + ut.lower(),
						"level", Utils.roman(ls[i])));
				DustOptions d = new DustOptions(ut.color, 2f);
				player.spawnParticle(Particle.REDSTONE, Utils.center(block), 50, 0.25, 0.25, 0.25, 0, d);
				
				int[] a = spawner.getUpgradeAttributes();
				a[i] += Settings.settings.spawner_value_increase.get(type)[i];
				if(a[i] < 0) a[i] = 0;
				spawner.setUpgradeAttributes(a);
				
				update();
			} else player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
		} catch(Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "This upgrade inventory is invalid, closing!");
			player.closeInventory();
		}
	}
	
	
	
	@EventHandler
	private void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		players.remove(player);
		if(players.isEmpty() == true) {
			HandlerList.unregisterAll(this);
			removeUpgrade(block);
		} else update();
	}
	
	public void update() {
		SpawnerType type = spawner.getType();
		Slot s;
		for(int i = 0; i < 27; i++) {
			if((s = SpawnerViewLayout.LAYOUT[i]).t == SlotType.BACKGROUND) v.setItem(i, x(s));
			else if(s.t == SlotType.STATS) v.setItem(i, stats(s));
			else if(s.t == SlotType.UPGRADE_RANGE) v.setItem(i, allowed(0) ? upgrade(s, 0) : denied(0));
			else if(s.t == SlotType.UPGRADE_DELAY) v.setItem(i, allowed(1) ? upgrade(s, 1) : denied(1));
			else if(s.t == SlotType.UPGRADE_AMOUNT) v.setItem(i, allowed(2) ? upgrade(s, 2) : denied(2));
			else if(s.t == SlotType.CHARGES) v.setItem(i, Settings.settings.charges_enabled
					? charges(type, s) : x(SpawnerViewLayout.background_slot));
		}
	}
	
	private ItemStack upgrade(Slot slot, int i) {
		ItemStack item = new ItemStack(slot.m);
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return item;
		int[] levels = spawner.getUpgradeLevels();
		int level = levels[i];
		UpgradeType u = UpgradeType.of(i);
		meta.setDisplayName(Language.get("Inventory.upgrades.items.upgrade.name." + u.lower(),
				"level", Utils.roman(level)).text());
		meta.addItemFlags(ItemFlag.values());
		if(slot.g == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		if(slot.o > 0) meta.setCustomModelData(slot.o);

		IOrder orderer = Settings.settings.order_upgrade.oderer();

		SpawnerType type = spawner.getType();
		int[] max_levels = Settings.settings.upgrades_levels.get(type);
		if(level < max_levels[i]) {
			orderer.submit("HELP", () -> {
				return Language.list("Inventory.upgrades.items.upgrade.help");
			});
		}
		orderer.submit("INFO", () -> {
			return Language.list("Inventory.upgrades.items.upgrade.info." + u.lower());
		});
		orderer.submit("CURRENT", () -> {
			return Language.list("Inventory.upgrades.items.upgrade.current." + u.lower(),
					"value", value(type, level, i));
		});
		if(level < max_levels[i]) {
			orderer.submit("NEXT", () -> {
				return Language.list("Inventory.upgrades.items.upgrade.next." + u.lower(),
						"value", value(type, level + 1, i));
			});
			orderer.submit("PRICE", () -> {
				Price price = price(type, level, i);
				return Language.list("Inventory.upgrades.items.upgrade.price",
						"price", price);
			});
		} else {
			orderer.submit("PRICE", () -> {
				return Language.list("Inventory.upgrades.items.upgrade.maximum-reached");
			});
		}
		
		meta.setLore(orderer.build());
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack denied(int i) {
		UpgradeType u = UpgradeType.of(i);
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Inventory.upgrades.items.disabled-upgrade.name." + u.lower()).text());
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);

		IOrder orderer = Settings.settings.order_disabled.oderer();

		int level = spawner.getUpgradeLevels()[i];
		SpawnerType type = spawner.getType();
		
		orderer.submit("HELP", () -> {
			return Language.list("Inventory.upgrades.items.disabled-upgrade.help");
		});
		orderer.submit("INFO", () -> {
			return Language.list("Inventory.upgrades.items.upgrade.info." + u.lower());
		});
		orderer.submit("CURRENT", () -> {
			return Language.list("Inventory.upgrades.items.disabled-upgrade.current." + u.lower(),
					"value", value(type, level, i));
		});
		
		meta.setLore(orderer.build());

		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack stats(Slot slot) {
		ItemStack item = new ItemStack(slot.m);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Inventory.upgrades.items.stats.name",
				"type", spawner.getType()).text());
		meta.addItemFlags(ItemFlag.values());
		if(slot.o > 0) meta.setCustomModelData(slot.o);
		if(slot.g == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		
		IOrder orderer = Settings.settings.order_stats.oderer();

		if(spawner.isEmpty() == true) {
			orderer.submit("EMPTY", () -> {
				return Language.list("Inventory.upgrades.items.stats.empty");
			});
		}
		if(Settings.settings.spawner_switching == true) {
			orderer.submit("SWITCHING", () -> {
				return enabled == true ? Language.list("Inventory.upgrades.items.stats.enabled")
						: Language.list("Inventory.upgrades.items.stats.disabled");
			});
		}
		int[] l = Utils.location(block);
		orderer.submit("LOCATION", () -> {
			return Language.list("Inventory.upgrades.items.stats.location",
					"x", c(l[0]), "y", l[1], "z", c(l[2]));
		});
		int stack = spawner.getStack();
		if(Settings.settings.stacking_enabled == true) {
			orderer.submit("STACK", () -> {
				if(Settings.settings.stacking_ignore_limit == true)
					return Language.list("Inventory.upgrades.items.stats.stacking.infinite",
							"stack", stack);
				return Language.list("Inventory.upgrades.items.stats.stacking.finite",
						"stack", stack, "limit", Settings.settings.stacking_spawner_limit);
			});
		}
		if(Settings.settings.spawnable_enabled == true) {
			int spawnable = spawner.getSpawnable();
			if(spawnable < 1_000_000_000) {
				orderer.submit("SPAWNABLE", () -> {
					return Language.list("Inventory.upgrades.items.stats.spawnable",
							"spawnable", spawnable);
				});
			}
		}
		if(Settings.settings.charges_enabled == true) {
			int c = spawner.getCharges();
			if(c <= 0) {
				boolean n = Settings.settings.charges_ignore_natural == true && spawner.isNatural() == true;
				if(n == false) {
					orderer.submit("WARNING", () -> {
						return Language.list("Inventory.upgrades.items.stats.charges.insufficient");
					});
				}
			}
		}
		orderer.submit("INFO", () -> {
			return Language.list("Inventory.upgrades.items.stats.lore");
		});
		
		meta.setLore(orderer.build());
		
		item.setItemMeta(meta);
		return item;
	}

	private String c(int i) {
		return "" + (i < 0 ? i == -1 ? "-0" : i + 1 : i);
	}
	
	private int lowestCharges() {
		Price c = Price.of(Group.charges, 0);
		if(players.size() == 1) return c.balance(players.get(0));
		int b = -1;
		for(Player player : players) {
			int a = c.balance(player);
			if(b > a || b < 0) b = a;
		}
		return b;
	}
	
	private ItemStack charges(SpawnerType type, Slot slot) {
		ItemStack item = new ItemStack(slot.m);
		ItemMeta meta = item.getItemMeta();
		int c = spawner.getCharges();
		boolean b = c >= 1_000_000_000;
		String charges = b ? "" + '\u221E' : "" + spawner.getCharges();
		meta.setDisplayName(Language.get("Inventory.upgrades.items.charges.name",
				"charges", charges).text());
		meta.addItemFlags(ItemFlag.values());
		if(slot.g == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		if(slot.o > 0) meta.setCustomModelData(slot.o);
		if(b == false) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			int f0 = Settings.settings.charges_buy_first;
			int f1 = Settings.settings.charges_buy_second;
			int r = Settings.settings.charges_price(type, block);
			int c0 = r * f0;
			int c1 = r * f1;
			int a = lowestCharges() / r;
			int c2 = r * a;
			lore.addAll(Text.toText(Language.list("Inventory.upgrades.items.charges.purchase.first",
					"charges", f0, "price", Price.of(Group.charges, c0))));
			lore.addAll(Text.toText(Language.list("Inventory.upgrades.items.charges.purchase.second",
					"charges", f1, "price", Price.of(Group.charges, c1))));
			if(a > 0) lore.addAll(Text.toText(Language.list("Inventory.upgrades.items.charges.purchase.all",
					"price", Price.of(Group.charges, c2), "charges", a)));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack x(Slot slot) {
		if(slot.m == null || slot.m == Material.AIR) return null;
		ItemStack item = new ItemStack(slot.m);
		ItemMeta meta = item.getItemMeta();
		if(slot.o > 0) meta.setCustomModelData(slot.o);
		meta.addItemFlags(ItemFlag.values());
		if(slot.g == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
	private Price price(SpawnerType type, int level, int i) {
		int value = Settings.settings.upgrades_prices.get(type)[i],
				increase = Settings.settings.upgrades_price_increase.get(type)[i];
		for(int j = 1; j < level; j++) value = Settings.settings.upgrade_increase_type.price(value, increase);
		return Price.of(Group.upgrades, value * spawner.getStack());
	}
	
	private int value(SpawnerType type, int level, int i) {
		int value = Settings.settings.spawner_values.get(type)[i],
				increase = Settings.settings.spawner_value_increase.get(type)[i];
		for(int j = 1; j < level; j++) value += increase;
		return convert(value, i);
	}
	
	private int convert(int v, int i) {
		if(i == 0) {
			if(v <= 0) return 1;
			else if(v > 512) return 512;
			return v;
		} else if(i == 1) {
			if(v < 20) return 1;
			else if(v > 12000) return 600;
			return (int) (v * 0.05);
		} else if(i == 2) {
			int s = spawner.getStack();
			if(v <= 0) return s;
			else if(v > 4048) return 4048 * s;
			return v * s;
		} else return v;
	}
	
	private boolean allowed(int i) {
		SpawnerType type = spawner.getType();
		return Settings.settings.upgrades_upgradeable.get(type)[i];
	}

}
