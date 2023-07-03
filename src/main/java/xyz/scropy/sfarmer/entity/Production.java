package xyz.scropy.sfarmer.entity;

import com.cryptomorin.xseries.XMaterial;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class Production {

    private int spawnCount;
    private Instant lastSpawnTime;
    private int productionRate;

    public Production() {
        this.spawnCount = 0;
        this.lastSpawnTime = Instant.now();
    }

    public void addItemSpawn(int amount, Instant spawnTime) {
        Duration duration = Duration.between(lastSpawnTime, spawnTime);
        if (duration.getSeconds() >= 60) {
            productionRate = (int) (spawnCount / duration.getSeconds());
            spawnCount = 0;
            lastSpawnTime = spawnTime;
            return;
        }
        spawnCount += amount;
    }

    public String getProductionRatePerMinute() {
        return StringUtils.numberFormat(productionRate * 60);
    }

    public String getProductionRatePerHour() {
        return StringUtils.numberFormat(productionRate * 3600);
    }

    public String getProductionRatePerDaily() {
        return StringUtils.numberFormat(productionRate * 3600 * 24);
    }

}
