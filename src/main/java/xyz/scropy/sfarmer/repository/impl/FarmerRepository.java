package xyz.scropy.sfarmer.repository.impl;

import com.j256.ormlite.support.ConnectionSource;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.model.CollectedItem;
import xyz.scropy.sfarmer.model.Farmer;
import xyz.scropy.sfarmer.repository.Repository;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class FarmerRepository extends Repository<Farmer, Integer> {
    public FarmerRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Farmer.class, Comparator.comparing(Farmer::getId));
        getEntries().forEach(farmer -> {
            List<CollectedItem> collectedItems = SFarmerPlugin.getInstance().getDatabaseManager().getCollectedItemRepository()
                    .getCollectedItems(farmer.getId());
            collectedItems.forEach(collectedItem -> farmer.getCollectedItems().put(collectedItem.getMaterial(), collectedItem));
        });
    }
}
