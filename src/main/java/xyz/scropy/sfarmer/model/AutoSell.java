package xyz.scropy.sfarmer.model;

import com.cryptomorin.xseries.XMaterial;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.scropy.sfarmer.DatabaseObject;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.config.Configuration;

import java.time.Duration;
import java.util.UUID;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "auto_sells")
public class AutoSell extends DatabaseObject {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "farmer_id")
    private UUID farmerId;

    @DatabaseField(columnName = "owner_uuid")
    private UUID owner;

    @DatabaseField(columnName = "expiry_time")
    private long expiryTime;

    public AutoSell(int id) {
        this.id = id;
    }

    public AutoSell(UUID farmerId) {
        this.farmerId = farmerId;
    }

    public AutoSell(UUID farmerId, UUID owner, long elapsedTime) {
        this.farmerId = farmerId;
        this.owner = owner;
        this.expiryTime = Duration.ofMinutes(elapsedTime).plusMillis(System.currentTimeMillis()).toMillis();
    }
}
