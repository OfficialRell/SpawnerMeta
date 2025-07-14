package mc.rellox.spawnermeta.version.types;

import java.util.Collection;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.utility.reflect.type.Invoker;
import mc.rellox.spawnermeta.version.IVersion;

public class IVersion1_16_2 implements IVersion {

	@Override
	public void send(Collection<? extends Player> players, Object... os) {
		players.forEach(player -> {
			
			Object a = RF.direct(player, "getHandle");
			
			Object b = RF.fetch(a, "playerConnection");
			
			Class<?> c = RF.get("net.minecraft.server.v1_16_R2.Packet");
			
			Invoker<?> d = RF.order(b, "sendPacket", c);
			
			Stream.of(os).forEach(d::invoke);
		});
	}

	@Override
	public Object spawn(Object entity) {
		
		Class<?> a = RF.get("net.minecraft.server.v1_16_R2.PacketPlayOutSpawnEntityLiving");
		
		Class<?> b = RF.get("net.minecraft.server.v1_16_R2.EntityLiving");
		
		Object c = RF.build(a, b).instance(entity);
		
		return c;
	}

	@Override
	public Object meta(Object entity) {
		
		Class<?> a = RF.get("net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata");

		int b = RF.direct(entity, "getId", int.class);
		
		Object c = RF.direct(entity, "getDataWatcher");
		
		Class<?> d = RF.get("net.minecraft.server.v1_16_R2.DataWatcher");
		
		Object e =  RF.build(a, int.class, d, boolean.class).instance(b, c, true);
		
		return e;
	}

	@Override
	public Object destroy(Object entity) {

		Class<?> a = RF.get("net.minecraft.server.v1_16_R2.PacketPlayOutEntityDestroy");
		
		int b = RF.direct(entity, "getId", int.class);
		
		Object c = RF.build(a, int[].class).instance(new int[] {b});
		
		return c;
	}

	@Override
	public Object hologram(Location l, String name) {
		
		Class<?> a = RF.get("net.minecraft.server.v1_16_R2.World");
		
		Object b = RF.direct(l.getWorld(), "getHandle");
		
		Class<?> c = RF.get("net.minecraft.server.v1_16_R2.EntityArmorStand");
		
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
		
		Class<?> a = RF.get("net.minecraft.server.v1_16_R2.IChatBaseComponent");
		
		Class<?> b = RF.craft("util.CraftChatMessage");
		
		Object c = RF.order(b, "fromStringOrNull", String.class).invoke(name);
		
		RF.order(entity, "setCustomName", a).invoke(c);
	}

}
