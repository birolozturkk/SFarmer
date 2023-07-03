package xyz.scropy.sfarmer.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.gui.ShopGui;
import xyz.scropy.sfarmer.model.Farmer;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.util.Optional;

@Command(value = "farmer", alias = "çiftçi")
public class BuyFarmerCommand extends BaseCommand {

    private final SFarmerPlugin plugin;

    public BuyFarmerCommand(SFarmerPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand(value = "satınal", alias = "buy")
    @Permission(value = "sfarmer.command.buy")
    public void buyCommand(Player player) {
        ShopGui.open(player);
    }
}
