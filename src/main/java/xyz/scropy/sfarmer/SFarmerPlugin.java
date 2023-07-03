package xyz.scropy.sfarmer;

import com.github.scropytr.serializationapi.Persist;
import dev.sergiferry.playernpc.api.NPCLib;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.scropy.sfarmer.command.*;
import xyz.scropy.sfarmer.config.Configuration;
import xyz.scropy.sfarmer.config.SQL;
import xyz.scropy.sfarmer.gui.FarmerGui;
import xyz.scropy.sfarmer.hook.money.EconomyHook;
import xyz.scropy.sfarmer.hook.money.impl.Vault.VaultEconomyHook;
import xyz.scropy.sfarmer.hook.region.RegionHook;
import xyz.scropy.sfarmer.hook.region.impl.SuperiorSkyBlockHook;
import xyz.scropy.sfarmer.listener.Listener;
import xyz.scropy.sfarmer.managers.DatabaseManager;
import xyz.scropy.sfarmer.managers.FarmerManager;
import xyz.scropy.sfarmer.managers.NPCManager;
import xyz.scropy.sfarmer.utils.StringUtils;

@Getter
public final class SFarmerPlugin extends JavaPlugin {

    private BukkitCommandManager<CommandSender> commandManager;
    private DatabaseManager databaseManager;
    private FarmerManager farmerManager;
    private NPCManager npcManager;

    private Persist persist;

    private Configuration configuration;
    private SQL sql;

    private RegionHook regionHook;
    private EconomyHook economyHook;

    @Getter
    private static SFarmerPlugin instance;
    private BukkitAudiences adventure;

    private BukkitTask saveTask;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        this.adventure = BukkitAudiences.create(this);
        NPCLib.getInstance().registerPlugin(this);
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        persist = new Persist(getDataFolder(), Persist.PersistType.YAML);
        registerHooks();
        loadConfigs();
        saveConfigs();
        setupManagers();
        registerListeners();
        setupCommands();

        saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveData, 0, 20 * 60 * 5);

        getServer().getScheduler().runTaskTimer(this,
                () -> {
            FarmerGui.getViewers().forEach((key, value) -> {
                        value.clearPageItems();
                        FarmerGui.addContent(value, SFarmerPlugin.getInstance().getFarmerManager().getFarmer(key).get(), key);
                        value.update();
                    });
                }, 0, 20);
    }

    private void saveData() {
        databaseManager.getCollectedItemRepository().save();
        databaseManager.getFarmerRepository().save();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new Listener(this), this);
        getServer().getPluginManager().registerEvents(npcManager, this);
    }

    private void registerHooks() {
        regionHook = new SuperiorSkyBlockHook(this);
        PluginManager pluginManager = this.getServer().getPluginManager();

        if (pluginManager.getPlugin("Vault") != null)
            economyHook = new VaultEconomyHook();
        economyHook.init();
    }

    public void loadConfigs() {
        configuration = persist.load(Configuration.class, "config");
        sql = persist.load(SQL.class, "database");
    }

    public void saveConfigs() {
        persist.save(sql, "database");
        persist.save(configuration, "config");
    }

    private void setupCommands() {
        this.commandManager = BukkitCommandManager.create(this);
        commandManager.registerCommand(new BuyFarmerCommand(this));
        commandManager.registerCommand(new FarmerCommand(this));
        commandManager.registerCommand(new SFarmerCommand(this));
        commandManager.registerCommand(new FarmerTeleportCommand(this));
        commandManager.registerCommand(new FarmerMenuCommand(this));

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.format(configuration.messages.invalidArgument, Placeholder.builder().build())));

        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.format(configuration.messages.unknownCommand, Placeholder.builder().build())));

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.format(configuration.messages.notEnoughArguments, Placeholder.builder().build())));

        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.format(configuration.messages.tooManyArguments, Placeholder.builder().build())));

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.format(configuration.messages.noPermission, Placeholder.builder().build())));
    }

    @SneakyThrows
    private void setupManagers() {
        farmerManager = new FarmerManager(this);
        databaseManager = new DatabaseManager();
        databaseManager.init();
        npcManager = new NPCManager(this);
        System.out.println(databaseManager.getFarmerRepository().getEntries());
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        saveTask.cancel();
        saveData();
    }
}
