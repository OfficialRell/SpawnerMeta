package mc.rellox.spawnermeta.utility;

import mc.rellox.spawnermeta.text.content.Content;
import org.bukkit.entity.Player;

import java.util.List;

public record Receiver(Player player) {

    public void send(Content content) {
        if(content == null) return;
        String text = content.text();
        if(!text.isEmpty()) player.sendMessage(text);
    }

    public void send(List<Content> list) {
        if(list.isEmpty()) return;
        if(list.size() == 1) {
            String text = list.get(0).text();
            if(!text.isEmpty()) player.sendMessage(text);
        } else list.stream()
                .map(Content::text)
                .forEach(player::sendMessage);
    }

}
