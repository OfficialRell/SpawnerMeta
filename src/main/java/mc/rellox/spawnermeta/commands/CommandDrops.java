package mc.rellox.spawnermeta.commands;

import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.items.ItemCollector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandDrops extends Command {

    protected CommandDrops(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String lable, String[] args) {
        if(Settings.settings.breaking_drop_on_ground) return false;
        if(!(sender instanceof Player player)) return false;

        ItemCollector drop = ItemCollector.get(player);
        if(drop == null) player.sendMessage(Language.get("Items.spawner-drop.empty").text());
        else drop.get(false);

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }

}
