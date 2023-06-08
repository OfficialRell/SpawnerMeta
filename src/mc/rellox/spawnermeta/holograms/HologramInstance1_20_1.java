package mc.rellox.spawnermeta.holograms;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Reflections.RF.Invoker;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.entity.decoration.EntityArmorStand;

public class HologramInstance1_20_1 implements HologramInstance {
	
	@Override
	public Object create(Location loc, String title) {
		EntityArmorStand entity = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(),
				loc.getX(), loc.getY(), loc.getZ());
		RF.order(entity, "u", boolean.class).invoke(true);
		RF.order(entity, "j", boolean.class).invoke(true);
		RF.order(entity, "b", IChatBaseComponent.class).invoke(nbtText(title));
		RF.order(entity, "n", boolean.class).invoke(true);
		return entity;
	}

	@Override
	public void spawn(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntity p0 = new PacketPlayOutSpawnEntity(entity);
		int id = RF.order(entity, int.class, "af").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "aj").invoke();
		Class<?> cc = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
		Object p1 =  RF.construct(cc, int.class, List.class)
				.instance(id, RF.order(dw, "c").invoke());
		Bukkit.getOnlinePlayers().forEach(player -> {
			Object pc = RF.access(((CraftPlayer) player).getHandle(), "c").field();
			Invoker<?> v = RF.order(pc, "a", Packet.class);
			v.invoke(p0); v.invoke(p1);
		});
	}

	@Override
	public void destroy(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		int id = RF.order(entity, int.class, "af").invoke(0);
		PacketPlayOutEntityDestroy p0 =
				RF.construct(PacketPlayOutEntityDestroy.class, int[].class)
				.instance(new int[] {id});
		Bukkit.getOnlinePlayers().forEach(player -> {
			Object pc = RF.access(((CraftPlayer) player).getHandle(), "c").field();
			RF.order(pc, "a", Packet.class).invoke(p0);
		});
	}

	@Override
	public void update(Object hologram, String title) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		RF.order(entity, "b", IChatBaseComponent.class).invoke(nbtText(title));
		int id = RF.order(entity, int.class, "af").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "aj").invoke();
		Class<?> cc = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
		Object p0 =  RF.construct(cc, int.class, List.class)
				.instance(id, RF.order(dw, "c").invoke());
		Bukkit.getOnlinePlayers().forEach(player -> {
			Object pc = RF.access(((CraftPlayer) player).getHandle(), "c").field();
			RF.order(pc, "a", Packet.class).invoke(p0);
		});
	}
	
	@Override
	public void spawn(Player player, Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntity p0 = new PacketPlayOutSpawnEntity(entity);
		int id = RF.order(entity, int.class, "af").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "aj").invoke();
		Class<?> cc = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
		Object p1 =  RF.construct(cc, int.class, List.class)
				.instance(id, RF.order(dw, "c").invoke());
		Object pc = RF.access(((CraftPlayer) player).getHandle(), "c").field();
		Invoker<?> v = RF.order(pc, "a", Packet.class);
		v.invoke(p0); v.invoke(p1);
	}
	
	@Override
	public Object nbtText(String text) {
		Class<?> c0 = RF.craft("util.CraftChatMessage");
		return RF.order(c0, "fromStringOrNull", String.class).invoke(text);
	}
}
