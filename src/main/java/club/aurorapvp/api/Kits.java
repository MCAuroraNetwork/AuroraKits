package club.aurorapvp.api;

import club.aurorapvp.modules.Kit;
import java.util.Objects;
import org.bukkit.entity.Player;

public class Kits {

  public static Kit createKit(String name, Player p) {
    Kit kit;

    kit = new Kit(name, "public");
    kit.create(p, true);

    return kit;
  }

  public static void deleteKit(String name) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        kit.delete(null, true);
      }
    }
  }

  public static Kit getKit(String name) {
    for (Kit kit : getKits()) {
      if (Objects.equals(kit.getName(), name)) {
        return kit;
      }
    }
    return null;
  }

  public static Kit getLastKit(Player p) {
    return Kit.getLastUsedKit(p);
  }

  public static Kit[] getKits() {
    return Kit.getKits(null, true);
  }

  public static void giveKit(Kit kit, Player p) {
    kit.giveKit(p);
  }
}
