package mc.rellox.spawnermeta.hook;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HookGriefPrevention implements HookInstance {

    private GriefPrevention plugin;

    @Override
    public boolean exists() {
        return plugin != null;
    }

    @Override
    public String message() {
        return "GriefPrevention has been found, claims support provided!";
    }

    @Override
    public void load() {
        if(Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention") == null) return;
        plugin = GriefPrevention.instance;
    }

    public boolean hasAccessTrust(IGenerator generator, Player player) {
        Claim claim = plugin.dataStore.getClaimAt(generator.block().getLocation(), false, null);
        if (claim == null) {
            return true;
        }
        return claim.allowContainers(player) == null;
    }

}
