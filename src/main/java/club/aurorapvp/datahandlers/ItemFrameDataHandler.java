package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.LangHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ItemFrameDataHandler {
    private static final File FILE = new File(AuroraKits.DATA_FOLDER, "/frames/data.yml");
    private static YamlConfiguration framesData;

    public static void save() throws IOException {
        get().save(FILE);
    }

    public static void delete(CommandSender sender, String arg) {
        if (arg != null) {

            get().set("frames." + arg, null);
            try {
                save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sender.sendMessage(LangHandler.getComponent("frame-deleted"));
        } else {
            sender.sendMessage(LangHandler.getComponent("frame-invalid"));
        }
    }

    public static void create(Player p, String frameName, ItemFrame frame) {
        get().set("frames." + frameName + ".item", p.getInventory().getItemInMainHand());
        get().set("frames." + frameName + ".location", frame.getLocation());
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFrame(Location clickLoc) {
        for (Object path : get().getConfigurationSection("frames").getKeys(false).toArray()) {
            if (get().getLocation("frames." + path + ".location").equals(clickLoc)) {
                return (String) path;
            }
        }
        return null;
    }

    public static YamlConfiguration get() {
        return framesData;
    }

    public static void reload() throws IOException {
        if (!FILE.exists()) {
            FILE.getParentFile().mkdirs();

            FILE.createNewFile();
        }
        framesData = YamlConfiguration.loadConfiguration(FILE);
        AuroraKits.PLUGIN.getLogger().info("Frame data reloaded!");
    }
}