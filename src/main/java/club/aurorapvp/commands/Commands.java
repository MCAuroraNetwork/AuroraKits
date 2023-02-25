package club.aurorapvp.commands;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.commands.frames.CreateFrame;
import club.aurorapvp.commands.frames.DeleteFrame;
import club.aurorapvp.commands.kits.CreateKit;
import club.aurorapvp.commands.kits.CreatePublicKit;
import club.aurorapvp.commands.kits.DeleteKit;
import club.aurorapvp.commands.kits.DeletePublicKit;
import club.aurorapvp.commands.kits.KitsGUI;
import club.aurorapvp.commands.plugin.Reload;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands {
  public Commands() {
    AuroraKits.INSTANCE.getCommand("createframe").setExecutor(new CreateFrame());
    AuroraKits.INSTANCE.getCommand("deleteframe").setExecutor(new DeleteFrame());
    AuroraKits.INSTANCE.getCommand("createkit").setExecutor(new CreateKit());
    AuroraKits.INSTANCE.getCommand("createpublickit").setExecutor(new CreatePublicKit());
    AuroraKits.INSTANCE.getCommand("deletekit").setExecutor(new DeleteKit());
    AuroraKits.INSTANCE.getCommand("deletepublickit").setExecutor(new DeletePublicKit());
    AuroraKits.INSTANCE.getCommand("kits").setExecutor(new KitsGUI());
    AuroraKits.INSTANCE.getCommand("aurorakits").setExecutor(new Reload());
  }
}