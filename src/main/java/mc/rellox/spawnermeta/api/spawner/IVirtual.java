package mc.rellox.spawnermeta.api.spawner;

import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IVirtual {

    static IVirtual of(ItemStack item) {
        return of(item, false);
    }

    static IVirtual of(ItemStack item, boolean nullable) {
        return DataManager.getSpawnerItem(item, nullable);
    }

    static IVirtual of(Block block) {
        return DataManager.getSpawnerItem(block);
    }

    /**
     * @param other virtual spawner to compare with
     * @return {@code true} if virtual spawners are exactly equal, otherwise {@code false}
     */

    boolean exact(IVirtual other);

    /**
     * @return Spawner type of this virtual spawner
     */

    SpawnerType getType();

    /**
     * @return Upgrade levels of this virtual spawner
     */

    int[] getUpgradeLevels();

    /**
     * @return Spawner charges of this virtual spawner
     */

    int getCharges();

    /**
     * @return Spawnable entity limit of this virtual spawner
     */

    int getSpawnable();

    /**
     * @return {@code true} if this virtual spawner is empty, otherwise {@code false}
     */

    boolean isEmpty();

    /**
     * @param amount amount
     * @return Virtual spawner item stack
     */

    ItemStack getItem(int amount);

    /**
     * @return Virtual spawner item stack
     */

    default ItemStack getItem() {
        return getItem(1);
    }

    /**
     * Places this virtual spawner at the specified block.
     *
     * @param block block to place virtual spawner
     */

    default void place(Block block) {
        place(null, block);
    }

    /**
     * Places this virtual spawner at the specified block with the specified owner.
     *
     * @param owner player who owns the virtual spawner
     * @param block block to place virtual spawner
     */

    default void place(Player owner, Block block) {
        block.setType(Material.SPAWNER);
        DataManager.setNewSpawner(owner, block, getType(), getUpgradeLevels(),
                getCharges(), getSpawnable(), isEmpty());
    }

}
