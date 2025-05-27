package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.events.custom.KitSelectEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class KitsGUI implements Listener, InventoryHolder {

  private static final Map<UUID, KitsGUI> KITS_GUIS = new HashMap<>();
  private final Player player;
  private final List<Page> pages = new ArrayList<>();
  private final Inventory choiceInventory;
  private int currentPage = 0;
  private int index;
  private Mode mode = Mode.CHOICE;
  private boolean showNoKitOption;

  private enum Mode { CHOICE, PUBLIC, PRIVATE }

  public KitsGUI(Player player) {
    this.player = player;
    this.showNoKitOption = false;

    this.choiceInventory = Bukkit.createInventory(this, 9, "Choose kits type");
    updateChoiceInventory(false);

    Bukkit.getPluginManager().registerEvents(this, AuroraKits.getInstance());
  }

  private void updateChoiceInventory(boolean includeNoKit) {
    choiceInventory.clear();
    ItemStack publicItem = new ItemStack(Material.CHEST);
    ItemMeta meta = publicItem.getItemMeta();
    meta.displayName(AuroraKits.getInstance().getLang().getComponent("public-kits"));
    publicItem.setItemMeta(meta);
    choiceInventory.setItem(3, publicItem);

    ItemStack privateItem = new ItemStack(Material.ENDER_CHEST);
    meta = privateItem.getItemMeta();
    meta.displayName(AuroraKits.getInstance().getLang().getComponent("private-kits"));
    privateItem.setItemMeta(meta);
    choiceInventory.setItem(5, privateItem);

    if (includeNoKit) {
      ItemStack noKitItem = new ItemStack(Material.BARRIER);
      meta = noKitItem.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("no-kit"));
      noKitItem.setItemMeta(meta);
      choiceInventory.setItem(4, noKitItem);
    }
  }

  public static void reloadAll() {
    for (KitsGUI kitsGui : KITS_GUIS.values()) {
      if (kitsGui.mode != Mode.CHOICE) {
        kitsGui.loadKits();
      }
    }
    AuroraKits.getInstance().getLogger().info("GUIs Reloaded!");
  }

  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }

    if (event.getInventory() == this.choiceInventory) {
      event.setCancelled(true);

      int slot = event.getSlot();
      if (slot == 3) {
        mode = Mode.PUBLIC;
        loadKits();
        currentPage = 0;
        player.openInventory(getCurrentPage());
      } else if (slot == 5) {
        mode = Mode.PRIVATE;
        loadKits();
        currentPage = 0;
        player.openInventory(getCurrentPage());
      } else if (slot == 4 && choiceInventory.getItem(4) != null) {
        new KitSelectEvent(player, null).callEvent();
        player.closeInventory();
      }
    } else if (event.getInventory().getHolder() instanceof Page page && page.getGui() == this) {
      event.setCancelled(true);
      if (event.getSlot() == 45) {
        previousPage();
      } else if (event.getSlot() == 53) {
        nextPage();
      } else if (event.getSlot() == 49) {
        new KitSelectEvent(player, null).callEvent();
        player.closeInventory();
      } else {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && !clickedItem.getType().isAir()) {
          String itemName = PlainTextComponentSerializer.plainText().serialize(clickedItem.displayName());
          String kitName = itemName.substring(1, itemName.length() - 1);
          Kit kit = Kit.getKit(player, kitName);
          if (kit != null) {
            if (event.isRightClick() && !kit.isPublic()) {
              kit.editKit(player);
            } else if (new KitSelectEvent(player, kit).callEvent()) {
              kit.giveKit(player);
              player.closeInventory();
            }
          } else {
            player.sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-invalid-name"));
          }
        }
      }
    } else if (event.getInventory().getHolder() == this && event.getInventory() != this.choiceInventory) {
      event.setCancelled(true);
    }
  }
  public static KitsGUI getGUI(Player player) {
      return KITS_GUIS.computeIfAbsent(player.getUniqueId(), k -> new KitsGUI(player));
  }

  public static void unregister(Player player) {
    KITS_GUIS.remove(player.getUniqueId());
  }

  public static Set<KitsGUI> getGuis() {
    return Set.copyOf(KITS_GUIS.values());
  }

  public Player getPlayer() {
    return player;
  }

  public Inventory getChoiceGUI(boolean showNoKitOption) {
    this.mode = Mode.CHOICE;
    updateChoiceInventory(showNoKitOption);
    return choiceInventory;
  }

  public void addKit(Kit kit) {
    if (mode == Mode.CHOICE) {
      return;
    }
    boolean isPublic = mode == Mode.PUBLIC;
    if (kit.isPublic() == isPublic) {
      Inventory lastPage = pages.get(pages.size() - 1).getInventory();
      if (lastPage.getContents()[44] != null) {
        pages.add(new Page(new Kit[]{kit}, this));
        index++;
      } else {
        lastPage.addItem(kit.getDisplayItem());
      }
    }
  }

  public void nextPage() {
    if (mode == Mode.CHOICE) {
      return;
    }
    if (currentPage < index) {
      currentPage++;
    } else {
      currentPage = 0;
    }
    player.openInventory(getCurrentPage());
  }

  public void previousPage() {
    if (mode == Mode.CHOICE) {
      return;
    }
    if (currentPage > 0) {
      currentPage--;
    } else {
      currentPage = index;
    }
    player.openInventory(getCurrentPage());
  }

  public Inventory getCurrentPage() {
    if (mode == Mode.CHOICE) {
      updateChoiceInventory(showNoKitOption);
      return choiceInventory;
    } else if (pages.isEmpty()) {
      Inventory emptyInv = Bukkit.createInventory(this, 54, "No kits available");
      ItemStack noKitsItem = new ItemStack(Material.BARRIER);
      ItemMeta meta = noKitsItem.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("no-kits-available"));
      noKitsItem.setItemMeta(meta);
      emptyInv.setItem(22, noKitsItem);
      return emptyInv;
    } else {
      return pages.get(currentPage).getInventory();
    }
  }

  private void loadKits() {
    List<Kit> kits;
    if (mode == Mode.PUBLIC) {
      kits = new ArrayList<>(Kit.getPublicKits());
    } else if (mode == Mode.PRIVATE) {
      Set<Kit> allKits = Kit.getKits(player, false);
      kits = new ArrayList<>();
      for (Kit kit : allKits) {
        if (!kit.isPublic()) {
          kits.add(kit);
        }
      }
    } else {
      return;
    }

    kits.sort(Comparator.comparing(Kit::getName));
    int pageSize = 45;
    int totalPages = (int) Math.ceil((double) kits.size() / pageSize);

    pages.clear();
    for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
      int start = pageIndex * pageSize;
      int end = Math.min(start + pageSize, kits.size());
      List<Kit> kitsOnPage = kits.subList(start, end);
      pages.add(new Page(kitsOnPage.toArray(new Kit[0]), this));
    }
    index = totalPages - 1;
  }

  public void reload() {
    if (mode == Mode.CHOICE) {
      return;
    }
    loadKits();
  }

  @Override
  public @NotNull Inventory getInventory() {
    return choiceInventory;
  }

  public static class Page implements InventoryHolder {
    private final Inventory inventory;
    private final KitsGUI gui;

    private Page(Kit[] kits, KitsGUI gui) {
      inventory = Bukkit.createInventory(this, 54, AuroraKits.getInstance().getLang().getComponent("kit-gui"));
      this.gui = gui;
      for (Kit kit : kits) {
        inventory.addItem(kit.getDisplayItem());
      }
      ItemMeta meta;
      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("next-page"));
      nextPage.setItemMeta(meta);
      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("previous-page"));
      previousPage.setItemMeta(meta);
      ItemStack noKit = new ItemStack(Material.BARRIER);
      meta = noKit.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("no-kit"));
      noKit.setItemMeta(meta);
      inventory.setItem(49, noKit);
      inventory.setItem(45, previousPage);
      inventory.setItem(53, nextPage);
    }

    public KitsGUI getGui() {
      return gui;
    }

    @Override
    public @NotNull Inventory getInventory() {
      return inventory;
    }
  }
}