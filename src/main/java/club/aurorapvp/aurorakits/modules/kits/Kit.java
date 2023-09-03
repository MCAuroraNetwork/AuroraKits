package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.data.KitDataHandler;
import club.aurorapvp.aurorakits.events.custom.KitCreateEvent;
import club.aurorapvp.aurorakits.events.custom.KitDeleteEvent;
import club.aurorapvp.aurorakits.events.custom.KitGiveEvent;
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
  private ItemStack[] contents;
  private ItemStack[] armor;
  private ItemStack offhand;
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

  public static Set<Kit> getPublicKits() {
    Set<Kit> set = new HashSet<>();

    for (Set<Kit> kits : PUBLIC_KITS.values()) {
      set.addAll(kits);
    }

    return set;
  }

  @SuppressWarnings("unused")
  public static Set<Kit> getPrivateKits() {
    Set<Kit> set = new HashSet<>();

    for (Set<Kit> kits : PRIVATE_KITS.values()) {
      set.addAll(kits);
    }

    return set;
  }

  @SuppressWarnings("unused")
  public static Set<Kit> getPublicKits(Player player) {
    return PUBLIC_KITS.get(player);
  }

  public static Set<Kit> getPrivateKits(Player player) {
    return PRIVATE_KITS.get(player);
  }

  public static void setLastUsedKit(Player player, Kit kit) {
    LAST_USED_KIT.put(player, kit);
  }

  public static int getAmount(Player player, boolean includePublic) {
    return getKits(player, includePublic).size();
  }

  @SuppressWarnings("unused")
  public static Kit createKit(String name, Player player) {
    Kit kit = new Kit(name, player, true);

    new KitCreateEvent(player, kit);

    return kit;
  }

  @SuppressWarnings("unused")
  public static void deleteKit(String name, boolean isPublic) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        new KitDeleteEvent(null, kit).callEvent();
      }
    }
  }

  public static void deleteKit(Player player, String name, boolean isPublic) {
    Kit kit = Kit.getKit(player, name);
    if (kit != null && kit.isPublic() == isPublic) {
      new KitDeleteEvent(player, kit).callEvent();
    } else {
      player.sendMessage(Lang.getComponent("kit-invalid-name"));
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

    new KitEditor(player);
    new KitsGUI(player);
  }

  public static void unregister(Player player) {
    for (Kit kit : getKits(player, true)) {
      if (!kit.isPublic()) {
        Kit.getKits().remove(kit);
      }
    }
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

  public ItemStack[] getContents() {
    return contents;
  }

  public ItemStack[] getArmor() { return armor; }

  public ItemStack getOffhand() { return offhand; }

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

  public void create() {
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

    this.contents = owner.getPlayer().getInventory().getStorageContents();
    this.armor = owner.getPlayer().getInventory().getArmorContents();
    this.offhand = owner.getPlayer().getInventory().getItemInOffHand();

    data.create();

    if (isPublic) {
      for (KitsGUI kitsGui : KitsGUI.getGuis()) {
        kitsGui.addKit(this);
      }
    } else {
      KitEditor.getEditor(owner.getPlayer()).addKit(this);
      KitsGUI.getGUI(owner.getPlayer()).addKit(this);
    }

    Objects.requireNonNull(owner.getPlayer()).sendMessage(Lang.getComponent("kit-created"));
  }

  public void edit(Player player, ItemStack[] contents, ItemStack[] armor, ItemStack offhand) {
    this.contents = contents;
    this.armor = armor;
    this.offhand = offhand;

    data.create();

    player.sendMessage(Lang.getComponent("kit-updated"));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void delete() {
    data.getFile().delete();
  }

  public void giveKit(Player player) {
    if (this.exists()) {
      new KitGiveEvent(player, this).callEvent();
    } else {
      player.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  public void editKit(Player player) {
    if (!(this.getOwner() == player)) {
      player.sendMessage(Lang.getComponent("not-your-kit"));
      return;
    }

    KitEditor.getEditor(player).open(player, this);
  }

  public void reload() {
    this.contents = this.getData().getContents();
    this.armor = this.getData().getArmor();
    this.offhand = this.getData().getOffhand();
    this.publicKit = this.getData().isPublic();
    this.displayItem = this.getData().getDisplayItem();
  }
}
