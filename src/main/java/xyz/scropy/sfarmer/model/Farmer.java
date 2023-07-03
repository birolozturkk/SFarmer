package xyz.scropy.sfarmer.model;

import com.cryptomorin.xseries.XMaterial;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.types.FloatType;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.DatabaseObject;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.entity.Production;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "farmers")
public class Farmer extends DatabaseObject {

    @DatabaseField(columnName = "id", id = true)
    private UUID id;

    @DatabaseField(columnName = "owner_uuid", unique = true)
    private UUID ownerUUID;

    @DatabaseField(columnName = "location")
    private String location;

    @DatabaseField(columnName = "glowing")
    private boolean glowing = true;

    @DatabaseField(columnName = "enabled")
    private boolean enabled = true;

    private final Map<XMaterial, CollectedItem> collectedItems = new HashMap<>();
    private final Map<XMaterial, Production> productions = new HashMap<>();

    public Farmer(UUID id) {
        this.id = id;
    }

    public Farmer(UUID ownerUUID, Location location) {
        this.ownerUUID = ownerUUID;
        this.id = UUID.randomUUID();
        setLocation(location);
    }

    public boolean haveAutoSell() {
        Optional<AutoSell> autoSell = SFarmerPlugin.getInstance().getDatabaseManager().getAutoSellRepository().getEntryByFarmer(id);
        return autoSell.isPresent() && autoSell.get().getExpiryTime() > System.currentTimeMillis();

    }

    public void sell(Player player) {
        AtomicReference<Double> price = new AtomicReference<>((double) 0);
        collectedItems.forEach((key, value) -> {
            if (value.getAmount() == 0) return;
            if (!value.isSellable()) return;

            double perPrice = value.getAmount() * SFarmerPlugin.getInstance().getConfiguration().collectedMaterials.get(key).price;
            SFarmerPlugin.getInstance().getEconomyHook().add(player, (perPrice - (perPrice * SFarmerPlugin.getInstance().getConfiguration().tax)));
            price.set(price.get() + perPrice);
            value.setAmount(0);
        });
        if (price.get() == 0.0) return;
        SFarmerPlugin.getInstance().getAdventure().player(player)
                .playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1f, 1f));
        StringUtils.sendMessage(player, SFarmerPlugin.getInstance().getConfiguration().messages.youSold, Placeholder.builder()
                .apply("%earning%", StringUtils.moneyFormat(price.get())));

    }

    public void addItem(Material type, int amount) {
        XMaterial material = XMaterial.matchXMaterial(type);
        if (!collectedItems.containsKey(material)) {
            collectedItems.put(material, new CollectedItem(id, material));
        }
        CollectedItem collectedItem = collectedItems.get(material);
        collectedItem.add(amount);
        SFarmerPlugin.getInstance().getDatabaseManager().getCollectedItemRepository().addEntry(collectedItem);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public Location getLocation() {
        if (location == null) return null;
        String[] args = location.split(",");
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]),
                Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }

    public void setLocation(Location location) {
        this.location = StringUtils.fromLocation(location);
        setChanged(true);
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        setChanged(true);
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        setChanged(true);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setChanged(true);
    }
}
