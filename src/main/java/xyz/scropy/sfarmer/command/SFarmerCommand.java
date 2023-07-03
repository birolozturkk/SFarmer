package xyz.scropy.sfarmer.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.model.AutoSell;
import xyz.scropy.sfarmer.model.Farmer;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.time.Duration;
import java.util.Optional;

@Command(value = "sfarmer", alias = "çiftçi")
public class SFarmerCommand extends BaseCommand {

    private final SFarmerPlugin plugin;

    public SFarmerCommand(SFarmerPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand(value = "reload")
    @Permission(value = "sfarmer.command.reload")
    public void buyCommand(CommandSender commandSender) {
       plugin.loadConfigs();
       commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfiguration().messages.reloaded
               .replace("%prefix%", plugin.getConfiguration().messages.prefix)));
    }

    @SubCommand(value = "giveAutoSell")
    @Permission(value = "sfarmer.command.giveautosell")
    public void giveAutoSell(ConsoleCommandSender commandSender, Player player, int time) {
        Optional<Farmer> farmerOptional =  plugin.getFarmerManager().getFarmer(player);
        if(farmerOptional.isEmpty()) return;
        Optional<AutoSell> autoSellOptional = plugin.getDatabaseManager().getAutoSellRepository().getEntryByFarmer(farmerOptional.get().getId());
        AutoSell autoSell;
        if(autoSellOptional.isPresent()) {
            autoSell = autoSellOptional.get();
            autoSell.setExpiryTime(Duration.ofMinutes(time).plusMillis(autoSell.getExpiryTime()).toMillis());
        } else {
            autoSell = new AutoSell(farmerOptional.get().getId(), player.getUniqueId(), time);
            autoSell.setChanged(true);
            plugin.getDatabaseManager().getAutoSellRepository().addEntry(autoSell);
        }
        StringUtils.sendMessage(player, plugin.getConfiguration().messages.addedAutoSell, Placeholder.builder()
                .apply("%time%", StringUtils.formatDuration(Duration.ofMinutes(time).toMillis())));
        plugin.getDatabaseManager().getAutoSellRepository().save(autoSell);
    }
}
