package club.aurorapvp.config;

import club.aurorapvp.AuroraKits;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigHandler {
    private static final HashMap<String, String> DEFAULTS = new HashMap<>();
    private static final File FILE = new File(AuroraKits.DATA_FOLDER, "config.yml");
    private static YamlConfiguration config;

    public static void generateDefaults() throws IOException {
        DEFAULTS.put("doFirstFallDamage", "false");
        DEFAULTS.put("kits.lastUsedKit.enabled", "false");
        DEFAULTS.put("kits.lastUsedKit.defaultKit", "Default");

        for (String path : DEFAULTS.keySet()) {
            if (!get().contains(path) || get().getString(path) == null) {
                get().set(path, DEFAULTS.get(path));
                get().save(FILE);
            }
        }
    }

    public static YamlConfiguration get() {
        return config;
    }

    public static void reload() throws IOException {
        if (!FILE.exists()) {
            FILE.getParentFile().mkdirs();

            FILE.createNewFile();
        }
        config = YamlConfiguration.loadConfiguration(FILE);
        AuroraKits.PLUGIN.getLogger().info("Config reloaded!");
    }
}