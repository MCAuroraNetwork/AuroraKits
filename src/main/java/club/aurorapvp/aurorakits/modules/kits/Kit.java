package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.data.KitDataHandler;
import club.aurorapvp.aurorakits.util.ItemStackUtil;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {
  private static final Set<Kit> kits = new HashSet<>();
  private static final HashMap<Player, Kit> LAST_USED_KIT = new HashMap<>();
  private final KitDataHandler data;
  private final String name;
  private OfflinePlayer owner;
  private boolean publicKit;
  private ItemStack[] itemStacks;
  private ItemStack displayItem;
  private final String path;

  public Kit(String kitName, String kitPath) {
    this.path = kitPath;
    this.name = kitName;
    this.data = new KitDataHandler(this);

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

  public KitDataHandler getData() {
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
        ItemStackUtil.createDisplayItem(p.getInventory().getItemInMainHand().getType(), name,
            owner.getName(), isPublic);

    if (p.getInventory().getItemInMainHand().getItemMeta() != null) {
      itemStacks = new ItemStack[p.getInventory().getContents().length];
      for (int i = 0; i < p.getInventory().getContents().length; i++) {
        itemStacks[i] = p.getInventory().getContents()[i] == null ? null :
            p.getInventory().getContents()[i].clone();
      }

      data.create();

      if (isPublic) {
        for (KitsGUI kitsGui : KitsGUI.getGuis()) {
          kitsGui.addKit(this);
        }
      } else {
        Objects.requireNonNull(KitsGUI.getGui(p)).addKit(this);
      }

      p.sendMessage(Lang.getComponent("kit-created"));
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-item"));
    }
  }

  public void edit(Player p, ItemStack[] newContents) {
    itemStacks = new ItemStack[newContents.length];

    for (int i = 0; i < newContents.length; i++) {
      itemStacks[i] = newContents[i] == null ? null : newContents[i].clone();
    }

    data.create();

    p.sendMessage(Lang.getComponent("kit-updated"));
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
      for (KitsGUI kitsGui : KitsGUI.getGuis()) {
        kitsGui.removeKit(this);
      }
    } else {
      Objects.requireNonNull(KitsGUI.getGui(p)).removeKit(this);
    }

    kits.remove(this);

    if (p != null) {
      p.sendMessage(Lang.getComponent("kit-deleted"));
    }
  }

  public void editKit(Player p) {
    Objects.requireNonNull(KitEditor.getEditor(p)).openEditor(p, this);
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
    new KitsGUI(p);
    new KitEditor(p);
  }

  public static void onPlayerQuit(Player p) {
    for (Kit kit : getKits(p, true)) {
      if (!kit.isPublic()) {
        kits.remove(kit);
      }
    }

    KitsGUI.onPlayerQuit(p);
  }

  public static Kit getLastUsedKit(Player p) {
    if (LAST_USED_KIT.containsKey(p)) {
      return LAST_USED_KIT.get(p);
    }

    return null;
  }
}