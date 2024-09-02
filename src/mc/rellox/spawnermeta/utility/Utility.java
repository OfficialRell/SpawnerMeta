package mc.rellox.spawnermeta.utility;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.utility.reflect.type.Accessor;
import mc.rellox.spawnermeta.version.Version;
import mc.rellox.spawnermeta.version.Version.VersionType;

public final class Utility {
	
	private static final Random R = new Random();
	
	public static boolean op(Player player) {
		return player.isOp() == true && player.getGameMode() == GameMode.CREATIVE;
	}
	
	// Experience

	public static void changeExp(Player player, int exp) {
		player.giveExp(exp);
	}

	public static int getExp(Player player) {
		int v = player.getLevel(), l = getExpAtLevel(v), u = getExpToLevelUp(v);
		return l + Math.round(u * player.getExp());
	}

	public static int getExpAtLevel(int level) {
		int lq = level * level;
		if(level <= 16) return (int) (lq + 6 * level);
		else if(level <= 31) return (int) (2.5 * lq - 40.5 * level + 360.0);
		else return (int) (4.5 * lq - 162.5 * level + 2220.0);
	}

	public static int getExpToLevelUp(int level) {
		if(level <= 15) return 2 * level + 7;
		else if(level <= 30) return 5 * level - 38;
		else return 9 * level - 158;
	}
	
	// Locations
	
	public static Location center(Block block) {
		return block.getLocation().add(0.5, 0.5, 0.5);
	}
	
	public static int[] location(Block block) {
		return new int[] {block.getX(), block.getY(), block.getZ()};
	}
	
	// Roman numerals
	
	// 1-I 5-V 10-X 50-L 100-C 500-D 1000-M
	public static String roman(int i) {
		if(i <= 0 || i > 5000) return "" + i;
		StringBuilder sb = new StringBuilder();
		if(i >= 1000) do sb.append("M"); while((i -= 1000) >= 1000);
		if(i >= 900) i -= r(sb, "CM", 900);
		if(i >= 500) i -= r(sb, "D", 500);
		if(i >= 400) i -= r(sb, "CD", 400);
		if(i >= 100) do sb.append("C"); while((i -= 100) >= 100);
		if(i >= 90) i -= r(sb, "XC", 90);
		if(i >= 50) i -= r(sb, "L", 50);
		if(i >= 40) i -= r(sb, "XL", 40);
		if(i >= 10) do sb.append("X"); while((i -= 10) >= 10);
		if(i >= 9) i -= r(sb, "IX", 9);
		if(i >= 5) i -= r(sb, "V", 5);
		if(i >= 4) i -= r(sb, "IV", 4);
		if(i >= 1) do sb.append("I"); while(--i >= 1);
		return sb.toString();
	}
	
	private static int r(StringBuilder sb, String s, int i) {
		sb.append(s);
		return i;
	}
	
	// Items
	
	public static String displayName(ItemStack item) {
		try {
			Class<?> clazz = RF.craft("inventory.CraftItemStack");
			Object nms_item = RF.order(clazz, "asNMSCopy", ItemStack.class).invoke(item);
			String a, b = "getString";
			
			if(Version.version == VersionType.v_18_1) {
				a = "v";
				b = "a";
			} else if(Version.version == VersionType.v_18_2) {
				a = "w";
				b = "a";
			} else if(Version.version == VersionType.v_19_1
					|| Version.version == VersionType.v_19_2
					|| Version.version == VersionType.v_19_3) {
				a ="x";
			} else if(Version.version == VersionType.v_20_1
					|| Version.version == VersionType.v_20_2
					|| Version.version == VersionType.v_20_3) {
				a = "y";
			} else if(Version.version == VersionType.v_20_4) {
				a = "x";
			} else if(Version.version == VersionType.v_21_1) {
				a = "w";
			} else {
				a = "getName";
				b = "getText";
			}
			
			Object component = RF.direct(nms_item, a);
			String name = RF.direct(component, b, String.class);
			
			if(name == null)
				Bukkit.getLogger().warning("Null name got returned when trying to fetch item name");
			
			return ChatColor.stripColor(name);
		} catch(Exception e) {
			Bukkit.getLogger().warning("Cannot get item display name");
			return "null";
		}
	}
	
	public static void hideCustomFlags(ItemMeta meta) {
		try {
			Accessor<Integer> a = RF.access(meta, "hideFlag", int.class, false);
			int h = a.get(0);
			a.set(h | 64);
		} catch (Exception e) {
			RF.debug(new RuntimeException("Unable to apply hidden item flag"));
		}
	}
	
	public static boolean isWindCharge(Entity entity) {
		if(Version.version.high(VersionType.v_21_1) == false) return false;
		return switch (entity.getType().name()) {
		case "WIND_CHARGE", "BREEZE_WIND_CHARGE" -> true;
		default -> false;
		};
	}
	
	public static boolean nulled(ItemStack item) {
		return item == null ? true : item.getType() == Material.AIR ? true : false;
	}
	
	// Math

	public static double round(double d) {
		int i = (int) (d * 100);
		return (double) (i / 100.0);
	}

	public static boolean chance(double chance) {
		return R.nextDouble() * 100 < chance;
	}

	public static int random(int r) {
		return R.nextInt(r);
	}
	
	public static int between(int a, int b) {
		return random(b - a + 1) + a;
	}

	public static <E> E random(List<E> list) {
		int size = list.size();
		return size > 0 ? list.get(R.nextInt(size)) : null;
	}
	
	// Validation
	
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static boolean isInteger(String... l) {
		if(l == null) return false;
		for(String s : l) if(isInteger(s) == false) return false;
		return true;
	}
	
	public static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	public static boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static boolean isValid(String s) {
		for(char c : s.toCharArray())
			if(isLetter(c) == false && isNumber(c) == false && c != '_' && c != '.')
				return false;
		return true;
	}
	
	public static boolean isItem(EntityType type) {
		return type == RF.enumerate(EntityType.class, "DROPPED_ITEM", "ITEM");
	}
	
	// Types
	
	public static final Particle particle_sharpness = RF.enumerate(Particle.class, "CRIT_MAGIC", "ENCHANTED_HIT");
	public static final Particle particle_redstone = RF.enumerate(Particle.class, "REDSTONE", "DUST");
	public static final Particle particle_happy = RF.enumerate(Particle.class, "VILLAGER_HAPPY", "HAPPY_VILLAGER");
	public static final Particle particle_angry = RF.enumerate(Particle.class, "VILLAGER_ANGRY", "ANGRY_VILLAGER");
	public static final Particle particle_firework = RF.enumerate(Particle.class, "FIREWORKS_SPARK", "FIREWORK");
	
	@SuppressWarnings("deprecation")
	public static final Enchantment enchantment_power = RF.enumerate(Enchantment::getByName, "ARROW_DAMAGE", "POWER");
	
	// Version check

	public static void check(final int id, final Consumer<String> action) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try(InputStream is = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + id)
						.toURL().openStream();
						Scanner sc = new Scanner(is)) {
					if(sc.hasNext() == true) action.accept(sc.next());
				} catch(Exception x) {}
			}
		}.runTaskLaterAsynchronously(SpawnerMeta.instance(), 50);
	}

}
