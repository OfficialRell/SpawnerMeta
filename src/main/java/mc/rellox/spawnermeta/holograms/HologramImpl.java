package mc.rellox.spawnermeta.holograms;

import mc.rellox.spawnermeta.api.hologram.IHologram;
import mc.rellox.spawnermeta.api.region.IBox;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.text.content.Content;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class HologramImpl implements IHologram {

    protected final IGenerator generator;
    private final Object hologram;
    private final IBox box;
    private final Set<Player> players;

    public HologramImpl(IGenerator generator, boolean above, int radius) {
        Objects.requireNonNull(HANDLER, "Hologram handler is not available");
        this.generator = generator;
        String title = title().text();
        Block block = generator.block();
        this.hologram = HANDLER
                .create(block.getLocation()
                        .add(0.5, 1 + (above ? 0.25 : 0)
                                + Settings.settings.holograms_height, 0.5), title);
        this.box = IBox.cube(block, radius);
        this.players = new HashSet<>();
    }

    @Override
    public IGenerator generator() {
        return generator;
    }

    @Override
    public Set<Player> viewers() {
        return players;
    }

    @Override
    public void update() {
        List<Player> list = generator.world().getPlayers();
        for(Player player : list) {
            if(box.in(player)) {
                if(players.add(player)) show(player);
            } else if(players.remove(player)) hide(player);
        }
    }

    @Override
    public void rewrite() {
        String title = title().text();
        HANDLER.update(players, hologram, title);
    }

    @Override
    public void show(Player player) {
        HANDLER.spawn(player, hologram);
    }

    @Override
    public void hide(Player player) {
        HANDLER.destroy(player, hologram);
    }

    @Override
    public void clear() {
        players.forEach(player -> HANDLER.destroy(player, hologram));
    }

    public abstract Content title();

}
