package mc.rellox.spawnermeta.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Configuration;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.events.EventListeners;
import mc.rellox.spawnermeta.holograms.HologramRegistry;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.shop.ShopRegistry;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.utils.DataManager;
import mc.rellox.spawnermeta.utils.Messagable;
import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Utils;
import mc.rellox.spawnermeta.views.SpawnerEditor;
import mc.rellox.spawnermeta.views.SpawnerUpgrade;
import mc.rellox.spawnermeta.views.SpawnerViewLayout;

public final class CommandManager {

	private static final Command SPAWNERMETA = Bukkit.getPluginCommand("spawnermeta");
	
	public static void initialize() {
		String name = Settings.settings.command_view;
		try {
			CommandMap scm = RF.access(Bukkit.getServer(), "commandMap", CommandMap.class).field();
			scm.register(name, new CommandSpawners(name));
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner view command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
		name = Settings.settings.command_shop;
		try {
			CommandMap scm = RF.access(Bukkit.getServer(), "commandMap", CommandMap.class).field();
			scm.register(name, new CommandShop(name));
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner shop command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
		name = Settings.settings.command_drops;
		try {
			CommandMap scm = RF.access(Bukkit.getServer(), "commandMap", CommandMap.class).field();
			scm.register(name, new CommandDrops(name));
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta" + ChatColor.DARK_PURPLE + "] "
					+ ChatColor.DARK_RED + "An error accured while registering spawner drops command (" + name + ")! "
					+ "Try changing it, and restart your server!");
			return;
		}
	}

