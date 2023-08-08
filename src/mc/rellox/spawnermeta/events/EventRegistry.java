package mc.rellox.spawnermeta.events;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.APIRegistry;
import mc.rellox.spawnermeta.api.events.IEvent;
import mc.rellox.spawnermeta.api.events.SpawnerBreakEvent;
import mc.rellox.spawnermeta.api.events.SpawnerChangeEvent;
import mc.rellox.spawnermeta.api.events.SpawnerEmptyEvent;
import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent;
import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent.ExplosionType;
import mc.rellox.spawnermeta.api.events.SpawnerOpenEvent;
import mc.rellox.spawnermeta.api.events.SpawnerPlaceEvent;
import mc.rellox.spawnermeta.api.events.SpawnerStackEvent;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.LocationFile.LF;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.items.ItemCollector;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Messagable;
import mc.rellox.spawnermeta.utility.Utils;

public final class EventRegistry {
	
	private static long time, chunk;
	
	private static Block verify;
	
	public static APIInstance getAPI() {
		return SpawnerMeta.instance().getAPI();
	}
	
	public static boolean registered() {
		return ((APIRegistry) getAPI()).registered();
	}
	
	public static <E extends IEvent> E call(E event) {
		APIRegistry api = (APIRegistry) getAPI();
		api.execute(event);
		return event;
	}

