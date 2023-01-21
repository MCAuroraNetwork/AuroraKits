package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.Config;
import club.aurorapvp.config.Lang;
import club.aurorapvp.datahandlers.KitData;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {
  private static final HashMap<String, String> LAST_USED_KIT = new HashMap<>();
  private static final List<Kit> kits = new ArrayList<>();
  private KitData kitData;
  private String kitName;
  private Player owner;
  private Integer kitType;
  private String kitPath;
  private ItemStack[] inventoryData;
  private ItemStack displayItem;

  public Kit(String kitName) {
    this.kitName = kitName;
    this.kitData = new KitData(this);

    kits.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public ItemStack getDisplayItem() {
    return displayItem;
  }

  public ItemStack[] getInventoryData() {
    return inventoryData;
  }

  public KitData getData() {
    return kitData;
  }

  public Player getOwner() {
    return owner;
  }

  public String getName() {
    return kitName;
  }

  public Integer getType() {
    return kitType;
  }

  public boolean exists() {
    return getData().getFile().exists();
  }

  public void create(Player p, int kitType) {
    this.kitType = kitType;
    this.owner = p;
    this.displayItem = p.getInventory().getItemInMainHand();

    if (p.getInventory().getItemInMainHand().getItemMeta() == null && KitData.getAmount(p) < 54) {
      if (kitName == null) {
        kitName = "Kit" + Math.addExact(KitData.getAmount(p), 1);
      }

      inventoryData = p.getInventory().getContents();
      kitData = new KitData(this);
      if (kitType == 2) {
        kitPath = "hidden";
      } else if (kitType == 0) {
        kitPath = String.valueOf(p.getUniqueId());
        GUI.getGui(p).addKit(this);
      } else {
        for (GUI gui : GUI.getGuis()) {
          gui.addKit(this);
        }
      }
      p.sendMessage(Lang.getComponent("kit-created"));
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-item"));
    }
  }

  public String getPath() {
    return kitPath;
  }

  public void delete(Player p) {
    kitData.getFile().delete();
    GUI.getGui(p).removeKit(this);
    kits.remove(this);

    if (p != null) {
      p.sendMessage(Lang.getComponent("kit-deleted"));
    }
  }

  public void giveKit(Player p) {
    if (this.exists()) {
      p.getInventory().setContents(inventoryData);
      p.sendMessage(Lang.getComponent("kit-used"));
      LAST_USED_KIT.put(p.getName(), kitName);
    } else {
      p.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  public void reload() {
    this.owner = this.getData().getOwner();
    this.inventoryData = kitData.getInventoryData();
    this.owner = kitData.getOwner();
    this.kitType = kitData.getType();
  }

  public static void reloadAll() {
    for (Kit kit : kits) {
      kit.reload();
    }
  }

  public static Kit[] getKits(Player p) {
    List<Kit> playerKits = new ArrayList<>();
    int i = 0;

    for (Kit kit : kits) {
      if (kit.getOwner() == p || kit.getOwner() == null) {
        playerKits.add(kit);
        i++;
      }
    }

    Kit[] kitArray = new Kit[i];
    i = 0;

    for (Kit kit : playerKits ) {
      kitArray[i] = kit;
      i++;
    }

    return kitArray;
  }

  public static Kit getKit(Player p, String kitName) {
    for (Kit kit : kits) {
      if (kit.getOwner() == p || kit.getOwner() == null) {
        if (Objects.equals(kit.getName(), kitName)) {
          return kit;
        }
      }
    }
    return null;
  }

  public static void onPlayerJoin(Player p) {
    File dir = new File(AuroraKits.DATA_FOLDER, "/kits/" + p.getUniqueId());

    for (File file : dir.listFiles()) {
      String kitName = file.getName().replace(".yml", "");
      if (!Kit.getKit(p, kitName).exists()) {
        new Kit(kitName);
      }
    }
    new GUI(p).create();
  }

  public static void onPlayerQuit(Player p) {
    File dir = new File(AuroraKits.DATA_FOLDER, "/kits/" + p.getUniqueId());

    for (File file : dir.listFiles()) {
      String kitName = file.getName().replace(".yml", "");

      kits.remove(getKit(p, kitName));
    }
    GUI.onPlayerQuit(p);
  }

  public static void giveLastUsedKit(Player p) {
    if (!LAST_USED_KIT.containsKey(p.getName())) {
      getKit(p, Config.get().getString("kits.lastUsedKit.defaultKit")).giveKit(p);
    } else {
      getKit(p, LAST_USED_KIT.get(p.getName())).giveKit(p);
    }
  }
}