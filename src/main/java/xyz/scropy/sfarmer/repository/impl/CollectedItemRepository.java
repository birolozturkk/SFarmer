package xyz.scropy.sfarmer.repository.impl;

import com.j256.ormlite.support.ConnectionSource;
import xyz.scropy.sfarmer.SortedList;
import xyz.scropy.sfarmer.model.CollectedItem;
import xyz.scropy.sfarmer.repository.Repository;

import java.sql.SQLException;
import java.util.*;

public class CollectedItemRepository extends Repository<CollectedItem, Integer> {

    private final SortedList<CollectedItem> collectedItemsByFarmer = new SortedList<>(
            Comparator.comparing(CollectedItem::getFarmerId));

    public CollectedItemRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CollectedItem.class, Comparator.comparing(CollectedItem::getId));
        collectedItemsByFarmer.addAll(getEntries());
        collectedItemsByFarmer.sort();
    }

    @Override
    public void addEntry(CollectedItem completedQuest) {
        super.addEntry(completedQuest);
        collectedItemsByFarmer.add(completedQuest);
    }

    public List<CollectedItem> getCollectedItems(UUID farmerId) {
        int index = Collections.binarySearch(collectedItemsByFarmer, new CollectedItem(farmerId),
                Comparator.comparing(CollectedItem::getFarmerId));
        if (index < 0) return Collections.emptyList();
        int currentIndex = index - 1;
        List<CollectedItem> result = new ArrayList<>();
        result.add(collectedItemsByFarmer.get(index));

        while (true) {
            if (currentIndex < 0) break;
            CollectedItem completedQuest = collectedItemsByFarmer.get(currentIndex);
            if (farmerId.equals(completedQuest.getFarmerId())) {
                result.add(collectedItemsByFarmer.get(currentIndex));
                currentIndex--;
            } else {
                break;
            }
        }

        currentIndex = index + 1;

        while (true) {
            if (currentIndex >= collectedItemsByFarmer.size()) break;
            CollectedItem completedQuest = collectedItemsByFarmer.get(currentIndex);
            if (farmerId.equals(completedQuest.getFarmerId())) {
                result.add(collectedItemsByFarmer.get(currentIndex));
                currentIndex++;
            } else {
                break;
            }
        }
        return result;
    }
}
