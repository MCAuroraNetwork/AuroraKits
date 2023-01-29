package club.aurorapvp.api;

import club.aurorapvp.modules.Kit;
import java.util.Objects;
import org.bukkit.entity.Player;

public class KitAPI {
  /**
   * Creates a kit object and saves it
   * @param name The name of the kit
   * @param p The player who is creating the kit, cannot be null
   * @return Returns the created kit
   */
  public static Kit createKit(String name, Player p) {
    Kit kit;

    kit = new Kit(name, "public");
    kit.create(p, true);

    return kit;
  }

  /**
   * Gets a kit object, then deletes it
   * @param name The name of the kit to delete
   */
  public static void deleteKit(String name) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        kit.delete(null, true);
      }
    }
  }

  /**
   * Gets a kit object from all currently saved and loaded kits
   * @param name The name of the kit to get
   * @return Returns the kit found, or null if not found
   */
  public static Kit getKit(String name) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        return kit;
      }
    }
    return null;
  }

  /**
   * Gets the last used kit by a player
   * @param p the player who to get the last kit from
   * @return Returns the last kit, the default kit to give, or null if there is no last kit or default kit
   */
  public static Kit getLastKit(Player p) {
    return Kit.getLastUsedKit(p);
  }

  /**
   * Gets an array of all public or hidden kits
   * @return Returns an array of kits that are public or hidden
   */
  public static Kit[] getKits() {
    return Kit.getKits(null);
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
