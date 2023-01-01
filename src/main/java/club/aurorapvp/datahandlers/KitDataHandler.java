package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.LangHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class KitDataHandler {

    private static YamlConfiguration kitFile;

    public static void create(Player p, String kitName, String kitLocation)
            throws IOException {
        setup(kitLocation, kitName);

        for (int i = 0; i < p.getInventory().getContents().length; i++) {
            try {
                kitFile.set("items." + i, p.getInventory().getContents()[i]);
            } catch (Exception e) {
                AuroraKits.PLUGIN.getLogger().severe("Unable to save kit");
                e.printStackTrace();
            }
        }
        save(kitLocation, kitName);
    }

    public static void delete(CommandSender sender, String kitName, String kitLocation) {
        if (get(kitLocation, kitName) != null) {
            new File(AuroraKits.DATA_FOLDER, "/kits/" + kitLocation + "/" + kitName + ".yml").delete();
            try {
                GUIDataHandler.deleteEntry(kitLocation, kitName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage(LangHandler.getComponent("kit-deleted"));
        } else {
            sender.sendMessage(LangHandler.getComponent("kit-invalid-name"));
        }
    }

    public static void setup(String kitLocation, String kitName) throws IOException {
        File file = new File(AuroraKits.DATA_FOLDER, "/kits/" + kitLocation + "/" + kitName + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            file.createNewFile();
        }
        kitFile = YamlConfiguration.loadConfiguration(file);
    }

    public static YamlConfiguration get(String kitLocation, String kitName) {
        File file = new File(AuroraKits.DATA_FOLDER, "/kits/" + kitLocation + "/" + kitName + ".yml");
        if (file.exists()) {
            kitFile = YamlConfiguration.loadConfiguration(file);
            return kitFile;
        } else {
            file = new File(AuroraKits.DATA_FOLDER, "/kits/public/" + kitName + ".yml");
            if (file.exists()) {
                kitFile = YamlConfiguration.loadConfiguration(file);
                return kitFile;
            }
        }
        return null;
    }

    public static void save(String kitLocation, String kitName) throws IOException {
        File file = new File(AuroraKits.DATA_FOLDER, "/kits/" + kitLocation + "/" + kitName + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            file.createNewFile();
        }
        kitFile.save(file);
    }

    public static int getAmount(UUID playerUUID) {
        File file = new File(AuroraKits.DATA_FOLDER, "/kits/" + playerUUID + "/");

        if (file.listFiles() == null) {
            return new File(AuroraKits.DATA_FOLDER, "/kits/public/").listFiles().length;
        } else {
            return file.listFiles().length + new File(AuroraKits.DATA_FOLDER, "/kits/public/").listFiles().length;
        }
    }
}