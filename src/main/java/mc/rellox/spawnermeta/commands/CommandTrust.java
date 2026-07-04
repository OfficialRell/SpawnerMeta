package mc.rellox.spawnermeta.commands;

import mc.rellox.spawnermeta.api.configuration.IPlayerData;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.utility.Messagable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommandTrust extends Command {

    protected CommandTrust(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if(!(sender instanceof Player player)) return false;
        Messagable messagable = new Messagable(player);
        IPlayerData data = LocationRegistry.get(player);

        if(args.length < 1) messagable.send(Language.list("Trusted.help.primary"));
        else if(args[0].equalsIgnoreCase("add")) {
            if(args.length < 2) messagable.send(Language.list("Trusted.help.add"));
            else {
                Player other = Bukkit.getPlayer(args[1]);
                if(other == null) messagable.send(Language.get("Trusted.info.unknow-player"));
                else if(other.equals(player)) messagable.send(Language.get("Trusted.info.already-trusted"));
                else {
                    if(data.trust(other.getUniqueId())) {
                        messagable.send(Language.get("Trusted.info.added"));
                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 1.5f);
                    } else {
                        messagable.send(Language.get("Trusted.info.already-trusted"));
                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
                    }
                }
            }
        } else if(args[0].equalsIgnoreCase("remove")) {
            if(args.length < 2) messagable.send(Language.list("Trusted.help.remove"));
            else {
                UUID id = data.trusted(args[1]);
                if(id == null) messagable.send(Language.get("Trusted.info.unknow-player"));
                else {
                    if(data.untrust(id)) {
                        messagable.send(Language.get("Trusted.info.removed"));
                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 2f, 1.5f);
                    } else {
                        messagable.send(Language.get("Trusted.info.not-trusted"));
                        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
                    }
                }
            }
        } else if(args[0].equalsIgnoreCase("clear")) {
            int s = data.untrust();
            if(s > 0) {
                messagable.send(Language.get("Trusted.info.cleared", "count", s));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 2f, 1.5f);
            } else {
                messagable.send(Language.get("Trusted.info.empty"));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
            }
        } else if(args[0].equalsIgnoreCase("view")) {
            List<String> list = data.trusted()
                    .stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();
            if(list.isEmpty()) {
                messagable.send(Language.get("Trusted.info.empty"));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 0.5f);
            } else {
                messagable.send(Language.get("Trusted.header", "count", list.size()));
                int i = 1;
                for(String name : list)
                    messagable.send(Language.get("Trusted.player", "index", i++, "player", name));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1f, 1.5f);
            }
        } else messagable.send(Language.list("Trusted.help.primary"));
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        List<String> l = new ArrayList<>();
        if(sender instanceof Player player) {
            if(args.length < 1) return null;
            else if(args.length < 2) return tab_options(args[0]);
            else if(args[0].equalsIgnoreCase("add")) {
                if(args.length < 3) return tab_players(player, args[1]);
                else return l;
            } else if(args[0].equalsIgnoreCase("remove")) {
                if(args.length < 3) return tab_trusted(player, args[1]);
                else return l;
            } else return l;
        }
        return l;
    }

    private List<String> tab_options(String s) {
        return CommandManager.reduce(List.of("add", "remove", "clear", "view"), s);
    }

    private List<String> tab_players(Player player, String s) {
        IPlayerData data = LocationRegistry.get(player);
        Set<UUID> trusted = data.trusted();
        List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        list.removeIf(p -> trusted.contains(p.getUniqueId()));
        list.remove(player);
        return CommandManager.reduce(list.stream()
                .map(Player::getName)
                .collect(Collectors.toList()), s);
    }

    private List<String> tab_trusted(Player player, String s) {
        IPlayerData data = LocationRegistry.get(player);
        Set<UUID> trusted = data.trusted();
        List<String> list = trusted.stream()
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return CommandManager.reduce(list, s);
    }

}
