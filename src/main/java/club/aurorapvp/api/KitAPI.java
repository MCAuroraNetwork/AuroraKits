package club.aurorapvp.api;

import club.aurorapvp.modules.Kit;
import java.util.Objects;
import org.bukkit.entity.Player;

public class KitAPI {
  /**
   * Creates a kit object and saves it
   * @param name The name of the kit
   * @param p The player who is creating the kit, cannot be null
   * @param isPublic If the kit is public or hidden
   * @return Returns the created kit
   */
  public static Kit createKit(String name, Player p, boolean isPublic) {
    Kit kit;

    if (isPublic) {
      kit = new Kit(name, "public");
      kit.create(p, 1);
    } else {
      kit = new Kit(name, "hidden");
      kit.create(p, 2);
    }
    return kit;
  }

  /**
   * Gets a kit object, then deletes it
   * @param name The name of the kit to delete
   * @param isPublic If the kit is public or hidden
   */
  public static void deleteKit(String name, boolean isPublic) {
    for (Kit kit : getKits(isPublic)) {
      if (Objects.equals(kit.getName(), name)) {
        if (isPublic) {
          kit.delete(null, 1);
        } else {
          kit.delete(null, 2);
        }
        return;
      }
    }
  }

  /**
   * Gets a kit object from all currently saved and loaded kits
   * @param name The name of the kit to get
   * @param isPublic If the kit is public or hidden
   * @return Returns the kit found, or null if not found
   */
  public static Kit getKit(String name, boolean isPublic) {
    for (Kit kit : getKits(isPublic)) {
      if (Objects.equals(kit.getName(), name)) {
        return kit;
      }
    }
    return null;
  }

  /**
   * Gets an array of all public or hidden kits
   * @param isPublic If the kits are public or hidden
   * @return Returns an array of kits that are public or hidden
   */
  public static Kit[] getKits(boolean isPublic) {
    if (isPublic) {
      return Kit.getKits(null);
    } else {
      return Kit.getHidden();
    }
  }

  /**
   * Gives the specified player a kit
   * @param kit The kit to give the player
   * @param p The player that will receive the kit
   */
  public static void giveKit(Kit kit, Player p) {
    kit.giveKit(p);
  }
}
