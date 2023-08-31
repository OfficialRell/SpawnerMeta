package mc.rellox.spawnermeta.configuration.location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.configuration.ILocations;
import mc.rellox.spawnermeta.configuration.AbstractFile;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class LocationFile extends AbstractFile implements ILocations {
	
	private static final int offline_time = 1000 * 60 * 5;
	
	private final UUID id;
	
	private final Map<World, Set<FinalPos>> locations;
	private final Set<UUID> trust;
	
	private long time;
	private boolean loaded;

	public LocationFile(UUID id) {
		super(LocationRegistry.parent, id.toString());
		this.id = id;
		this.locations = new HashMap<>();
		this.trust = new HashSet<>();
		use();
	}

	@Override
	protected void initialize() {
		check();
		Set<String> keys = keys("Spawners");
		try {
			locations.clear();
			keys.forEach(key -> {
				World world = Bukkit.getWorld(key);
				if(world == null) return;
				List<String> list = getStrings("Spawners." + key);
				if(list.isEmpty() == true) return;
				locations.put(world, list.stream()
						.map(s -> LocationFile.parse(world, s))
						.collect(Collectors.toSet()));
			});
		} catch (Exception e) {
			RF.debug(e);
		}
		try {
			trust.clear();
			List<String> list = getStrings("Trusted-players");
			if(list.isEmpty() == true) return;
			list.stream()
				.map(UUID::fromString)
				.forEach(trust::add);
		} catch (Exception e) {
			RF.debug(e);
		}
		
	}
	
	@Override
	public void load() {
		check();
		if(loaded == true) return;
		loaded = true;
		super.load();
	}
	
	private void update() {
		check();
		hold("Spawners", List.of());
		if(locations.isEmpty() == false) {
			locations.forEach((world, set) -> {
				List<String> list = set.stream()
						.map(LocationFile::parse)
						.toList();
				hold("Spawners." + world.getName(), list);
			});
		}
		hold("Trusted-players", trust.stream()
				.map(UUID::toString)
				.toList());
		save();
	}
	
	private void check() {
		if(using() == false)
			throw new IllegalStateException("This file is no longer in use");
	}

	@Override
	public boolean online() {
		return player() != null;
	}

	@Override
	public boolean using() {
		return System.currentTimeMillis() - time < offline_time;
	}
	
	@Override
	public void use() {
		this.time = System.currentTimeMillis();
	}

	@Override
	public UUID id() {
		return id;
	}

	@Override
	public Player player() {
		return Bukkit.getPlayer(id);
	}

	@Override
	public Set<Location> get(World world) {
		check();
		var set = locations.get(world);
		return set == null ? Set.of() : set.stream()
				.map(FinalPos::location)
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public Set<Location> all() {
		check();
		return locations.values().stream()
				.flatMap(Set::stream)
				.map(FinalPos::location)
				.collect(Collectors.toUnmodifiableSet());
	}
	
	@Override
	public Set<UUID> trusted() {
		check();
		return Set.copyOf(trust);
	}
	
	@Override
	public UUID trusted(String name) {
		check();
		for(UUID id : trust) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(id);
			if(name.equalsIgnoreCase(player.getName()) == true) return id;
		}
		return null;
	}
	
	@Override
	public boolean trusts(UUID id) {
		check();
		return trust.contains(id);
	}
	
	@Override
	public boolean trusts(Player player) {
		return trusts(player.getUniqueId());
	}
	
	@Override
	public boolean trust(UUID id) {
		check();
		boolean t = trust.add(id);
		if(t == true) update();
		return t;
	}
	
	@Override
	public boolean trust(Player player) {
		return trust(player.getUniqueId());
	}
	
	@Override
	public boolean untrust(UUID id) {
		check();
		boolean t = trust.remove(id);
		if(t == true) update();
		return t;
	}
	
	@Override
	public boolean untrust(Player player) {
		return untrust(player.getUniqueId());
	}
	
	@Override
	public int untrust() {
		int s = trust.size();
		trust.clear();
		if(s > 0) update();
		return s;
	}

	@Override
	public boolean remove(Block block) {
		check();
		World world = block.getWorld();
		var set = locations.get(world);
		if(set == null) return false;
		var r = set.remove(FinalPos.of(block));
		if(set.isEmpty() == true) {
			locations.remove(world);
			r = true;
		}
		if(r == true) update();
		return r;
	}

	@Override
	public boolean add(Block block) {
		check();
		World world = block.getWorld();
		var set = locations.get(world);
		if(set == null) locations.put(world, set = new HashSet<>());
		var r = set.add(FinalPos.of(block));
		if(r == true) update();
		return r;
	}

	@Override
	public int amount(World world) {
		check();
		var set = locations.get(world);
		return set == null ? 0 : set.size();
	}

	@Override
	public int amount() {
		check();
		return locations.values().stream()
				.mapToInt(Set::size)
				.sum();
	}

	@Override
	public int clear(World world) {
		check();
		var set = locations.remove(world);
		if(set == null) return 0;
		set.stream()
			.map(FinalPos::block)
			.peek(b -> b.setType(Material.AIR))
			.forEach(GeneratorRegistry::remove);
		update();
		return set.size();
	}

	@Override
	public int clear() {
		check();
		if(locations.isEmpty() == true) return 0;
		int s = amount();
		locations.values().stream()
			.flatMap(Set::stream)
			.map(FinalPos::block)
			.peek(b -> b.setType(Material.AIR))
			.forEach(GeneratorRegistry::remove);
		locations.clear();
		update();
		return s;
	}

	@Override
	public int validate(World world) {
		check();
		var set = locations.get(world);
		if(set == null) return 0;
		var it = set.iterator();
		int r = 0;
		while(it.hasNext() == true) {
			var block = it.next().block();
			if(block.getType() == Material.SPAWNER) continue;
			it.remove();
			block.setType(Material.AIR);
			GeneratorRegistry.remove(block);
			r++;
		}
		if(r > 0) update();
		return r;
	}

	@Override
	public int validate() {
		check();
		if(locations.isEmpty() == true) return 0;
		var sets = locations.values();
		int r = 0;
		var it = sets.iterator();
		while(it.hasNext() == true) {
			var set = it.next();
			var ir = set.iterator();
			while(ir.hasNext() == true) {
				var block = ir.next().block();
				if(block.getType() == Material.SPAWNER) continue;
				ir.remove();
				block.setType(Material.AIR);
				GeneratorRegistry.remove(block);
				r++;
			}
			if(set.isEmpty() == true) it.remove();
		}
		if(r > 0) update();
		return r;
	}
	
	public static String parse(FinalPos pos) {
		return to(pos.x) + ", " + to(pos.y) + ", " + to(pos.z);
	}
	
	private static String to(int i) {
		return i < 0 ? i == -1 ? "-0" : "" + (i + 1) : "" + i;
	}
	
	private static int from(String s) {
		if(s.equals("-0") == true) return -1;
		int i = Integer.parseInt(s);
		return i < 0 ? (i - 1) : i;
	}
	
	public static FinalPos parse(World world, String s) {
		try {
			if(s.matches("(-?\\d+)((,\\s?)(-?\\d+)){2}") == false)
				return null;
			String[] ps = s.replace(" ", "").split(",");
			if(ps.length != 3) return null;
			return new FinalPos(world, from(ps[0]), from(ps[1]), from(ps[2]));
		} catch (Exception e) {
			RF.debug(e);
		}
		return null;
	}
	
	public static record FinalPos(World world, int x, int y, int z) {
		
		public static FinalPos of(Block block) {
			return new FinalPos(block.getWorld(),
					block.getX(), block.getY(), block.getZ());
		}
		
		public static FinalPos of(Location loc) {
			return new FinalPos(loc.getWorld(),
					loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
		
		public String fx() {
			return to(x);
		}
		
		public String fy() {
			return to(y);
		}
		
		public String fz() {
			return to(z);
		}
		
		public Location location() {
			return new Location(world, x, y, z);
		}
		
		public Block block() {
			return world.getBlockAt(x, y, z);
		}
		
		public String toString() {
			return "[" + x + ", " + y + ", " + z + "]";
		}
		
	}

}
