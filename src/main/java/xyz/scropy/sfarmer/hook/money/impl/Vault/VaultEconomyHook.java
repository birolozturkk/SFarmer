package xyz.scropy.sfarmer.hook.money.impl.Vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.scropy.sfarmer.hook.money.EconomyHook;

public class VaultEconomyHook implements EconomyHook {

    private Economy economy = null;

    @Override
    public void init() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;

        economy = rsp.getProvider();
    }


    @Override
    public void add(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    @Override
    public boolean has(Player player, int price) {
        return economy.has(player, price);
    }

}
