package mc.rellox.spawnermeta.commands;

import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utility.Receiver;
import mc.rellox.spawnermeta.view.SpawnerView;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSpawners extends Command {

    protected CommandSpawners(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String lable, String[] args) {
        if(!(sender instanceof Player player)) return false;
        Receiver receiver = new Receiver(player);
        if(Settings.settings.spawner_view_entities.isEmpty()) receiver.send(Language.get("Spawners.view.empty"));
        else if(!Settings.settings.spawner_view_enabled) receiver.send(Language.get("Spawners.view.disabled"));
        else {
            if(!player.hasPermission("spawnermeta.spawners")) {
                receiver.send(Language.get("Inventory.spawner-view.permission"));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
            } else new SpawnerView(player);
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }

}
