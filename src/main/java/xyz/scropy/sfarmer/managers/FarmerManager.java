package xyz.scropy.sfarmer.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.model.Farmer;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class FarmerManager {

    private final SFarmerPlugin plugin;

    public FarmerManager(SFarmerPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getNpcManager().load(), 60);
    }

    public void buyFarmer(Player player) {
        if (!plugin.getRegionHook().hasRegion(player)) {
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.notHaveAnIsland);
            return;
        }
        if(!plugin.getRegionHook().isInside(player, player.getLocation())) {
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.thisIslandDoesNotBelongToYou);
            return;
        }
        if (getFarmer(player).isPresent()) {
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.alreadyHaveFarmer);
            return;
        }

        plugin.getEconomyHook().withdraw(player, plugin.getConfiguration().farmerPrice);
        StringUtils.sendMessage(player, plugin.getConfiguration().messages.successfullyBought);
        Farmer farmer = new Farmer(player.getUniqueId(), player.getLocation());
        plugin.getNpcManager().createNPC(farmer);
        plugin.getDatabaseManager().getFarmerRepository().addEntry(farmer);
        plugin.getDatabaseManager().getFarmerRepository().save(farmer);
    }

    public Optional<Farmer> getFarmer(Player player) {
        return plugin.getRegionHook().getFarmer(player);
    }

    public Optional<Farmer> getFarmer(Location location) {
        return plugin.getRegionHook().getFarmer(location);
    }

    public void removeFarmer(Farmer farmer) {
        plugin.getDatabaseManager().getFarmerRepository().delete(farmer);
        farmer.getCollectedItems().values()
                .forEach(collectedItem -> {
                    plugin.getDatabaseManager().getCollectedItemRepository().delete(collectedItem);
                    farmer.getCollectedItems().remove(collectedItem.getMaterial());
                });
    }

}
