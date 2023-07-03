package xyz.scropy.sfarmer.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.gui.FarmerGui;
import xyz.scropy.sfarmer.model.Farmer;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.util.Optional;

@Command(value = "farmer", alias = "çiftçi")
public class FarmerTeleportCommand extends BaseCommand {

    private final SFarmerPlugin plugin;

    public FarmerTeleportCommand(SFarmerPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand(value = "getir", alias = "teleport")
    @Permission(value = "sfarmer.command.teleport")
    public void teleportCommand(Player player) {
        Optional<Farmer> farmerOptional = plugin.getFarmerManager().getFarmer(player);
        if (farmerOptional.isEmpty()) {
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.notHaveAFarmer, Placeholder.builder());
            return;
        }
        if (FarmerGui.getCooldownProvider().isOnCooldown(player.getUniqueId())) {
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.canNotMove, Placeholder.builder());
            return;
        }

        if(!plugin.getRegionHook().isInside(player, player.getLocation())) {
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.thisIslandDoesNotBelongToYou, Placeholder.builder());
            return;
        }
        plugin.getAdventure().player(player)
                .playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.MASTER, 1f, 1f));
        FarmerGui.getCooldownProvider().applyCooldown(player.getUniqueId());
        Farmer farmer = farmerOptional.get();
        farmer.setLocation(player.getLocation());
        plugin.getDatabaseManager().getFarmerRepository().save(farmer);
        plugin.getNpcManager().getNpcMap().get(farmerOptional.get()).teleport(player.getLocation());
    }
}