	public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (sender instanceof Player) ? (Player) sender : null;
		if(command.equals(CommandManager.SPAWNERMETA) == true) {
			String help = help(command, null, "update", "give", "edit", "modify", "disable");
			if(args.length < 1) sender.sendMessage(help);
			else if(args[0].equalsIgnoreCase("update") == true) {
				String help0 = help(command, "update", "configuration", "language", "shop", "holograms", "spawners");
				if(args.length < 2) sender.sendMessage(help0);
				else if(args[1].equalsIgnoreCase("configuration") == true) {
					sender.sendMessage(ChatColor.DARK_GREEN + "(!) " + ChatColor.AQUA + "Updating configuration...");
					if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
					SpawnerMeta.ECONOMY.load();
					Configuration.initialize();
					EventListeners.update();
					SpawnerViewLayout.initialize();
				} else if(args[1].equalsIgnoreCase("language") == true) {
					sender.sendMessage(ChatColor.DARK_GREEN + "(!) " + ChatColor.AQUA + "Updating language...");
					if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
					Language.initialize();
				} else if(args[1].equalsIgnoreCase("shop") == true) {
					sender.sendMessage(ChatColor.DARK_GREEN + "(!) " + ChatColor.AQUA + "Updating shop...");
					if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
					ShopRegistry.initialize();
				} else if(args[1].equalsIgnoreCase("holograms") == true) {
					sender.sendMessage(ChatColor.DARK_GREEN + "(!) " + ChatColor.AQUA + "Updating holograms...");
					if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
					HologramRegistry.initialize();
				} else if(args[1].equalsIgnoreCase("spawners") == true) {
					sender.sendMessage(ChatColor.DARK_GREEN + "(!) " + ChatColor.AQUA + "Updating spawners...");
					if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
					Settings.settings.update_spawners();
				} else sender.sendMessage(help0);
			} else if(args[0].equalsIgnoreCase("give") == true) {
				String help0 = help(command, "give", "type^") + extra("amount*") + extra("player?") + extra("values?");
				if(args.length < 2) sender.sendMessage(help0);
				else {
					String a = args[1];
					SpawnerType type = SpawnerType.of(a);
					if(type == null) warn(sender, "Invalid entity type!");
					else {
						boolean empty = type == SpawnerType.EMPTY;
						if(Settings.settings.disabled(type) == true) warn(sender, "This spawner is disabled!");
						else if(args.length < 3) {
							if(player != null) {
								success(sender, "Added #0 to your inventory!", type.formated() + " Spawner");
								player.getInventory().addItem(DataManager.getSpawners(type, 1, empty, true).get(0));
								player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 2f);
							}
						} else if(Utils.isInteger(args[2]) == false) warn(sender, "Invalid amount!");
						else {
							int amount = Integer.parseInt(args[2]);
							if(amount < 1) warn(sender, "Amount must be greater then 0!");
							else if(args.length < 4) {
								if(player != null) {
									new Messagable(player).send(Language.list("Spawners.give.success",
											"amount", amount, "type", type.formated()));
									player.getInventory().addItem(DataManager.getSpawners(type, amount, empty, true).get(0));
								}
							} else {
								Player getter = Bukkit.getPlayer(args[3]);
								if(getter == null) warn(sender, "Player not online!");
								else if(args.length < 5) {
									ItemStack item = DataManager.getSpawners(type, amount, empty, true).get(0);
									ItemMatcher.add(getter, item);
								} else {
									String values = "";
									for(int i = 4; i < args.length; i++) values += args[i];
									List<ItemStack> items = DataManager.getSpawner(type, values, amount, empty);
									if(items.isEmpty() == true) warn(sender, "Unable to read values!");
									else items.forEach(item -> ItemMatcher.add(getter, item));
								}
							}
						}
					}
				}
			} else if(args[0].equalsIgnoreCase("edit") == true) {
				String help0 = help(command, "update", "edit", "open", "reset");
				if(args.length < 2) sender.sendMessage(help0);
				else if(args[1].equalsIgnoreCase("open") == true && player != null) SpawnerEditor.open(player);
				else if(args[1].equalsIgnoreCase("reset") == true) {
					SpawnerViewLayout.resetLayout();
					if(player != null) player.playSound(player.getEyeLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.5f, 2f);
					success(sender, "Reseting Spawner GUI Layout!");
				} else sender.sendMessage(help0);
			} else if(args[0].equalsIgnoreCase("modify") == true) {
				String help0 = help(command, "modify", "type", "stack", "entities", "charges");
				if(args.length < 2) sender.sendMessage(help0);
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
								SpawnerUpgrade.update(block);
								success(sender, "Spawner type set to #0!", type.formated());
							}
						}
					}
				} else if(args[1].equalsIgnoreCase("stack") == true) {
					String help1 = help(command, "modify stack", "(+/-) value*");
					if(args.length < 3) sender.sendMessage(help1);
					else {
						String a = args[2];
						if(Utils.isInteger(a) == false) warn(sender, "Invalid value!");
						else {
							int s = Integer.parseInt(a);
							Block block = player.getTargetBlock(null, 10);
							if(block == null) warn(sender, "Target spawner not found!");
							else if(block.getType() != Material.SPAWNER) warn(sender, "Target spawner is not a spawner!");
							else {
								if(a.charAt(0) == '+' || a.charAt(0) == '-') s += DataManager.getStack(block);
								DataManager.setStack(block, s < 1 ? 1 : s);
								SpawnerUpgrade.update(block);
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
						if(Utils.isInteger(a) == false && inf == false) warn(sender, "Invalid value!");
						else {
							int s = inf ? 1_500_000_000 : Integer.parseInt(a);
							Block block = player.getTargetBlock(null, 10);
							if(block == null) warn(sender, "Target spawner not found!");
							else if(block.getType() != Material.SPAWNER) warn(sender, "Target spawner is not a spawner!");
							else {
								if(a.charAt(0) == '+' || a.charAt(0) == '-') s += DataManager.getSpawnable(block);
								DataManager.setSpawnable(block, s < 1 ? 1 : s);
								SpawnerUpgrade.update(block);
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
						if(Utils.isInteger(a) == false && inf == false) warn(sender, "Invalid value!");
						else {
							int s = inf ? 1_500_000_000 : Integer.parseInt(a);
							Block block = player.getTargetBlock(null, 10);
							if(block == null) warn(sender, "Target spawner not found!");
							else if(block.getType() != Material.SPAWNER) warn(sender, "Target spawner is not a spawner!");
							else {
								if(a.charAt(0) == '+' || a.charAt(0) == '-') s += DataManager.getCharges(block);
								DataManager.setCharges(block, s);
								SpawnerUpgrade.update(block);
								success(sender, "Spawner charges set to #0!", inf ? "infinite" : s);
							}
						}
					}
				} else sender.sendMessage(help0);
			} else if(args[0].equalsIgnoreCase("disable") == true) {
				String help1 = help(command, "disable", "true", "false");
				if(args.length < 2) sender.sendMessage(help1);
				else if(args[1].equalsIgnoreCase("true") == false && args[1].equalsIgnoreCase("false") == false) sender.sendMessage(help1);
				else {
					Settings.settings.disable_spawning = Boolean.parseBoolean(args[1]);
					success(sender, "Spawner spawning #0!", Settings.settings.disable_spawning ? "disabled" : "enabled");
				}
			} else sender.sendMessage(help);
		}
		return false;
	}
	
	private static String help(Command command, String arg, String... ss) {
		String help = ChatColor.LIGHT_PURPLE + "Usage: " + ChatColor.AQUA + "/" + command.getLabel();
		if(arg != null) help += " " + arg;
		String a =  ChatColor.DARK_AQUA + " [";
		for(int i = 0, l = ss.length - 1; i < ss.length; i++) {
			a += ChatColor.DARK_PURPLE + ss[i];
			if(i < l) a += ChatColor.AQUA + "/";
		}
		return help + a + ChatColor.DARK_AQUA + "]";
	}
	
	private static String extra(String... ss) {
		String a =  ChatColor.DARK_AQUA + " [";
		for(int i = 0, l = ss.length - 1; i < ss.length; i++) {
			a += ChatColor.DARK_PURPLE + ss[i];
			if(i < l) a += ChatColor.AQUA + "/";
		}
		return a + ChatColor.DARK_AQUA + "]";
	}
	
	private static void warn(CommandSender sender, String warning, Object... os) {
		String w = ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + warning;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, ChatColor.YELLOW + os[i].toString() + ChatColor.GOLD);
		sender.sendMessage(w);
	}
	
	public static void success(CommandSender sender, String success, Object... os) {
		String w = ChatColor.DARK_GREEN + "(!) " + ChatColor.DARK_AQUA + success;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, ChatColor.AQUA + os[i].toString() + ChatColor.DARK_AQUA);
		sender.sendMessage(w);
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
					if(Utils.isInteger(args[2]) == true) {
						if(args.length < 5) return pl(args[3]);
						else return l;
					} else return l;
				}
			} else if(args[0].equalsIgnoreCase("edit") == true) {
				if(args.length < 3) return or(args[1]);
				else return l;
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
			} else if(args[0].equalsIgnoreCase("disable") == true) {
				if(args.length < 3) return tf(args[1]);
				else return l;
			} else return l;
		}
		return null;
	}
	
	private static List<String> sm(String s) {
		List<String> l = new ArrayList<>();
		l.add("update");
		l.add("give");
		l.add("edit");
		l.add("modify");
		l.add("disable");
		return reduce(l, s);
	}

	private static List<String> pl(String s) {
		return reduce(Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.collect(Collectors.toList()), s);
	}

	private static List<String> or(String s) {
		List<String> l = new ArrayList<>();
		l.add("open");
		l.add("reset");
		return reduce(l, s);
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

	private static List<String> up(String s) {
		List<String> l = new ArrayList<>();
		l.add("configuration");
		l.add("language");
		l.add("shop");
		l.add("spawners");
		l.add("holograms");
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

	private static List<String> reduce(List<String> l, String s) {
		if(s.isEmpty() == true) return l;
		return l.stream()
				.filter(a -> a.toLowerCase().contains(s.toLowerCase()))
				.collect(Collectors.toList());
	}
	

}
