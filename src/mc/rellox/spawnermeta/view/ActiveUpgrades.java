package mc.rellox.spawnermeta.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.events.SpawnerChargeEvent;
import mc.rellox.spawnermeta.api.events.SpawnerSwitchEvent;
import mc.rellox.spawnermeta.api.events.SpawnerUpgradeEvent;
import mc.rellox.spawnermeta.api.spawner.ICache;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.SpawnerWarning;
import mc.rellox.spawnermeta.api.view.IUpgrades;
import mc.rellox.spawnermeta.api.view.layout.ILayout;
import mc.rellox.spawnermeta.api.view.layout.ISlot;
import mc.rellox.spawnermeta.api.view.layout.SlotField;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.events.EventRegistry;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.spawner.type.UpgradeType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.text.order.IOrder;
import mc.rellox.spawnermeta.utility.Messagable;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.view.layout.LayoutRegistry;

public final class ActiveUpgrades implements Listener, IUpgrades {
	
	private final ILayout layout;

	private final IGenerator generator;
	private final ICache cache;
	private final ISpawner spawner;
	
	private final List<Player> players;
	private final Inventory v;
	private long pause;
	private boolean enabled;
	
	private boolean active;

	public ActiveUpgrades(IGenerator generator) {
		this.layout = LayoutRegistry.upgrades();
		
		this.generator = generator;
		this.cache = generator.cache();
		this.spawner = generator.spawner();
		
		this.players = new ArrayList<>();
		this.v = layout.create(Language.get("Upgrade-GUI.name",
				"type", cache.type().formated()));
		this.enabled = cache.enabled();
		
		this.active = true;
		
		Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
	}

	@Override
	public IGenerator generator() {
		return generator;
	}
	
	@Override
	public ISpawner spawner() {
		return spawner;
	}
	
	@Override
	public List<Player> viewers() {
		return players;
	}
	
	@Override
	public boolean active() {
		return active;
	}

	@Override
	public void open(Player player) {
		players.add(player);
		player.openInventory(v);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
		update();
	}

