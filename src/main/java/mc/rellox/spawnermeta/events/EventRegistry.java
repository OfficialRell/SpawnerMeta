package mc.rellox.spawnermeta.events;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
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
import org.bukkit.util.Vector;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.APIRegistry;
import mc.rellox.spawnermeta.api.configuration.IPlayerData;
import mc.rellox.spawnermeta.api.events.IEvent;
import mc.rellox.spawnermeta.api.events.SpawnerBreakEvent;
import mc.rellox.spawnermeta.api.events.SpawnerChangeEvent;
import mc.rellox.spawnermeta.api.events.SpawnerEmptyEvent;
import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent;
import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent.ExplosionType;
import mc.rellox.spawnermeta.api.events.SpawnerOpenEvent;
import mc.rellox.spawnermeta.api.events.SpawnerPlaceEvent;
import mc.rellox.spawnermeta.api.events.SpawnerStackEvent;
import mc.rellox.spawnermeta.api.spawner.ICache;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.items.ItemCollector;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.ActiveVirtual;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.spawner.generator.SpawningManager;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Messagable;
import mc.rellox.spawnermeta.utility.Utility;

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

	protected static void open_upgrades(Player player, Messagable m, IGenerator generator) {
		if(Settings.settings.upgrade_interface_enabled == false) return;
		if(player.hasPermission("spawnermeta.upgrades.open") == false) {
			m.send(Language.list("Spawners.upgrades.permission.opening"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.natural_can_open == false && generator.cache().natural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.interact") == false) {
				m.send(Language.list("Spawners.natural.opening.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		x: if(Settings.settings.owned_can_open == false) {
			UUID owner = generator.spawner().getOwnerID();
			if(owner != null && owner.equals(player.getUniqueId()) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.interact") == false) {
					if(Settings.settings.trusted_can_open == true
							&& LocationRegistry.trusted(owner, player) == true) break x;
					m.send(Language.list("Spawners.ownership.opening.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
		}
		
		SpawnerOpenEvent call = call(new SpawnerOpenEvent(player, generator));
		if(call.cancelled() == true) return;
		
		generator.open(player);
	}

	protected static void changing_regular(Player player, Messagable m, IGenerator generator,
			ItemStack item, SpawnerType change) {
		if(Settings.settings.changing_enabled == false) return;
		if(change.unique() == true && !(player.isOp() == true && player.getGameMode() == GameMode.CREATIVE)) return;
		if(player.hasPermission("spawnermeta.eggs") == false) {
			m.send(Language.list("Spawners.changing.permission"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		ICache cache = generator.cache();
		if(Settings.settings.natural_can_change == false && cache.natural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.changing") == false) {
				m.send(Language.list("Spawners.natural.changing.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		ISpawner spawner = generator.spawner();
		x: if(Settings.settings.owned_can_change == false) {
			UUID owner = generator.spawner().getOwnerID();
			if(owner != null && owner.equals(player.getUniqueId()) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
					if(Settings.settings.trusted_can_change == true
							&& LocationRegistry.trusted(owner, player) == true) break x;
					m.send(Language.list("Spawners.ownership.changing.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
		}
		SpawnerType type = cache.type();
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
		int stack = cache.stack();
		if(ItemMatcher.has(player, item, stack) == false) {
			m.send(Language.list("Spawners.changing.eggs.insufficient", "required", stack));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		Price price = null;
		if(Settings.settings.changing_price.using() == true)
			price = Price.of(Group.changing, Settings.settings.changing_price.get(type) * stack);
		
		SpawnerChangeEvent call = call(new SpawnerChangeEvent(player, generator, price, change, false));
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
		player.spawnParticle(Utility.particle_happy, spawner.center(), 25, 0.25, 0.25, 0.25, 0.1);
		ItemMatcher.remove(player, item, stack);
		
		spawner.setType(change);
		if(Settings.settings.changing_reset_regular == true) spawner.resetUpgradeLevels();
		spawner.update();
		
		generator.refresh();
		SpawningManager.unlink(generator.block());
	}

	protected static void changing_empty(Player player, Messagable m, IGenerator generator, ItemStack item,
			SpawnerType change) {
		if(change.unique() == true && Utility.op(player) == false) return;
		ICache cache = generator.cache();
		SpawnerType type = cache.type();
		if(type != SpawnerType.EMPTY) return;
		if(Settings.settings.natural_can_change == false && cache.natural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.changing") == false) {
				m.send(Language.list("Spawners.natural.changing.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		ISpawner spawner = generator.spawner();
		x: if(Settings.settings.owned_can_change == false) {
			UUID owner = generator.spawner().getOwnerID();
			if(owner != null && owner.equals(player.getUniqueId()) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
					if(Settings.settings.trusted_can_change == true
							&& LocationRegistry.trusted(owner, player) == true) break x;
					m.send(Language.list("Spawners.ownership.changing.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
		}
		int stack = cache.stack();
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

		SpawnerChangeEvent call = call(new SpawnerChangeEvent(player, generator, price, change, true));
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
		player.spawnParticle(Utility.particle_happy, spawner.center(), 25, 0.25, 0.25, 0.25, 0.1);
		
		ItemMatcher.remove(player, item, stack);
		spawner.setType(change);
		if(Settings.settings.changing_reset_empty == true) spawner.resetUpgradeLevels();
		spawner.update();
		
		generator.refresh();
		SpawningManager.unlink(generator.block());
	}
	
	@SuppressWarnings("deprecation")
	protected static boolean remove_eggs_from_regular(PlayerInteractEvent event, Player player, Messagable m, ItemStack item, IGenerator generator) {
		if(generator.cache().empty() == true) return true;
		if(Settings.settings.empty_remove_from_regular == false) return true;
		if(item != null && item.getType() == Material.SPAWNER) return true;
		
		event.setCancelled(true);
		
		if(Settings.settings.empty_enabled == false) {
			m.send(Language.list("Spawners.empty.disabled"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(player.isSneaking() == false) return true;
		
		x: if(Settings.settings.owned_can_change == false) {
			UUID owner = generator.spawner().getOwnerID();
			if(owner != null && owner.equals(player.getUniqueId()) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
					if(Settings.settings.trusted_can_change == true
							&& LocationRegistry.trusted(owner, player) == true) break x;
					m.send(Language.list("Spawners.ownership.changing.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return false;
				}
			}
		}
		boolean b = Utility.nulled(player.getInventory().getItemInMainHand()) == false;
		if(b == true) {
			m.send(Language.list("Spawners.empty.hand-full"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(Settings.settings.empty_verify_removing == true) {
			Block block = generator.block();
			verify = block;
			SpawnerMeta.scheduler().runAtLocationLater(block.getLocation(), () -> {
				if(block.equals(verify) == true) {
					verify = null;
					player.spawnParticle(Utility.particle_redstone, Utility.center(block).add(0, 0.52, 0), 5, 0.1, 0.1, 0.1, 0,
							new DustOptions(Color.MAROON, 2f));
				}
			}, 20);
			m.send(Language.list("Spawners.empty.verify-removing.first"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 0f);
			return false;
		}

		remove_eggs(player, generator, false);
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	protected static boolean remove_eggs_empty(PlayerInteractEvent event, Player player, Messagable m, ItemStack item, IGenerator generator) {
		ICache cache = generator.cache();
		
		if(cache.empty() == false) return true;
		if(item != null && item.getType() == Material.SPAWNER) return true;
		
		event.setCancelled(true);
		
		if(Settings.settings.empty_enabled == false) {
			m.send(Language.list("Spawners.empty.disabled"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		
		SpawnerType type = cache.type();
		if(player.isSneaking() == false) {
			if(type == SpawnerType.EMPTY) {
				m.send(Language.list("Spawners.empty.try-open"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return false;
			}
			return true;
		}
		if(type == SpawnerType.EMPTY) return true;
		
		x: if(Settings.settings.owned_can_change == false) {
			UUID owner = cache.owner();
			if(owner != null && owner.equals(player.getUniqueId()) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
					if(Settings.settings.trusted_can_change == true
							&& LocationRegistry.trusted(owner, player) == true) break x;
					m.send(Language.list("Spawners.ownership.changing.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return false;
				}
			}
		}
		
		boolean b = Utility.nulled(player.getInventory().getItemInMainHand()) == false;
		if(b == true) {
			m.send(Language.list("Spawners.empty.hand-full"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		
		if(Settings.settings.empty_verify_removing == true) {
			Block block = generator.block();
			verify = block;
			SpawnerMeta.scheduler().runAtLocationLater(block.getLocation(), () -> {
				if(block.equals(verify) == true) {
					verify = null;
					player.spawnParticle(Utility.particle_redstone, Utility.center(block).add(0, 0.52, 0), 5, 0.1, 0.1, 0.1, 0,
							new DustOptions(Color.MAROON, 2f));
				}
			}, 20);
			m.send(Language.list("Spawners.empty.verify-removing.first"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 0f);
			return false;
		}
		remove_eggs(player, generator, true);
		return false;
	}

	protected static void stack_nearby(PlayerInteractEvent event, Player player, Messagable m, Block block) {
		if(Settings.settings.stacking_nearby_enabled == false) return;
		
		ItemStack item = event.getItem();
		if(item == null || item.getType() != Material.SPAWNER
				|| player.isSneaking() == false) return;
		event.setCancelled(true);
		
		IVirtual virtual = IVirtual.of(item);
		if(virtual == null) return;
		
		// check before stacking() to save performance
		if(player.hasPermission("spawnermeta.stacking") == false) {
			m.send(Language.list("Spawners.stacking.permission"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Settings.settings.stacking_disabled_types.contains(virtual.getType()) == true) {
			m.send(Language.list("Spawners.stacking.disabled-type"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		
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
					if(virtual.exact(of) == true) {
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
		
		IGenerator generator = GeneratorRegistry.get(valid);
		stacking(player, m, generator, item, false);
	}

	protected static void verify_removing(PlayerInteractEvent event, Player player, Messagable m) {
		if(Settings.settings.empty_verify_removing == false) return;
		Block block = event.getClickedBlock();
		
		if(player.isSneaking() == false || block.getType() != Material.SPAWNER) return;
		
		IGenerator generator = GeneratorRegistry.get(block);
		if(generator == null) return;
		
		SpawnerType type = generator.cache().type();
		if(type == SpawnerType.EMPTY) return;
		
		boolean empty = generator.cache().empty();
		if(Settings.settings.empty_remove_from_regular == false && empty == false) return;
		
		boolean b = Utility.nulled(player.getInventory().getItemInMainHand()) == false;
		if(b == false && Utility.op(player) == true) event.setCancelled(true);
		
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
		remove_eggs(player, generator, empty);
	}

	protected static boolean stacking(Player player, Messagable m, IGenerator generator, ItemStack item, boolean direct) {
		int stack = generator.cache().stack();
		Settings s = Settings.settings;
		int limit = s.stacking_limit(player, generator);
		if(s.stacking_ignore_limit == false) {
			if(stack >= limit) {
				m.send(Language.list("Spawners.stacking.limit-reached"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return true;
			}
		}
		int tt = s.stacking_ticks;
		if(tt > 0) {
			long b = System.currentTimeMillis() / 50;
			if(time >= b - tt) return false;
			time = b;
		}
		
		IVirtual virtual = IVirtual.of(item);
		if(virtual == null) return false;
		
		if(player.hasPermission("spawnermeta.stacking") == false) {
			m.send(Language.list("Spawners.stacking.permission"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(s.stacking_disabled_types.contains(virtual.getType()) == true) {
			m.send(Language.list("Spawners.stacking.disabled-type"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		if(s.natural_can_stack == false && generator.cache().natural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.stacking") == false) {
				m.send(Language.list("Spawners.natural.breaking.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return false;
			}
		}
		x: if(s.owned_can_stack == false) {
			UUID owner = generator.spawner().getOwnerID();
			if(owner != null && owner.equals(player.getUniqueId()) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.stacking") == false) {
					if(s.trusted_can_stack == true
							&& LocationRegistry.trusted(owner, player) == true) break x;
					m.send(Language.list("Spawners.ownership.stacking.warning"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return false;
				}
			}
		}
		IVirtual other = IVirtual.of(generator.block());
		if(virtual.exact(other) == false) {
			m.send(Language.list("Spawners.stacking.unequal-spawner"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		Price price = null;
		if(s.stacking_price.using() == true)
			price = Price.of(Group.stacking, s.stacking_price.get(generator.cache().type()));

		SpawnerStackEvent call = call(new SpawnerStackEvent(player, generator, price, virtual, direct));
		if(call.cancelled() == true) return false;
		
		if(call.withdraw(player) == false) {
			price = call.getUnsafePrice();
			m.send(Language.list("Prices.insufficient", 
					"insufficient", price.insufficient(), "price", price.requires(player)));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		int stacked;
		if(s.stacking_stack_all == true) {
			int amount = item.getAmount();
			if(s.stacking_ignore_limit == false) {
				int left = limit - stack;
				stacked = Math.min(amount, left);
			} else stacked = amount;
		} else stacked = 1;
		stack += stacked;
		ISpawner spawner = generator.spawner();
		ICache cache = generator.cache();
		spawner.setStack(stack);
		m.send((s.stacking_ignore_limit
				? Language.list("Spawners.stacking.stacked.infinite", "stack", stack)
						: Language.list("Spawners.stacking.stacked.finite", "stack", stack,
								"limit", limit)));
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.75f, 1.5f);
		if(s.spawnable_enabled == true) {
			int spawnable = cache.spawnable() + virtual.getSpawnable() * stacked;
			spawner.setSpawnable(spawnable);
		}
		if(s.charges_enabled == true
				&& s.charges_allow_stacking == true) {
			if(cache.charges() < 1_000_000_000
					|| virtual.getCharges() < 1_000_000_000) {
				int b = cache.charges() * (stack - stacked) + virtual.getCharges() * stacked;
				int r = b / stack;
				spawner.setCharges(r);
				int f = b % stack;
				if(f > 0)
					m.send(Language.list("Spawners.charges.lose-by-stacking", 
							"charges", f));
			}
		}
		if(direct == false && s.stacking_nearby_particles == true) {
			Location start = spawner.center();
			Location end = player.getLocation().add(0, 1, 0);
			Vector dis = end.toVector().subtract(start.toVector())
					.normalize().multiply(0.25);
			double loops = start.distance(end) / 0.25;
			for(int i = 0; i < loops; i++) {
				start.add(dis);
				player.spawnParticle(Utility.particle_sharpness, start, 1, 0, 0, 0, 0);
			}
		}
		ItemMatcher.remove(player, item, stacked);

		generator.refresh();
		return false;
	}

	protected static void interact(PlayerInteractEvent event, Player player, Messagable m, IGenerator generator) {
		
		SpawnerType type = generator.cache().type();
		if(type.disabled() == true) return;
		event.setCancelled(false);
		
		if(HookRegistry.PLOT_SQUARED.exists() == true
				&& HookRegistry.PLOT_SQUARED.modifiable(generator, player) == false) return;
		
		ItemStack item = event.getItem();
		
		if(EventRegistry.remove_eggs_empty(event, player, m, item, generator) == false) return;
		if(EventRegistry.remove_eggs_from_regular(event, player, m, item, generator) == false) return;
		
		if(item != null) {
			if(player.isSneaking() == true) {
				y: if(Settings.settings.stacking_enabled == true) {
					/*
					 * Stacking
					 */
					if(item.getType() != Material.SPAWNER) break y;
					event.setCancelled(true);
					
					if(EventRegistry.stacking(player, m, generator, item, true) == true) break y;
					return;
				}
				SpawnerType change = SpawnerType.of(item.getType());
				if(change == null) {
					if(item.getType().name().endsWith("_EGG") == true) event.setCancelled(true);
					return;
				}
				event.setCancelled(true);
				if(change.disabled() == true) return;
				if(generator.cache().empty() == true) {
					/*
					 * Empty spawner type changing
					 */
					EventRegistry.changing_empty(player, m, generator, item, change);
				} else {
					/*
					 * Regular spawner type changing
					 */
					EventRegistry.changing_regular(player, m, generator, item, change);
				}
				return;
			} else if(item.getType().name().endsWith("_EGG") == true) event.setCancelled(true);
		}
		if(generator.cache().empty() == true && type == SpawnerType.EMPTY) return;
		if(item != null && item.getType() == Material.SPAWNER) return;
		if(item != null) {
			if(item.getType() == Material.SPAWNER) return;
//			Checks if player tries to open spawner with a block placement
//			if(Settings.settings.cancel_placement_when_opening == false && event.isBlockInHand() == true) return;
		}
		EventRegistry.open_upgrades(player, m, generator);
		event.setCancelled(true);
	}

	protected static void remove_eggs(Player player, IGenerator generator, boolean empty) {
		ISpawner spawner = generator.spawner();
		
		ItemStack refund = null;
		if(Settings.settings.empty_destroy_eggs_removing == false) {
			Material m = generator.cache().type().material();
			if(m != null) refund = new ItemStack(m, spawner.getStack());
		}

		SpawnerEmptyEvent call = call(new SpawnerEmptyEvent(player, generator, refund));
		if(call.cancelled() == true) return;

		call.getRefund().ifPresent(i -> player.getInventory().setItemInMainHand(i));
		
		player.playSound(player.getEyeLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1f);
		player.spawnParticle(Utility.particle_firework, spawner.center(), 25, 0.3, 0.3, 0.3, 0.1);
		
		spawner.setType(SpawnerType.EMPTY);
		if(empty == false) spawner.setEmpty();
		if(Settings.settings.changing_reset_empty == true) spawner.resetUpgradeLevels();
		spawner.update();
		
		generator.refresh();
		
		if(generator.block().equals(verify) == true) verify = null;
	}

	public static void breaking(BlockBreakEvent event, IGenerator generator) {
		if(Settings.settings.ignore_break_event == true) return;
		ICache cache = generator.cache();
		SpawnerType type = cache.type();
		boolean ce = Settings.settings.cancel_break_event;
		event.setCancelled(true);
		event.setExpToDrop(0);
		if(type.disabled() == true) return;
		Player player = event.getPlayer();
		Messagable m = new Messagable(player);
		
		ISpawner spawner = generator.spawner();
		if(Utility.op(player) == true) {
			Location bl = spawner.center();
			List<ItemStack> items = DataManager.getSpawners(generator, false);
			if(items.isEmpty() == true) {
				items.add(DataManager.getSpawner(cache.type(), cache.stack()));
			}
			items.forEach(item -> {
				player.getWorld().dropItem(bl, item).setVelocity(new Vector());
			});
			player.spawnParticle(Particle.CLOUD, bl, 25, 0.25, 0.25, 0.25, 0);
			player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.35f, 0f);
			m.send(Language.list("Spawners.breaking.success"));
			event.setCancelled(false);
			
			dropAfterChanging(player, generator);
			
			generator.remove(true);
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
		if(Settings.settings.natural_can_break == false && cache.natural() == true) {
			if(player.hasPermission("spawnermeta.natural.bypass.breaking") == false) {
				m.send(Language.list("Spawners.natural.breaking.warning"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		x: if(Settings.settings.owned_can_break == false) {
			UUID owner = spawner.getOwnerID();
			if(owner != null && owner.equals(player.getUniqueId()) == false) {
				if(player.hasPermission("spawnermeta.ownership.bypass.breaking") == false) {
					if(Settings.settings.trusted_can_break == true
							&& LocationRegistry.trusted(owner, player) == true) break x;
					m.send(Language.list("Spawners.ownership.breaking.warning"));
					if(Settings.settings.breaking_show_owner == true) {
						if(owner != null) {
							OfflinePlayer off = Bukkit.getOfflinePlayer(owner);
							var name = off.getName();
							if(name != null) m.send(Language.list("Spawners.ownership.show-owner",
									"player", name));
						}
					}
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
				
			}
		}
		if(Settings.settings.breaking_drop_on_ground == false) {
			if(ItemCollector.exists(player) == true) {
				m.send(Language.list("Items.spawner-drop.try-breaking"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
			if(Settings.settings.breaking_cancel_if_full == true && ItemMatcher.free(player) <= 0) {
				m.send(Language.list("Inventory.insufficient-space"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		boolean silk = Settings.settings.breaking_silk_enabled ? getAPI().hasSilkTouch(player) == true : true;
		if(player.hasPermission("spawnermeta.breaking.bypass.silktouch") == true)
			silk = true;
		else if(Settings.settings.breaking_silk_destroy == false && silk == false) {
			m.send(Language.list("Spawners.breaking.failure"));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		Location center = spawner.center();
		Price price = null;
		if(Settings.settings.breaking_price.using() == true)
			price = Price.of(Group.breaking, Settings.settings.breaking_price.get(type) * cache.stack());

		double chance = Settings.settings.breaking_chance(player);

		if(cache.owned() == true) {
			if(spawner.isOwner(player) == true)
				chance = Settings.settings.breaking_chance_changer_owned.change(chance);
			else
				chance = Settings.settings.breaking_chance_changer_not_owned.change(chance);
		} else
			chance = Settings.settings.breaking_chance_changer_natural.change(chance);
		
		SpawnerBreakEvent call = EventRegistry.call(new SpawnerBreakEvent(player, generator, price, chance));
		if(call.cancelled() == true) return;

		if(call.withdraw(player) == false) {
			price = call.getUnsafePrice();
			m.send(Language.list("Prices.insufficient", 
					"insufficient", price.insufficient(), "price", price.requires(player)));
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		Block block = generator.block();
		List<Content> mm;
		if(Utility.chance(call.chance) == true) {
			boolean give = true;
			if(Settings.settings.breaking_silk_enabled == true) {
				give = silk;
				if(cache.owned() == true) give &= Settings.settings.breaking_silk_break_owned;
				else give &= Settings.settings.breaking_silk_break_natural;
			}
			if(player.getGameMode() == GameMode.CREATIVE) give = true;
			if(give == true) {
				DataManager.getSpawners(generator, false).forEach(item -> {
					if(Settings.settings.breaking_drop_on_ground == true)
						player.getWorld().dropItem(center, item).setVelocity(new Vector());
					else ItemCollector.add(player, item);
				});
				player.spawnParticle(Particle.CLOUD, center, 25, 0.25, 0.25, 0.25, 0);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.35f, 0f);
				mm = Language.list("Spawners.breaking.success");
			} else {
				player.spawnParticle(Particle.SQUID_INK, center, 25, 0.25, 0.25, 0.25, 0.1);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.2f, 0f);
				mm = Language.list("Spawners.breaking.failure");
				spawnXP(block, event, give);
			}
		} else {
			player.spawnParticle(Particle.SQUID_INK, center, 25, 0.25, 0.25, 0.25, 0.1);
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
		dropAfterChanging(player, generator);
		
		generator.remove(true);
		
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
			
			IGenerator generator = EventListeners.fetch(block);
			if(generator == null) continue;
			
			boolean[] xs = Settings.settings.explosion_types.get(ExplosionType.TNT);

			SpawnerExplodeEvent call = call(new SpawnerExplodeEvent(generator, ExplosionType.TNT, xs));
			if(call.cancelled() == true) {
				it.remove();
				continue;
			}
			
			if(generator.cache().owned() == true) {
				if(xs[0] == true) destroy(generator, xs[1]);
			} else {
				if(xs[2] == true) destroy(generator, xs[3]);
			}
			block.getWorld().spawnParticle(Utility.particle_angry,
					block.getLocation().add(0.5, 0.5, 0.5), 10, 0.2, 0.2, 0.2, 0);
			it.remove();
		}
	}
	
	public static boolean destroy(Block block, boolean drop, boolean particles) {
		IGenerator generator = EventListeners.fetch(block);
		if(generator == null) return false;
		return destroy(generator, drop, particles);
	}
	
	public static boolean destroy(IGenerator generator, boolean drop) {
		return destroy(generator, drop, true);
	}
	
	public static boolean destroy(IGenerator generator, boolean drop, boolean particles) {
		Block block = generator.block();
		if(block == null || block.getType() != Material.SPAWNER) return false;
		Location loc = block.getLocation().add(0.5, 0.5, 0.5);
		if(drop == true) {
			DataManager.getSpawners(block, false).forEach(item -> {
				loc.getWorld().dropItem(loc, item).setVelocity(new Vector());
			});
			dropAfterChanging(null, generator);
		}
		
		generator.remove(true);
		
		HookRegistry.SUPERIOR_SKYBLOCK_2.breaking(block);
		
		if(particles == true) loc.getWorld().spawnParticle(Particle.CLOUD, loc, 25, 0.25, 0.25, 0.25, 0);
		return true;
	}
	
	public static void dropAfterChanging(Player player, IGenerator generator) {
		Block block = generator.block();
		if(Settings.settings.empty_destroy_eggs_breaking == true
				|| Settings.settings.empty_store_inside == true) {
			block.getWorld().spawnParticle(Particle.CRIT, Utility.center(block), 10, 0, 0, 0, 0.1);
			return;
		}
		ICache cache = generator.cache();
		SpawnerType type = cache.type();
		if(cache.empty() == true && type != SpawnerType.EMPTY) {
			if(type.unique() == false) {
				Material mat = type.material();
				if(mat != null) {
					int s = cache.stack();
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
			if(Settings.settings.changing_reset_empty == true)
				generator.spawner().resetUpgradeLevels();
		}
	}

	protected static void explode_entity(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		Iterator<Block> it = event.blockList().iterator();
		Block block;
		while(it.hasNext() == true && (block = it.next()) != null) {
			if(block.getType() != Material.SPAWNER) continue;
			
			if(Utility.isWindCharge(entity) == true) {
				it.remove();
				continue;
			}
			
			IGenerator generator = EventListeners.fetch(block);
			if(generator == null) continue;
			
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
			} else if(entity instanceof Wither || entity instanceof WitherSkull) {
				xs = Settings.settings.explosion_types.get(explosion = ExplosionType.WITHER);
			} else continue;
			
			SpawnerExplodeEvent call = call(new SpawnerExplodeEvent(generator, explosion, xs));
			if(call.cancelled() == true) {
				it.remove();
				continue;
			}
			
			if(xs != null) {
				if(generator.cache().owned() == true) {
					if(xs[0] == true) destroy(generator, xs[1]);
				} else {
					if(xs[2] == true) destroy(generator, xs[3]);
				}
			}
			block.getWorld().spawnParticle(Utility.particle_angry,
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

	@SuppressWarnings("deprecation")
	protected static void place(BlockPlaceEvent event, Block block) {
		ItemStack item = event.getItemInHand().clone();
		IVirtual temp = IVirtual.of(item, true), data;
		if(temp == null) {
			EntityType entity = DataManager.getEntity(item);
			if(entity == null) return;
			SpawnerType type = SpawnerType.ofAll(entity);
			if(type == null) return;
			data = new ActiveVirtual(type, null, 0,
					Settings.settings.spawnable_amount.get(type), type == SpawnerType.EMPTY);
		} else data = temp;
		if(Settings.settings.ignored(data.getType().entity()) == true) return;
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		Messagable m = new Messagable(player);
		
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
				int p = LocationRegistry.get(player).amount();
				if(p >= Settings.settings.owning_limit(player)) {
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
		SpawnerMeta.scheduler().runAtLocation(block.getLocation(), task -> {
			if(block.getType() != Material.SPAWNER) return;
			place(block, player, data);
		});
	}
	
	public static int spawnersInChunk(Block block) {
		return spawnersInChunk(block.getChunk());
	}
	
	public static int spawnersInChunk(Chunk chunk) {
		BlockState[] bs = chunk.getTileEntities();
		return bs == null ? 0 : (int) Stream.of(bs)
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
		if(player != null) {
			IPlayerData il = LocationRegistry.get(player);
			il.add(block);
			if(Settings.settings.owned_ignore_limit == false)
				player.sendMessage(Language.get("Spawners.ownership.limit.place",
						"placed", il.amount(), "limit", Settings.settings.owning_limit(player)).text());
		}
		
		GeneratorRegistry.put(block);
		
		HookRegistry.SUPERIOR_SKYBLOCK_2.placing(block);
		
		return true;
	}

	protected static void spawn(SpawnerSpawnEvent event, Entity entity) {
		if(Settings.settings.spawning == false) {
			event.setCancelled(true);
			return;
		}
		if(Utility.isItem(entity.getType()) == true
				&& Settings.settings.disable_item_spawners == true) {
			event.setCancelled(true);
			return;
		}
		if(Settings.settings.ignored(event.getSpawner().getSpawnedType()) == true) {
			event.setCancelled(false);
			return;
		}
		event.setCancelled(Settings.settings.cancel_spawning_event);
		if(entity.isDead() == false) entity.remove();
		
		// creates new generator if it doesn't exit yet
		IGenerator generator = GeneratorRegistry.get(event.getSpawner().getBlock());
		if(generator == null) return;
		
		if(generator.cache().type().disabled() == true) {
			event.setCancelled(true);
			return;
		}
	}

}
