package xyz.scropy.sfarmer.gui;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.utils.ItemUtils;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.util.Collections;
import java.util.List;

public class ShopGui {

    private final static SFarmerPlugin plugin = SFarmerPlugin.getInstance();

    public static void open(Player player) {
        Gui gui = Gui.gui().title(StringUtils.format(plugin.getConfiguration().shopGUI.title, Collections.emptyList()))
                .rows(plugin.getConfiguration().shopGUI.rows)
                .disableAllInteractions()
                .create();

        List<Integer> fillerSlots = plugin.getConfiguration().shopGUI.fillerSlots;

        for (Integer slot : fillerSlots) {
            gui.setItem(slot, ItemBuilder.from(plugin.getConfiguration().shopGUI.fillerMaterial.parseItem())
                    .name(Component.space()).asGuiItem());
        }

        GuiItem buyFarmer = ItemUtils.makeItem(plugin.getConfiguration().shopGUI.items.get("buy"), Placeholder.builder().build())
                .asGuiItem((event) -> {
                    plugin.getFarmerManager().buyFarmer(player);
                });
        gui.setItem(plugin.getConfiguration().shopGUI.items.get("buy").slots, buyFarmer);
        gui.open(player);
    }
}
