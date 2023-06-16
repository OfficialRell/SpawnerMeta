package mc.rellox.spawnermeta.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotWorldConfig;

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
import mc.rellox.spawnermeta.api.events.SpawnerPostSpawnEvent;
import mc.rellox.spawnermeta.api.events.SpawnerPreSpawnEvent;
import mc.rellox.spawnermeta.api.events.SpawnerStackEvent;
import mc.rellox.spawnermeta.api.spawner.Spawner;
import mc.rellox.spawnermeta.api.spawner.VirtualSpawner;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.LocationFile.LF;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.holograms.HologramRegistry;
import mc.rellox.spawnermeta.items.ItemCollector;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.SpawnerManager;
import mc.rellox.spawnermeta.spawner.SpawnerSpawning;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.spawner.UpgradeType;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.utils.DataManager;
import mc.rellox.spawnermeta.utils.EntityBox;
import mc.rellox.spawnermeta.utils.Messagable;
import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Reflections.RF.Invoker;
import mc.rellox.spawnermeta.utils.Utils;
import mc.rellox.spawnermeta.views.SpawnerUpgrade;

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

	protected static void open_upgrades(Player player, Messagable m, Block block, Spawner spawner) {
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
		
		SpawnerUpgrade.newUpgrade(player, block);
		HologramRegistry.update(block);
	}

	protected static void changing_regular(Player player, Messagable m, Block block, Spawner spawner, SpawnerType type,
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
		HologramRegistry.update(block);
		unlink(block);
	}

	protected static void changing_empty(Player player, Messagable m, Block block, Spawner spawner, SpawnerType type, ItemStack item,
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
		HologramRegistry.update(block);
		unlink(block);
	}
	
	protected static boolean remove_eggs_empty(Player player, Messagable m, Block block, SpawnerType type, ItemStack item, Spawner spawner) {
		if(spawner.isEmpty() == false) return true;
		if(item != null && item.getType() == Material.SPAWNER) return true;
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
		VirtualSpawner data = VirtualSpawner.of(item);
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
					VirtualSpawner of = VirtualSpawner.of(rel);
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
		stacking(player, m, valid, Spawner.of(valid), item, false);
	}

	protected static void verify_removing(PlayerInteractEvent event, Player player, Messagable m) {
		if(Settings.settings.empty_verify_removing == false) return;
		Block block = event.getClickedBlock();
		if(player.isSneaking() == false || block.getType() != Material.SPAWNER) return;
		Spawner spawner = getAPI().getSpawner(block);
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
			Spawner spawner, ItemStack item, boolean direct) {
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
		VirtualSpawner data = VirtualSpawner.of(item);
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
		VirtualSpawner other = VirtualSpawner.of(block);
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
		SpawnerUpgrade.update(block);
		HologramRegistry.update(block);
		return false;
	}

	protected static void interact(PlayerInteractEvent event, Player player, Messagable m, Block block, Spawner spawner) {
		if(DataManager.isItemSpawner(block) == true) return;
		SpawnerType type = spawner.getType();
		if(type.disabled() == true) return;
		event.setCancelled(true);
		ItemStack item = event.getItem();
		
		if(EventRegistry.remove_eggs_empty(player, m, block, type, item, spawner) == false) return;
		
		if(item != null) {
			if(player.isSneaking() == true) {
				y: if(Settings.settings.stacking_enabled == true) {
					
					/*
					 * Stacking
					 */
					
					if(item.getType() != Material.SPAWNER) {
						event.setCancelled(false);
						break y;
					}
					if(EventRegistry.stacking(player, m, block, spawner, item, true) == true) break y;
					return;
				}
				SpawnerType change = SpawnerManager.fromEgg(item.getType());
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
		if(item != null && item.getType() == Material.SPAWNER) {
			event.setCancelled(false);
			return;
		}
		EventRegistry.open_upgrades(player, m, block, spawner);
	}

	protected static void remove_eggs(Player player, Block block, SpawnerType type) {
		Spawner spawner = getAPI().getSpawner(block);
		
		ItemStack refund = null;
		if(Settings.settings.empty_destroy_eggs_removing == false) {
			Material m = type.changer();
			if(m != null) refund = new ItemStack(m, spawner.getStack());
		}

		SpawnerEmptyEvent call = call(new SpawnerEmptyEvent(player, block, refund));
		if(call.cancelled() == true) return;

		call.getRefund().ifPresent(i -> player.getInventory().setItemInMainHand(i));
		
		player.playSound(player.getEyeLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1f);
		player.spawnParticle(Particle.FIREWORKS_SPARK, block.getLocation().add(0.5, 0.5, 0.5), 25, 0.3, 0.3, 0.3, 0.1);
		spawner.setType(SpawnerType.EMPTY);
		spawner.setRotating(false);
		HologramRegistry.update(block);
		unlink(block);
		
		if(block.equals(verify) == true) verify = null;
	}

	public static void breaking(BlockBreakEvent event, Block block) {
		Spawner spawner = getAPI().getSpawner(block);
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
			
			SpawnerUpgrade.removeUpgrade(block);
			SpawnerManager.dropEggs(player, block);
			HologramRegistry.remove(block);
			unlink(block);
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
			m.send(Language.list("Spawners.breaking.failure"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
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
		SpawnerUpgrade.removeUpgrade(block);
		SpawnerManager.dropEggs(player, block);
		LF.remove(block);
		if(ce == true) block.setType(Material.AIR);
		unlink(block);
		HologramRegistry.remove(block);
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
				if(xs[0] == true) SpawnerManager.breakSpawner(block, xs[1]);
			} else {
				if(xs[2] == true) SpawnerManager.breakSpawner(block, xs[3]);
			}
			block.getWorld().spawnParticle(Particle.VILLAGER_ANGRY,
					block.getLocation().add(0.5, 0.5, 0.5), 10, 0.2, 0.2, 0.2, 0);
			it.remove();
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
					if(xs[0] == true) SpawnerManager.breakSpawner(block, xs[1]);
				} else {
					if(xs[2] == true) SpawnerManager.breakSpawner(block, xs[3]);
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
		VirtualSpawner data = VirtualSpawner.of(item);
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
			int sa = SpawnerManager.getChunkSpawnerAmount(block);
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
				SpawnerManager.placeSpawner(block, player, data);
			}
		}.runTaskLater(SpawnerMeta.instance(), 1);
	}
	
	private static final Set<Block> SET = new HashSet<>(4);

	protected static void spawn(SpawnerSpawnEvent event, Entity entity) {
		if(Settings.settings.disable_spawning == true) return;
		if(entity.getType() == EntityType.DROPPED_ITEM) {
			if(Settings.settings.disable_item_spawners == true) {
				event.setCancelled(true);
				if(entity.isDead() == false) entity.remove();
			} else {
				event.setCancelled(false);
				return;
			}
			return;
		}
		CreatureSpawner cs = event.getSpawner();
		Block block = event.getSpawner().getBlock();
		Spawner spawner = getAPI().getSpawner(block);
		
		SpawnerType type = spawner.getType();
		if(type.disabled() == true) return;
		if(Settings.settings.spawner_switching == true && spawner.isEnabled() == false) return;
		if(spawner.isEmpty() == true && DataManager.isEmptyType(block) == true) return;
		DataManager.recalculate(block);

		if(SET.contains(block) == true) return;
		SET.add(block);
		new BukkitRunnable() {
			@Override
			public void run() {
				SET.remove(block);
			}
		}.runTaskLater(SpawnerMeta.instance(), 1);
		
		DataManager.setNewSpawner(null, block, false);
		DataManager.resetDelay(block);
		
		int stack = spawner.getStack();
		int count = stack * spawner.getUpgradeAttribute(UpgradeType.AMOUNT);
		if(count > Settings.settings.safety_limit) count = Settings.settings.safety_limit;
		
		int cl = Settings.settings.chunk_entity_limit;
		if(cl > 0) {
			long ic = Stream.of(block.getChunk().getEntities())
					.filter(e -> e instanceof LivingEntity)
					.filter(e -> e instanceof Player == false)
					.count();
			if(ic >= cl) return;
		}
		
		SpawnerPreSpawnEvent call = call(new SpawnerPreSpawnEvent(block, count));
		if(call.cancelled() == true) return;
		
		count = call.count;
		
		int charges = spawner.getCharges();
		if(call.bypass_checks == false && Settings.settings.charges_enabled == true && charges <= 0) {
			boolean n = Settings.settings.charges_ignore_natural == true && spawner.isNatural() == true;
			if(n == false) {
				block.getWorld().spawnParticle(Particle.REDSTONE,
						block.getLocation().add(0.5, 0.5, 0.5), 25, 0.5, 0.5, 0.5, 0.075,
						new DustOptions(Color.MAROON, 2.5f));
				return;
			}
		}
		
		boolean clear = false;
		x: if(call.bypass_checks == false && Settings.settings.spawnable_enabled == true) {
			int spawnable = spawner.getSpawnable();
			if(spawnable >= 1_000_000_000) break x;
			if(spawnable <= 0) {
				clear = true;
				count = 0;
				break x;
			}
			if(spawnable < count) {
				count = spawnable;
				spawnable = 0;
			} else spawnable -= count;
			spawner.setSpawnable(spawnable);
			if(spawnable < stack) {
				if(spawnable <= 0) clear = true;
				else {
					spawner.setStack(spawnable);
					HologramRegistry.update(block);
				}
			}
		}
		
		final Location l = event.getLocation();
		
		if(count <= 0) {
			l.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().add(0.5, 0.5, 0.5), 25, 0.45, 0.45, 0.45, 0.075,
					new DustOptions(Color.MAROON, 2.5f));
			return;
		}
		
		List<Entity> entities = spawnEntities(block, l.getBlock(), type, event.getEntityType(), count, cs);
		if(entities.isEmpty() == true) return;
		SpawnerUpgrade.update(block);
		
		call(new SpawnerPostSpawnEvent(block, entities));

		if(Settings.settings.kill_entities_on_spawn == true) {
			if(Settings.settings.entities_drop_xp == true) {
				Optional<Player> opt = block.getWorld().getNearbyEntities(block.getLocation(), 32, 32, 32).stream()
						.filter(n -> n instanceof Player)
						.findAny()
						.map(n -> (Player) n);
				entities.forEach(e -> {
					if(e instanceof LivingEntity living) {
						opt.ifPresent(near -> living.damage(10_000_000, near));
						living.setHealth(0);
					}
				});
			} else {
				entities.forEach(e -> {
					if(e instanceof LivingEntity living) living.setHealth(0);
				});
			}
		}
		if(call.bypass_checks == false && Settings.settings.charges_enabled == true
				&& charges < 1_000_000_000) spawner.setCharges(--charges);
		
		if(clear == true) {
			SpawnerUpgrade.close(block);
			block.setType(Material.AIR);
			unlink(block);
			l.getWorld().spawnParticle(Particle.LAVA, block.getLocation().add(0.5, 0.5, 0.5), 25, 0.1, 0.1, 0.1, 0);
			HologramRegistry.remove(block);
		}
	}
	
	private static List<Entity> spawnEntities(Block block, Block at, SpawnerType st, EntityType type, int a, CreatureSpawner cs) {
		List<Entity> entities;
		try {
			Class<?> entity_class = type.getEntityClass();

			Invoker<Entity> invoker = spawner(block.getWorld());

			SpawnerSpawning spread = Settings.settings.spawner_spawning;
			
			int s = switch(st) {
			case SLIME, MAGMA_CUBE -> {
				int z = Settings.settings.slime_size;
				yield z <= 0 ? Utils.random(3) + 1 : z;
			}
			default -> 0;
			};
			
			EntityBox box = st.box();
			
			final Consumer<Entity> f, n = function(cs);
			
			if(s > 0) {
				box = box.multiply(s);
				f = e -> {
					n.accept(e);
					if(e instanceof Slime m) m.setSize(s);
				};
			} else f = n;
			
			spread.set(block, at, box);

			if(isLiving(type) == true && SpawnerMeta.WILD_STACKER.exists() == true
					&& SpawnerMeta.WILD_STACKER.enabled() == true) {
//				Bukkit.getLogger().info("Spawning through WS");
				entities = SpawnerMeta.WILD_STACKER.combine(block, type, spread, a, cs);
			} else {
//				Bukkit.getLogger().info("Spawning as regular");
				entities = Stream.generate(spread::get)
					.limit(a)
					.peek(EventRegistry::particle)
					.map(l -> spawn(entity_class, invoker, l, f))
					.collect(Collectors.toList());
			}
			spread.clear();
			return entities;
		} catch(Exception e) {
			RF.debug(e);
		}
		return new ArrayList<>();
	}
	
	public static void particle(Location loc) {
		if(Settings.settings.spawning_particles == false) return;
		loc.getWorld().spawnParticle(Particle.CLOUD, loc.add(0, 0.25, 0), 5, 0.2, 0.2, 0.2, 0.1);
 	}
	
	private static boolean isLiving(EntityType type) {
		return LivingEntity.class.isAssignableFrom(type.getEntityClass());
	}

	private static Invoker<Entity> spawner(World world) {
		return RF.order(RF.craft("CraftWorld").cast(world), "spawn",
				Location.class, Class.class, Consumer.class, SpawnReason.class).as(Entity.class);
	}

	public static Entity spawn(Class<?> entity_class, Invoker<Entity> invoker, Location at, Consumer<Entity> function) {
		return invoker.invoke(at, entity_class, function, SpawnReason.SPAWNER);
	}

	public static Entity spawn(Location l, EntityType type, Consumer<Entity> function) {
		return spawner(l.getWorld()).invoke(l, type.getEntityClass(), function, SpawnReason.SPAWNER);
	}

	public static Consumer<Entity> function(CreatureSpawner cs) {
		return entity -> {
			try {
				if(entity instanceof EnderDragon dragon) dragon.setPhase(Phase.CIRCLING);
				if(Settings.settings.entity_movement == false
						&& entity instanceof Attributable le) {
					AttributeInstance at = le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
					if(at != null) at.setBaseValue(0);
				}
				if(Settings.settings.check_spawner_nerf == true && entity instanceof Mob mob) {
					Object w = RF.order(mob.getWorld(), "getHandle").invoke();
					SpigotWorldConfig f = RF.access(w, "spigotConfig").as(SpigotWorldConfig.class).field();
					if(f.nerfSpawnerMobs == true) {
						Object a = RF.order(mob, "getHandle").invoke();
						RF.access(a, "aware").as(boolean.class).set(false);
					}
				}
				if(Settings.settings.spawn_babies == false && entity instanceof Ageable ageable) ageable.setAdult();
				if(Settings.settings.spawn_with_equipment == false && entity instanceof LivingEntity a) {
					EntityEquipment e = a.getEquipment();
					e.clear();
				}
				Object o = RF.order(entity, "getHandle").invoke();
				RF.accessI(o, "spawnedViaMobSpawner").as(boolean.class).set(true);
				RF.accessI(o, "spawnReason").as(SpawnReason.class).set(SpawnReason.SPAWNER);
				if(Settings.settings.send_spawning_event == true) {
					SpawnerSpawnEvent event = new SpawnerSpawnEvent(entity, cs);
					entity.getServer().getPluginManager().callEvent(event);
				}
			} catch (Exception e) {
				RF.debug(e);
			}
		};
	}

	public static void modify(Entity entity) {
		try {
			if(entity instanceof EnderDragon dragon) dragon.setPhase(Phase.CIRCLING);
			if(Settings.settings.entity_movement == false
					&& entity instanceof Attributable le) {
				AttributeInstance at = le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
				if(at != null) at.setBaseValue(0);
			}
			if(Settings.settings.check_spawner_nerf == true && entity instanceof Mob mob) {
				Object w = RF.order(mob.getWorld(), "getHandle").invoke();
				SpigotWorldConfig f = RF.access(w, "spigotConfig").as(SpigotWorldConfig.class).field();
				if(f.nerfSpawnerMobs == true) {
					Object a = RF.order(mob, "getHandle").invoke();
					RF.access(a, "aware").as(boolean.class).set(false);
				}
			}
			Object o = RF.order(entity, "getHandle").invoke();
			RF.accessI(o, "spawnedViaMobSpawner").as(boolean.class).set(true);
			RF.accessI(o, "spawnReason").as(SpawnReason.class).set(SpawnReason.SPAWNER);
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	public static void unlink(Block block) {
		if(SpawnerMeta.WILD_STACKER.exists() == false) return;
		SpawnerMeta.WILD_STACKER.unlink(block);
	}

}
