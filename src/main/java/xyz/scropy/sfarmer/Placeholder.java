package xyz.scropy.sfarmer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Placeholder {

    private final String key;
    private final String value;

    public static PlaceholderBuilder builder() {
        return new PlaceholderBuilder();
    }

    public static class PlaceholderBuilder {

        private final List<Placeholder> placeholders = new ArrayList<>();

        private PlaceholderBuilder() {
            placeholders.add(new Placeholder("%prefix%", SFarmerPlugin.getInstance().getConfiguration().messages.prefix));
        }

        public PlaceholderBuilder apply(String key, String value) {
            placeholders.add(new Placeholder(key, value));
            return this;
        }

        public List<Placeholder> build() {
            return placeholders;
        }
    }
}
