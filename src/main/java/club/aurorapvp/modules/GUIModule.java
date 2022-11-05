package club.aurorapvp.modules;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.serializeComponent;
import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.GUIDataHandler.getGUIPublicData;
import static club.aurorapvp.modules.kitModule.getKit;

import java.io.File;
import java.util.List;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIModule {
  private static Inventory inv;

  public static void onGUIInventoryClicked(InventoryClickEvent event) {
    if (event.getInventory().equals(inv)) {
      event.setCancelled(true);

      final ItemStack clickedItem = event.getCurrentItem();

      if (clickedItem == null || clickedItem.getType().isAir()) {
        return;
      }

      final Player p = (Player) event.getWhoClicked();

      getKit(p, serializeComponent.serialize(event.getCurrentItem().getItemMeta().displayName()));
    }
  }

  public static void openGUI(Player p) {
    initializeGUI(p);
    p.openInventory(inv);
  }

  public static void initializeGUI(Player p) {
    inv = Bukkit.createInventory(null, 54, getLangComponent("GUIName"));
    YamlConfiguration GUIFile = getGUIPublicData();

    for (int i = 0; i < 2; i++) {
      if (GUIFile.getConfigurationSection("kits") != null) {
        for (Object path : GUIFile.getConfigurationSection("kits").getKeys(false).toArray()) {
          inv.addItem(createGUIItem(GUIFile.getItemStack("kits." + path + ".displayItem").getType(),
              (String) path, GUIFile.getString("kits." + path + ".creator"),
              GUIFile.getInt("kits." + path + ".type")));
        }
      }
      GUIFile = YamlConfiguration.loadConfiguration(
          new File(DataFolder, "/GUIs/" + p.getUniqueId() + ".yml"));
    }
  }

  protected static ItemStack createGUIItem(final Material material, final String name,
                                           String creator, int type) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    meta.displayName(
        MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF>" + name)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, true));

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
}
