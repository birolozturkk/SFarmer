package xyz.scropy.sfarmer.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.gui.FarmerGui;
import xyz.scropy.sfarmer.utils.StringUtils;

@Command(value = "farmer", alias = "çiftçi")
public class FarmerCommand extends BaseCommand {

    private final SFarmerPlugin plugin;

    public FarmerCommand(SFarmerPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(value = "sfarmer.command.farmer")
    public void farmerCommand(Player player) {
        plugin.getConfiguration().messages.helpMessage.forEach(message -> StringUtils.sendMessage(player, message));
    }
}
