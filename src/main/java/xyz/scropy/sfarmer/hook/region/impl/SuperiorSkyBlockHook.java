package xyz.scropy.sfarmer.hook.region.impl;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.hook.region.RegionHook;
import xyz.scropy.sfarmer.model.Farmer;

import java.util.Objects;
import java.util.Optional;

public class SuperiorSkyBlockHook extends RegionHook implements Listener {

    private final SFarmerPlugin plugin;

    public SuperiorSkyBlockHook(SFarmerPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean hasRegion(Player player) {
        return SuperiorSkyblockAPI.getPlayer(player).hasIsland();
    }

    @Override
    public boolean isInside(Player player, Location location) {
        if(!hasRegion(player)) return false;
        return SuperiorSkyblockAPI.getPlayer(player).getIsland().isInside(location);
    }

    @Override
    public Optional<Farmer> getFarmer(Player player) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        Island island = superiorPlayer.getIsland();
        if(island == null) return Optional.empty();

        return SFarmerPlugin.getInstance().getDatabaseManager().getFarmerRepository().getEntries().stream()
                .filter(farmer -> island.isInside(farmer.getLocation()))
                .findFirst();

    }

    @Override
    public Optional<Farmer> getFarmer(Location location) {
        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        for (Farmer farmer : SFarmerPlugin.getInstance().getDatabaseManager().getFarmerRepository().getEntries()) {
            Island i = SuperiorSkyblockAPI.getIslandAt(farmer.getLocation());
            if (i == null) continue;
            if (!i.equals(island)) continue;

            return Optional.of(farmer);
        }
        return Optional.empty();
    }


    @EventHandler
    public void onDelete(IslandDisbandEvent event) {
        Optional<Farmer> farmerOptional = getFarmer(event.getPlayer().asPlayer());
        if(farmerOptional.isEmpty()) return;
        Farmer farmer = farmerOptional.get();
        plugin.getFarmerManager().removeFarmer(farmer);
        NPC.Global npc = plugin.getNpcManager().getNpcMap().get(farmer);
        npc.destroy();
        plugin.getNpcManager().getNpcMap().remove(farmer);
    }
}
