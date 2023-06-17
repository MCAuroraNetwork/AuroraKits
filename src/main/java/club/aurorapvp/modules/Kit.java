package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.datahandlers.KitData;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
  private static final Set<Kit> kits = new HashSet<>();
  private static final HashMap<Player, Kit> LAST_USED_KIT = new HashMap<>();
  private final KitData data;
  private final String name;
  private OfflinePlayer owner;
  private boolean publicKit;
  private ItemStack[] itemStacks;
  private ItemStack displayItem;
  private final String path;

  public Kit(String kitName, String kitPath) {
    this.path = kitPath;
    this.name = kitName;
    this.data = new KitData(this);

    kits.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public static void init() {
    File dir = new File(AuroraKits.INSTANCE.getDataFolder(), "/kits/public/");

    if (dir.exists()) {
      for (File file : Objects.requireNonNull(dir.listFiles())) {
        String kitName = file.getName().replace(".yml", "");
        new Kit(kitName, "public");
      }
    }

    AuroraKits.INSTANCE.getLogger().info("All kits loaded!");
  }

  public String getPath() {
    return path;
  }

  public ItemStack getDisplayItem() {
    return displayItem;
  }

  public ItemStack[] getItemStacks() {
    return itemStacks;
  }

  public boolean isPublic() {
    return publicKit;
  }

  public OfflinePlayer getOwner() {
    return owner;
  }

  public String getName() {
    return name;
  }

  public KitData getData() {
    return data;
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

    if (p.getInventory().getItemInMainHand().getItemMeta() != null) {
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
        Objects.requireNonNull(GUI.getGui(p)).addKit(this);
      }

      p.sendMessage(Lang.getComponent("kit-created"));
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-item"));
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void delete(Player p, boolean isPublic) {
    if (!Objects.equals(this.isPublic(), isPublic)) {
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
      Objects.requireNonNull(GUI.getGui(p)).removeKit(this);
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
        MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF>" + name)
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

  public static int getAmount(Player p, boolean includePublic) {
    return getKits(p, includePublic).size();
  }

  @SuppressWarnings("unused")
  public static Kit createKit(String name, Player p) {
    Kit kit;

    kit = new Kit(name, "public");
    kit.create(p, true);

    return kit;
  }

  @SuppressWarnings("unused")
  public static void deleteKit(String name) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        kit.delete(null, true);
      }
    }
  }

  @SuppressWarnings("unused")
  public static Kit getKit(String name) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        return kit;
      }
    }
    return null;
  }

  @SuppressWarnings("unused")
  public static Kit getLastKit(Player p) {
    return Kit.getLastUsedKit(p);
  }

  public static Set<Kit> getKits() {
    return Kit.getKits(null, true);
  }

  @SuppressWarnings("unused")
  public static void giveKit(Kit kit, Player p) {
    kit.giveKit(p);
  }

  public static void reloadAll() {
    kits.clear();

    Kit.init();

    for (Player p : Bukkit.getOnlinePlayers()) {
      setupPlayer(p);
    }

    for (Kit kit : kits) {
      kit.reload();
    }
    AuroraKits.INSTANCE.getLogger().info("All kits reloaded!");
  }

  public static Set<Kit> getKits(Player p, boolean includePublic) {
    Set<Kit> playerKits = new HashSet<>();

    for (Kit kit : kits) {
      if (kit.getOwner() == p || (kit.isPublic() && includePublic)) {
        playerKits.add(kit);
      }
    }

    return playerKits;
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
    File dir = new File(AuroraKits.INSTANCE.getDataFolder(), "/kits/" + p.getUniqueId());

    if (dir.exists()) {
      for (File file : Objects.requireNonNull(dir.listFiles())) {
        String kitName = file.getName().replace(".yml", "");
        new Kit(kitName, String.valueOf(p.getUniqueId()));
      }
    }
    new GUI(p).reload();
  }

  public static void onPlayerQuit(Player p) {
    for (Kit kit : getKits(p, true)) {
      if (!kit.isPublic()) {
        kits.remove(kit);
      }
    }

    GUI.onPlayerQuit(p);
  }

  public static Kit getLastUsedKit(Player p) {
    if (LAST_USED_KIT.containsKey(p)) {
      return LAST_USED_KIT.get(p);
    }

    return null;
  }
}