	@Override
	public void close() {
		if(active == true) {
			HandlerList.unregisterAll(this);
			active = false;
			generator.close();
		}
		players.forEach(Player::closeInventory);
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		if(event.getInventory().equals(v) == true) event.setCancelled(true);
		Inventory clicked = event.getClickedInventory();
		Player player = (Player) event.getWhoClicked();
		if(clicked == null || clicked.equals(v) == false || players.contains(player) == false) return;
		Messagable m = new Messagable(player);
		try {
			long now = System.currentTimeMillis();
			if(now - pause < 250) return;
			pause = now;
			
			int o = event.getSlot();
			UpgradeType upgrade;
			if(layout.is(o, SlotField.upgrade_stats) == true) {
				if(Settings.settings.spawner_switching == false) return;
				
				SpawnerSwitchEvent call = EventRegistry.call(new SpawnerSwitchEvent(player, generator, !enabled));
				if(call.cancelled() == true) return;
				
				spawner.setEnabled(enabled = !enabled);
				player.playSound(player.getEyeLocation(), enabled
						? Sound.ENTITY_ITEM_FRAME_ADD_ITEM : Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 2f, 1f);
				update();
				return;
			} else if(layout.is(o, SlotField.upgrade_range) == true) upgrade = UpgradeType.RANGE;
			else if(layout.is(o, SlotField.upgrade_delay) == true) upgrade = UpgradeType.DELAY;
			else if(layout.is(o, SlotField.upgrade_amount) == true) upgrade = UpgradeType.AMOUNT;
			else {
				if(Settings.settings.charges_enabled == true
						&& layout.is(o, SlotField.upgrade_charges) == true) {
					if(Settings.settings.charges_ignore_natural == true
							&& cache.natural() == true) return;
					ClickType ct = event.getClick();
					SpawnerType type = cache.type();
					
					int r = Settings.settings.charges_price(type, generator);
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
					
					SpawnerChargeEvent call = EventRegistry.call(new SpawnerChargeEvent(player, generator, price, a));
					if(call.cancelled() == true) return;
					if(call.withdraw(player) == false) return;
					
					int charges = cache.charges() + call.charges;
					spawner.setCharges(charges);
					m.send(Language.list("Upgrade-GUI.charges.purchase", "charges", call.charges));
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
				m.send(Language.list("Upgrade-GUI.disabled-upgrade"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
			if(Settings.settings.natural_can_upgrade == false && cache.natural() == true) {
				if(player.hasPermission("spawnermeta.ownership.bypass.upgrading") == false) {
					m.send(Language.list("Spawners.natural.upgrading.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
			x: if(Settings.settings.owned_can_upgrade == false) {
				UUID owner = generator.spawner().getOwnerID();
				if(owner != null && owner.equals(player.getUniqueId()) == false) {
					if(player.hasPermission("spawnermeta.ownership.bypass.upgrading") == false) {
						if(Settings.settings.trusted_can_upgrade == true
								&& LocationRegistry.trusted(owner, player) == true) break x;
						m.send(Language.list("Spawners.ownership.upgrading.warning"));
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
						return;
					}
				}
			}
			SpawnerType type = cache.type();
			int[] ls = spawner.getUpgradeLevels();
			int[] ms = Settings.settings.upgrades_levels.get(type);
			if(ls[i] < ms[i]) {
				Price price = price(type, ls[i], i);
				
				SpawnerUpgradeEvent call = EventRegistry.call(
						new SpawnerUpgradeEvent(player, generator, upgrade, ls[i] + 1, ms[i], price));
				
				if(price.has(player) == false) {
					price = call.getUnsafePrice();
					m.send(Language.list("Prices.insufficient", 
							"insufficient", price.insufficient(), "price", price.requires(player)));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
				if(call.cancelled() == true || call.withdraw(player) == false) return;
				
				int u = call.upgrade_level;
				ls[i] = u < 1 ? 1 : u > ms[i] ? ms[i] : u;
				
				spawner.setUpgradeLevels(ls);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
				UpgradeType ut = UpgradeType.of(i);
				m.send(Language.list("Upgrade-GUI.purchase." + ut.lower(),
						"level", Utility.roman(ls[i])));
				DustOptions d = new DustOptions(ut.color, 2f);
				player.spawnParticle(Utility.particle_redstone, Utility.center(spawner.block()), 50, 0.25, 0.25, 0.25, 0, d);
				
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
		if(players.isEmpty() == true) close();
		else update();
	}

	@Override
	public void update() {
		generator.update();
		
		layout.fill(v);
		layout.fill(v, stats(), SlotField.upgrade_stats);
		layout.fill(v, upgrade(0, SlotField.upgrade_range), SlotField.upgrade_range);
		layout.fill(v, upgrade(1, SlotField.upgrade_delay), SlotField.upgrade_delay);
		layout.fill(v, upgrade(2, SlotField.upgrade_amount), SlotField.upgrade_amount);
		if(Settings.settings.charges_enabled == true) {
			if(Settings.settings.charges_ignore_natural == true
					&& cache.natural() == true) return;
			layout.fill(v, charges(), SlotField.upgrade_charges);
		}
	}
	
	private ItemStack upgrade(ISlot slot, int i) {
		ItemStack item = slot.toItem();
		ItemMeta meta = item.getItemMeta();
		int[] levels = spawner.getUpgradeLevels();
		int level = levels[i];
		UpgradeType u = UpgradeType.of(i);
		
		List<Content> name = Language.list("Upgrade-GUI.items.upgrade.name." + u.lower(),
				"level", Utility.roman(level));
		if(name.size() > 0) meta.setDisplayName(name.remove(0).text());

		IOrder order = LayoutRegistry.order_upgrade.oderer();
		
		order.named(name);

		SpawnerType type = cache.type();
		int[] max_levels = Settings.settings.upgrades_levels.get(type);
		if(level < max_levels[i]) {
			order.submit("HELP", () -> {
				return Language.list("Upgrade-GUI.items.upgrade.help");
			});
		}
		order.submit("INFO", () -> {
			return Language.list("Upgrade-GUI.items.upgrade.info." + u.lower());
		});
		order.submit("CURRENT", () -> {
			return Language.list("Upgrade-GUI.items.upgrade.current." + u.lower(),
					"value", value(type, level, i));
		});
		if(level < max_levels[i]) {
			order.submit("NEXT", () -> {
				return Language.list("Upgrade-GUI.items.upgrade.next." + u.lower(),
						"value", value(type, level + 1, i));
			});
			order.submit("PRICE", () -> {
				Price price = price(type, level, i);
				return Language.list("Upgrade-GUI.items.upgrade.price",
						"price", price);
			});
		} else {
			order.submit("PRICE", () -> {
				return Language.list("Upgrade-GUI.items.upgrade.maximum-reached");
			});
		}
		
		meta.setLore(order.build());
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack upgrade(int i, SlotField field) {
		ISlot slot = layout.get(field);
		return allowed(i) == true ? upgrade(slot, i) : denied(slot, i);
	}
	
	private ItemStack denied(ISlot slot, int i) {
		UpgradeType u = UpgradeType.of(i);
		ItemStack item = slot.toItem(true);
		ItemMeta meta = item.getItemMeta();
		
		List<Content> name = Language.list("Upgrade-GUI.items.disabled-upgrade.name." + u.lower());
		if(name.size() > 0) meta.setDisplayName(name.remove(0).text());

		IOrder order = LayoutRegistry.order_disabled.oderer();

		order.named(name);
		
		int level = spawner.getUpgradeLevels()[i];
		SpawnerType type = cache.type();
		
		order.submit("HELP", () -> {
			return Language.list("Upgrade-GUI.items.disabled-upgrade.help");
		});
		order.submit("INFO", () -> {
			return Language.list("Upgrade-GUI.items.upgrade.info." + u.lower());
		});
		order.submit("CURRENT", () -> {
			return Language.list("Upgrade-GUI.items.disabled-upgrade.current." + u.lower(),
					"value", value(type, level, i));
		});
		
		meta.setLore(order.build());

		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack stats() {
		ISlot slot = layout.get(SlotField.upgrade_stats);
		if(slot == null) return null;
		ItemStack item = slot.toItem();
		ItemMeta meta = item.getItemMeta();
		
		List<Content> name = Language.list("Upgrade-GUI.items.stats.name",
				"type", cache.type());
		if(name.size() > 0) meta.setDisplayName(name.remove(0).text());
		
		IOrder order = LayoutRegistry.order_stats.oderer();

		order.named(name);

		if(cache.empty() == true) {
			order.submit("EMPTY", () -> Language.list("Upgrade-GUI.items.stats.empty"));
		}
		if(Settings.settings.spawner_switching == true) {
			order.submit("SWITCHING", () -> {
				return enabled == true ? Language.list("Upgrade-GUI.items.stats.enabled")
						: Language.list("Upgrade-GUI.items.stats.disabled");
			});
		}
		int[] l = Utility.location(spawner.block());
		order.submit("LOCATION", () -> {
			return Language.list("Upgrade-GUI.items.stats.location",
					"x", c(l[0]), "y", l[1], "z", c(l[2]));
		});
		int stack = cache.stack();
		if(Settings.settings.stacking_enabled == true) {
			order.submit("STACK", () -> {
				if(Settings.settings.stacking_ignore_limit == true)
					return Language.list("Upgrade-GUI.items.stats.stacking.infinite",
							"stack", stack);
				return Language.list("Upgrade-GUI.items.stats.stacking.finite",
						"stack", stack, "limit", Settings.settings.stacking_limit(null, generator));
			});
		}
		if(Settings.settings.spawnable_enabled == true) {
			int spawnable = cache.spawnable();
			if(spawnable < 1_000_000_000) {
				order.submit("SPAWNABLE", () -> {
					return Language.list("Upgrade-GUI.items.stats.spawnable",
							"spawnable", spawnable);
				});
			}
		}
		if(generator.warned() == true) {
			order.submit("WARNING", () -> {
				List<Content> list = new ArrayList<>();
				long count = Stream.of(SpawnerWarning.values())
						.filter(generator::warned)
						.map(type -> Language
								.list("Upgrade-GUI.items.stats.warnings."
										+ type.name().toLowerCase()))
						.peek(list::addAll)
						.count();
				list.addAll(0, Language
						.list("Upgrade-GUI.items.stats.warnings.header",
								"count", count));
				return list;
			});
		}
		
		if(generator.online() == false) {
			order.submit("OFFLINE", () -> Language.list("Upgrade-GUI.items.stats.owner-offline"));
		}
		
		order.submit("INFO", () -> Language.list("Upgrade-GUI.items.stats.lore"));
		
		meta.setLore(order.build());
		
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
	
	private ItemStack charges() {
		ISlot slot = layout.get(SlotField.upgrade_charges);
		if(slot == null) return null;
		ItemStack item = slot.toItem(false);
		ItemMeta meta = item.getItemMeta();
		int charges = cache.charges();
		boolean b = charges >= 1_000_000_000;
		String charges_text = b ? Text.infinity : "" + charges;
		meta.setDisplayName(Language.get("Upgrade-GUI.items.charges.name",
				"charges", charges_text).text());
		if(b == false) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			int f0 = Settings.settings.charges_buy_first;
			int f1 = Settings.settings.charges_buy_second;
			int r = Settings.settings.charges_price(cache.type(), generator);
			int c0 = r * f0;
			int c1 = r * f1;
			int a = lowestCharges() / r;
			int c2 = r * a;
			lore.addAll(Text.toText(Language.list("Upgrade-GUI.items.charges.purchase.first",
					"charges", f0, "price", Price.of(Group.charges, c0))));
			lore.addAll(Text.toText(Language.list("Upgrade-GUI.items.charges.purchase.second",
					"charges", f1, "price", Price.of(Group.charges, c1))));
			if(a > 0) lore.addAll(Text.toText(Language.list("Upgrade-GUI.items.charges.purchase.all",
					"price", Price.of(Group.charges, c2), "charges", a)));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	private Price price(SpawnerType type, int level, int i) {
		int value = Settings.settings.upgrades_prices.get(type)[i],
				increase = Settings.settings.upgrades_price_increase.get(type)[i];
		for(int j = 1; j < level; j++) value = Settings.settings.upgrade_increase_type.price(value, increase);
		return Price.of(Group.upgrades, value * cache.stack());
	}
	
	private String value(SpawnerType type, int level, int i) {
		int value = Settings.settings.spawner_values.get(type)[i],
				increase = Settings.settings.spawner_value_increase.get(type)[i];
		for(int j = 1; j < level; j++) value += increase;
		return convert(value, i);
	}
	
	private String convert(int v, int i) {
		if(i == 0) {
			if(v <= 0) return "1";
			else if(v > 512) return "512";
			return v + "";
		} else if(i == 1) {
			if(v <= 0) return "0";
			return time(v * 0.05);
		} else if(i == 2) {
			int s = cache.stack();
			if(v <= 0) return s + "";
			else if(v > 4048) return 4048 * s + "";
			return v * s + "";
		} else return v + "";
	}
	
	private String time(double t) {
		if(t == (int) t) return "" + (int) t;
		return String.format("%.1f", t);
	}
	
	private boolean allowed(int i) {
		return Settings.settings.upgrades_upgradeable.get(cache.type())[i];
	}

}
