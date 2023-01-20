package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.Lang;
import club.aurorapvp.datahandlers.GUIData;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI {
  private static final List<GUI> GUIs = new ArrayList<>();
  private final Player p;
  private final GUIData guiData;
  private Inventory inv;

  public GUI(Player p) {
    this.p = p;
    this.guiData = new GUIData(this);

    GUIs.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public Player getPlayer() {
    return p;
  }

  public GUIData getData() {
    return guiData;
  }

  public static GUI getGui(Player p) {
    for (GUI gui : GUIs) {
      if (p == gui.getPlayer()) {
        return gui;
      }
    }
    return null;
  }

  public static List<GUI> getGUIs() {
    return GUIs;
  }

  public Inventory asInventory() {
    return inv;
  }

  public boolean exists() {
    return this.getData().getFile() != null;
  }

  public void addEntry(Kit kit) {
    this.getData().addEntry(kit);
    inv = this.getData().getInventory();
  }

  public void reload() {
    inv = this.getData().getInventory();
  }

  public void addItem(Kit kit) {
    this.getData().addItem(kit);
  }

  public void deleteEntry(Kit kit) {
    this.getData().deleteEntry(kit);
  }

  public static void inventoryClicked(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (event.getInventory().equals(GUI.getGui(p).asInventory())) {
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

  public static void onPlayerJoin(Player p) {
    if (!GUI.getGui(p).exists()) {
      new GUI(p);
    }
  }

  public static void onPlayerQuit(Player p) {
    if (!GUI.getGui(p).exists()) {
      new GUI(p);
    }
  }

  public static void open(Player p) {
    p.openInventory(getGui(p).asInventory());
  }
}
