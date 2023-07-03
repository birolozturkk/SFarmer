package xyz.scropy.sfarmer.managers;

import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownProvider {

    private final HashMap<UUID, Duration> cooldownTimes = new HashMap<>();
    private final Duration duration;

    public CooldownProvider(Duration duration) {
        this.duration = duration;
    }

    public boolean isOnCooldown(UUID uuid) {
        return cooldownTimes.containsKey(uuid) && cooldownTimes.get(uuid).toMillis() >= System.currentTimeMillis();
    }

    public Duration getRemainingTime(UUID uuid) {
        if (!isOnCooldown(uuid)) return Duration.ZERO;

        return cooldownTimes.get(uuid).minusMillis(System.currentTimeMillis());
    }

    public void applyCooldown(UUID uuid) {
        cooldownTimes.put(uuid, duration.plusMillis(System.currentTimeMillis()));
    }
}
