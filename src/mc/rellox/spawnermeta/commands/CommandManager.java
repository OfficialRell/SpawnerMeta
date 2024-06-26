package mc.rellox.spawnermeta.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.configuration.IPlayerData;
import mc.rellox.spawnermeta.configuration.Configuration;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.configuration.location.LocationFile;
import mc.rellox.spawnermeta.configuration.location.LocationFile.FinalPos;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.events.EventListeners;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.shop.ShopRegistry;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.content.Colorer.Colors;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Messagable;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class CommandManager {

	private static final Command SPAWNERMETA = Bukkit.getPluginCommand("spawnermeta");
	
	public static void initialize() {
		CommandMap cm;
		String name = Settings.settings.command_view;
		List<String> aliases = Settings.settings.aliases_view;
		try {
			cm = RF.fetch(Bukkit.getServer(), "commandMap", CommandMap.class);
		} catch (Exception e) {
			return;
		}
		try {
			CommandSpawners c = new CommandSpawners(name);
			if(aliases.isEmpty() == false) c.setAliases(aliases);
			c.setPermission("spawnermeta.command.view");
			cm.register(name, c);
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner view command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
		name = Settings.settings.command_shop;
		aliases = Settings.settings.aliases_shop;
		try {
			CommandShop c = new CommandShop(name);
			if(aliases.isEmpty() == false) c.setAliases(aliases);
			c.setPermission("spawnermeta.command.shop");
			cm.register(name, c);
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner shop command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
		name = Settings.settings.command_drops;
		aliases = Settings.settings.aliases_drops;
		try {
			CommandDrops c = new CommandDrops(name);
			if(aliases.isEmpty() == false) c.setAliases(aliases);
			c.setPermission("spawnermeta.command.drops");
			cm.register(name, c);
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner drops command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
		name = Settings.settings.command_locations;
		aliases = Settings.settings.aliases_locations;
		try {
			CommandLocations c = new CommandLocations(name);
			if(aliases.isEmpty() == false) c.setAliases(aliases);
			c.setPermission("spawnermeta.command.locations");
			cm.register(name, c);
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner locations command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
		name = Settings.settings.command_trust;
		aliases = Settings.settings.aliases_trust;
		try {
			CommandTrust c = new CommandTrust(name);
			if(aliases.isEmpty() == false) c.setAliases(aliases);
			c.setPermission("spawnermeta.command.trust");
			cm.register(name, c);
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner trust command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
	}

	public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (sender instanceof Player p) ? p : null;
		if(command.equals(CommandManager.SPAWNERMETA) == true) {
			String help = help(command, null, "update", "give", "modify", "location", "active", "disable", "version");
			if(args.length < 1) sender.sendMessage(help);
			else if(args[0].equalsIgnoreCase("update") == true) {
				update0(sender, command, args, player);
			} else if(args[0].equalsIgnoreCase("give") == true) {
				give0(sender, command, args, player);
			} else if(args[0].equalsIgnoreCase("modify") == true) {
				modify0(sender, command, args, player);
			} else if(args[0].equalsIgnoreCase("location") == true) {
				location0(sender, command, args);
			} else if(args[0].equalsIgnoreCase("disable") == true) {
				disable0(sender, command, args);
			} else if(args[0].equalsIgnoreCase("active") == true) {
				active0(sender, command, args);
			} else if(args[0].equalsIgnoreCase("version") == true) {
				success(sender, "You are running SpawnerMeta v#0", SpawnerMeta.PLUGIN_VERSION);
			} else sender.sendMessage(help);
		}
		return false;
	}

	private static void active0(CommandSender sender, Command command, String[] args) {
		String help0 = help(command, "active", "world^");
		if(args.length < 2) sender.sendMessage(help0);
		else {
			String w = args[1];
			if(w.equalsIgnoreCase("#all") == true) {
				int a = GeneratorRegistry.active(null);
				success(sender, "There are #0 active spawner" + (a > 1 ? "s" : ""), a);
			} else {
				World world = Bukkit.getWorld(w);
				if(world == null) warn(sender, "This world (#0) does not exist!", w);
				else {
					int a = GeneratorRegistry.active(world);
					success(sender, "There are #0 active spawner in world (#1)",
							a, world.getName());
					
				}
			}
		}
	}

	private static void disable0(CommandSender sender, Command command, String[] args) {
		String help0 = help(command, "disable", "true", "false");
		if(args.length < 2) sender.sendMessage(help0);
		else if(args[1].equalsIgnoreCase("true") == false && args[1].equalsIgnoreCase("false") == false) sender.sendMessage(help0);
		else {
			Settings.settings.spawning = !Boolean.parseBoolean(args[1]);
			success(sender, "Spawner spawning #0!", Settings.settings.spawning ? "enabled" : "disabled");
		}
	}

	private static void location0(CommandSender sender, Command command, String[] args) {
		String help0 = help(command, "location", "view", "clear", "validate") + extra("player^") + extra("world^?");
		if(args.length < 2) sender.sendMessage(help0);
		else if(args[1].equalsIgnoreCase("view") == true) {
			String help1 = help(command, "location view", "player^") + extra("world^?");
			if(args.length < 3) sender.sendMessage(help1);
			else {
				String name = args[2];
				List<World> worlds;
				if(args.length > 3) {
					String w = args[3];
					World world = Bukkit.getWorld(w);
					if(world == null) {
						warn(sender, "This world (#0) does not exist!", w);
						return;
					}
					worlds = List.of(world);
				} else worlds = Bukkit.getWorlds();
				IPlayerData il = LocationRegistry.find(name);
				if(il == null) {
					warn(sender, "This player does not have any placed spawners!");
					return;
				}
				List<World> has = new LinkedList<>();
				List<Set<Location>> locations = new LinkedList<>();
				
				worlds.stream().forEach(world -> {
					Set<Location> set = il.get(world);
					if(set.isEmpty() == true) return;
					has.add(world);
					locations.add(set);
				});
				if(has.isEmpty() == true) warn(sender, "This player does not have any placed spawners in this world!");
				else {
					send(sender, Text.color(Colors.aqua) + "Spawner locations for "
							+ Text.color(Colors.orange) + name + Text.color(Colors.aqua) + ":");
					for(int i = 0, j = 0; i < has.size(); i++, j = 0) {
						send(sender, Text.color(Colors.gray_75) + "  (" + has.get(i).getName() + ")");
						for(Location l : locations.get(i)) {
							send(sender, Text.color(Colors.lime) + ++j + ": "
									+ Text.color(Colors.gray_75) + LocationFile.parse(FinalPos.of(l)));
						}
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("clear") == true) {
			String help1 = help(command, "location clear", "player^") + extra("world^?");
			if(args.length < 3) sender.sendMessage(help1);
			else {
				String name = args[2];
				List<World> worlds;
				if(args.length > 3) {
					String w = args[3];
					World world = Bukkit.getWorld(w);
					if(world == null) {
						warn(sender, "This world (#0) does not exist!", w);
						return;
					}
					worlds = List.of(world);
				} else worlds = Bukkit.getWorlds();
				IPlayerData il = LocationRegistry.find(name);
				if(il == null) {
					warn(sender, "This player does not have any placed spawners!");
					return;
				}
				int r = worlds.stream()
						.mapToInt(il::clear)
						.sum();
				if(r <= 0) send(sender, Text.color(Colors.orange) + "No spawners where cleared!");
				else send(sender, Text.color(Colors.orange) + "Cleared " + Text.color(Colors.aqua)
						+ r + Text.color(Colors.orange) + " spawner" + (r > 1 ? "s!" : "!"));
			}
		} else if(args[1].equalsIgnoreCase("validate") == true) {
			String help1 = help(command, "location validate", "player^") + extra("world^?");
			if(args.length < 3) sender.sendMessage(help1);
			else {
				String name = args[2];
				List<World> worlds;
				if(args.length > 3) {
					String w = args[3];
					World world = Bukkit.getWorld(w);
					if(world == null) {
						warn(sender, "This world (#0) does not exist!", w);
						return;
					}
					worlds = List.of(world);
				} else worlds = Bukkit.getWorlds();
				IPlayerData il = LocationRegistry.find(name);
				if(il == null) {
					warn(sender, "This player does not have any placed spawners!");
					return;
				}
				int r = worlds.stream()
						.mapToInt(il::validate)
						.sum();
				if(r <= 0) send(sender, Text.color(Colors.lime) + "No invalid spawners were found!");
				else send(sender, Text.color(Colors.lime) + "Removed " + Text.color(Colors.aqua)
						+ r + Text.color(Colors.lime) + " invalid spawner" + (r > 1 ? "s!" : "!"));
			}
		} else send(sender, help0);
	}

	private static void modify0(CommandSender sender, Command command, String[] args, Player player) {
		String help0 = help(command, "modify", "type", "stack", "entities", "charges");
		if(player == null) warn(sender, "Cannot use this command in console!");
		else if(args.length < 2) sender.sendMessage(help0);
		else if(args[1].equalsIgnoreCase("type") == true) {
			String help1 = help(command, "modify type", "type^");
			if(args.length < 3) sender.sendMessage(help1);
			else {
				String a = args[2].toUpperCase();
				SpawnerType type = SpawnerType.of(a);
				if(type == null) warn(sender, "Invalid type!");
				else {
					Block block = player.getTargetBlock(null, 10);
					if(block == null) warn(sender, "Target spawner not found!");
					else if(block.getType() != Material.SPAWNER) warn(sender, "Target spawner is not a spawner!");
					else {
						DataManager.setType(block, type);
						GeneratorRegistry.update(block);
						success(sender, "Spawner type set to #0!", type.formated());
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("stack") == true) {
			String help1 = help(command, "modify stack", "(+/-) value*");
			if(args.length < 3) sender.sendMessage(help1);
			else {
				String a = args[2];
				if(Utility.isInteger(a) == false) warn(sender, "Invalid value!");
				else {
					int s = Integer.parseInt(a);
					Block block = player.getTargetBlock(null, 10);
					if(block == null) warn(sender, "Target spawner not found!");
					else if(block.getType() != Material.SPAWNER) warn(sender, "Target spawner is not a spawner!");
					else {
						if(a.charAt(0) == '+' || a.charAt(0) == '-') s += DataManager.getStack(block);
						DataManager.setStack(block, s < 1 ? 1 : s);
						GeneratorRegistry.update(block);
						success(sender, "Spawner stack size set to #0!", s);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("entities") == true) {
			String help1 = help(command, "modify entities", "(+/-) value*");
			if(args.length < 3) sender.sendMessage(help1);
			else {
				String a = args[2];
				boolean inf = a.equalsIgnoreCase("infinite");
				if(Utility.isInteger(a) == false && inf == false) warn(sender, "Invalid value!");
				else {
					int s = inf ? 1_500_000_000 : Integer.parseInt(a);
					Block block = player.getTargetBlock(null, 10);
					if(block == null) warn(sender, "Target spawner not found!");
					else if(block.getType() != Material.SPAWNER) warn(sender, "Target spawner is not a spawner!");
					else {
						if(a.charAt(0) == '+' || a.charAt(0) == '-') s += DataManager.getSpawnable(block);
						DataManager.setSpawnable(block, s < 1 ? 1 : s);
						GeneratorRegistry.update(block);
						success(sender, "Spawner spawnable entity set to #0!", inf ? "infinite" : s);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("charges") == true) {
			String help1 = help(command, "modify charges", "(+/-) value*");
			if(args.length < 3) sender.sendMessage(help1);
			else {
				String a = args[2];
				boolean inf = a.equalsIgnoreCase("infinite");
				if(Utility.isInteger(a) == false && inf == false) warn(sender, "Invalid value!");
				else {
					int s = inf ? 1_500_000_000 : Integer.parseInt(a);
					Block block = player.getTargetBlock(null, 10);
					if(block == null) warn(sender, "Target spawner not found!");
					else if(block.getType() != Material.SPAWNER) warn(sender, "Target spawner is not a spawner!");
					else {
						if(a.charAt(0) == '+' || a.charAt(0) == '-') s += DataManager.getCharges(block);
						DataManager.setCharges(block, s);
						GeneratorRegistry.update(block);
						success(sender, "Spawner charges set to #0!", inf ? "infinite" : s);
					}
				}
			}
		} else sender.sendMessage(help0);
	}

	private static void give0(CommandSender sender, Command command, String[] args, Player player) {
		String help0 = help(command, "give", "type^") + extra("amount*") + extra("player?") + extra("values?");
		if(args.length < 2) sender.sendMessage(help0);
		else {
			String a = args[1];
			SpawnerType type = SpawnerType.of(a);
			if(type == null) warn(sender, "Invalid entity type!");
			else {
				Messagable m = new Messagable(player);
				boolean empty = type == SpawnerType.EMPTY;
				if(Settings.settings.disabled(type) == true) warn(sender, "This spawner is disabled!");
				else if(args.length < 3) {
					if(player != null) {
						List<ItemStack> items = DataManager.getSpawners(type, 1, empty, true);
						if(items.isEmpty() == true) warn(sender, "Could not create any items!");
						else {
							m.send(Language.list("Spawners.give.success-single", "type", type.formated()));
							player.getInventory().addItem(items.get(0));
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 2f);
						}
					}
				} else if(Utility.isInteger(args[2]) == false) warn(sender, "Invalid amount!");
				else {
					int amount = Integer.parseInt(args[2]);
					if(amount < 1) warn(sender, "Amount must be greater then 0!");
					else if(args.length < 4) {
						if(player != null) {
							m.send(Language.list("Spawners.give.success",
									"amount", amount, "type", type.formated()));
							player.getInventory().addItem(DataManager.getSpawners(type, amount, empty, true).get(0));
						} else warn(sender, "You must define a player to use this command in console!");
					} else {
						Player getter = Bukkit.getPlayer(args[3]);
						if(getter == null) warn(sender, "Player not online!");
						else if(args.length < 5) {
							ItemStack item = DataManager.getSpawners(type, amount, empty, true).get(0);
							ItemMatcher.add(getter, item);
						} else {
							String values = "";
							for(int i = 4; i < args.length; i++) values += args[i];
							if(values.matches("((\\d+|-)[,;:]){2}(\\d+|-)([,;:](\\d+|-|[a-z]+)){0,2}") == true) {
								List<ItemStack> items = DataManager.getSpawner(type, values, amount, empty);
								if(items.isEmpty() == true) warn(sender, "Unable to read values (#0)!", values);
								else items.forEach(item -> ItemMatcher.add(getter, item));
							} else warn(sender, "Unable to read values (#0)!", values);
						}
					}
				}
			}
		}
	}

	private static void update0(CommandSender sender, Command command, String[] args, Player player) {
		String help0 = help(command, "update", "#all", "configuration", "language", "shop", "spawners");
		if(args.length < 2) sender.sendMessage(help0);
		else if(args[1].equalsIgnoreCase("#all") == true) {
			update((byte) 0x1f, sender, player);
		} else if(args[1].equalsIgnoreCase("configuration") == true) {
			update((byte) 0x1, sender, player);
		} else if(args[1].equalsIgnoreCase("language") == true) {
			update((byte) 0x2, sender, player);
		} else if(args[1].equalsIgnoreCase("shop") == true) {
			update((byte) 0x4, sender, player);
		} else if(args[1].equalsIgnoreCase("spawners") == true) {
			update((byte) 0x8, sender, player);
		} else sender.sendMessage(help0);
	}
	
	private static void update(byte i, CommandSender sender, Player player) {
		if(is(i, 0) == true) {
			sender.sendMessage(c6 + "(!) " + c0 + "Updating configuration...");
			if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
			HookRegistry.ECONOMY.load();
			Configuration.initialize();
			EventListeners.update();
			GeneratorRegistry.retime(false);
		}
		if(is(i, 1) == true) {
			sender.sendMessage(c6 + "(!) " + c0 + "Updating language...");
			if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
			CF.l.load();
		}
		if(is(i, 2) == true) {
			sender.sendMessage(c6 + "(!) " + c0 + "Updating shop...");
			if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
			ShopRegistry.initialize();
		}
		if(is(i, 3) == true) {
			sender.sendMessage(c6 + "(!) " + c0 + "Updating spawners...");
			if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
			GeneratorRegistry.reload();
		}
	}
	
	private static boolean is(byte i, int j) {
		return ((i >> j) & 1) == 1;
	}
	
	private static final String c0 = Text.color(Colors.aqua),
			c1 = Text.color(Colors.green),
			c2 = Text.color(Colors.purple_50),
			c3 = Text.color(Colors.purple),
			c4 = Text.color(Colors.yellow),
			c5 = Text.color(Colors.orange),
			c6 = Text.color(Colors.green_50),
			c7 = Text.color(Colors.red_50);

	private static String help(Command command, String arg, String... ss) {
		String help = c3 + "Usage: " + c0 + "/" + command.getLabel();
		if(arg != null) help += " " + arg;
		String a = c1 + " [";
		for(int i = 0, l = ss.length - 1; i < ss.length; i++) {
			a += c2 + ss[i];
			if(i < l) a += c0 + "/";
		}
		return help + a + c1 + "]";
	}
	
	private static String extra(String... ss) {
		String a = c1 + " [";
		for(int i = 0, l = ss.length - 1; i < ss.length; i++) {
			a += c2 + ss[i];
			if(i < l) a += c0 + "/";
		}
		return a + c1 + "]";
	}
	
	public static void warn(CommandSender sender, String warning, Object... os) {
		String w = c7 + "(!) " + c5 + warning;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, c4 + os[i].toString() + c5);
		if(sender instanceof Player == false) w = ChatColor.stripColor(w);
		sender.sendMessage(w);
	}
	
	public static void success(CommandSender sender, String success, Object... os) {
		String w = c6 + "(!) " + c1 + success;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, c0 + os[i].toString() + c1);
		if(sender instanceof Player == false) w = ChatColor.stripColor(w);
		sender.sendMessage(w);
	}
	
	public static void send(CommandSender sender, String message) {
		if(sender instanceof Player == false) message = ChatColor.stripColor(message);
		sender.sendMessage(message);
	}
	
	public static List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> l = new ArrayList<>();
		if(command.equals(CommandManager.SPAWNERMETA) == true) {
			if(args.length < 1) return null;
			else if(args.length < 2) return sm(args[0]);
			else if(args[0].equalsIgnoreCase("update") == true) {
				if(args.length < 3) return up(args[1]);
				else return l;
			} else if(args[0].equalsIgnoreCase("give") == true) {
				if(args.length < 3) return entities(args[1]);
				else if(args.length < 4) return l;
				else {
					if(Utility.isInteger(args[2]) == true) {
						if(args.length < 5) return pl(args[3]);
						else return l;
					} else return l;
				}
			} else if(args[0].equalsIgnoreCase("modify") == true) {
				if(args.length < 3) return mo(args[1]);
				else if(args[1].equalsIgnoreCase("type") == true) {
					if(args.length < 4) return entities(args[2]);
					else return l;
				} else if(args[1].equalsIgnoreCase("charges") == true
						|| args[1].equalsIgnoreCase("entities") == true) {
					if(args.length < 4) return inf(args[2]);
					else return l;
				} else return l;
			} else if(args[0].equalsIgnoreCase("location") == true) {
				if(args.length < 3) return ll(args[1]);
				else if(args.length < 4) return oo(args[2]);
				else if(args.length < 5) return ww(args[3]);
				else return l;
			} else if(args[0].equalsIgnoreCase("disable") == true) {
				if(args.length < 3) return tf(args[1]);
				else return l;
			} else if(args[0].equalsIgnoreCase("active") == true) {
				if(args.length < 3) return wa(args[1]);
				else return l;
			} else return l;
		}
		return null;
	}
	
	private static List<String> sm(String s) {
		List<String> l = new ArrayList<>();
		l.add("update");
		l.add("modify");
		l.add("location");
		l.add("give");
		l.add("disable");
		l.add("active");
		l.add("version");
		return reduce(l, s);
	}

	private static List<String> pl(String s) {
		return reduce(Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.collect(Collectors.toList()), s);
	}

	private static List<String> tf(String s) {
		List<String> l = new ArrayList<>();
		l.add("true");
		l.add("false");
		return reduce(l, s);
	}

	private static List<String> mo(String s) {
		List<String> l = new ArrayList<>();
		l.add("type");
		l.add("stack");
		l.add("entities");
		l.add("charges");
		return reduce(l, s);
	}

	private static List<String> ll(String s) {
		List<String> l = new ArrayList<>();
		l.add("view");
		l.add("clear");
		l.add("validate");
		return reduce(l, s);
	}

	private static List<String> oo(String s) {
		return reduce(LocationRegistry.names(), s);
	}

	private static List<String> ww(String s) {
		return reduce(Bukkit.getWorlds()
				.stream()
				.map(World::getName)
				.toList(), s);
	}

	private static List<String> wa(String s) {
		List<String> list = Bukkit.getWorlds()
				.stream()
				.map(World::getName)
				.collect(Collectors.toList());
		list.add("#all");
		return reduce(list, s);
	}

	private static List<String> up(String s) {
		List<String> l = new ArrayList<>();
		l.add("#all");
		l.add("configuration");
		l.add("language");
		l.add("shop");
		l.add("spawners");
		return reduce(l, s);
	}

	private static List<String> inf(String s) {
		List<String> l = new ArrayList<>();
		l.add("infinite");
		return reduce(l, s);
	}

	private static List<String> entities(String s) {
		return reduce(Stream.of(SpawnerType.values())
				.filter(SpawnerType::exists)
				.map(SpawnerType::name)
				.collect(Collectors.toList()), s);
	}

	public static List<String> reduce(List<String> l, String s) {
		if(s.isEmpty() == true) return l;
		return l.stream()
				.filter(a -> a.toLowerCase().contains(s.toLowerCase()))
				.collect(Collectors.toList());
	}
	

}
