package mc.rellox.spawnermeta.holograms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R2.ChatComponentText;
import net.minecraft.server.v1_16_R2.EntityArmorStand;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R2.PlayerConnection;

public class HologramInstance1_16_2 implements HologramInstance {
	
	@Override
	public Object create(Location loc, String title) {
		EntityArmorStand entity = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(),
				loc.getX(), loc.getY(), loc.getZ());
		entity.setMarker(true);
		entity.setInvisible(true);
		entity.setCustomName(nbtText(title));
		entity.setCustomNameVisible(true);
		return entity;
	}

	@Override
	public void spawn(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntityLiving p0 = new PacketPlayOutSpawnEntityLiving(entity);
		PacketPlayOutEntityMetadata p1 = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerConnection pc = ((CraftPlayer) player).getHandle().playerConnection;
			pc.sendPacket(p0);
			pc.sendPacket(p1);
		});
	}

	@Override
	public void destroy(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getId());
		Bukkit.getOnlinePlayers().forEach(player -> {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		});
	}

	@Override
	public void update(Object hologram, String title) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		entity.setCustomName(nbtText(title));
		PacketPlayOutEntityMetadata p0 = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
		Bukkit.getOnlinePlayers().forEach(player -> {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(p0);
		});
	}
	
	@Override
	public void spawn(Player player, Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntityLiving p0 = new PacketPlayOutSpawnEntityLiving(entity);
		PacketPlayOutEntityMetadata p1 = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
		PlayerConnection pc = ((CraftPlayer) player).getHandle().playerConnection;
		pc.sendPacket(p0);
		pc.sendPacket(p1);
	}
	
	@Override
	public ChatComponentText nbtText(String text) {
		return new ChatComponentText(text);
	}
}
