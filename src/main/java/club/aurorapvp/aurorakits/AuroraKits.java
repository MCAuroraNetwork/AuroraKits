package club.aurorapvp.aurorakits;

import club.aurorapvp.aurorakits.commands.CommandManager;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.EventManager;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AuroraKits extends JavaPlugin {

  private static AuroraKits INSTANCE;
  private boolean luckPermsInstalled = false;
  private boolean auroraCombatInstalled;
  private Config config;
  private Lang lang;

  private MongoClient mongoClient;
  private MongoDatabase mongoDatabase;

  public static AuroraKits getInstance() {
    return INSTANCE;
  }

  public boolean isLuckPermsInstalled() {
    return luckPermsInstalled;
  }

  public boolean isAuroraCombatInstalled() {
    return auroraCombatInstalled;
  }

  public Lang getLang() {
    return lang;
  }

  public @NotNull YamlConfiguration getConfig() {
    return config.getYaml();
  }

  public MongoDatabase getDatabase() {
    return mongoDatabase;
  }

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Initialize configs
    config = new Config();
    lang = new Lang();

    // Setup classes
    CommandManager.init();
    EventManager.init();
    Frame.init();
    Kit.init();

    // Check soft depends
    if ((Bukkit.getPluginManager().getPlugin("LuckPerms") != null)
            && getConfig().getBoolean("optional-plugins.luckperms-compatibility")) {
      luckPermsInstalled = true;
    }

    if (Bukkit.getPluginManager().getPlugin("AuroraCombat") != null) {
      if (this.getConfig().getBoolean("optional-plugins.auroracombat-compatibility")) {
        auroraCombatInstalled = true;
      }
    }

    String connectionString =
            this.getConfig().getString("mongodb.address", "mongodb://localhost:27017");
    String databaseName = this.getConfig().getString("mongodb.database-name", "aurora_combat");

    mongoClient = MongoClients.create(connectionString);
    mongoDatabase = mongoClient.getDatabase(databaseName);

    getLogger().info("Connected to MongoDB database: " + databaseName);

    getLogger().info("AuroraKits enabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onDisable() {
    long startTime = System.currentTimeMillis();

    if (mongoClient != null) {
      mongoClient.close();
      getLogger().info("MongoDB connection closed.");
    }

    getLogger().info("AuroraKits disabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  public void reloadConfig() {
    config.reload();
  }
}
