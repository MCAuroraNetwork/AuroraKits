package club.aurorapvp.listeners;

import club.aurorapvp.config.ConfigHandler;
import club.aurorapvp.config.LangHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import club.aurorapvp.datahandlers.KitDataHandler;
import club.aurorapvp.modules.GUIModule;
import club.aurorapvp.modules.ItemFramesModule;
import club.aurorapvp.modules.KitModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class CommandListener implements CommandExecutor {
    private Player p;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {

        if (sender instanceof Player) {
            p = (Player) sender;
        }

        switch (command.getName()) {
            case "aurorakits" -> {
                if (Objects.equals(args[0], "reload")) {
                    try {
                        ItemFrameDataHandler.reload();
                        ConfigHandler.reload();
                        LangHandler.reload();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case "kit" -> {
                if (args.length != 0) {
                    KitModule.getKit(Bukkit.getPlayer(sender.getName()), args[0]);
                } else {
                    GUIModule.open(p);
                }
            }
            case "kits" -> GUIModule.open(p);
            case "createkit" -> {
                if (args.length == 0) {
                    KitModule.create(sender, null, String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
                } else {
                    KitModule.create(sender, args[0],
                            String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
                }
            }
            case "deletekit" -> KitDataHandler.delete(sender, args[0],
                    String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
            case "createpublickit" -> KitModule.create(sender, args[0], "public");
            case "deletepublickit" -> KitDataHandler.delete(sender, args[0], "public");
            case "createframe" -> ItemFramesModule.create(sender, args[0]);
            case "deleteframe" -> ItemFrameDataHandler.delete(sender, args[0]);
        }
        return true;
    }
}