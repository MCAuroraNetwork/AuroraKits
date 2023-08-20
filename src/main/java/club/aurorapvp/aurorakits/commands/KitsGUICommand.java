package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;

@CommandAlias("kits")
@CommandPermission("aurorakits.command.kits")
public class KitsGUICommand extends BaseCommand {
  @Default
  @Description("Opens the kit GUI")
  @SuppressWarnings("unused")
  public void onGUIOpen(Player player) {
    KitsGUI.open(player);
  }
}
