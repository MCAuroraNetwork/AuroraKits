package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.datahandlers.KitData;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
  private static final HashMap<Player, Kit> LAST_USED_KIT = new HashMap<>();
  private static final List<Kit> kits = new ArrayList<>();
  @Getter private final KitData data;
  @Getter private String name;
  @Getter private OfflinePlayer owner;
  @Getter private boolean publicKit;
  @Getter private ItemStack[] itemStacks;
  @Getter private ItemStack displayItem;
  @Getter private final String path;

  public Kit(String kitName, String kitPath) {
    this.path = kitPath;
    this.name = kitName;
    this.data = new KitData(this);

    kits.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public boolean exists() {
    return this.getData().getFile().exists();
  }

  public void create(Player p, boolean isPublic) {
    this.owner = p;
    this.publicKit = isPublic;

    if (this.exists()) {
      p.sendMessage(Lang.getComponent("kit-already-exists"));
      return;
    }

    displayItem =
        createDisplayItem(p.getInventory().getItemInMainHand().getType(), name, owner.getName(),
            isPublic);

    if (p.getInventory().getItemInMainHand().getItemMeta() != null && getAmount(p) < 54) {
      itemStacks = new ItemStack[p.getInventory().getContents().length];
      for (int i = 0; i < p.getInventory().getContents().length; i++) {
        itemStacks[i] = p.getInventory().getContents()[i] == null ? null :
            p.getInventory().getContents()[i].clone();
      }

      data.create();
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
    if (!Objects.equals(this.isPublicKit(), isPublic)) {
      if (p != null) {
        p.sendMessage(Lang.getComponent("kit-not-found"));
      }
      return;
    }

    data.getFile().delete();

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
      LAST_USED_KIT.put(p, this);
    } else {
      p.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  public void reload() {
    this.owner = this.getData().getOwner();
    this.itemStacks = this.getData().getItemStacks();
    this.publicKit = this.getData().isPublic();
    this.displayItem = this.getData().getDisplayItem();
  }

  private static ItemStack createDisplayItem(final Material material, final String name,
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
          .deserialize("This is a Public Kit")));
    } else {
      meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Private Kit")));
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
    AuroraKits.INSTANCE.getLogger().info("All kits reloaded!");
  }

  public static Kit[] getKits(Player p) {
    List<Kit> playerKits = new ArrayList<>();

    for (Kit kit : kits) {
      if (kit.getOwner().getPlayer() == p || kit.isPublicKit()) {
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
      if (kit.getOwner().getPlayer() == p || kit.publicKit) {
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

    AuroraKits.INSTANCE.getLogger().info("All kits loaded!");
  }

  public static void onPlayerQuit(Player p) {
    for (Kit kit : getKits(p)) {
      if (!kit.isPublicKit()) {
        kits.remove(kit);
      }
    }

    GUI.onPlayerQuit(p);
  }

  public static Kit getLastUsedKit(Player p) {
    if (!LAST_USED_KIT.containsKey(p)) {
      return getKit(p, Config.get().getString("kits.lastUsedKit.defaultKit"));
    } else {
      return LAST_USED_KIT.get(p);
    }
  }

  public static void giveLastUsedKit(Player p) {
    if (!LAST_USED_KIT.containsKey(p)) {
      getKit(p, Config.get().getString("kits.lastUsedKit.defaultKit")).giveKit(p);
    } else {
      LAST_USED_KIT.get(p).giveKit(p);
    }
  }
}