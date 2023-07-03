package xyz.scropy.sfarmer.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.gui.FarmerGui;
import xyz.scropy.sfarmer.utils.StringUtils;

@Command(value = "farmer", alias = "çiftçi")
public class FarmerMenuCommand extends BaseCommand {

    private final SFarmerPlugin plugin;

    public FarmerMenuCommand(SFarmerPlugin plugin) {
        this.plugin = plugin;
    }

    @Permission(value = "sfarmer.command.menu")
    @SubCommand(value = "menü", alias = "menu")
    public void menuCommand(Player player) {
        if (plugin.getFarmerManager().getFarmer(player).isEmpty()) {
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.notHaveAFarmer, Placeholder.builder());
            return;
        }
        FarmerGui.open(player);
    }
}
