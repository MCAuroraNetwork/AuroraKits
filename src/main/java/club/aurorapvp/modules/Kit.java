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
  private static final HashMap<String, Kit> LAST_USED_KIT = new HashMap<>();
  private static final List<Kit> kits = new ArrayList<>();
  private final KitData kitData;
  private String kitName;
  private Player owner;
  private boolean isPublic;
  private ItemStack[] itemStacks;
  private ItemStack displayItem;
  private final String kitPath;

  public Kit(String kitName, String kitPath) {
    this.kitPath = kitPath;
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

  public boolean isPublic() {
    return isPublic;
  }

  public boolean exists() {
    return this.getData().getFile().exists();
  }

  public void create(Player p, boolean isPublic) {
    this.owner = p;
    this.isPublic = isPublic;

    if (this.exists()) {
      p.sendMessage(Lang.getComponent("kit-already-exists"));
      return;
    }

    displayItem =
        createDisplayItem(p.getInventory().getItemInMainHand().getType(), kitName, owner.getName(),
            isPublic);

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
      if (isPublic) {
        for (GUI gui : GUI.getGuis()) {
          gui.addKit(this);
        }
      } else {
        GUI.getGui(p).addKit(this);
      }

      p.sendMessage(Lang.getComponent("kit-created"));
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-item"));
    }
  }

  public void delete(Player p, boolean isPublic) {
    if (!Objects.equals(this.isPublic(), isPublic)) {
      if (p != null) {
        p.sendMessage(Lang.getComponent("kit-not-found"));
      }
      return;
    }

    kitData.getFile().delete();

    if (isPublic) {
      for (GUI gui : GUI.getGuis()) {
        gui.removeKit(this);
      }
    } else {
      GUI.getGui(p).removeKit(this);
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
      LAST_USED_KIT.put(p.getName(), this);
    } else {
      p.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  public void reload() {
    this.owner = this.getData().getOwner();
    this.itemStacks = this.getData().getItemStacks();
    this.isPublic = this.getData().isPublic();
    this.displayItem = this.getData().getDisplayItem();
  }

  protected static ItemStack createDisplayItem(final Material material, final String name,
                                               String creator, boolean isPublic) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    meta.displayName(
        AuroraKits.DESERIALIZE_COMPONENT.deserialize("<gradient:#FFAA00:#FF55FF>" + name)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, true));

    if (isPublic) {
      meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Private Kit")));
    } else {
      meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Public Kit")));
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
    AuroraKits.PLUGIN.getLogger().info("All kits reloaded!");
  }

  public static Kit[] getKits(Player p) {
    List<Kit> playerKits = new ArrayList<>();

    for (Kit kit : kits) {
      if (kit.getOwner().getUniqueId() == p.getUniqueId() || kit.isPublic()) {
        playerKits.add(kit);
      }
    }

    Kit[] kitArray = new Kit[playerKits.size()];

    for (int i = 0; i < playerKits.size(); i++) {
      kitArray[i] = playerKits.get(i);
    }

    return kitArray;
  }

  public static Kit getKit(Player p, String kitName) {
    for (Kit kit : kits) {
      if (kit.getOwner().getUniqueId() == p.getUniqueId() || kit.isPublic) {
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

    AuroraKits.PLUGIN.getLogger().info("All kits loaded!");
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
      LAST_USED_KIT.get(p.getName()).giveKit(p);
    }
  }
}