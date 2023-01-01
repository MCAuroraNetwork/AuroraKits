package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraKits;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GUIDataHandler {
    public static YamlConfiguration GUIFile;

    public static YamlConfiguration getFile() {
        return GUIFile;
    }

    public static void setFile(String GUIMenu) throws IOException {
        File file = new File(AuroraKits.DATA_FOLDER, "/GUIs/" + GUIMenu + ".yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            file.createNewFile();
        }
        GUIFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveFile(String GUIMenu) throws IOException {
        GUIFile.save(new File(AuroraKits.DATA_FOLDER, "/GUIs/" + GUIMenu + ".yml"));
    }

    public static void createEntry(Player p, String GUIMenu, String kitName) throws IOException {
        setFile(GUIMenu);

        getFile().set("kits." + kitName + ".displayItem", p.getInventory().getItemInMainHand());
        getFile().set("kits." + kitName + ".creator",
                AuroraKits.SERIALIZE_COMPONENT.serialize(p.displayName()));
        if (Objects.equals(GUIMenu, "public")) {
            getFile().set("kits." + kitName + ".type", 0);
        } else {
            getFile().set("kits." + kitName + ".type", 1);
        }
        saveFile(GUIMenu);
    }

    public static void deleteEntry(String GUIMenu, String KitName) throws IOException {
        setFile(GUIMenu);

        getFile().set("kits." + KitName, null);
        saveFile(GUIMenu);
    }
}