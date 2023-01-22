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
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
  private static final HashMap<String, String> LAST_USED_KIT = new HashMap<>();
  private static final List<Kit> kits = new ArrayList<>();
  private final KitData kitData;
  private String kitName;
  private Player owner;
  private Integer kitType;
  private ItemStack[] itemStacks;
  private ItemStack displayItem;
  private final String kitPath;

  public Kit(String kitName, String kitPath) {
    this.kitPath = kitPath;
    this.kitName = kitName;
    this.kitData = new KitData(this);
    itemStacks = new ItemStack[0];

    kits.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public ItemStack getDisplayItem() {
    return displayItem;
  }

  public String getPath() {
    return kitPath;
  }

  public ItemStack[] getItemStacks() {
    return itemStacks;
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
    return this.getData().getFile().exists();
  }

  public void create(Player p, Integer kitType) {
    this.owner = p;
    this.kitType = kitType;

    if (this.exists()) {
      p.sendMessage(Lang.getComponent("kit-already-exists"));
      return;
    }

    displayItem =
        createDisplayItem(p.getInventory().getItemInMainHand().getType(), kitName, owner.getName(),
            kitType);

    if (p.getInventory().getItemInMainHand().getItemMeta() != null && getAmount(p) < 54) {
      if (kitName == null) {
        kitName = "Kit" + Math.addExact(getAmount(p), 1);
      }

      itemStacks = new ItemStack[p.getInventory().getContents().length];
      for (int i = 0; i < p.getInventory().getContents().length; i++) {
        itemStacks[i] = p.getInventory().getContents()[i] == null ? null :
            p.getInventory().getContents()[i].clone();
      }

      kitData.create();
      if (kitType == 0) {
        GUI.getGui(p).addKit(this);
      } else if (kitType == 1) {
        for (GUI gui : GUI.getGuis()) {
          gui.addKit(this);
        }
      }

      p.sendMessage(Lang.getComponent("kit-created"));
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-item"));
    }
  }

  public void delete(Player p, Integer kitType) {
    if (!Objects.equals(this.getType(), kitType)) {
      p.sendMessage(Lang.getComponent("kit-not-found"));
      return;
    }

    kitData.getFile().delete();

    if (kitType == 0) {
      GUI.getGui(p).removeKit(this);
    } else if (kitType == 1) {
      for (GUI gui : GUI.getGuis()) {
        gui.removeKit(this);
      }
    }

    kits.remove(this);

    if (p != null) {
      p.sendMessage(Lang.getComponent("kit-deleted"));
    }
  }

  public void giveKit(Player p) {
    if (this.exists()) {
      p.getInventory().setContents(this.getItemStacks());
      p.sendMessage(Lang.getComponent("kit-used"));
      LAST_USED_KIT.put(p.getName(), kitName);
    } else {
      p.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  public void reload() {
    this.owner = this.getData().getOwner();
    this.itemStacks = this.getData().getItemStacks();
    this.kitType = this.getData().getType();
    this.displayItem = this.getData().getDisplayItem();
    this.kitType = this.getData().getType();
  }

  protected static ItemStack createDisplayItem(final Material material, final String name,
                                               String creator, int type) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    meta.displayName(
        AuroraKits.DESERIALIZE_COMPONENT.deserialize("<gradient:#FFAA00:#FF55FF>" + name)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, true));

    switch (type) {
      case 0 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Private Kit")));
      case 1 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Public Kit")));
      default -> meta.lore();
    }

    item.setItemMeta(meta);

    return item;
  }

  public static int getAmount(Player p) {
    return getKits(p).length;
  }

  public static void reloadAll() {
    kits.clear();

    setup();

    for (Player p : Bukkit.getOnlinePlayers()) {
      setupPlayer(p);
    }

    for (Kit kit : kits) {
      kit.reload();
    }
    AuroraKits.PLUGIN.getLogger().info("Kits Reloaded!");
  }

  public static Kit[] getKits(Player p) {
    List<Kit> playerKits = new ArrayList<>();
    int i = 0;

    for (Kit kit : kits) {
      if (kit.getOwner() == p || kit.getType() == 1) {
        playerKits.add(kit);
        i++;
      }
    }

    Kit[] kitArray = new Kit[i];
    i = 0;

    for (Kit kit : playerKits) {
      kitArray[i] = kit;
      i++;
    }

    return kitArray;
  }

  public static Kit getKit(Player p, String kitName) {
    for (Kit kit : kits) {
      if (kit.getOwner() == p || kit.getType() == 1) {
        if (Objects.equals(kit.getName(), kitName)) {
          return kit;
        }
      }
    }
    return null;
  }

  public static void setupPlayer(Player p) {
    File dir = new File(AuroraKits.DATA_FOLDER, "/kits/" + p.getUniqueId());

    if (dir.exists()) {
      for (File file : dir.listFiles()) {
        String kitName = file.getName().replace(".yml", "");
        new Kit(kitName, String.valueOf(p.getUniqueId()));
      }
    }
    new GUI(p).reload();
  }

  public static void setup() {
    File dir = new File(AuroraKits.DATA_FOLDER, "/kits/public/");

    if (dir.exists()) {
      for (File file : dir.listFiles()) {
        String kitName = file.getName().replace(".yml", "");
        new Kit(kitName, "public");
      }
    }
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