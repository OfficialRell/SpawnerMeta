package mc.rellox.spawnermeta.version.types;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.utility.reflect.type.Invoker;
import mc.rellox.spawnermeta.version.IVersion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class IVersion26 implements IVersion {

	@Override
	public void send(Collection<? extends Player> players, Object... os) {
		players.forEach(player -> {
			Object a = RF.direct(player, "getHandle");
			Object b = RF.access(a, "connection").get();
			Class<?> c = RF.get("net.minecraft.network.protocol.Packet");
			Invoker<?> d = RF.order(b, "send", c);
			Stream.of(os).forEach(d::invoke);
		});
	}

	@Override
	public Object spawn(Object entity) {
		Class<?> a = RF.get("net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
		Class<?> b = RF.get("net.minecraft.world.entity.Entity");
		Class<?> c = RF.get("net.minecraft.core.BlockPos");
		Object d = RF.fetch(c, "ZERO"); // empty block position
		Object m = RF.build(a, b, int.class, c).instance(entity, 0, d);
		double x = RF.direct(entity, "getX", double.class);
		RF.access(m, "x", double.class).set(x);
		double y = RF.direct(entity, "getY", double.class);
		RF.access(m, "y", double.class).set(y);
		double z = RF.direct(entity, "getZ", double.class);
		RF.access(m, "z", double.class).set(z);
		return m;
	}

	@Override
	public Object meta(Object entity) {
		Class<?> a = RF.get("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket");
		Entity bukkit = RF.direct(entity, "getBukkitEntity", Entity.class);
		int b = bukkit.getEntityId();
		Object c = RF.direct(entity, "getEntityData");
		Object d = RF.direct(c, "getNonDefaultValues");
        return RF.build(a, int.class, List.class).instance(b, d);
	}

	@Override
	public Object destroy(Object entity) {
		Class<?> a = RF.get("net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket");
		Entity bukkit = RF.direct(entity, "getBukkitEntity", Entity.class);
		int b = bukkit.getEntityId();
        return RF.build(a, int[].class).instance(new int[] {b});
	}

	@Override
	public Object hologram(Location l, String name) {
		Class<?> a = RF.get("net.minecraft.world.level.Level");
		Object b = RF.direct(l.getWorld(), "getHandle");
		Class<?> c = RF.get("net.minecraft.world.entity.decoration.ArmorStand");
		Object d = RF.build(c, a, double.class, double.class, double.class)
				.instance(b, l.getX(), l.getY(), l.getZ());
		RF.order(d, "setMarker", boolean.class).invoke(true);
		RF.order(d, "setInvisible", boolean.class).invoke(true);
		name(d, name);
		RF.order(d, "setCustomNameVisible", boolean.class).invoke(true);
		
		return d;
	}
	
	@Override
	public void name(Object entity, String name) {
		Class<?> a = RF.get("net.minecraft.network.chat.Component");
		Class<?> b = RF.craft("util.CraftChatMessage");
		Object c = RF.order(b, "fromStringOrNull", String.class).invoke(name);
		RF.order(entity, "setCustomName", a).invoke(c);
	}

}
