package mc.rellox.spawnermeta.hook;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.events.PlotDeleteEvent;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.util.query.PlotQuery;
import com.sk89q.worldedit.math.BlockVector3;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;

public class HookPlotSquared implements HookInstance {
	
	private PlotAPI api;

	@Override
	public boolean exists() {
		return api != null;
	}

	@Override
	public String message() {
		return "PlotSquared has been found, plot support provided!";
	}

	@Override
	public void load() {
		if(Bukkit.getPluginManager().getPlugin("PlotSquared") == null) return;
		api = new PlotAPI();
		api.registerListener(this);
	}
	
	public void filter(IGenerator generator, List<Location> locations) {
		UUID owner = generator.cache().owner();
		if(owner == null) return;
		
		List<Plot> plots = PlotQuery.newQuery()
				.inWorld(generator.world().getName())
				.withMember(owner)
				.asList();
		
		for(int i = locations.size() - 1; i >= 0; i--) {
			Location l = locations.get(i);
			if(plots.stream().anyMatch(plot -> in(plot, l))) continue;
			locations.remove(i);
		}
	}
	
	public boolean modifiable(IGenerator generator, Player player) {
		UUID owner = generator.cache().owner();
		if(owner == null || owner.equals(player.getUniqueId()) == true) return true;
		Block block = generator.block();
		Plot plot = Plot.getPlot(com.plotsquared.core.location.Location.at(
				block.getWorld().getName(),
				block.getX(), block.getY(), block.getZ()));
		return plot == null ? true : plot.isAdded(player.getUniqueId());
	}

	@Subscribe
	public void onPlotDelete(PlotDeleteEvent event) {
		Plot plot = event.getPlot();
		World world = Bukkit.getWorld(event.getWorld());
		GeneratorRegistry.remove(world, true, g -> in(plot, g.block()));
	}
	
	public boolean inside(Block block, Entity entity) {
		Plot plot = Plot.getPlot(com.plotsquared.core.location.Location.at(
				block.getWorld().getName(),
				block.getX(), block.getY(), block.getZ()));
		if(plot == null) return true;
		Location at = entity.getLocation();
		return plot.getRegions().stream().anyMatch(region -> {
			return region.contains(BlockVector3.at(at.getX(), at.getY(), at.getZ()));
		});
	}
	
	private boolean in(Plot plot, Block block) {
		return plot.getRegions().stream().anyMatch(region -> {
			return region.contains(BlockVector3.at(block.getX(), block.getY(), block.getZ()));
		});
	}
	
	private boolean in(Plot plot, Location l) {
		return plot.getRegions().stream().anyMatch(region -> {
			return region.contains(BlockVector3.at(l.getX(), l.getY(), l.getZ()));
		});
	}

}
