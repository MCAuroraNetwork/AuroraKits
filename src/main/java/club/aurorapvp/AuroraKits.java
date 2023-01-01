package club.aurorapvp;

import club.aurorapvp.config.ConfigHandler;
import club.aurorapvp.config.LangHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import club.aurorapvp.listeners.CommandListener;
import club.aurorapvp.listeners.EventListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class AuroraKits extends JavaPlugin {
    public static Plugin PLUGIN;
    public static File DATA_FOLDER;
    public static PlainTextComponentSerializer SERIALIZE_COMPONENT;
    public static MiniMessage DESERIALIZE_COMPONENT;

    @Override
    public void onEnable() {

        // Register important variables
        PLUGIN = this;
        DATA_FOLDER = this.getDataFolder();
        SERIALIZE_COMPONENT = PlainTextComponentSerializer.plainText();
        DESERIALIZE_COMPONENT = MiniMessage.miniMessage();

        // Setup configs
        try {
            LangHandler.reload();
            ConfigHandler.reload();
            ItemFrameDataHandler.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Generate default values
        try {
            LangHandler.generateDefaults();
            ConfigHandler.generateDefaults();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        List<Command> commandList = PluginCommandYamlParser.parse(PLUGIN);
        for (Command command : commandList) {
            getCommand(command.getName()).setExecutor(new CommandListener());
        }

        getLogger().info("Aurora Kits Loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Aurora Kits Unloaded");
    }
}

