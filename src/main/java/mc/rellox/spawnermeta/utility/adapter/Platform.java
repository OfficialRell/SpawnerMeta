package mc.rellox.spawnermeta.utility.adapter;

public final class Platform {
    private Platform() {}

    public static final BukkitPlatformAdapter ADAPTER;

    static {
        BukkitPlatformAdapter adapter;
        try {
            // Try to build Paper adapter (will fail on Spigot)
            adapter = new PaperAdapter();
        } catch (Throwable ignored) {
            adapter = new SpigotAdapter();
        }
        ADAPTER = adapter;
    }

}
