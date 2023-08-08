package mc.rellox.spawnermeta.utility;

import java.util.List;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.text.content.Content;

public record Messagable(Player player) {
	
	public void send(Content content) {
		if(content == null) return;
		String text = content.text();
		if(text.isEmpty() == false) player.sendMessage(text);
	}
	
	public void send(List<Content> list) {
		if(list.isEmpty() == true) return;
		if(list.size() == 1) {
			String text = list.get(0).text();
			if(text.isEmpty() == false) player.sendMessage(text);
		} else list.stream()
			.map(Content::text)
			.forEach(player::sendMessage);
	}

}
