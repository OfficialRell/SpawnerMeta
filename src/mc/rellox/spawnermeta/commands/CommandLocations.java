package mc.rellox.spawnermeta.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.configuration.IPlayerData;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.location.LocationFile.FinalPos;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.utility.Messagable;

public class CommandLocations extends Command {

	protected CommandLocations(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		if(sender instanceof Player == false) return false;
		Player player = (Player) sender;
		Messagable m = new Messagable(player);
		IPlayerData il = LocationRegistry.get(player);
		
		List<World> worlds = new LinkedList<>();
		List<Set<Location>> locations = new LinkedList<>();
		int s = 0;
		
		for(World world : Bukkit.getWorlds()) {
			Set<Location> set = il.get(world);
			if(set.isEmpty() == true) continue;
			worlds.add(world);
			locations.add(set);
			s += set.size();
		}
		
		if(worlds.isEmpty() == true) m.send(Language.get("Locations.none-owned"));
		else {
			m.send(Language.get("Locations.header", "count", s));
			for(int i = 0, j = 0; i < worlds.size(); i++, j = 0) {
				m.send(Language.get("Locations.world", "world",
						worlds.get(i).getName()));
				for(Location loc : locations.get(i)) {
					FinalPos f = FinalPos.of(loc);
					m.send(Language.get("Locations.position",
							"index", ++j, "x", f.fx(), "y", f.fy(), "z", f.fz()));
				}
			}
		}
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		return new ArrayList<>();
	}

}
