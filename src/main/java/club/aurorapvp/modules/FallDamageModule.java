package club.aurorapvp.modules;

import static club.aurorapvp.config.ConfigHandler.getConfigFile;

import java.util.ArrayList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallDamageModule {
  public static ArrayList<Entity> falldamage = new ArrayList<>();

  public static void cancelFallDamage(EntityDamageEvent event) {
    Entity p = event.getEntity();
    if (event.getEntity() instanceof Player &&
        event.getCause() == EntityDamageEvent.DamageCause.FALL && falldamage.contains(p) &&
        !getConfigFile().getBoolean("doFirstFallDamage") && !event.isCancelled()) {
      event.setCancelled(true);
      falldamage.remove(p);
    }
  }

  public static void checkFallDamage(Player p) {
    if (!falldamage.contains(p) && !getConfigFile().getBoolean("doFirstFallDamage")) {
      falldamage.add(p);
    }
  }
}