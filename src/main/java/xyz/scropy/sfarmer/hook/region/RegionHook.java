package xyz.scropy.sfarmer.hook.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.model.Farmer;

import java.util.Optional;

public abstract class RegionHook {


    public abstract boolean hasRegion(Player player);
    public abstract boolean isInside(Player player, Location location);
    public abstract Optional<Farmer> getFarmer(Player player);
    public abstract Optional<Farmer> getFarmer(Location location);
}
