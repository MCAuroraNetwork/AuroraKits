package club.aurorapvp.listeners;

import club.aurorapvp.config.Config;
import club.aurorapvp.config.Lang;
import club.aurorapvp.datahandlers.ItemFrameData;
import club.aurorapvp.datahandlers.KitData;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.ItemFrames;
import club.aurorapvp.modules.Kits;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class Commands implements CommandExecutor {
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
                        ItemFrameData.reload();
                        Config.reload();
                        Lang.reload();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case "kit" -> {
                if (args.length != 0) {
                    Kits.getKit(Bukkit.getPlayer(sender.getName()), args[0]);
                } else {
                    GUI.open(p);
                }
            }
            case "kits" -> GUI.open(p);
            case "createkit" -> {
                if (args.length == 0) {
                    Kits.create(sender, null, String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
                } else {
                    Kits.create(sender, args[0],
                            String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
                }
            }
            case "deletekit" -> KitData.delete(sender, args[0],
                    String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
            case "createpublickit" -> Kits.create(sender, args[0], "public");
            case "deletepublickit" -> KitData.delete(sender, args[0], "public");
            case "createframe" -> ItemFrames.create(sender, args[0]);
            case "deleteframe" -> ItemFrameData.delete(sender, args[0]);
        }
        return true;
    }
}