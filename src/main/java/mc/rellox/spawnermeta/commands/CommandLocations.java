package mc.rellox.spawnermeta.commands;

import mc.rellox.spawnermeta.api.configuration.IPlayerData;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.location.LocationFile.FinalPos;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.utility.Messagable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CommandLocations extends Command {

    protected CommandLocations(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String lable, String[] args) {
        if(!(sender instanceof Player player)) return false;

        Messagable messagable = new Messagable(player);
        IPlayerData data = LocationRegistry.get(player);

        List<World> worlds = new LinkedList<>();
        List<Set<Location>> locations = new LinkedList<>();
        int s = 0;

        for(World world : Bukkit.getWorlds()) {
            Set<Location> set = data.get(world);
            if(set.isEmpty()) continue;
            worlds.add(world);
            locations.add(set);
            s += set.size();
        }

        if(worlds.isEmpty()) {
            messagable.send(Language.get("Locations.none-owned"));
            return true;
        }

        messagable.send(Language.get("Locations.header",
                "count", s));
        for(int i = 0, j = 0; i < worlds.size(); i++, j = 0) {
            messagable.send(Language.get("Locations.world",
                    "world", worlds.get(i).getName()));
            for(Location at : locations.get(i)) {
                FinalPos pos = FinalPos.of(at);
                messagable.send(Language.get("Locations.position",
                        "index", ++j,
                        "x", pos.fx(),
                        "y", pos.fy(),
                        "z", pos.fz()));
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }

}
