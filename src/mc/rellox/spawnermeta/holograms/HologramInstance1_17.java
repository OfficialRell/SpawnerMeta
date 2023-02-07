package mc.rellox.spawnermeta.holograms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Reflections.RF.Invoker;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.decoration.EntityArmorStand;

public class HologramInstance1_17 implements HologramInstance {
	
	@Override
	public Object create(Location loc, String title) {
		EntityArmorStand entity = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(),
				loc.getX(), loc.getY(), loc.getZ());
		RF.order(entity, "setMarker", boolean.class).invoke(true);
		RF.order(entity, "setInvisible", boolean.class).invoke(true);
		RF.order(entity, "setCustomName", IChatBaseComponent.class).invoke(nbtText(title));
		RF.order(entity, "setCustomNameVisible", boolean.class).invoke(true);
		return entity;
	}

	@Override
	public void spawn(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntityLiving p0 = new PacketPlayOutSpawnEntityLiving(entity);
		int id = RF.order(entity, int.class, "getId").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "getDataWatcher").invoke();
		PacketPlayOutEntityMetadata p1 = new PacketPlayOutEntityMetadata(id, dw, true);
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
			Invoker<?> v = RF.order(pc, "sendPacket", Packet.class);
			v.invoke(p0); v.invoke(p1);
		});
	}

	@Override
	public void destroy(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		int id = RF.order(entity, int.class, "getId").invoke(0);
		PacketPlayOutEntityDestroy p0 =
				RF.construct(PacketPlayOutEntityDestroy.class, int[].class).instance(new int[] {id});
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
			RF.order(pc, "sendPacket", Packet.class).invoke(p0);
		});
	}

	@Override
	public void update(Object hologram, String title) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		RF.order(entity, "setCustomName", IChatBaseComponent.class).invoke(nbtText(title));
		int id = RF.order(entity, int.class, "getId").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "getDataWatcher").invoke();
		PacketPlayOutEntityMetadata p0 = new PacketPlayOutEntityMetadata(id, dw, true);
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
			RF.order(pc, "sendPacket", Packet.class).invoke(p0);
		});
	}
	
	@Override
	public void spawn(Player player, Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntityLiving p0 = new PacketPlayOutSpawnEntityLiving(entity);
		int id = RF.order(entity, int.class, "getId").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "getDataWatcher").invoke();
		PacketPlayOutEntityMetadata p1 = new PacketPlayOutEntityMetadata(id, dw, true);
		PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
		Invoker<?> v = RF.order(pc, "sendPacket", Packet.class);
		v.invoke(p0); v.invoke(p1);
	}
	
	@Override
	public Object nbtText(String text) {
		Class<?> c0 = RF.craft("util.CraftChatMessage");
		return RF.order(c0, "fromStringOrNull", String.class).invoke(text);
	}
}
