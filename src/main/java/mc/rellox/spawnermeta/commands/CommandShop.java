package mc.rellox.spawnermeta.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.shop.ShopRegistry;

public class CommandShop extends Command {

	protected CommandShop(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		if(sender instanceof Player == false) return false;
		Player player = (Player) sender;
		if(ShopRegistry.open(player) == false) player.sendMessage(Language.get("Inventory.sell-shop.disabled").text());
		return false;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		return new ArrayList<>();
	}

}
