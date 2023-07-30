package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import club.aurorapvp.aurorakits.modules.kits.KitsGui;
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

import java.util.Objects;

@CommandAlias("kit|kits")
public class KitCommands extends BaseCommand {

  @Default
  @Description("Opens the kit GUI")
  @SuppressWarnings("unused")
  public void onGUIOpen(Player p) {
    KitsGui.open(p);
  }

  @Default
  @Syntax("[name]")
  @CommandCompletion("@kits")
  @Description("Gives the player the specified kit")
  @SuppressWarnings("unused")
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
  @SuppressWarnings("unused")
  public void onCreate(Player p, @Optional String name) {
    new Kit(
            Objects.requireNonNullElseGet(
                name, () -> "Kit" + Math.addExact(Kit.getAmount(p, false), 1)),
            p,
            false)
        .create(p.getInventory());
  }

  @Subcommand("createpublic")
  @CommandPermission("aurorakits.admin")
  @Syntax("[name]")
  @Description("Creates a public kit")
  @SuppressWarnings("unused")
  public void onCreatePublic(Player p, String name) {
    new Kit(name, p, true).create(p.getInventory());
  }

  @Subcommand("delete")
  @CommandCompletion("@kits")
  @Syntax("[name]")
  @Description("Deletes a private kit")
  @SuppressWarnings("unused")
  public void onDelete(Player p, String name) {
    Kit kit = Kit.getKit(p, name);
    if (kit != null) {
      kit.delete(p, false);
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-name"));
    }
  }

  @Subcommand("deletepublic")
  @CommandPermission("aurorakits.admin")
  @CommandCompletion("@kits")
  @Syntax("[name]")
  @Description("Deletes a public kit")
  @SuppressWarnings("unused")
  public void onDeletePublic(Player p, String name) {
    Kit kit = Kit.getKit(p, name);
    if (kit != null) {
      kit.delete(p, true);
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-name"));
    }
  }

  @Subcommand("edit")
  @Syntax("[name]")
  @Description("Edits a kit")
  @SuppressWarnings("unused")
  public void onEdit(Player p, String name) {
    Kit kit = Kit.getKit(p, name);
    if (kit != null) {
      kit.editKit(p);
    } else {
      p.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  @CatchUnknown
  @SuppressWarnings("unused")
  public void onUnknown(CommandSender sender) {
    sender.sendMessage(Lang.getComponent("unknown-kit-command"));
  }
}
