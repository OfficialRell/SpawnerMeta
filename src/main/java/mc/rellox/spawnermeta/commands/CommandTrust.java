package mc.rellox.spawnermeta.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.configuration.IPlayerData;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.utility.Messagable;

public class CommandTrust extends Command {

	protected CommandTrust(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		if(sender instanceof Player == false) return false;
		Player player = (Player) sender;
		Messagable m = new Messagable(player);
		IPlayerData il = LocationRegistry.get(player);
		
		if(args.length < 1) m.send(Language.list("Trusted.help.primary"));
		else if(args[0].equalsIgnoreCase("add") == true) {
			if(args.length < 2) m.send(Language.list("Trusted.help.add"));
			else {
				Player other = Bukkit.getPlayer(args[1]);
				if(other == null) m.send(Language.get("Trusted.info.unknow-player"));
				else if(other.equals(player) == true) m.send(Language.get("Trusted.info.already-trusted"));
				else {
					if(il.trust(other.getUniqueId()) == true) {
						m.send(Language.get("Trusted.info.added"));
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 1.5f);
					} else {
						m.send(Language.get("Trusted.info.already-trusted"));
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
					}
				}
			}
		} else if(args[0].equalsIgnoreCase("remove") == true) {
			if(args.length < 2) m.send(Language.list("Trusted.help.remove"));
			else {
				UUID id = il.trusted(args[1]);
				if(id == null) m.send(Language.get("Trusted.info.unknow-player"));
				else {
					if(il.untrust(id) == true) {
						m.send(Language.get("Trusted.info.removed"));
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 2f, 1.5f);
					} else {
						m.send(Language.get("Trusted.info.not-trusted"));
						player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
					}
				}
			}
		} else if(args[0].equalsIgnoreCase("clear") == true) {
			int s = il.untrust();
			if(s > 0) {
				m.send(Language.get("Trusted.info.cleared", "count", s));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 2f, 1.5f);
			} else {
				m.send(Language.get("Trusted.info.empty"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
			}
		} else if(args[0].equalsIgnoreCase("view") == true) {
			List<String> list = il.trusted()
					.stream()
					.map(Bukkit::getOfflinePlayer)
					.map(OfflinePlayer::getName)
					.filter(s -> s != null)
					.sorted()
					.collect(Collectors.toList());
			if(list.isEmpty() == true) {
				m.send(Language.get("Trusted.info.empty"));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
			} else {
				m.send(Language.get("Trusted.header", "count", list.size()));
				int i = 1;
				for(String name : list)
					m.send(Language.get("Trusted.player", "index", i++, "player", name));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1f, 1.5f);
			}
		} else m.send(Language.list("Trusted.help.primary"));
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		List<String> l = new ArrayList<>();
		if(sender instanceof Player player) {
			if(args.length < 1) return null;
			else if(args.length < 2) return a(args[0]);
			else if(args[0].equalsIgnoreCase("add") == true) {
				if(args.length < 3) return b(player, args[1]);
				else return l;
			} else if(args[0].equalsIgnoreCase("remove") == true) {
				if(args.length < 3) return c(player, args[1]);
				else return l;
			} else return l;
		}
		return l;
	}
	
	private List<String> a(String s) {
		return CommandManager.reduce(List.of("add", "remove", "clear", "view"), s);
	}
	
	private List<String> b(Player player, String s) {
		IPlayerData il = LocationRegistry.get(player);
		Set<UUID> trusted = il.trusted();
		List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
		list.removeIf(p -> trusted.contains(p.getUniqueId()));
		list.remove(player);
		return CommandManager.reduce(list.stream()
				.map(Player::getName)
				.collect(Collectors.toList()), s);
	}
	
	private List<String> c(Player player, String s) {
		IPlayerData il = LocationRegistry.get(player);
		Set<UUID> trusted = il.trusted();
		List<String> list = trusted.stream()
				.map(id -> Bukkit.getOfflinePlayer(id))
				.map(o -> o.getName())
				.filter(n -> n != null)
				.collect(Collectors.toList());
		return CommandManager.reduce(list, s);
	}

}
