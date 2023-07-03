package xyz.scropy.sfarmer.model;

import com.cryptomorin.xseries.XMaterial;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.scropy.sfarmer.DatabaseObject;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.config.Configuration;

import java.util.UUID;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "collected_items")
public class CollectedItem extends DatabaseObject {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "farmer_id")
    private UUID farmerId;

    @DatabaseField(columnName = "material")
    private XMaterial material;

    @DatabaseField(columnName = "enabled")
    private boolean enabled = true;

    @DatabaseField(columnName = "amount")
    private int amount;

    @DatabaseField(columnName = "level")
    private int level;

    @DatabaseField(columnName = "sellable")
    private boolean sellable = true;

    public CollectedItem(int id) {
        this.id = id;
    }

    public CollectedItem(UUID farmerId) {
        this.farmerId = farmerId;
    }

    public CollectedItem(UUID farmerId, XMaterial material) {
        this.farmerId = farmerId;
        this.material = material;
    }

    public void add(int amount) {
        setAmount(this.amount + amount);
    }

    public void upgrade() {
        level++;
        setChanged(true);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void toggleSellable() {
        setSellable(!sellable);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setChanged(true);
    }

    public void setAmount(int amount) {
        this.amount = amount;
        setChanged(true);
    }

    public void setLevel(int level) {
        this.level = level;
        setChanged(true);
    }

    public void setSellable(boolean sellable) {
        this.sellable = sellable;
        setChanged(true);
    }

    public Configuration.Level getCurrentLevel() {
        return SFarmerPlugin.getInstance().getConfiguration().collectedMaterials.get(material).levels.get(level);
    }

    public Configuration.Level getNextLevel() {
        return SFarmerPlugin.getInstance().getConfiguration().collectedMaterials.get(material).levels.get(level + 1);
    }

    public boolean isFull() {
        return getCurrentLevel().capacity <= amount;
    }

}
