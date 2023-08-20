package club.aurorapvp.aurorakits.util;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtil {

  public static ItemStack createDisplayItem(
      final Material material, final String name, String creator, boolean isPublic) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    meta.displayName(
        MiniMessage.miniMessage()
            .deserialize("<gradient:#FFAA00:#FF55FF><bold>" + name)
            .decoration(TextDecoration.ITALIC, false));

    if (isPublic) {
      meta.lore(
          List.of(
              MiniMessage.miniMessage()
                  .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator),
              MiniMessage.miniMessage().deserialize("This is a Public Kit")));
    } else {
      meta.lore(
          List.of(
              MiniMessage.miniMessage()
                  .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator),
              Component.text("This is a Private Kit"),
              Component.text("Right click to edit")));
    }

    item.setItemMeta(meta);

    return item;
  }
}
