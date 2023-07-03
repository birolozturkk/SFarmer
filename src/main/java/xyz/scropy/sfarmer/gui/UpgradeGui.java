package xyz.scropy.sfarmer.gui;

import com.j256.ormlite.stmt.query.In;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.config.ItemConfig;
import xyz.scropy.sfarmer.model.CollectedItem;
import xyz.scropy.sfarmer.utils.ItemUtils;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UpgradeGui {
    private final static SFarmerPlugin plugin = SFarmerPlugin.getInstance();

    public static void open(Player player, CollectedItem collectedItem) {
        Gui gui = Gui.gui().title(StringUtils.format(plugin.getConfiguration().upgradeGUI.title, Collections.emptyList()))
                .rows(plugin.getConfiguration().upgradeGUI.rows)
                .disableAllInteractions()
                .create();

        List<Integer> fillerSlots = plugin.getConfiguration().upgradeGUI.fillerSlots;

        for (Integer slot : fillerSlots) {
            gui.setItem(slot, ItemBuilder.from(plugin.getConfiguration().upgradeGUI.fillerMaterial.parseItem())
                    .name(Component.space()).asGuiItem());
        }

        GuiItem upgradeItem = getUpgradeItem(gui, collectedItem, player);
        List<Integer> slots = plugin.getConfiguration().upgradeGUI.items.get("upgrade").slots;
        gui.setItem(slots, upgradeItem);

        gui.open(player);
    }

    private static GuiItem getUpgradeItem(Gui gui, CollectedItem collectedItem, Player player) {
        ItemBuilder itemBuilder;
        ItemConfig upgradeConfig = plugin.getConfiguration().upgradeGUI.items.get("upgrade");
        if (!plugin.getConfiguration().collectedMaterials.get(collectedItem.getMaterial())
                .levels.containsKey(collectedItem.getLevel() + 1)) {
            List<String> lore = upgradeConfig.lore.stream()
                    .filter(s -> !s.contains("%price%"))
                    .toList();

            List<Placeholder> placeholders = Placeholder.builder()
                    .apply("%current_capacity%",StringUtils.numberFormat(collectedItem.getCurrentLevel().capacity))
                    .apply("%next_capacity%", plugin.getConfiguration().maxLevel).build();
            itemBuilder = ItemUtils.makeItem(upgradeConfig, placeholders).lore(StringUtils.format(lore, placeholders));
        } else {
            itemBuilder = ItemUtils.makeItem(upgradeConfig,
                    Placeholder.builder()
                            .apply("%current_capacity%", StringUtils.numberFormat(collectedItem.getCurrentLevel().capacity))
                            .apply("%next_capacity%", StringUtils.numberFormat(collectedItem.getNextLevel().capacity))
                            .apply("%price%", StringUtils.numberFormat(collectedItem.getNextLevel().price)).build());
        }
        List<Integer> slots = plugin.getConfiguration().upgradeGUI.items.get("upgrade").slots;
        return itemBuilder.asGuiItem(event -> {
            if(!plugin.getConfiguration().collectedMaterials.get(collectedItem.getMaterial())
                    .levels.containsKey(collectedItem.getLevel() + 1)) return;
            if(!plugin.getEconomyHook().has(player, collectedItem.getNextLevel().price)) {
                StringUtils.sendMessage(player, plugin.getConfiguration().messages.notEnoughtMoney);
                return;
            }
            SFarmerPlugin.getInstance().getAdventure().player(player)
                    .playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1f, 1f));
            StringUtils.sendMessage(player, plugin.getConfiguration().messages.successfullyUpgraded);
            plugin.getEconomyHook().withdraw(player, collectedItem.getNextLevel().price);
            collectedItem.upgrade();
            slots.forEach(slot -> gui.updateItem(slot, getUpgradeItem(gui, collectedItem, player)));
            plugin.getDatabaseManager().getCollectedItemRepository().save(collectedItem);
        });
    }
}

