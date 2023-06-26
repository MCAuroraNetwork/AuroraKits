package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class Commands {
  public static PaperCommandManager MANAGER = new PaperCommandManager(AuroraKits.INSTANCE);

  public static void init() {
    MANAGER.registerCommand(new PluginCommands());
    MANAGER.registerCommand(new KitCommands());
    MANAGER.registerCommand(new FrameCommands());

    CommandCompletions<BukkitCommandCompletionContext> commandCompletions =
        MANAGER.getCommandCompletions();

    commandCompletions.registerCompletion("kits", c -> {
      Player p = c.getPlayer();
      List<String> kitNames = new ArrayList<>();

      for (Kit kit : Kit.getKits(p, true)) {
        kitNames.add(kit.getName());
      }

      return kitNames;
    });

    commandCompletions.registerCompletion("frames", c -> {
      List<String> kitNames = new ArrayList<>();

      for (Frame frame : Frame.getFrames()) {
        kitNames.add(frame.getName());
      }

      return kitNames;
    });
  }
}