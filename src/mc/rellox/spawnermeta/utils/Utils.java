package mc.rellox.spawnermeta.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Reflections.RF.Accessor;
import mc.rellox.spawnermeta.utils.Version.VersionType;

public final class Utils {
	
	private static final Random R = new Random();
	
	public static boolean op(Player player) {
		return player.isOp() == true && player.getGameMode() == GameMode.CREATIVE;
	}

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
	
	public static Location center(Block block) {
		return block.getLocation().add(0.5, 0.5, 0.5);
	}
	
	public static int[] location(Block block) {
		return new int[] {block.getX(), block.getY(), block.getZ()};
	}
	
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
	
	public static String displayName(ItemStack item) {
		Class<?> c = RF.craft("inventory.CraftItemStack");
		try {
			Method m = c.getMethod("asNMSCopy", ItemStack.class);
			Object o = m.invoke(null, item);
			c = o.getClass();
			if(Version.version == VersionType.v_18_1) {
				m = c.getDeclaredMethod("v");
				o = m.invoke(o);
				m = o.getClass().getMethod("a");
			} else if(Version.version == VersionType.v_18_2) {
				m = c.getDeclaredMethod("w");
				o = m.invoke(o);
				m = o.getClass().getMethod("a");
			} else if(Version.version == VersionType.v_19_1
					|| Version.version == VersionType.v_19_2
					|| Version.version == VersionType.v_19_3) {
				m = c.getDeclaredMethod("x");
				o = m.invoke(o);
				m = o.getClass().getMethod("getString");
			} else {
				m = c.getDeclaredMethod("getName");
				o = m.invoke(o);
				m = o.getClass().getMethod("getText");
			}
			String name = (String) m.invoke(o);
			if(name == null || name.isEmpty() == true) {
				m = o.getClass().getMethod("getString");
				name = (String) m.invoke(o);
			}
			return name;
		} catch(Exception e) {
			return "null";
		}
	}
	
	public static void hideCustomFlags(ItemMeta meta) {
		try {
			Class<?> craft_meta_class = RF.craft("inventory.CraftMetaItem");
			Object craft_meta = craft_meta_class.cast(meta);
			if(meta.getClass().equals(craft_meta_class) == false) return;
			Accessor<Integer> a = RF.access(craft_meta, "hideFlag", int.class);
			int h = a.field(0);
			a.set(h | 64);
		} catch (Exception e) {}
	}

	public static double round(double d) {
		int i = (int) (d * 100);
		return (double) (i / 100.0);
	}
	
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
	
	public static boolean nulled(ItemStack item) {
		return item == null ? true : item.getType() == Material.AIR ? true : false;
	}
	
	public static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	public static boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static boolean isValid(String s) {
		for(char c : s.toCharArray()) if(isLetter(c) == false && isNumber(c) == false && c != '_' && c != '.') return false;
		return true;
	}

	public static boolean chance(double chance) {
		return R.nextDouble() * 100 < chance;
	}

	public static int random(int r) {
		return R.nextInt(r);
	}

	public static <E> E random(List<E> list) {
		return list.get(R.nextInt(list.size()));
	}

	public static void check(final int id, final Consumer<String> action) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try(InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id).openStream();
						Scanner sc = new Scanner(is)) {
					if(sc.hasNext() == true) action.accept(sc.next());
				} catch(IOException x) {}
			}
		}.runTaskLaterAsynchronously(SpawnerMeta.instance(), 50);
	}

}