	protected static void open_upgrades(Player player, Messagable m, Block block, ISpawner spawner) {
		if(Settings.settings.upgrade_interface_enabled == false) return;
		DataManager.recalculate(block);
		if(player.hasPermission("spawnermeta.upgrades.open") == false) {
			m.send(Language.list("Spawners.upgrades.permission.opening"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.owned_can_open == false && spawner.isOwner(player, true) == false) {
			if(player.hasPermission("spawnermeta.ownership.bypass.interact") == false) {
				m.send(Language.list("Spawners.ownership.opening.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(Settings.settings.natural_can_open == false && spawner.isNatural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.interact") == false) {
				m.send(Language.list("Spawners.natural.opening.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		
		SpawnerOpenEvent call = call(new SpawnerOpenEvent(player, block));
		if(call.cancelled() == true) return;
		
		GeneratorRegistry.get(block).open(player);
	}

	protected static void changing_regular(Player player, Messagable m, Block block, ISpawner spawner, SpawnerType type,
			ItemStack item, SpawnerType change) {
		if(Settings.settings.changing_enabled == false) return;
		if(change.unique() == true && !(player.isOp() == true && player.getGameMode() == GameMode.CREATIVE)) return;
		if(player.hasPermission("spawnermeta.eggs") == false) {
			m.send(Language.list("Spawners.changing.permission"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.natural_can_change == false && spawner.isNatural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.changing") == false) {
				m.send(Language.list("Spawners.natural.changing.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(Settings.settings.owned_can_change == false && spawner.isOwner(player, true) == false) {
			if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
				m.send(Language.list("Spawners.ownership.changing.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(type == change) {
			m.send(Language.list("Spawners.changing.same-type"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.changing_deny_from.contains(type) == true) {
			m.send(Language.list("Spawners.changing.dany.from"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.changing_deny_to.contains(change) == true) {
			m.send(Language.list("Spawners.changing.dany.to"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		int stack = spawner.getStack();
		if(ItemMatcher.has(player, item, stack) == false) {
			m.send(Language.list("Spawners.changing.eggs.insufficient", "required", stack));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		Price price = null;
		if(Settings.settings.changing_price.using() == true)
			price = Price.of(Group.changing, Settings.settings.changing_price.get(type) * stack);
		
		SpawnerChangeEvent call = call(new SpawnerChangeEvent(player, block, price, change, false));
		if(call.cancelled() == true) return;
		
		if(call.withdraw(player) == false) {
			price = call.getUnsafePrice();
			m.send(Language.list("Prices.insufficient", 
					"insufficient", price.insufficient(), "price", price.requires(player)));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		m.send(Language.list("Spawners.changing.type-changed", "type", call.getNewType()));
		player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2f, 2f);
		player.spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation().add(0.5, 0.5, 0.5), 25, 0.25, 0.25, 0.25, 0.1);
		ItemMatcher.remove(player, item, stack);
		spawner.setType(change);
		spawner.update();
		
		GeneratorRegistry.update(block);
	}

	protected static void changing_empty(Player player, Messagable m, Block block, ISpawner spawner, SpawnerType type, ItemStack item,
			SpawnerType change) {
		if(change.unique() == true && Utils.op(player) == false) return;
		if(type != SpawnerType.EMPTY) return;
		if(Settings.settings.owned_can_change == false && spawner.isOwner(player, true) == false) {
			if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
				m.send(Language.list("Spawners.ownership.changing.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		int stack = spawner.getStack();
		if(ItemMatcher.has(player, item, stack) == false) {
			m.send(Language.list("Spawners.changing.eggs.insufficient", "required", stack));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.changing_deny_to.contains(change) == true) {
			m.send(Language.list("Spawners.changing.dany.to"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		Price price = null;
		if(Settings.settings.changing_price.using() == true)
			price = Price.of(Group.changing, Settings.settings.changing_price.get(type) * stack);

		SpawnerChangeEvent call = call(new SpawnerChangeEvent(player, block, price, change, true));
		if(call.cancelled() == true) return;
		
		if(call.withdraw(player) == false) {
			price = call.getUnsafePrice();
			m.send(Language.list("Prices.insufficient", 
					"insufficient", price.insufficient(), "price", price.requires(player)));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		m.send(Language.list("Spawners.changing.type-changed", "type", change));
		player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2f, 2f);
		player.spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation().add(0.5, 0.5, 0.5), 25, 0.25, 0.25, 0.25, 0.1);
		
		ItemMatcher.remove(player, item, stack);
		spawner.setType(change);
		spawner.update();
		
		GeneratorRegistry.update(block);
	}
	
	protected static boolean remove_eggs_empty(PlayerInteractEvent event, Player player, Messagable m, Block block, SpawnerType type, ItemStack item, ISpawner spawner) {
		if(spawner.isEmpty() == false) return true;
		if(item != null && item.getType() == Material.SPAWNER) return true;
		event.setCancelled(true);
		if(Settings.settings.empty_enabled == false) {
			m.send(Language.list("Spawners.empty.disabled"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(player.isSneaking() == false) {
			if(type == SpawnerType.EMPTY) {
				m.send(Language.list("Spawners.empty.try-open"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return false;
			}
			return true;
		}
		if(type == SpawnerType.EMPTY) return true;
		if(Settings.settings.owned_can_change == false && spawner.isOwner(player, true) == false) {
			if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
				m.send(Language.list("Spawners.ownership.changing.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return false;
			}
		}
		boolean b = Utils.nulled(player.getInventory().getItemInMainHand()) == false;
		if(b == true) {
			m.send(Language.list("Spawners.empty.hand-full"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(Settings.settings.empty_verify_removing == true) {
			verify = block;
			new BukkitRunnable() {
				@Override
				public void run() {
					if(block.equals(verify) == true) {
						verify = null;
						player.spawnParticle(Particle.REDSTONE, Utils.center(block).add(0, 0.52, 0), 5, 0.1, 0.1, 0.1, 0,
								new DustOptions(Color.MAROON, 2f));
					}
				}
			}.runTaskLater(SpawnerMeta.instance(), 20);
			m.send(Language.list("Spawners.empty.verify-removing.first"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 0f);
			return false;
		}
		remove_eggs(player, block, type);
		return false;
	}

	protected static void stack_nearby(PlayerInteractEvent event, Player player, Messagable m, Block block) {
		if(Settings.settings.stacking_nearby_enabled == false) return;
		ItemStack item = event.getItem();
		if(item == null || item.getType() != Material.SPAWNER
				|| player.isSneaking() == false) return;
		event.setCancelled(true);
		IVirtual data = IVirtual.of(item);
		if(data == null) return;
		final int r = Settings.settings.stacking_nearby_radius;
		Block valid = null;
		int x = -r, y, z;
		f: do {
			y = -r;
			do {
				z = -r;
				do {
					Block rel = block.getRelative(x, y, z);
					if(rel.getType() != Material.SPAWNER) continue;
					IVirtual of = IVirtual.of(rel);
					if(of == null) continue;
					if(data.exact(of) == true) {
						valid = rel;
						break f;
					}
				} while(++z <= r);
			} while(++y <= r);
		} while(++x <= r);
		if(valid == null) {
			m.send(Language.list("Spawners.stacking.nearby.none-match"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		stacking(player, m, valid, ISpawner.of(valid), item, false);
	}

	protected static void verify_removing(PlayerInteractEvent event, Player player, Messagable m) {
		if(Settings.settings.empty_verify_removing == false) return;
		Block block = event.getClickedBlock();
		if(player.isSneaking() == false || block.getType() != Material.SPAWNER) return;
		ISpawner spawner = getAPI().getSpawner(block);
		SpawnerType type = spawner.getType();
		if(type == SpawnerType.EMPTY || spawner.isEmpty() == false) return;
		boolean b = Utils.nulled(player.getInventory().getItemInMainHand()) == false;
		if(b == false && Utils.op(player) == true) event.setCancelled(true);
		if(verify == null) {
			m.send(Language.list("Spawners.empty.verify-removing.try-again"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(b == true) {
			m.send(Language.list("Spawners.empty.hand-full"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		remove_eggs(player, block, type);
	}

	protected static boolean stacking(Player player, Messagable m, Block block,
			ISpawner spawner, ItemStack item, boolean direct) {
		int stack = spawner.getStack();
		if(Settings.settings.stacking_ignore_limit == false) {
			if(stack >= Settings.settings.stacking_spawner_limit) {
				m.send(Language.list("Spawners.stacking.limit-reached"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return true;
			}
		}
		int tt = Settings.settings.stacking_ticks;
		if(tt > 0) {
			long b = System.currentTimeMillis() / 50;
			if(time >= b - tt) return false;
			time = b;
		}
		IVirtual data = IVirtual.of(item);
		if(data == null) return false;
		if(player.hasPermission("spawnermeta.stacking") == false) {
			m.send(Language.list("Spawners.stacking.permission"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(Settings.settings.natural_can_stack == false && spawner.isNatural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.stacking") == false) {
				m.send(Language.list("Spawners.natural.breaking.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return false;
			}
		}
		if(Settings.settings.owned_can_stack == false && spawner.isOwner(player, true) == false) {
			if(player.hasPermission("spawnermeta.ownership.bypass.stacking") == false) {
				m.send(Language.list("Spawners.ownership.stacking.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return false;
			}
		}
		IVirtual other = IVirtual.of(block);
		if(data.exact(other) == false) {
			m.send(Language.list("Spawners.stacking.unequal-spawner"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(Settings.settings.owned_ignore_limit == false) {
			int p = LF.placed(player);
			if(p >= Settings.settings.owned_spawner_limit) {
				m.send(Language.list("Spawners.ownership.limit.reached",
						"limit", Settings.settings.owned_spawner_limit));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return false;
			}
		}
		Price price = null;
		if(Settings.settings.stacking_price.using() == true)
			price = Price.of(Group.stacking, Settings.settings.stacking_price.get(spawner.getType()));

		SpawnerStackEvent call = call(new SpawnerStackEvent(player, block, price, data, direct));
		if(call.cancelled() == true) return false;
		
		if(call.withdraw(player) == false) {
			price = call.getUnsafePrice();
			m.send(Language.list("Prices.insufficient", 
					"insufficient", price.insufficient(), "price", price.requires(player)));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		stack++;
		spawner.setStack(stack);
		m.send((Settings.settings.stacking_ignore_limit
				? Language.list("Spawners.stacking.stacked.infinite", "stack", stack)
						: Language.list("Spawners.stacking.stacked.finite", "stack", stack,
								"limit", Settings.settings.stacking_spawner_limit)));
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.75f, 1.5f);
		if(Settings.settings.spawnable_enabled == true) {
			int s = spawner.getSpawnable() + data.getSpawnable();
			spawner.setSpawnable(s);
		}
		if(Settings.settings.charges_enabled == true
				&& Settings.settings.charges_allow_stacking == true) {
			int b = spawner.getCharges() * (stack - 1) + data.getCharges();
			int r = b / stack;
			spawner.setCharges(r);
			int f = b % stack;
			if(f > 0)
				m.send(Language.list("Spawners.charges.lose-by-stacking", 
						"charges", f));
		}
		if(direct == false && Settings.settings.stacking_nearby_particles == true) {
			Location start = block.getLocation().add(0.5, 0.5, 0.5);
			Location end = player.getLocation().add(0, 1, 0);
			Vector dis = end.toVector().subtract(start.toVector())
					.normalize().multiply(0.25);
			double loops = start.distance(end) / 0.25;
			for(int i = 0; i < loops; i++) {
				start.add(dis);
				player.spawnParticle(Particle.CRIT_MAGIC, start, 1, 0, 0, 0, 0);
			}
		}
		ItemMatcher.remove(player, item, 1);
		LF.add(block, player);
		
		GeneratorRegistry.update(block);
		return false;
	}

	protected static void interact(PlayerInteractEvent event, Player player, Messagable m, Block block, ISpawner spawner) {
		if(DataManager.isItemSpawner(block) == true) return;
		SpawnerType type = spawner.getType();
		if(type.disabled() == true) return;
		event.setCancelled(false);
		ItemStack item = event.getItem();
		
		if(EventRegistry.remove_eggs_empty(event, player, m, block, type, item, spawner) == false) return;
		
		if(item != null) {
			if(player.isSneaking() == true) {
				y: if(Settings.settings.stacking_enabled == true) {
					/*
					 * Stacking
					 */
					if(item.getType() != Material.SPAWNER) break y;
					event.setCancelled(true);
					if(EventRegistry.stacking(player, m, block, spawner, item, true) == true) break y;
					return;
				}
				SpawnerType change = SpawnerType.of(item.getType());
				if(change == null) {
					if(item.getType().name().endsWith("_EGG") == true) event.setCancelled(true);
					return;
				}
				event.setCancelled(true);
				if(change.disabled() == true) return;
				if(spawner.isEmpty() == true) {
					/*
					 * Empty spawner type changing
					 */
					EventRegistry.changing_empty(player, m, block, spawner, type, item, change);
				} else {
					/*
					 * Regular spawner type changing
					 */
					EventRegistry.changing_regular(player, m, block, spawner, type, item, change);
				}
				return;
			} else if(item.getType().name().endsWith("_EGG") == true) event.setCancelled(true);
		}
		if(spawner.isEmpty() == true && type == SpawnerType.EMPTY) return;
		if(item != null && item.getType() == Material.SPAWNER) return;
		if(item != null) {
			if(item.getType() == Material.SPAWNER) return;
//			Checks if player tries to open spawner with a block placement
//			if(Settings.settings.cancel_placement_when_opening == false && event.isBlockInHand() == true) return;
		}
		EventRegistry.open_upgrades(player, m, block, spawner);
		event.setCancelled(true);
	}

	protected static void remove_eggs(Player player, Block block, SpawnerType type) {
		ISpawner spawner = getAPI().getSpawner(block);
		
		ItemStack refund = null;
		if(Settings.settings.empty_destroy_eggs_removing == false) {
			Material m = type.material();
			if(m != null) refund = new ItemStack(m, spawner.getStack());
		}

		SpawnerEmptyEvent call = call(new SpawnerEmptyEvent(player, block, refund));
		if(call.cancelled() == true) return;

		call.getRefund().ifPresent(i -> player.getInventory().setItemInMainHand(i));
		
		player.playSound(player.getEyeLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1f);
		player.spawnParticle(Particle.FIREWORKS_SPARK, block.getLocation().add(0.5, 0.5, 0.5), 25, 0.3, 0.3, 0.3, 0.1);
		spawner.setType(SpawnerType.EMPTY);
		spawner.setRotating(false);
		
		GeneratorRegistry.update(block);
		
		if(block.equals(verify) == true) verify = null;
	}

	public static void breaking(BlockBreakEvent event, Block block) {
		ISpawner spawner = getAPI().getSpawner(block);
		SpawnerType type = spawner.getType();
		boolean ce = Settings.settings.cancel_break_event;
		event.setCancelled(true);
		if(type.disabled() == true) return;
		Player player = event.getPlayer();
		Messagable m = new Messagable(player);
		
		if(Utils.op(player) == true) {
			Location bl = block.getLocation().add(0.5, 0.5, 0.5);
			DataManager.getSpawners(block, false).forEach(item -> {
				player.getWorld().dropItem(bl, item).setVelocity(new Vector());
			});
			player.spawnParticle(Particle.CLOUD, bl, 25, 0.25, 0.25, 0.25, 0);
			player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.35f, 0f);
			m.send(Language.list("Spawners.breaking.success"));
			event.setCancelled(false);
			
			dropAfterChanging(player, block);
			
			GeneratorRegistry.remove(block);
			
			LF.remove(block);
			return;
		}
		
		if(Settings.settings.unbreakable == true) {
			if(player.hasPermission("spawnermeta.unbreakable.bypass") == false) {
				m.send(Language.list("Spawners.breaking.permission"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(Settings.settings.ignore_permission == false) {
			if(player.hasPermission("spawnermeta.break") == false) {
				m.send(Language.list("Spawners.breaking.permission"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(Settings.settings.natural_can_break == false && spawner.isNatural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.breaking") == false) {
				m.send(Language.list("Spawners.natural.breaking.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(Settings.settings.owned_can_break == false && spawner.isOwner(player, true) == false) {
			if(player.hasPermission("spawnermeta.ownership.bypass.breaking") == false) {
				m.send(Language.list("Spawners.ownership.breaking.warning"));
				if(Settings.settings.breaking_show_owner == true) {
					var id = spawner.getOwnerID();
					if(id != null) {
						OfflinePlayer off = Bukkit.getOfflinePlayer(id);
						var name = off.getName();
						if(name != null) m.send(Language.list("Spawners.ownership.show-owner",
								"player", name));
					}
				}
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(Settings.settings.breaking_drop_on_ground == false && ItemCollector.exists(player) == true) {
			m.send(Language.list("Items.spawner-drop.try-breaking"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		boolean silk = Settings.settings.has_silk(player) == true;
		if(Settings.settings.breaking_silk_enabled == true
				&& Settings.settings.breaking_silk_destroy == false
				&& silk == false) {
			if(player.hasPermission("spawnermeta.breaking.bypass.silktouch") == false) {
				m.send(Language.list("Spawners.breaking.failure"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		Location bl = block.getLocation().add(0.5, 0.5, 0.5);
		Price price = null;
		if(Settings.settings.breaking_price.using() == true)
			price = Price.of(Group.breaking, Settings.settings.breaking_price.get(type) * spawner.getStack());

		double chance = Settings.settings.breaking_chance(player);

		if(spawner.isOwned() == true) {
			if(spawner.isOwner(player) == true)
				chance = Settings.settings.breaking_chance_changer_owned.change(chance);
			else
				chance = Settings.settings.breaking_chance_changer_not_owned.change(chance);
		} else
			chance = Settings.settings.breaking_chance_changer_natural.change(chance);
		
		SpawnerBreakEvent call = EventRegistry.call(new SpawnerBreakEvent(player, block, price, chance));
		if(call.cancelled() == true) return;

		if(call.withdraw(player) == false) {
			price = call.getUnsafePrice();
			m.send(Language.list("Prices.insufficient", 
					"insufficient", price.insufficient(), "price", price.requires(player)));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		List<Content> mm;
		if(Utils.chance(call.chance) == true) {
			boolean give = true;
			if(Settings.settings.breaking_silk_enabled == true) {
				give = Settings.settings.has_silk(player) == true;
				if(spawner.isOwned() == true) give &= Settings.settings.breaking_silk_break_owned;
				else give &= Settings.settings.breaking_silk_break_natural;
			}
			if(player.getGameMode() == GameMode.CREATIVE) give = true;
			if(give == true) {
				DataManager.getSpawners(block, false).forEach(item -> {
					if(Settings.settings.breaking_drop_on_ground == true)
						player.getWorld().dropItem(bl, item).setVelocity(new Vector());
					else ItemCollector.add(player, item);
				});
				player.spawnParticle(Particle.CLOUD, bl, 25, 0.25, 0.25, 0.25, 0);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.35f, 0f);
				mm = Language.list("Spawners.breaking.success");
			} else {
				player.spawnParticle(Particle.SQUID_INK, bl, 25, 0.25, 0.25, 0.25, 0.1);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.2f, 0f);
				mm = Language.list("Spawners.breaking.failure");
				spawnXP(block, event, give);
			}
		} else {
			player.spawnParticle(Particle.SQUID_INK, bl, 25, 0.25, 0.25, 0.25, 0.1);
			player.playSound(player.getEyeLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.35f, 0f);
			mm = Language.list("Spawners.breaking.failure");
			spawnXP(block, event, ce);
		}
		m.send(mm);
		if(Settings.settings.breaking_durability_enabled == true) {
			if(player.getGameMode() != GameMode.CREATIVE) {
				ItemStack item = player.getInventory().getItemInMainHand();
				ItemMeta meta = item.getItemMeta();
				if(meta instanceof Damageable damageable) {
					int d = damageable.getDamage() + Settings.settings.breaking_durability_to_remove;
					if(d >= item.getType().getMaxDurability()) {
						player.getInventory().setItemInMainHand(null);
						player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 2f, 1f);
					} else damageable.setDamage(d);
					item.setItemMeta(meta);
				}
			}
		}
		dropAfterChanging(player, block);
		LF.remove(block);
		if(ce == true) block.setType(Material.AIR);
		
		GeneratorRegistry.remove(block);
		
		ItemCollector.execute(player);
		event.setCancelled(ce);
	}

	private static void spawnXP(Block block, BlockBreakEvent event, boolean ce) {
		event.setExpToDrop(0);
		int xp = Settings.settings.breaking_xp_on_failure;
		if(xp > 0) {
			if(ce == true) spawnXP(block, xp);
			else event.setExpToDrop(xp);
		}
	}

	public static void spawnXP(Block block, int xp) {
		ExperienceOrb orb = (ExperienceOrb) block.getWorld()
				.spawnEntity(block.getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
		orb.setExperience(xp);
	}

	protected static void explode_block(Iterator<Block> it) {
		Block block;
		while(it.hasNext() == true && (block = it.next()) != null) {
			if(block.getType() != Material.SPAWNER) continue;
			BlockState bs = block.getState();
			if(bs instanceof CreatureSpawner == false) continue;
			boolean[] xs = Settings.settings.explosion_types.get(ExplosionType.TNT);

			SpawnerExplodeEvent call = call(new SpawnerExplodeEvent(block, ExplosionType.TNT, xs));
			if(call.cancelled() == true) {
				it.remove();
				continue;
			}
			
			if(DataManager.isPlaced(block) == true) {
				if(xs[0] == true) destroy(block, xs[1]);
			} else {
				if(xs[2] == true) destroy(block, xs[3]);
			}
			block.getWorld().spawnParticle(Particle.VILLAGER_ANGRY,
					block.getLocation().add(0.5, 0.5, 0.5), 10, 0.2, 0.2, 0.2, 0);
			it.remove();
		}
	}
	
	public static boolean destroy(Block block, boolean drop) {
		return destroy(block, drop, true);
	}
	
	public static boolean destroy(Block block, boolean drop, boolean particles) {
		if(block == null || block.getType() != Material.SPAWNER) return false;
		Location loc = block.getLocation().add(0.5, 0.5, 0.5);
		if(drop == true) {
			DataManager.getSpawners(block, false).forEach(item -> {
				loc.getWorld().dropItem(loc, item).setVelocity(new Vector());
			});
			dropAfterChanging(null, block);
		}
		LF.remove(block);
		block.setType(Material.AIR);
		
		GeneratorRegistry.remove(block);
		
		if(particles == true) loc.getWorld().spawnParticle(Particle.CLOUD, loc, 25, 0.25, 0.25, 0.25, 0);
		return true;
	}
	
	public static void dropAfterChanging(Player player, Block block) {
		if(Settings.settings.empty_destroy_eggs_breaking == true
				|| Settings.settings.empty_store_inside == true) {
			block.getWorld().spawnParticle(Particle.CRIT, Utils.center(block), 10, 0, 0, 0, 0.1);
			return;
		}
		ISpawner spawner = ISpawner.of(block);
		SpawnerType type = spawner.getType();
		if(spawner.isEmpty() == true && type != SpawnerType.EMPTY) {
			if(type.unique() == false) {
				Material mat = type.material();
				if(mat != null) {
					int s = spawner.getStack();
					if(Settings.settings.breaking_drop_on_ground == true) {
						ItemStack item = new ItemStack(mat, s);
						block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), item)
							.setVelocity(new Vector());
					} else if(player != null) {
						while(s > 0) {
							ItemStack item = new ItemStack(mat, s >= 64 ? 64 : s);
							ItemCollector.add(player, item);
							s -= 64;
						}
					}
				}
			}
		}
	}

	protected static void explode_entity(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		Iterator<Block> it = event.blockList().iterator();
		Block block;
		while(it.hasNext() == true && (block = it.next()) != null) {
			if(block.getType() != Material.SPAWNER) continue;
			BlockState bs = block.getState();
			if(bs instanceof CreatureSpawner == false) continue;
			ExplosionType explosion;
			boolean[] xs;
			if(entity instanceof TNTPrimed) {
				xs = Settings.settings.explosion_types.get(explosion = ExplosionType.TNT);
			} else if(entity instanceof Creeper) {
				xs = Settings.settings.explosion_types.get(explosion = ExplosionType.CREEPERS);
			} else if(entity instanceof Fireball || entity instanceof LargeFireball) {
				xs = Settings.settings.explosion_types.get(explosion = ExplosionType.FIREBALLS);
			} else if(entity instanceof EnderCrystal) {
				xs = Settings.settings.explosion_types.get(explosion = ExplosionType.END_CRYSTALS);
			} else continue;
			
			SpawnerExplodeEvent call = call(new SpawnerExplodeEvent(block, explosion, xs));
			if(call.cancelled() == true) {
				it.remove();
				continue;
			}
			
			if(xs != null) {
				if(DataManager.isPlaced(block) == true) {
					if(xs[0] == true) destroy(block, xs[1]);
				} else {
					if(xs[2] == true) destroy(block, xs[3]);
				}
			}
			block.getWorld().spawnParticle(Particle.VILLAGER_ANGRY,
					block.getLocation().add(0.5, 0.5, 0.5), 10, 0.2, 0.2, 0.2, 0);
			it.remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onEntityDamaged(EntityDamageEvent event) {
		DamageCause cause = event.getCause();
		if(cause != DamageCause.BLOCK_EXPLOSION && cause != DamageCause.ENTITY_EXPLOSION) return;
		Entity entity = event.getEntity();
		if(entity instanceof Item drop) {
			ItemStack item = drop.getItemStack();
			if(item == null || item.getType() != Material.SPAWNER) return;
			event.setCancelled(true);
		}
	}

	protected static void place(BlockPlaceEvent event, Block block) {
		Player player = event.getPlayer();
		Messagable m = new Messagable(player);
		ItemStack item = event.getItemInHand().clone();
		IVirtual data = IVirtual.of(item);
		if(data == null) return;
		event.setCancelled(true);
		SpawnerType type = data.getType();
		if(type.disabled() == true) return;
		if(data.isEmpty() == true && Settings.settings.empty_enabled == false) {
			m.send(Language.list("Spawners.empty.disabled"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(player.hasPermission("spawnermeta.place") == false) {
			m.send(Language.list("Spawners.placing.permission"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		x: if(Settings.settings.chunk_enabled == true) {
			int sa = spawnersInChunk(block);
			if(sa <= Settings.settings.chunk_limit) break x;

			long b = System.currentTimeMillis() / 50;
			if(chunk >= b - 20) return;
			chunk = b;
			
			m.send(Language.list("Spawners.chunks.limit-reached"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.owned_ignore_limit == false) {
			if(player.hasPermission("spawnermeta.ownership.bypass.limit") == false) {
				int p = LF.placed(player);
				if(p >= Settings.settings.owned_spawner_limit) {
					m.send(Language.list("Spawners.ownership.limit.reached",
							"limit", Settings.settings.owned_spawner_limit));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
		}
		Price price = null;
		if(Settings.settings.placing_price.using() == true)
			price = Price.of(Group.placing, Settings.settings.placing_price.get(type));
		
		SpawnerPlaceEvent call = call(new SpawnerPlaceEvent(player, block, price, data));
		if(call.cancelled() == true) return;

		if(call.withdraw(player) == false) {
			price = call.getUnsafePrice();
			m.send(Language.list("Prices.insufficient", 
					"insufficient", price.insufficient(), "price", price.requires(player)));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		event.setCancelled(false);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(block.getType() != Material.SPAWNER) return;
				place(block, player, data);
			}
		}.runTaskLater(SpawnerMeta.instance(), 1);
	}
	
	public static int spawnersInChunk(Block block) {
		return spawnersInChunk(block.getChunk());
	}
	
	public static int spawnersInChunk(Chunk chunk) {
		BlockState[] bs = chunk.getTileEntities();
		if(bs == null) return 0;
		return (int) Stream.of(bs)
				.filter(s -> s instanceof CreatureSpawner)
				.count();
	}

	public static boolean place(Block block, Player player, IVirtual virtual) {
		if(block == null || virtual == null) return false;
		if(block.getType() != Material.SPAWNER) block.setType(Material.SPAWNER);
		
		int[] l = virtual.getUpgradeLevels();
		SpawnerType type = virtual.isEmpty() == true && Settings.settings.empty_store_inside == false
				? SpawnerType.EMPTY : virtual.getType();
		DataManager.setNewSpawner(player, block, type,
				l, virtual.getCharges(), virtual.getSpawnable(), virtual.isEmpty());
		DataManager.setPlaced(block);
		if(player != null) {
			LF.add(block, player);
			if(Settings.settings.owned_ignore_limit == false)
				player.sendMessage(Language.get("Spawners.ownership.limit.place",
						"placed", LF.placed(player), "limit", Settings.settings.owned_spawner_limit).text());
		}
		
		GeneratorRegistry.put(block);
		
		return true;
	}

	protected static void spawn(SpawnerSpawnEvent event, Entity entity) {
		if(Settings.settings.spawning == false) {
			event.setCancelled(true);
			return;
		}
		if(entity.getType() == EntityType.DROPPED_ITEM) {
			if(Settings.settings.disable_item_spawners == true) {
				event.setCancelled(true);
				if(entity.isDead() == false) entity.remove();
			} else event.setCancelled(false);
			return;
		}
		event.setCancelled(Settings.settings.cancel_spawning_event);
		if(entity.isDead() == false) entity.remove();
		
		if(ISpawner.of(event.getSpawner().getBlock()).getType().disabled() == true) {
			event.setCancelled(true);
			return;
		}
		// creates new generator if it doesn't exit yet
		GeneratorRegistry.get(event.getSpawner().getBlock());
	}

}
