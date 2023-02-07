package mc.rellox.spawnermeta.holograms;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Reflections.RF.Invoker;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.decoration.EntityArmorStand;

public class HologramInstance1_19_2 implements HologramInstance {
	
	@Override
	public Object create(Location loc, String title) {
		EntityArmorStand entity = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(),
				loc.getX(), loc.getY(), loc.getZ());
		RF.order(entity, "t", boolean.class).invoke(true);
		RF.order(entity, "j", boolean.class).invoke(true);
		RF.order(entity, "b", IChatBaseComponent.class).invoke(nbtText(title));
		RF.order(entity, "n", boolean.class).invoke(true);
		return entity;
	}

	@Override
	public void spawn(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntity p0 = new PacketPlayOutSpawnEntity(entity);
		int id = RF.order(entity, int.class, "ah").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "al").invoke();
		Class<?> cc = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
		Object p1 =  RF.construct(cc, int.class, List.class)
				.instance(id, RF.order(dw, "c").invoke());
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
			Invoker<?> v = RF.order(pc, "a", Packet.class);
			v.invoke(p0); v.invoke(p1);
		});
	}

	@Override
	public void destroy(Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		int id = RF.order(entity, int.class, "ah").invoke(0);
		PacketPlayOutEntityDestroy p0 =
				RF.construct(PacketPlayOutEntityDestroy.class, int[].class).instance(new int[] {id});
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
			RF.order(pc, "a", Packet.class).invoke(p0);
		});
	}

	@Override
	public void update(Object hologram, String title) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		RF.order(entity, "b", IChatBaseComponent.class).invoke(nbtText(title));
		int id = RF.order(entity, int.class, "ah").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "al").invoke();
		Class<?> cc = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
		Object p0 =  RF.construct(cc, int.class, List.class)
				.instance(id, RF.order(dw, "c").invoke());
		Bukkit.getOnlinePlayers().forEach(player -> {
			PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
			RF.order(pc, "a", Packet.class).invoke(p0);
		});
	}
	
	@Override
	public void spawn(Player player, Object hologram) {
		EntityArmorStand entity = (EntityArmorStand) hologram;
		PacketPlayOutSpawnEntity p0 = new PacketPlayOutSpawnEntity(entity);
		int id = RF.order(entity, int.class, "ah").invoke(0);
		DataWatcher dw = RF.order(entity, DataWatcher.class, "al").invoke();
		Class<?> cc = RF.get("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
		Object p1 =  RF.construct(cc, int.class, List.class)
				.instance(id, RF.order(dw, "c").invoke());
		PlayerConnection pc = ((CraftPlayer) player).getHandle().b;
		Invoker<?> v = RF.order(pc, "a", Packet.class);
		v.invoke(p0); v.invoke(p1);
	}
	
	@Override
	public Object nbtText(String text) {
		Class<?> c0 = RF.craft("util.CraftChatMessage");
		return RF.order(c0, "fromStringOrNull", String.class).invoke(text);
	}
}
