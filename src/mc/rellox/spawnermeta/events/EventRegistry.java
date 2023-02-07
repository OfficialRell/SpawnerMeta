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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
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

public final class EventRegistry implements Listener {
	
	private static final RegistryAbstract[] REGISTRIES = {new RegistryAI(), new RegistryWorldLoad()};
	
	private static long time, chunk;
	
	private static Block verify;
	
	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new EventRegistry(), SpawnerMeta.instance());
		update();
	}
	
	public static void update() {
		for(RegistryAbstract registry : REGISTRIES) registry.update();
	}
	
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
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Messagable m = new Messagable(player);
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
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
			removeEggs(player, block, type);
			return;
		}
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if(event.getHand() == EquipmentSlot.OFF_HAND) {
			ItemStack item = event.getItem();
			if(item != null && item.getType().name().endsWith("_EGG") == true
					&& block.getType() == Material.SPAWNER) event.setCancelled(true);
			return;
		}
		if(block.getType() != Material.SPAWNER) return;
		Spawner spawner = getAPI().getSpawner(block);
		try {
			if(DataManager.isItemSpawner(block) == true) return;
			SpawnerType type = spawner.getType();
			if(type.disabled() == true) return;
			event.setCancelled(true);
			ItemStack item = event.getItem();
			x: if(spawner.isEmpty() == true) {
				
				/*
				 * Remove eggs from empty spawner
				 */
				
				if(item != null && item.getType() == Material.SPAWNER) break x;
				if(Settings.settings.empty_enabled == false) {
					m.send(Language.list("Spawners.empty.disabled"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
				if(player.isSneaking() == false) {
					if(type == SpawnerType.EMPTY) {
						m.send(Language.list("Spawners.empty.try-open"));
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
						return;
					}
					break x;
				}
				if(type == SpawnerType.EMPTY) break x;
				if(Settings.settings.owned_can_change == false && spawner.isOwner(player, true) == false) {
					if(player.hasPermission("spawnermeta.ownership.bypass.changing") == false) {
						m.send(Language.list("Spawners.ownership.changing.warning"));
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
						return;
					}
				}
				boolean b = Utils.nulled(player.getInventory().getItemInMainHand()) == false;
				if(b == true) {
					m.send(Language.list("Spawners.empty.hand-full"));
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
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
					return;
				}
				
				removeEggs(player, block, type);
				return;
			}
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
						int stack = spawner.getStack();
						if(Settings.settings.stacking_ignore_limit == false) {
							if(stack >= Settings.settings.stacking_spawner_limit) {
								m.send(Language.list("Spawners.stacking.limit-reached"));
								player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
								break y;
							}
						}
						int tt = Settings.settings.stacking_ticks;
						if(tt > 0) {
							long b = System.currentTimeMillis() / 50;
							if(time >= b - tt) return;
							time = b;
						}
						VirtualSpawner data = VirtualSpawner.of(item);
						if(data == null) return;
						if(player.hasPermission("spawnermeta.stacking") == false) {
							m.send(Language.list("Spawners.stacking.permission"));
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
							return;
						}
						if(Settings.settings.natural_can_stack == false && spawner.isNatural() == true) {
							if(player.hasPermission("spawnermeta.natural.bypass.stacking") == false) {
								m.send(Language.list("Spawners.natural.breaking.warning"));
								player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
								return;
							}
						}
						if(Settings.settings.owned_can_stack == false && spawner.isOwner(player, true) == false) {
							if(player.hasPermission("spawnermeta.ownership.bypass.stacking") == false) {
								m.send(Language.list("Spawners.ownership.stacking.warning"));
								player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
								return;
							}
						}
						VirtualSpawner other = VirtualSpawner.of(block);
						if(data.exact(other) == false) {
							m.send(Language.list("Spawners.stacking.unequal-spawner"));
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
							return;
						}
						if(Settings.settings.owned_ignore_limit == false) {
							int p = LF.placed(player);
							if(p >= Settings.settings.owned_spawner_limit) {
								m.send(Language.list("Spawners.ownership.limit.reached",
										"limit", Settings.settings.owned_spawner_limit));
								player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
								return;
							}
						}
						Price price = null;
						if(Settings.settings.stacking_price.using() == true)
							price = Price.of(Group.stacking, Settings.settings.stacking_price.get(type));

						SpawnerStackEvent call = call(new SpawnerStackEvent(player, block, price, data));
						if(call.cancelled() == true) return;
						
						if(call.withdraw(player) == false) {
							price = call.getUnsafePrice();
							m.send(Language.list("Prices.insufficient", 
									"insufficient", price.insufficient(), "price", price.requires(player)));
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
							return;
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
						ItemMatcher.remove(player, item, 1);
						LF.add(block, player);
						SpawnerUpgrade.update(block);
						HologramRegistry.update(block);
						return;
					}
					SpawnerType change = SpawnerManager.fromEgg(item.getType());
					if(change == null) return;
					event.setCancelled(true);
					if(change.disabled() == true) return;
					if(spawner.isEmpty() == true) {
						
						/*
						 * Empty spawner type changing
						 */
						
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
						
						return;
					} else {
						
						/*
						 * Regular spawner type changing
						 */
						
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
					}
					return;
				} else if(item.getType().name().endsWith("_EGG") == true) event.setCancelled(true);
			}
			if(spawner.isEmpty() == true && type == SpawnerType.EMPTY) return;
			if(item != null && item.getType() == Material.SPAWNER) {
				event.setCancelled(false);
				return;
			}
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
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	private void removeEggs(Player player, Block block, SpawnerType type) {
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
		
		if(block.equals(verify) == true) verify = null;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onBreak(BlockBreakEvent event) {
		try {
			Block block = event.getBlock();
			if(block.getType() != Material.SPAWNER) return;
			Spawner spawner = getAPI().getSpawner(block);
			SpawnerType type = spawner.getType();
			event.setCancelled(true);
			if(type.disabled() == true) return;
			Player player = event.getPlayer();
			Messagable m = new Messagable(player);
			if(Settings.settings.breaking_enabled == false) {
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
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
			if(Settings.settings.breaking_drop_on_ground == false && ItemCollector.exists(player) == true) {
				m.send(Language.list("Items.spawner-drop.try-breaking"));
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
					mm = Language.list("Spawners.breaking.failure");
					spawnXP(block);
				}
			} else {
				player.spawnParticle(Particle.SQUID_INK, bl, 25, 0.25, 0.25, 0.25, 0.1);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.35f, 0f);
				mm = Language.list("Spawners.breaking.failure");
				spawnXP(block);
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
			block.setType(Material.AIR);
			HologramRegistry.remove(block);
			ItemCollector.execute(player);
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	private static void spawnXP(Block block) {
		int xp = Settings.settings.breaking_xp_on_failure;
		if(xp > 0) {
			ExperienceOrb orb = (ExperienceOrb) block.getWorld()
					.spawnEntity(block.getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
			orb.setExperience(xp);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onBlockExplodeByBlock(BlockExplodeEvent event) {
		Iterator<Block> it = event.blockList().iterator();
		try {
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
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onBlockExplodeByEntity(EntityExplodeEvent event) {
		try {
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
		} catch (Exception e) {
			RF.debug(e);
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
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onPlace(BlockPlaceEvent event) {
		if(event.isCancelled() == true) return;
		Block block = event.getBlockPlaced();
		if(block.getType() != Material.SPAWNER) return;
		try {
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
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	private static final Set<Block> SET = new HashSet<>(4);
	
	@EventHandler(priority = EventPriority.LOW)
	private void onSpawn(SpawnerSpawnEvent event) {
		Entity entity = event.getEntity();
		if(entity.getCustomName() != null) return;
		if(Settings.settings.cancel_spawning_event == true) event.setCancelled(true);
		else entity.remove();
		try {
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
			}
			if(call.bypass_checks == false && Settings.settings.charges_enabled == true) spawner.setCharges(--charges);
			
			if(clear == true) {
				SpawnerUpgrade.close(block);
				block.setType(Material.AIR);
				l.getWorld().spawnParticle(Particle.LAVA, block.getLocation().add(0.5, 0.5, 0.5), 25, 0.1, 0.1, 0.1, 0);
				HologramRegistry.remove(block);
			}
		} catch(Exception e) {
			RF.debug(e);
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
			
			final Consumer<Entity> f;
			final Consumer<Entity> n = function(cs);
			
			if(s > 0) {
				box = box.multiply(s);
				f = e -> {
					n.accept(e);
					if(e instanceof Slime m) m.setSize(s);
				};
			} else f = n;
			
			spread.set(block, at, box);

			if(isLiving(type) == true && SpawnerMeta.WILD_STACKER.exists() == true) {
				entities = SpawnerMeta.WILD_STACKER.combine(block, type, spread, a, cs);
			} else {
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
		return RF.order(RF.craft("CraftWorld").cast(world), Entity.class, "spawn",
				Location.class, Class.class, Consumer.class, SpawnReason.class);
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
					SpigotWorldConfig f = RF.access(w, "spigotConfig", SpigotWorldConfig.class).field();
					if(f.nerfSpawnerMobs == true) {
						Object a = RF.order(mob, "getHandle").invoke();
						RF.access(a, "aware", boolean.class).set(false);
					}
				}
				if(Settings.settings.spawn_babies == false && entity instanceof Ageable ageable) ageable.setAdult();
				if(Settings.settings.spawn_with_equipment == false && entity instanceof LivingEntity a) {
					EntityEquipment e = a.getEquipment();
					e.clear();
				}
				Object o = RF.order(entity, "getHandle").invoke();
				RF.access(o, "spawnedViaMobSpawner", boolean.class).set(true);
				RF.access(o, "spawnReason", SpawnReason.class).set(SpawnReason.SPAWNER);
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
				SpigotWorldConfig f = RF.access(w, "spigotConfig", SpigotWorldConfig.class).field();
				if(f.nerfSpawnerMobs == true) {
					Object a = RF.order(mob, "getHandle").invoke();
					RF.access(a, "aware", boolean.class).set(false);
				}
			}
			Object o = RF.order(entity, "getHandle").invoke();
			RF.access(o, "spawnedViaMobSpawner", boolean.class).set(true);
			RF.access(o, "spawnReason", SpawnReason.class).set(SpawnReason.SPAWNER);
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	private static abstract class RegistryAbstract implements Listener {
		
		private boolean registered;
		
		protected void register() {
			if(registered == true) return;
			Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
		}
		
		protected void unregister() {
			if(registered == false) return;
			HandlerList.unregisterAll(this);
		}
		
		public abstract void update();
		
	}
	
	private static final class RegistryAI extends RegistryAbstract {
		
		@Override
		public void update() {
			if(Settings.settings.entity_target == true) unregister();
			else register();
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		private void onEntityTarget(EntityTargetEvent event) {
			Entity entity = event.getEntity();
			if(DataManager.isSpawned(entity) == false) return;
			event.setCancelled(true);
		}
		
	}
	
	private static final class RegistryWorldLoad extends RegistryAbstract {
		
		@Override
		public void update() {
			if(HologramRegistry.loaded() == false) unregister();
			else register();
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		private void onWorldLoad(WorldLoadEvent event) {
			HologramRegistry.load(event.getWorld());
		}
		
	}

}
