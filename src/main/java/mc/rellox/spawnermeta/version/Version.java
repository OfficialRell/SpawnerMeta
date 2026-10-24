package mc.rellox.spawnermeta.version;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.version.types.IVersion26;
import org.bukkit.Bukkit;

public final class Version {

    public static final String server;

    public static final VersionType TYPE;
    private static final IVersion VERSION;

    static {
        String s = Bukkit.getServer().getClass().getPackage().getName();
        server = s.substring(s.lastIndexOf('.') + 1);
        String bukkit = Bukkit.getBukkitVersion();

        if(bukkit.startsWith("26.3")) TYPE = VersionType.v_26_3;
        else if(bukkit.startsWith("26.2")) TYPE = VersionType.v_26_2;
        else if(bukkit.startsWith("26.1.2")
                || bukkit.startsWith("26.1.1")
                || bukkit.startsWith("26.1")) TYPE = VersionType.v_26_1;
        else if(server.contains("v1_21_R7")
                || bukkit.startsWith("1.21.11-R0.1")) TYPE = VersionType.v_21_7;
        else if(server.contains("v1_21_R6")
                || bukkit.startsWith("1.21.9-R0.1")
                || bukkit.startsWith("1.21.10-R0.1")) TYPE = VersionType.v_21_6;
        else if(server.contains("v1_21_R5")
                || bukkit.startsWith("1.21.6-R0.1")
                || bukkit.startsWith("1.21.7-R0.1")
                || bukkit.startsWith("1.21.8-R0.1")) TYPE = VersionType.v_21_5;
        else if(server.contains("v1_21_R4")
                || bukkit.startsWith("1.21.5-R0.1")) TYPE = VersionType.v_21_4;
        else if(server.contains("v1_21_R3")
                || bukkit.startsWith("1.21.4-R0.1")) TYPE = VersionType.v_21_3;
        else if(server.contains("v1_21_R2")
                || bukkit.startsWith("1.21.3-R0.1")) TYPE = VersionType.v_21_2;
        else if(server.contains("v1_21_R1")
                || bukkit.startsWith("1.21-R0.1")
                || bukkit.startsWith("1.21.1-R0.1")) TYPE = VersionType.v_21_1;
        else if(server.contains("v1_20_R4")
                || bukkit.startsWith("1.20.6-R0.1")) TYPE = VersionType.v_20_4;
        else if(server.contains("v1_20_R3")) TYPE = VersionType.v_20_3;
        else if(server.contains("v1_20_R2")) TYPE = VersionType.v_20_2;
        else if(server.contains("v1_20_R1")) TYPE = VersionType.v_20_1;
        else if(server.contains("v1_19_R3")) TYPE = VersionType.v_19_3;
        else if(server.contains("v1_19_R2")) TYPE = VersionType.v_19_2;
        else if(server.contains("v1_19_R1")) TYPE = VersionType.v_19_1;
        else if(server.contains("v1_18_R2")) TYPE = VersionType.v_18_2;
        else if(server.contains("v1_18_R1")) TYPE = VersionType.v_18_1;
        else if(server.contains("v1_17_R1")) TYPE = VersionType.v_17_1;
        else if(server.contains("v1_16_R3")) TYPE = VersionType.v_16_3;
        else if(server.contains("v1_16_R2")) TYPE = VersionType.v_16_2;
        else if(server.contains("v1_16_R1")) TYPE = VersionType.v_16_1;
        else if(server.contains("v1_15_R1")) TYPE = VersionType.v_15_1;
        else if(server.contains("v1_14_R1")) TYPE = VersionType.v_14_1;
        else TYPE = null;
        VERSION = TYPE == null ? null : TYPE.build();
    }

    public static IVersion instance() {
        return VERSION;
    }

    public enum VersionType {

        v_14_1,
        v_15_1,
        v_16_1, v_16_2, v_16_3,
        v_17_1,
        v_18_1, v_18_2,
        v_19_1, v_19_2, v_19_3,
        v_20_1, v_20_2, v_20_3, v_20_4,
        v_21_1, v_21_2, v_21_3, v_21_4, v_21_5, v_21_6, v_21_7,
        v_26_1, v_26_2, v_26_3;

        public boolean atleast(VersionType type) {
            return ordinal() >= type.ordinal();
        }

        private IVersion build() {
            if(atleast(v_26_1)) return new IVersion26();
            return RF.build(
                            RF.get("mc.rellox.spawnermeta.version.types.IVersion1"
                                    + name().substring(1)))
                    .as(IVersion.class)
                    .instance();
        }

    }

}
