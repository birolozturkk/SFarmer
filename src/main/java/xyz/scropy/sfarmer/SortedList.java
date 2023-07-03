package xyz.scropy.sfarmer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

@Getter
public class SortedList<T> extends ArrayList<T> {
    private final Comparator<T> comparator;

    public SortedList(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean add(T element) {
        int index = Collections.binarySearch(this, element, comparator);
        if (index < 0) index = ~index;
        super.add(index, element);
        return true;
    }

    public Optional<T> getEntry(T entry) {
        int index = Collections.binarySearch(this, entry, comparator);
        if (index < 0) return Optional.empty();
        return Optional.of(get(index));
    }

    public void sort() {
        sort(comparator);
    }
}