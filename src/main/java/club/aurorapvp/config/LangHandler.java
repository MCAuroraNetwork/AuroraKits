package club.aurorapvp.config;

import club.aurorapvp.AuroraKits;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LangHandler {
    private static final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
    private static final HashMap<String, String> DEFAULTS = new HashMap<>();
    private static final File FILE = new File(AuroraKits.DATA_FOLDER, "lang.yml");

    private static YamlConfiguration lang;

    public static void generateDefaults() throws IOException {
        for (Object path : get().getKeys(false).toArray()) {
            if (get().getString((String) path).startsWith("~") &&
                    get().getString((String) path).endsWith("~")) {
                PLACEHOLDERS.put((String) path, get().getString((String) path).replace("~", ""));
            }
        }

        DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraKits ><reset>~");
        DEFAULTS.put("frame-created",
                "prefix <gradient:#FFAA00:#FF55FF>Frame successfully created");
        DEFAULTS.put("frame-deleted", "prefix <gradient:#FFAA00:#FF55FF>Frame successfully deleted");
        DEFAULTS.put("frame-invalid", "prefix <gradient:#FFAA00:#FF55FF>Invalid frame name!");
        DEFAULTS.put("GUIName", "<gradient:#FFAA00:#FF55FF>KitGUI");
        DEFAULTS.put("kit-created",
                "prefix <gradient:#FFAA00:#FF55FF>Kit sucessfully created! Use /kits to access it!");
        DEFAULTS.put("kit-used", "prefix <gradient:#FFAA00:#FF55FF>Kit sucessfully used!");
        DEFAULTS.put("kit-deleted", "prefix <gradient:#FFAA00:#FF55FF>Kit sucessfully deleted");
        DEFAULTS.put("kit-too-many", "prefix <gradient:#FFAA00:#FF55FF>You have too many kits!");
        DEFAULTS.put("kit-invalid-name", "prefix <gradient:#FFAA00:#FF55FF>Invalid kit name!");
        DEFAULTS.put("kit-invalid-item", "prefix <gradient:#FFAA00:#FF55FF>Invalid kit display item!");
        DEFAULTS.put("kit-not-found", "prefix <gradient:#FFAA00:#FF55FF>Kit not found!");

        for (String path : DEFAULTS.keySet()) {
            if (!get().contains(path) || get().getString(path) == null) {
                get().set(path, DEFAULTS.get(path));
                get().save(FILE);
            }
        }
    }

    public static Component getComponent(String path) {
        if (get().contains(path)) {
            String pathString = get().getString(path);
            for (String placeholder : PLACEHOLDERS.keySet()) {
                if (pathString.contains(placeholder)) {
                    pathString = pathString.replace(placeholder,
                            PLACEHOLDERS.get(placeholder));
                }
            }
            return AuroraKits.DESERIALIZE_COMPONENT.deserialize(pathString);
        }
        return null;
    }

    public static YamlConfiguration get() {
        return lang;
    }

    public static void reload() throws IOException {
        if (!FILE.exists()) {
            FILE.getParentFile().mkdirs();

            FILE.createNewFile();
        }

        lang = YamlConfiguration.loadConfiguration(FILE);
        AuroraKits.PLUGIN.getLogger().info("Lang reloaded!");
    }
}