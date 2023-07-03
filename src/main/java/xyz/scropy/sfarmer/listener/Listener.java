package xyz.scropy.sfarmer.listener;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.entity.Production;
import xyz.scropy.sfarmer.gui.FarmerGui;
import xyz.scropy.sfarmer.model.AutoSell;
import xyz.scropy.sfarmer.model.CollectedItem;
import xyz.scropy.sfarmer.model.Farmer;

import java.time.Instant;
import java.util.Optional;

public class Listener implements org.bukkit.event.Listener {

    private final SFarmerPlugin plugin;

    public Listener(SFarmerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDropItem(ItemSpawnEvent event) {
        if (event.getEntity().getPickupDelay() >= 39) return;
        Location location = event.getLocation();
        Optional<Farmer> farmerOptional = plugin.getFarmerManager().getFarmer(location);
        if (farmerOptional.isEmpty()) return;

        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        XMaterial material = XMaterial.matchXMaterial(itemStack.getType());
        if (!plugin.getConfiguration().collectedMaterials.containsKey(material)) return;

        Farmer farmer = farmerOptional.get();
        Production production = farmer.getProductions().getOrDefault(material, new Production());
        production.addItemSpawn(itemStack.getAmount(), Instant.now());
        farmer.getProductions().put(material, production);

        if (!farmer.isEnabled()) return;

        CollectedItem collectedItem = farmer.getCollectedItems().getOrDefault(material, new CollectedItem(farmer.getId(), material));
        if (!collectedItem.isEnabled()) return;
        if(collectedItem.isFull()) {
            Optional<AutoSell> autoSell = plugin.getDatabaseManager().getAutoSellRepository().getEntryByFarmer(farmer.getId());
            if (autoSell.isPresent() && collectedItem.isSellable()) {
                if(System.currentTimeMillis() > autoSell.get().getExpiryTime()) {
                    plugin.getDatabaseManager().getAutoSellRepository().delete(autoSell.get());
                    return;
                }
                double price = collectedItem.getAmount() * plugin.getConfiguration().collectedMaterials.get(material).price;
                plugin.getEconomyHook().add(Bukkit.getOfflinePlayer(autoSell.get().getOwner()), price - price / 4);
                collectedItem.setAmount(item.getItemStack().getAmount());
                return;
            }
            return;
        }

        int emptySize = collectedItem.getCurrentLevel().capacity - collectedItem.getAmount();
        if (emptySize < item.getItemStack().getAmount()) {
            farmer.addItem(itemStack.getType(), emptySize);
            item.getItemStack().setAmount(item.getItemStack().getAmount() - emptySize);
        } else {
            farmer.addItem(itemStack.getType(), itemStack.getAmount());
            event.setCancelled(true);
        }
    }

}
