package xyz.scropy.sfarmer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StringUtils {

    private final static LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();

    public static void sendMessage(Player player, String text) {
        sendMessage(player, text, Placeholder.builder());
    }

    public static void sendMessage(Player player, String text, Placeholder.PlaceholderBuilder placeholderBuilder) {
        Component message = StringUtils.format(text, placeholderBuilder.apply("%player_name%", player.getName()).build());
        SFarmerPlugin.getInstance().getAdventure().player(player).sendMessage(message);
    }

    public static String numberFormat(int number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }


    public static String moneyFormat(double number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }

    public static Component format(String string, List<Placeholder> placeholders) {
        string = processPlaceholders(string, placeholders);
        return legacyComponentSerializer.deserialize(string).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> format(List<String> list, List<Placeholder> placeholders) {
        return list.stream().map(string -> format(string, placeholders)).collect(Collectors.toList());
    }

    public static String processPlaceholders(String content, List<Placeholder> placeholders) {
        for (Placeholder placeholder : placeholders) {
            content = content.replace(placeholder.getKey(), placeholder.getValue());
        }
        return content;
    }

    public static String fromLocation(Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld().getName() +
                "," +
                location.getX() +
                "," +
                location.getY() +
                "," +
                location.getZ() +
                "," +
                location.getYaw() +
                "," +
                location.getPitch();
    }

    public static String formatDuration(long elapsedTime) {
        Duration duration = new Duration(elapsedTime);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" gün ")
                .appendHours()
                .appendSuffix(" saat ")
                .appendMinutes()
                .appendSuffix(" dakika ")
                .toFormatter();

        Period period = duration.toPeriod();
        Period dayTimePeriod = period.normalizedStandard(PeriodType.dayTime());
        return formatter.print(dayTimePeriod);

    }
}
