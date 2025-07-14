package mc.rellox.spawnermeta.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utility.Messagable;
import mc.rellox.spawnermeta.view.SpawnerView;

public class CommandSpawners extends Command {

	protected CommandSpawners(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		if(sender instanceof Player == false) return false;
		Player player = (Player) sender;
		Messagable m = new Messagable(player);
		if(Settings.settings.spawner_view_entities.size() <= 0) m.send(Language.get("Spawners.view.empty"));
		else if(Settings.settings.spawner_view_enabled == false) m.send(Language.get("Spawners.view.disabled"));
		else {
			if(player.hasPermission("spawnermeta.spawners") == false) {
				m.send(Language.get("Inventory.spawner-view.permission"));
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
