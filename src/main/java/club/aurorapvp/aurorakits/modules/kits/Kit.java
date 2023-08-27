package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.data.KitDataHandler;
import club.aurorapvp.aurorakits.util.ItemStackUtil;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {

  private static final Map<OfflinePlayer, Set<Kit>> PRIVATE_KITS = new HashMap<>();
  private static final Map<OfflinePlayer, Set<Kit>> PUBLIC_KITS = new HashMap<>();
  private static final HashMap<Player, Kit> LAST_USED_KIT = new HashMap<>();
  private final KitDataHandler data;
  private final boolean isPublic;
  private final String name;
  private final String path;
  private final OfflinePlayer owner;
  private boolean publicKit;
  private ItemStack[] itemStacks;
  private ItemStack displayItem;

  public Kit(String kitName, OfflinePlayer owner, boolean isPublic) {
    this.owner = owner;
    this.isPublic = isPublic;
    this.path = isPublic ? "public" : String.valueOf(owner.getUniqueId());
    this.name = kitName;
    this.data = new KitDataHandler(this);

    Set<Kit> set = (isPublic ? PUBLIC_KITS : PRIVATE_KITS).getOrDefault(owner, new HashSet<>());

    set.add(this);

    (isPublic ? PUBLIC_KITS : PRIVATE_KITS).put(owner, set);

    if (this.exists()) {
      this.reload();
    }
  }

  public Kit(String kitName, OfflinePlayer owner, boolean isPublic, ItemStack[] contents) {
    this.owner = owner;
    this.isPublic = isPublic;
    this.path = isPublic ? "public" : String.valueOf(owner.getUniqueId());
    this.name = kitName;
    this.data = new KitDataHandler(this);

    Set<Kit> set = (isPublic ? PUBLIC_KITS : PRIVATE_KITS).getOrDefault(owner, new HashSet<>());

    set.add(this);

    (isPublic ? PUBLIC_KITS : PRIVATE_KITS).put(owner, set);

    this.create(contents);
  }

  public static void init() {
    File dir = new File(AuroraKits.INSTANCE.getDataFolder(), "/kits/public/");

    if (dir.exists()) {
      for (File file : Objects.requireNonNull(dir.listFiles())) {
        String kitName = file.getName().replace(".yml", "");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        new Kit(
            kitName,
            Bukkit.getOfflinePlayer(
                UUID.fromString(Objects.requireNonNull(yaml.getString("owner")))),
            yaml.getBoolean("isPublic"));
      }
    }

    AuroraKits.INSTANCE.getLogger().info("All kits loaded!");
  }

  public static int getAmount(Player player, boolean includePublic) {
    return getKits(player, includePublic).size();
  }

  @SuppressWarnings("unused")
  public static Kit createKit(String name, Player player) {
    Kit kit;

    kit = new Kit(name, player, true);
    kit.create(player.getInventory().getContents());

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

  public static Set<Kit> getKits() {
    Set<Kit> set = new HashSet<>();

    for (Set<Kit> kits : PRIVATE_KITS.values()) {
      set.addAll(kits);
    }

    for (Set<Kit> kits : PUBLIC_KITS.values()) {
      set.addAll(kits);
    }

    return set;
  }

  public static Set<Kit> getPublicKits() {
    Set<Kit> set = new HashSet<>();

    for (Set<Kit> kits : PUBLIC_KITS.values()) {
      set.addAll(kits);
    }

    return set;
  }

  @SuppressWarnings("unused")
  public static void giveKit(Kit kit, Player player) {
    kit.giveKit(player);
  }

  public static void reloadAll() {
    PUBLIC_KITS.clear();
    PRIVATE_KITS.clear();

    Kit.init();

    for (Player player : Bukkit.getOnlinePlayers()) {
      register(player);
    }

    for (Kit kit : Kit.getKits()) {
      kit.reload();
    }

    AuroraKits.INSTANCE.getLogger().info("All kits reloaded!");
  }

  public static Set<Kit> getKits(Player player, boolean includePublic) {
    Set<Kit> playerKits = new HashSet<>();

    if (PRIVATE_KITS.containsKey(player)) {
      playerKits.addAll(PRIVATE_KITS.get(player));
    }

    if (includePublic) {
      playerKits.addAll(Kit.getPublicKits());
    }

    for (Kit kit : Kit.getKits()) {
      if (kit.getOwner() == player || (kit.isPublic() && includePublic)) {
        playerKits.add(kit);
      }
    }

    return playerKits;
  }

  public static Kit getKit(Player player, String kitName) {
    if (PRIVATE_KITS.containsKey(player)) {
      for (Kit kit : PRIVATE_KITS.get(player)) {
        if (Objects.equals(kit.getName(), kitName)) {
          return kit;
        }
      }
    }

    for (Kit kit : Kit.getPublicKits()) {
      if (Objects.equals(kit.getName(), kitName)) {
        return kit;
      }
    }

    return null;
  }

  public static void register(Player player) {
    File dir = new File(AuroraKits.INSTANCE.getDataFolder(), "/kits/" + player.getUniqueId());

    if (dir.exists()) {
      for (File file : Objects.requireNonNull(dir.listFiles())) {
        String kitName = file.getName().replace(".yml", "");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        new Kit(
            kitName,
            Bukkit.getOfflinePlayer(
                UUID.fromString(Objects.requireNonNull(yaml.getString("owner")))),
            yaml.getBoolean("isPublic"));
      }
    }

    new KitsGUI(player);
    new KitEditor(player);
  }

  public static void unregister(Player player) {
    for (Kit kit : getKits(player, true)) {
      if (!kit.isPublic()) {
        Kit.getKits().remove(kit);
      }
    }

    KitsGUI.unregister(player);
  }

  public static Kit getLastUsedKit(Player player) {
    if (LAST_USED_KIT.containsKey(player)) {
      return LAST_USED_KIT.get(player);
    }

    return Kit.getKit(Config.get().getString("kits.give-last-kit.default-kit"));
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

  public void create(ItemStack[] contents) {
    this.publicKit = isPublic;

    if (this.exists()) {
      Objects.requireNonNull(owner.getPlayer())
          .sendMessage(Lang.getComponent("kit-already-exists"));
      return;
    }

    if (Objects.requireNonNull(owner.getPlayer()).getInventory().getItemInMainHand().getItemMeta()
        == null) {
      Objects.requireNonNull(owner.getPlayer()).sendMessage(Lang.getComponent("kit-invalid-item"));
      return;
    }

    displayItem =
        ItemStackUtil.createDisplayItem(
            owner.getPlayer().getInventory().getItemInMainHand().getType(),
            name,
            owner.getName(),
            isPublic);

    itemStacks = new ItemStack[contents.length];

    for (int i = 0; i < contents.length; i++) {
      itemStacks[i] = contents[i] == null ? null : contents[i].clone();
    }

    data.create();

    if (isPublic) {
      for (KitsGUI kitsGui : KitsGUI.getGuis()) {
        kitsGui.addKit(this);
      }
    } else {
      Objects.requireNonNull(KitEditor.getEditor(owner.getPlayer())).addKit(this);
      Objects.requireNonNull(KitsGUI.getGUI(owner.getPlayer())).addKit(this);
    }

    Objects.requireNonNull(owner.getPlayer()).sendMessage(Lang.getComponent("kit-created"));
  }

  public void edit(Player player, ItemStack[] newContents) {
    itemStacks = new ItemStack[newContents.length];

    for (int i = 0; i < newContents.length; i++) {
      itemStacks[i] = newContents[i] == null ? null : newContents[i].clone();
    }

    data.create();

    player.sendMessage(Lang.getComponent("kit-updated"));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void delete(Player player, boolean isPublic) {
    if (!Objects.equals(this.isPublic(), isPublic)) {
      if (player != null) {
        player.sendMessage(Lang.getComponent("kit-not-found"));
      }
      return;
    }

    data.getFile().delete();

    if (isPublic) {
      for (KitsGUI kitsGui : KitsGUI.getGuis()) {
        kitsGui.removeKit(this);
      }
    } else {
      Objects.requireNonNull(KitsGUI.getGUI(player)).removeKit(this);
    }

    Set<Kit> set = (isPublic ? PUBLIC_KITS : PRIVATE_KITS).get(player);

    set.remove(this);

    if (player != null) {
      player.sendMessage(Lang.getComponent("kit-deleted"));
    }
  }

  public void editKit(Player player) {
    // TODO do this better
    if (this.isPublic()) {
      return;
    }

    Objects.requireNonNull(KitEditor.getEditor(player)).open(player, this);
  }

  public void giveKit(Player player) {
    if (this.exists()) {
      player.getInventory().setContents(this.getItemStacks());

      LAST_USED_KIT.put(player, this);
    } else {
      player.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  public void reload() {
    this.itemStacks = this.getData().getItemStacks();
    this.publicKit = this.getData().isPublic();
    this.displayItem = this.getData().getDisplayItem();
  }
}
