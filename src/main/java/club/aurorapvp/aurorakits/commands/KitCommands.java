package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.custom.KitGiveEvent;
import club.aurorapvp.aurorakits.modules.kits.Kit;
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
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("kit")
@CommandPermission("aurorakits.command.kit")
public class KitCommands extends BaseCommand {

  @Default
  @CommandCompletion("@kits")
  @Syntax("<name>")
  @Description("Gives the player the specified kit")
  @SuppressWarnings("unused")
  public void onGive(Player player, String name) {
    Kit kit = Kit.getKit(player, name);
    if (kit != null) {
      KitGiveEvent event = new KitGiveEvent(kit, player);

      event.callEvent();

      if (event.isCancelled()) {
        return;
      }

      kit.giveKit(player);
      player.sendMessage(Lang.getComponent("kit-used"));
    } else {
      player.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  @Subcommand("create|save")
  @CommandPermission("aurorakits.command.kit.create")
  @Syntax("<name>")
  @Description("Creates a private kit")
  @SuppressWarnings("unused")
  public void onCreate(Player player, @Optional String name) {
    new Kit(
        Objects.requireNonNullElseGet(
            name, () -> "Kit" + Math.addExact(Kit.getAmount(player, false), 1)),
        player,
        false)
        .create(player.getInventory().getContents());
  }

  @Subcommand("delete|remove")
  @CommandPermission("aurorakits.command.kit.delete")
  @CommandCompletion("@kits")
  @Syntax("<name>")
  @Description("Deletes a private kit")
  @SuppressWarnings("unused")
  public void onDelete(Player player, String name) {
    Kit kit = Kit.getKit(player, name);
    if (kit != null) {
      kit.delete(player, false);
    } else {
      player.sendMessage(Lang.getComponent("kit-invalid-name"));
    }
  }

  @Subcommand("createpublic|savepublic")
  @CommandPermission("aurorakits.command.kit.createpublic")
  @Syntax("<name>")
  @Description("Creates a public kit")
  @SuppressWarnings("unused")
  public void onCreatePublic(Player player, String name) {
    new Kit(name, player, true).create(player.getInventory().getContents());
  }

  @Subcommand("deletepublic|removepublic")
  @CommandPermission("aurorakits.command.kit.delete")
  @CommandCompletion("@kits")
  @Syntax("<name>")
  @Description("Deletes a public kit")
  @SuppressWarnings("unused")
  public void onDeletePublic(Player player, String name) {
    Kit kit = Kit.getKit(player, name);
    if (kit != null) {
      kit.delete(player, true);
    } else {
      player.sendMessage(Lang.getComponent("kit-invalid-name"));
    }
  }

  @Subcommand("edit")
  @CommandPermission("aurorakits.command.kit.edit")
  @CommandCompletion("@kits")
  @Syntax("<name>")
  @Description("Edits a kit")
  @SuppressWarnings("unused")
  public void onEdit(Player player, String name) {
    Kit kit = Kit.getKit(player, name);
    if (kit != null) {
      kit.editKit(player);
    } else {
      player.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  @Subcommand("copy")
  @Syntax("<name>")
  @Description("Copies a kit")
  @SuppressWarnings("unused")
  public void onCopy(Player player, String name, String newName) {
    Kit kit = Kit.getKit(player, name);
    if (kit != null) {
      new Kit(newName, player, false).create(kit.getItemStacks());
    } else {
      player.sendMessage(Lang.getComponent("kit-not-found"));
    }
  }

  // TODO
  @CatchUnknown
  @Subcommand("help")
  @SuppressWarnings("unused")
  public void onUnknown(CommandSender sender) {
    sender.sendMessage(Lang.getComponent("unknown-kit-command"));
  }
}
