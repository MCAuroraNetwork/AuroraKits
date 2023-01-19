package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.Lang;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIData {
  private final YamlConfiguration PUBLIC_ENTRIES =
      YamlConfiguration.loadConfiguration(new File(AuroraKits.DATA_FOLDER, "/GUIs/public.yml"));
  private final Inventory inv = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));
  private final YamlConfiguration GUI_ENTRIES;
  private final File FILE;

  public GUIData(GUI gui) {
    Player p = gui.getPlayer();

    FILE = new File(AuroraKits.DATA_FOLDER, "/GUIs/" + p.getUniqueId() + ".yml");
    GUI_ENTRIES = YamlConfiguration.loadConfiguration(FILE);

    this.create();
  }

  public File getFile() {
    return FILE;
  }

  public YamlConfiguration getEntries() {
    return GUI_ENTRIES;
  }

  public YamlConfiguration getPublicEntries() {
    return PUBLIC_ENTRIES;
  }

  public void create() {
    for (int i = 0; i < 2; i++) {
      for (Object path : getEntries().getConfigurationSection("kits").getKeys(false).toArray()) {
        inv.addItem(createItem(getEntries().getItemStack("kits." + path + ".displayItem").getType(),
            (String) path, getEntries().getString("kits." + path + ".creator"),
            getEntries().getInt("kits." + path + ".type")));
      }
    }
  }

  public void deleteEntry(Kit kit) {
    getEntries().set("kits." + kit.getName(), null);
    save();
  }

  public Inventory getInventory() {
    return inv;
  }

  protected ItemStack createItem(final Material material, final String name,
                                 String creator, int type) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    AuroraKits.DESERIALIZE_COMPONENT.deserialize("<gradient:#FFAA00:#FF55FF>" + name)
        .decoration(TextDecoration.ITALIC, false)
        .decoration(TextDecoration.BOLD, true);

    switch (type) {
      case 0 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Public Kit")));
      case 1 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Private Kit")));
      default -> meta.lore();
    }

    item.setItemMeta(meta);

    return item;
  }

  public void addEntry(Kit kit) {
    String kitName = kit.getName();
    Player p = kit.getOwner();
    int kitType = kit.getType();

    inv.addItem(
        createItem(p.getInventory().getItemInMainHand().getType(), kitName, p.getName(), kitType));

    getEntries().set("kits." + kitName + ".displayItem", p.getInventory().getItemInMainHand());
    getEntries().set("kits." + kitName + ".creator",
        AuroraKits.SERIALIZE_COMPONENT.serialize(p.displayName()));
    getEntries().get("kits." + kitName + ".type", kitType);
    save();
  }

  public void save() {
    try {
      getEntries().save(FILE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}