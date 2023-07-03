package xyz.scropy.sfarmer.repository.impl;

import com.j256.ormlite.support.ConnectionSource;
import xyz.scropy.sfarmer.SortedList;
import xyz.scropy.sfarmer.model.AutoSell;
import xyz.scropy.sfarmer.model.CollectedItem;
import xyz.scropy.sfarmer.model.Farmer;
import xyz.scropy.sfarmer.repository.Repository;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AutoSellRepository extends Repository<AutoSell, Integer> {

    private final SortedList<AutoSell> autoSellsByFarmer = new SortedList<>(
            Comparator.comparing(AutoSell::getFarmerId));

    public AutoSellRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, AutoSell.class, Comparator.comparing(AutoSell::getId));
        autoSellsByFarmer.addAll(getEntries());
        autoSellsByFarmer.sort();
    }

    public Optional<AutoSell> getEntryByFarmer(UUID farmerUUID) {
        return autoSellsByFarmer.getEntry(new AutoSell(farmerUUID));
    }

    @Override
    public void addEntry(AutoSell autoSell) {
        super.addEntry(autoSell);
        autoSellsByFarmer.add(autoSell);
    }

    @Override
    public CompletableFuture<Void> delete(AutoSell autoSell) {
        autoSellsByFarmer.remove(autoSell);
        return super.delete(autoSell);
    }
}
