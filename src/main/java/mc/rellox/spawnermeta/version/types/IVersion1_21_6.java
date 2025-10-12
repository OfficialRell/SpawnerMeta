package mc.rellox.spawnermeta.version.types;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.utility.reflect.type.Invoker;
import mc.rellox.spawnermeta.version.IVersion;

public class IVersion1_21_6 implements IVersion {

	@Override
	public void send(Collection<? extends Player> players, Object... os) {
		players.forEach(player -> {
			
			Object a = RF.direct(player, "getHandle");
			
			Object b = RF.access(a, "g").get();
			
			Class<?> c = RF.get("net.minecraft.network.protocol.Packet");
			
			Invoker<?> d = RF.order(b, "b", c);
			
			Stream.of(os).forEach(d::invoke);
		});
	}

	@Override
	public Object spawn(Object entity) {
		
		Class<?> a = RF.get("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
		
		Class<?> b = RF.get("net.minecraft.world.entity.Entity");
		
		Class<?> c = RF.get("net.minecraft.core.BlockPosition");
		
		Object d = RF.fetch(c, "c"); // empty block position
		
		Object m = RF.build(a, b, int.class, c).instance(entity, 0, d);

		double x = RF.direct(entity, "dK", double.class);
		RF.access(m, "e", double.class).set(x);
		double y = RF.direct(entity, "dM", double.class);
		RF.access(m, "f", double.class).set(y);
		double z = RF.direct(entity, "dQ", double.class);
		RF.access(m, "g", double.class).set(z);
		
		return m;
	}

	@Override
	public Object meta(Object entity) {
		
		Class<?> a = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");

		Entity bukkit = RF.direct(entity, "getBukkitEntity", Entity.class);
		int b = bukkit.getEntityId();
		
		Object c = RF.direct(entity, "aC");
		
		Object d = RF.direct(c, "c");
		
		Object e = RF.build(a, int.class, List.class).instance(b, d);
		
		return e;
	}

	@Override
	public Object destroy(Object entity) {

		Class<?> a = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");

		Entity bukkit = RF.direct(entity, "getBukkitEntity", Entity.class);
		int b = bukkit.getEntityId();
		
		Object c = RF.build(a, int[].class).instance(new int[] {b});
		
		return c;
	}

	@Override
	public Object hologram(Location l, String name) {
		
		Class<?> a = RF.get("net.minecraft.world.level.World");
		
		Object b = RF.direct(l.getWorld(), "getHandle");
		
		Class<?> c = RF.get("net.minecraft.world.entity.decoration.EntityArmorStand");
		
		Object d = RF.build(c, a, double.class, double.class, double.class)
				.instance(b, l.getX(), l.getY(), l.getZ());
		
		RF.order(d, "v", boolean.class).invoke(true);
		RF.order(d, "l", boolean.class).invoke(true);
		name(d, name);
		RF.order(d, "p", boolean.class).invoke(true);
		
		return d;
	}
	
	@Override
	public void name(Object entity, String name) {
		
		Class<?> a = RF.get("net.minecraft.network.chat.IChatBaseComponent");
		
		Class<?> b = RF.craft("util.CraftChatMessage");
		
		Object c = RF.order(b, "fromStringOrNull", String.class).invoke(name);
		
		RF.order(entity, "b", a).invoke(c);
	}

}
