package club.aurorapvp.aurorakits.events.custom;

import club.aurorapvp.aurorakits.modules.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitSelectEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private final Player player;
  private final Kit kit;
  private boolean cancelled;

  public KitSelectEvent(Player player, Kit kit) {
    this.player = player;
    this.kit = kit;
  }

  public Player getPlayer() {
    return player;
  }

  public Kit getKit() {
    return kit;
  }

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }
}

