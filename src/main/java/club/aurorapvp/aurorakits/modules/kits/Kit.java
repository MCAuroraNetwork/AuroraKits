package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.data.KitDataHandler;
import club.aurorapvp.aurorakits.events.custom.KitCreateEvent;
import club.aurorapvp.aurorakits.events.custom.KitDeleteEvent;
import club.aurorapvp.aurorakits.events.custom.KitGiveEvent;
import club.aurorapvp.aurorakits.events.custom.KitRefillEvent;
import club.aurorapvp.aurorakits.util.ItemStackUtil;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {

  private static final Map<UUID, Set<Kit>> PRIVATE_KITS = new HashMap<>();
  private static final Map<UUID, Set<Kit>> PUBLIC_KITS = new HashMap<>();
  private static final HashMap<UUID, Kit> LAST_USED_KIT = new HashMap<>();
  private final KitDataHandler data;
  private final boolean isPublic;
  private final String name;
  private final String path;
  private final OfflinePlayer owner;
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

    Set<Kit> set =
        (isPublic ? PUBLIC_KITS : PRIVATE_KITS).getOrDefault(owner.getUniqueId(), new HashSet<>());

    set.add(this);

    (isPublic ? PUBLIC_KITS : PRIVATE_KITS).put(owner.getUniqueId(), set);

    if (this.exists()) {
      this.reload();
    }
  }

  public static void init() {
    File dir = new File(AuroraKits.getInstance().getDataFolder(), "/kits/public/");

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

    AuroraKits.getInstance().getLogger().info("All kits loaded!");
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
    return PUBLIC_KITS.get(player.getUniqueId());
  }

  public static Set<Kit> getPrivateKits(Player player) {
    return PRIVATE_KITS.get(player.getUniqueId());
  }

  public static Set<Kit> getPrivateKits(UUID uuid) {
    return PRIVATE_KITS.get(uuid);
  }

  public static void setLastUsedKit(Player player, Kit kit) {
    LAST_USED_KIT.put(player.getUniqueId(), kit);
  }

  public static int getAmount(Player player, boolean includePublic) {
    return getKits(player, includePublic).size();
  }

  @SuppressWarnings("unused")
  public static void deleteKit(String name, boolean isPublic) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        kit.delete(null);
      }
    }
  }

  public static void deleteKit(Player player, String name, boolean isPublic) {
    Kit kit = Kit.getKit(player, name);
    if (kit != null && kit.isPublic() == isPublic) {
      kit.delete(player);
    } else {
      player.sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-invalid-name"));
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

    AuroraKits.getInstance().getLogger().info("All kits reloaded!");
  }

  public static Set<Kit> getKits(Player player, boolean includePublic) {
    Set<Kit> playerKits = new HashSet<>();

    if (PRIVATE_KITS.containsKey(player.getUniqueId())) {
      playerKits.addAll(PRIVATE_KITS.get(player.getUniqueId()));
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
    if (PRIVATE_KITS.containsKey(player.getUniqueId())) {
      for (Kit kit : PRIVATE_KITS.get(player.getUniqueId())) {
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
    File dir = new File(AuroraKits.getInstance().getDataFolder(), "/kits/" + player.getUniqueId());

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
  }

  public static void unregister(Player player) {
    for (Kit kit : getKits(player, true)) {
      if (!kit.isPublic()) {
        Kit.getKits().remove(kit);
      }
    }
  }

  public static Kit getLastUsedKit(Player player) {
    if (LAST_USED_KIT.containsKey(player.getUniqueId())) {
      return LAST_USED_KIT.get(player.getUniqueId());
    }

    return Kit.getKit(
        AuroraKits.getInstance().getConfig().getString("kits.give-last-kit.default-kit"));
  }

  public static void refillKit(Player player) {
    if (!new KitRefillEvent(player).callEvent()) {
      return;
    }

    player.getInventory().setStorageContents(Kit.getLastUsedKit(player).getContents().clone());
    player.getInventory().setArmorContents(Kit.getLastUsedKit(player).getArmor().clone());
    player.getInventory().setItemInOffHand(Kit.getLastUsedKit(player).getOffhand().clone());

    player.sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-refilled"));
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

  public ItemStack[] getArmor() {
    return armor;
  }

  public ItemStack getOffhand() {
    return offhand;
  }

  public boolean isPublic() {
    return isPublic;
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

  public boolean create() {
    if (!new KitCreateEvent(owner.getPlayer(), this).callEvent()) {
      return false;
    }

    if (this.exists()) {
      Objects.requireNonNull(owner.getPlayer())
          .sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-already-exists"));
      return false;
    }

    if (Objects.requireNonNull(owner.getPlayer()).getInventory().getItemInMainHand().getItemMeta()
        == null) {
      Objects.requireNonNull(owner.getPlayer())
          .sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-invalid-item"));
      return false;
    }

    int max = Integer.MAX_VALUE;

    if (AuroraKits.getInstance().isLuckPermsInstalled()) {
      LuckPerms luckPerms =
          Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(LuckPerms.class))
              .getProvider();

      User user = luckPerms.getPlayerAdapter(Player.class).getUser(owner.getPlayer());

      String meta = user.getCachedData().getMetaData().getMetaValue("max-kits");
      if (meta != null) {
        max = Integer.parseInt(meta);
      }
    }

    int kitAmount =
        Kit.getAmount(Objects.requireNonNull(owner.getPlayer()), false)
            + Kit.getPublicKits(owner.getPlayer()).size();

    if (kitAmount >= max) {
      owner
          .getPlayer()
          .sendMessage(AuroraKits.getInstance().getLang().getComponent("too-many-kits"));
      return false;
    }

    displayItem =
        ItemStackUtil.createDisplayItem(
            owner.getPlayer().getInventory().getItemInMainHand().getType(),
            name,
            owner.getName(),
            isPublic);

    this.contents = owner.getPlayer().getInventory().getStorageContents().clone();
    this.armor = owner.getPlayer().getInventory().getArmorContents().clone();
    this.offhand = owner.getPlayer().getInventory().getItemInOffHand().clone();

    data.create();

    if (isPublic) {
      for (KitsGUI kitsGui : KitsGUI.getGuis()) {
        kitsGui.addKit(this);
      }
    } else {
      KitEditor.getEditor(owner.getPlayer()).addKit(this);
      KitsGUI.getGUI(owner.getPlayer()).addKit(this);
    }

    Objects.requireNonNull(owner.getPlayer())
        .sendMessage(
            AuroraKits.getInstance()
                .getLang()
                .getComponentCommand("kit-created", "Click to Open Kit", "/kit " + this.getName()));

    return true;
  }

  public void create(ItemStack[] contents, ItemStack[] armor, ItemStack offhand) {
    if (this.exists()) {
      Objects.requireNonNull(owner.getPlayer())
          .sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-already-exists"));
      return;
    }

    if (Objects.requireNonNull(owner.getPlayer()).getInventory().getItemInMainHand().getItemMeta()
        == null) {
      Objects.requireNonNull(owner.getPlayer())
          .sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-invalid-item"));
      return;
    }

    displayItem =
        ItemStackUtil.createDisplayItem(
            owner.getPlayer().getInventory().getItemInMainHand().getType(),
            name,
            owner.getName(),
            isPublic);

    this.contents = contents;
    this.armor = armor;
    this.offhand = offhand;

    data.create();

    if (isPublic) {
      for (KitsGUI kitsGui : KitsGUI.getGuis()) {
        kitsGui.addKit(this);
      }
    } else {
      KitEditor.getEditor(owner.getPlayer()).addKit(this);
      KitsGUI.getGUI(owner.getPlayer()).addKit(this);
    }

    Objects.requireNonNull(owner.getPlayer())
        .sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-created"));
  }

  public void edit(Player player, ItemStack[] contents, ItemStack[] armor, ItemStack offhand) {
    this.contents = contents;
    this.armor = armor;
    this.offhand = offhand;

    data.create();

    player.sendMessage(AuroraKits.getInstance().getLang().getComponentCommand("kit-updated", "Click to Open Kit", "/kit " + this.getName()));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void delete(Player player) {
    if (!new KitDeleteEvent(player, this).callEvent()) {
      return;
    }

    Set<Kit> set;
    if (this.isPublic()) {
      set = (Kit.getPublicKits());
    } else {
      set = (Kit.getPrivateKits(this.getOwner().getUniqueId()));
    }

    set.remove(this);

    if (this.isPublic()) {
      KitsGUI.reloadAll();
    } else if (this.getOwner().isOnline()) {
      Objects.requireNonNull(KitsGUI.getGUI(Objects.requireNonNull(this.getOwner().getPlayer())))
          .reload();
    }

    this.getData().getFile().delete();
  }

  public void giveKit(Player player) {
    if (this.exists()) {
      if (!new KitGiveEvent(player, this).callEvent()) {
        return;
      }

      player.getInventory().setStorageContents(this.getContents().clone());
      player.getInventory().setArmorContents(this.getArmor().clone());
      player.getInventory().setItemInOffHand(this.getOffhand().clone());

      Kit.setLastUsedKit(player, this);

      player.sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-used"));
    } else {
      player.sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-not-found"));
    }
  }

  public void editKit(Player player) {
    if (!(this.getOwner() == player)) {
      player.sendMessage(AuroraKits.getInstance().getLang().getComponent("not-your-kit"));
      return;
    }

    KitEditor.getEditor(player).open(player, this);
  }

  public void reload() {
    this.contents = this.getData().getContents();
    this.armor = this.getData().getArmor();
    this.offhand = this.getData().getOffhand();
    this.displayItem = this.getData().getDisplayItem();
  }
}
