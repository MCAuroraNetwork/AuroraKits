package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.configs.Lang;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI {
  private static final Set<GUI> guis = new HashSet<>();
  private final Player owner;
  private Inventory[] inventories;
  private int currentPage = 0;
  private int pages;

  public GUI(Player p) {
    this.owner = p;

    guis.add(this);
  }

  public Player getOwner() {
    return owner;
  }

  public void addKit(Kit kit) {
    Inventory lastPage = inventories[pages];

    if (lastPage.getContents()[44] != null) {
      createPage(kit);
    } else {
      lastPage.addItem(kit.getDisplayItem());
    }
  }

  public void removeKit(Kit kit) {
    ItemStack displayItem = kit.getDisplayItem();

    for (Inventory inventory : inventories) {
      for (ItemStack itemStack : inventory.getContents()) {
        if (itemStack == displayItem) {
          inventory.remove(itemStack);
        }
      }
    }
  }

  public void createPage(Kit kit) {
    Inventory inventory = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));
    inventory.addItem(kit.getDisplayItem());

    ItemMeta meta;

    ItemStack nextPage = new ItemStack(Material.PAPER);
    meta = nextPage.getItemMeta();
    meta.displayName(Lang.getComponent("next-page"));
    nextPage.setItemMeta(meta);

    ItemStack previousPage = new ItemStack(Material.PAPER);
    meta = previousPage.getItemMeta();
    meta.displayName(Lang.getComponent("previous-page"));
    previousPage.setItemMeta(meta);

    inventory.setItem(45, nextPage);
    inventory.setItem(53, nextPage);

    inventories[pages] = inventory;
    pages++;
  }

  public void createPage(Kit[] kits) {
    Inventory inventory = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));

    for (Kit kit : kits) {
      inventory.addItem(kit.getDisplayItem());
    }

    ItemMeta meta;

    ItemStack nextPage = new ItemStack(Material.PAPER);
    meta = nextPage.getItemMeta();
    meta.displayName(Lang.getComponent("next-page"));
    nextPage.setItemMeta(meta);

    ItemStack previousPage = new ItemStack(Material.PAPER);
    meta = previousPage.getItemMeta();
    meta.displayName(Lang.getComponent("previous-page"));
    previousPage.setItemMeta(meta);

    inventory.setItem(45, nextPage);
    inventory.setItem(53, nextPage);

    inventories[pages] = inventory;
    pages++;
  }

  public void nextPage() {
    if (currentPage <= pages) {
      currentPage++;
    } else {
      currentPage = 0;
    }
    GUI.open(owner);
  }

  public void previousPage() {
    if (currentPage >= 1) {
      currentPage--;
    } else {
      currentPage = pages;
    }
    GUI.open(owner);
  }

  public Inventory getCurrentPage() {
    return inventories[currentPage];
  }

  public boolean isGuiInventory(Inventory inventory) {
    for (Inventory guiInventory : inventories) {
      if (inventory.equals(guiInventory)) {
        return true;
      }
    }
    return false;
  }

  public void reload() {
    pages = 0;

    for (int i = 0; i < Kit.getKits(this.getOwner(), true).length; i++) {
      if (i % 44 == 0 && i != 0) {
        pages++;
      }
    }

    inventories = new Inventory[pages + 1];

    for (int i = 0; i < pages + 1; i++) {
      Kit[] kits = new Kit[Kit.getKits(this.getOwner(), true).length - i * 45];

      for (int j = i * 45; j < Kit.getKits(this.getOwner(), true).length; j++) {
        kits[j] = Kit.getKits(this.getOwner(), true)[j];
      }

      createPage(kits);
    }
  }

  public static void reloadAll() {
    for (GUI gui : guis) {
      gui.reload();
    }
    AuroraKits.LOGGER.info("GUIs Reloaded!");
  }

  public static GUI[] getGuis() {
    GUI[] guiArray = new GUI[guis.size()];
    int i = 0;

    for (GUI gui : guis) {
      guiArray[i] = gui;
      i++;
    }

    return guiArray;
  }

  public static void onPlayerQuit(Player p) {
    guis.removeIf(gui -> gui.getOwner() == p);
  }

  public static void inventoryClicked(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (GUI.getGui(p).isGuiInventory(event.getInventory())) {
      event.setCancelled(true);

      if (event.getSlot() == 45) {
        GUI.getGui(p).previousPage();
        return;
      } else if (event.getSlot() == 53) {
        GUI.getGui(p).nextPage();
        return;
      }

      final ItemStack clickedItem = event.getCurrentItem();

      if (clickedItem == null || clickedItem.getType().isAir()) {
        return;
      }

      if (event.getCurrentItem().getItemMeta().displayName() == null) {
        p.sendMessage(Lang.getComponent("kit-invalid-name"));
      } else {
        Kit.getKit(p, AuroraKits.SERIALIZE_COMPONENT.serialize(
            event.getCurrentItem().getItemMeta().displayName())).giveKit(p);
      }
    }
  }

  public static GUI getGui(Player p) {
    for (GUI gui : guis) {
      if (gui.getOwner() == p) {
        return gui;
      }
    }
    return null;
  }

  public static void open(Player p) {
    p.openInventory(GUI.getGui(p).getCurrentPage());
  }
}
