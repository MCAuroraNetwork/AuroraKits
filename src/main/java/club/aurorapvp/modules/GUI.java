package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.Lang;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI {
  private static final List<GUI> guis = new ArrayList<>();
  private final Player owner;
  private final Inventory inv = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));

  public GUI(Player p) {
    this.owner = p;

    guis.add(this);
  }

  public Player getOwner() {
    return owner;
  }

  public Inventory getInventory() {
    return inv;
  }

  public void addKit(Kit kit) {
    inv.addItem(kit.getDisplayItem());
  }

  public void removeKit(Kit kit) {
    inv.remove(kit.getDisplayItem());
  }

  public void create() {
    for (Kit kit : Kit.getKits(this.getOwner())) {
      inv.addItem(kit.getDisplayItem());
    }
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

    if (event.getInventory().equals(GUI.getGui(p).getInventory())) {
      event.setCancelled(true);

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
    p.openInventory(GUI.getGui(p).getInventory());
  }
}
