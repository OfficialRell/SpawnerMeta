package mc.rellox.spawnermeta.spawner;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.SpawnerMeta;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.spawner.type.UpgradeType;
import mc.rellox.spawnermeta.utility.DataManager;

public record ActiveSpawner(Block block) implements ISpawner {
	
	@Override
	public World world() {
		return block().getWorld();
	}

	@Override
	public Location center() {
		return block.getLocation().add(0.5, 0.5, 0.5);
	}
	
	@Override
	public boolean isEmpty() {
		return DataManager.isEmpty(block());
	}
	
	@Override
	public void setEmpty() {
		DataManager.setEmpty(block());
	}
	
	@Override
	public boolean isEnabled() {
		return DataManager.isEnabled(block());
	}
	
	@Override
	public void setEnabled(boolean b) {
		DataManager.setEnabled(block(), b);
	}
	
	@Override
	public boolean isRotating() {
		return DataManager.isRotating(block());
	}
	
	@Override
	public void setRotating(boolean b) {
		DataManager.setRotating(block(), b);
	}
	
	@Override
	public SpawnerType getType() {
		return DataManager.getType(block());
	}

	@Override
	public void setType(SpawnerType type) {
		DataManager.setType(block(), type);
	}

	@Deprecated(forRemoval = true)
	@Override
	public int getStackSize() {
		return getStack();
	}
	
	@Override
	public int getStack() {
		return DataManager.getStack(block());
	}

	@Deprecated(forRemoval = true)
	@Override
	public void setStackSize(int s) {
		setStack(s);
	}
	
	@Override
	public void setStack(int s) {
		DataManager.setStack(block(), s);
	}
	
	@Deprecated(forRemoval = true)
	@Override
	public int getSpawnableSize() {
		return getSpawnable();
	}
	
	@Override
	public int getSpawnable() {
		return DataManager.getSpawnable(block());
	}

	@Deprecated(forRemoval = true)
	@Override
	public void setSpawnableSize(int s) {
		setSpawnable(s);
	}
	
	@Override
	public void setSpawnable(int s) {
		DataManager.setSpawnable(block(), s);
	}
	
	@Override
	public boolean isNatural() {
		return isOwned() == false;
	}
	
	@Override
	public boolean isOwned() {
		return DataManager.isOwned(block());
	}
	
	@Override
	public Player getOwner() {
		UUID id = DataManager.getOwner(block());
		return id == null ? null : Bukkit.getPlayer(id);
	}
	
	@Override
	public UUID getOwnerID() {
		return DataManager.getOwner(block());
	}
	
	@Override
	public boolean isOwner(Player player) {
		return isOwner(player, false);
	}
	
	@Override
	public boolean isOwner(Player player, boolean def) {
		UUID id = DataManager.getOwner(block());
		return id == null ? def : id.equals(player.getUniqueId());
	}
	
	@Override
	public void setOwner(Player player) {
		DataManager.setOwner(block(), player);
	}
	
	@Override
	public int[] getUpgradeLevels() {
		return DataManager.getUpgradeLevels(block());
	}
	
	@Override
	public int getUpgradeLevel(UpgradeType type) {
		return DataManager.getUpgradeLevels(block())[type.ordinal()];
	}
	
	@Override
	public void setUpgradeLevels(int[] as) {
		DataManager.setUpgradeLevels(block(), as);
	}
	
	@Override
	public void resetUpgradeLevels() {
		DataManager.setUpgradeLevels(block(), DataManager.i());
	}
	
	@Override
	public int[] getUpgradeAttributes() {
		return DataManager.getUpgradeAttributes(block());
	}
	
	@Override
	public int getUpgradeAttribute(UpgradeType type) {
		return DataManager.getUpgradeAttributes(block())[type.ordinal()];
	}
	
	@Override
	public int getSpawnerLevel() {
		return DataManager.getSpawnerLevel(block());
	}
	
	@Override
	public int getCharges() {
		return DataManager.getCharges(block());
	}
	
	@Override
	public void setCharges(int a) {
		DataManager.setCharges(block(), a);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setDelay(int t) {
		if (SpawnerMeta.scheduler().isOwnedByCurrentRegion(block())) {
			DataManager.setDelay(block, t + 1);
		} else {
			SpawnerMeta.scheduler().runAtLocation(block.getLocation(),
					task -> DataManager.setDelay(block, t + 1));
		}
	}
	
	@Override
	public void update() {
		DataManager.updateValues(block());
	}

	@Override
	public void reset() {
		DataManager.reset(block);
	}
	
	@Override
	public List<ItemStack> toItems() {
		return DataManager.getSpawners(block(), false);
	}

	@Override
	public String toData() {
		int[] levels = getUpgradeLevels();
		return Stream.of(getType().name(),
				"" + levels[0],
				"" + levels[1],
				"" + levels[2],
				"" + getCharges(),
				"" + getSpawnable(),
				"" + getStack(),
				"" + isEmpty())
				.collect(Collectors.joining(";"));
	}

}