package mc.rellox.spawnermeta.holograms;

import mc.rellox.spawnermeta.api.hologram.IHologram;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public record HologramNull(IGenerator generator) implements IHologram {

    @Override
    public Set<Player> viewers() {
        return new HashSet<>();
    }

    @Override
    public void update() {}

    @Override
    public void rewrite() {}

    @Override
    public void show(Player player) {}

    @Override
    public void hide(Player player) {}

    @Override
    public void clear() {}
}
