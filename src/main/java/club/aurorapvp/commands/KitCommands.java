package club.aurorapvp.commands;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("kit|kits")
public class KitCommands extends BaseCommand {

  @Default
  @Description("Opens the kit GUI")
  public void onGUIOpen(Player p) {
    GUI.open(p);
  }

  @Default
  @Syntax("[name]")
  @CommandCompletion("@kits")
  @Description("Gives the player the specified kit")
  public void onGive(Player p, String name) {
    Kit kit = Kit.getKit(p, name);
    if (kit != null) {
      kit.giveKit(p);
    } else {
      p.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  @Subcommand("create")
  @Syntax("[name]")
  @Description("Creates a private kit")
  public void onCreate(Player p, @Optional String name) {
    if (name != null) {
      new Kit(name, p.getUniqueId().toString()).create(p, false);
    } else {
      new Kit("Kit" + Math.addExact(Kit.getAmount(p, false), 1),
          String.valueOf(p.getUniqueId())).create(p, false);
    }
  }

  @Subcommand("createpublic")
  @CommandPermission("aurorakits.admin")
  @Syntax("[name]")
  @Description("Creates a public kit")
  public void onCreatePublic(Player p, String name) {
    new Kit(name, "public").create(p, true);
  }

  @Subcommand("delete")
  @CommandCompletion("@kits")
  @Syntax("[name]")
  @Description("Deletes a private kit")
  public void onDelete(Player p, String name) {
    Kit kit = Kit.getKit(p, name);
    if (kit != null) {
      kit.delete(p, false);
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-name"));
    }
  }

  @Subcommand("deletepublic")
  @CommandCompletion("@kits")
  @Syntax("[name]")
  @Description("Deletes a public kit")
  public void onDeletePublic(Player p, String name) {
    Kit kit = Kit.getKit(p, name);
    if (kit != null) {
      kit.delete(p, true);
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-name"));
    }
  }

  @CatchUnknown
  public void onUnknown(CommandSender sender) {
    sender.sendMessage(Lang.getComponent(""));
  }
}