package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public class CommandManager {

  public static PaperCommandManager MANAGER = new PaperCommandManager(AuroraKits.getInstance());

  public static void init() {
    MANAGER.registerCommand(new PluginCommands());
    MANAGER.registerCommand(new KitCommands());
    MANAGER.registerCommand(new FrameCommands());
    MANAGER.registerCommand(new KitsGUICommand());

    CommandCompletions<BukkitCommandCompletionContext> commandCompletions =
        MANAGER.getCommandCompletions();

    commandCompletions.registerCompletion(
        "kits",
        c -> {
          Player player = c.getPlayer();
          List<String> kitNames = new ArrayList<>();

          for (Kit kit : Kit.getKits(player, true)) {
            kitNames.add(kit.getName());
          }

          return kitNames;
        });

    commandCompletions.registerCompletion(
        "frames",
        c -> {
          List<String> frameNames = new ArrayList<>();

          for (Frame frame : Frame.getFrames()) {
            frameNames.add(PlainTextComponentSerializer.plainText().serialize(frame.getName()));
          }

          return frameNames;
        });
  }
}
