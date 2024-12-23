package mc.rellox.spawnermeta.hook;

import java.util.ArrayList;
import java.util.List;

import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class HookRegistry {

	private static final List<HookInstance<?>> HOOKS = new ArrayList<>();

	public static final HookEconomy ECONOMY = new HookEconomy();
	public static final HookWildStacker WILD_STACKER = new HookWildStacker();
	public static final HookWildTools WILD_TOOLS = new HookWildTools();
	public static final HookShopGUI SHOP_GUI = new HookShopGUI();
	public static final HookFlareTokens FLARE_TOKENS = new HookFlareTokens();
	public static final HookSuperiorSkyblock2 SUPERIOR_SKYBLOCK_2 = new HookSuperiorSkyblock2();
	public static final HookPlayerPoints PLAYER_POINTS = new HookPlayerPoints();

	public static void load() {
		try {
			HOOKS.clear();
			HOOKS.add(ECONOMY);
			HOOKS.add(WILD_STACKER);
			HOOKS.add(WILD_TOOLS);
			HOOKS.add(SHOP_GUI);
			HOOKS.add(FLARE_TOKENS);
			HOOKS.add(SUPERIOR_SKYBLOCK_2);
			HOOKS.add(PLAYER_POINTS);
			HOOKS.forEach(i -> {
				try {
					i.load();
				} catch (Exception e) {
					RF.debug(e);
				}
			});
			HOOKS.stream()
				.filter(HookInstance::exists)
				.map(HookInstance::message)
				.forEach(Text::logInfo);
		} catch (Exception e) {
			RF.debug(e);
			Text.logFail("Unable to initialise hooks, make sure other supported plugins are enabled."
					+ " If the API has changed please contact developer!");
		}
	}

}
