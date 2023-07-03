package xyz.scropy.sfarmer.hook.money;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.hook.Hook;

public interface EconomyHook extends Hook {

    default void init(){}
    void add(OfflinePlayer player, double amount);
    void withdraw(OfflinePlayer player, double amount);
    boolean has(Player player, int price);
}
