package xyz.scropy.sfarmer.gui;

import com.cryptomorin.xseries.XMaterial;
import dev.sergiferry.playernpc.api.NPC;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.scropy.sfarmer.Placeholder;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.config.Configuration;
import xyz.scropy.sfarmer.config.ItemConfig;
import xyz.scropy.sfarmer.entity.Production;
import xyz.scropy.sfarmer.gui.UpgradeGui;
import xyz.scropy.sfarmer.managers.CooldownProvider;
import xyz.scropy.sfarmer.model.AutoSell;
import xyz.scropy.sfarmer.model.CollectedItem;
import xyz.scropy.sfarmer.model.Farmer;
import xyz.scropy.sfarmer.utils.ItemUtils;
import xyz.scropy.sfarmer.utils.StringUtils;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;

public class FarmerGui {

    private static final SFarmerPlugin plugin = SFarmerPlugin.getInstance();

    @Getter
    private static final CooldownProvider cooldownProvider = new CooldownProvider(Duration.ofMinutes(3));

    @Getter
    private static final Map<Player, PaginatedGui> viewers = new HashMap<>();

    public static void open(Player player) {
        PaginatedGui gui = Gui.paginated().title(StringUtils.format(plugin.getConfiguration().farmerGUI.title, Collections.emptyList()))
                .rows(plugin.getConfiguration().farmerGUI.rows)
                .disableAllInteractions()
                .pageSize(plugin.getConfiguration().farmerGUI.pageSize)
                .create();


        List<Integer> fillerSlots = plugin.getConfiguration().farmerGUI.fillerSlots;

        for (Integer slot : fillerSlots) {
            gui.setItem(slot, ItemBuilder.from(plugin.getConfiguration().farmerGUI.fillerMaterial.parseItem())
                    .name(Component.space()).asGuiItem());
        }

        GuiItem next = ItemUtils.makeItem(plugin.getConfiguration().farmerGUI.items.get("next"), Placeholder.builder().build())
                .asGuiItem(event -> {
                    if (gui.next())
                        plugin.getAdventure().player(player)
                                .playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 1f));
                });
        gui.setItem(plugin.getConfiguration().farmerGUI.items.get("next").slots, next);

        GuiItem previous = ItemUtils.makeItem(plugin.getConfiguration().farmerGUI.items.get("previous"), Placeholder.builder().build())
                .asGuiItem(event -> {
                    if (gui.previous())
                        plugin.getAdventure().player(player)
                                .playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1f, 1f));
                });
        gui.setItem(plugin.getConfiguration().farmerGUI.items.get("previous").slots, previous);

        Optional<Farmer> farmerOptional = plugin.getFarmerManager().getFarmer(player);
        if (farmerOptional.isEmpty()) return;
        Farmer farmer = farmerOptional.get();

        GuiItem sellAll = ItemUtils.makeItem(plugin.getConfiguration().farmerGUI.items.get("sellAll"), Placeholder.builder().build())
                .asGuiItem(event -> {
                    farmer.sell(player);
                    gui.clearPageItems();
                    FarmerGui.addContent(gui, farmer, player);
                    gui.update();
                });
        gui.setItem(plugin.getConfiguration().farmerGUI.items.get("sellAll").slots, sellAll);

        GuiItem moveHere = ItemUtils.makeItem(plugin.getConfiguration().farmerGUI.items.get("moveHere"), Placeholder.builder().build())
                .asGuiItem(event -> {
                    if (cooldownProvider.isOnCooldown(player.getUniqueId())) {
                        StringUtils.sendMessage(player, plugin.getConfiguration().messages.canNotMove, Placeholder.builder());
                        return;
                    }
                    if (!plugin.getRegionHook().isInside(player, player.getLocation())) {
                        StringUtils.sendMessage(player, plugin.getConfiguration().messages.thisIslandDoesNotBelongToYou, Placeholder.builder());
                        return;
                    }
                    plugin.getAdventure().player(player)
                            .playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.MASTER, 1f, 1f));
                    farmer.setLocation(player.getLocation());
                    plugin.getDatabaseManager().getFarmerRepository().save(farmer);
                    plugin.getNpcManager().getNpcMap().get(farmerOptional.get()).teleport(player.getLocation());
                    cooldownProvider.applyCooldown(player.getUniqueId());
                });
        gui.setItem(plugin.getConfiguration().farmerGUI.items.get("moveHere").slots, moveHere);
        gui.setCloseGuiAction(event -> viewers.remove(player));
        viewers.put(player, gui);
        addContent(gui, farmer, player);
        gui.open(player);
    }

    private static GuiItem getChangeStatusItem(Player player, Farmer farmer, PaginatedGui gui) {
        ItemConfig itemConfig = farmer.isEnabled() ? plugin.getConfiguration().farmerGUI.items.get("changeStatusEnabled")
                : plugin.getConfiguration().farmerGUI.items.get("changeStatusDisabled");
        GuiItem changeStatus = ItemUtils.makeItem(itemConfig, Placeholder.builder().build())
                .asGuiItem();
        changeStatus.setAction(event -> {
            farmer.toggle();
            gui.updateItem(itemConfig.slots.get(0), getChangeStatusItem(player, farmer, gui));
            SFarmerPlugin.getInstance().getAdventure().player(player)
                    .playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f));

        });
        return changeStatus;
    }

    private static GuiItem getGlowingItem(Player player, Farmer farmer, PaginatedGui gui) {
        ItemConfig itemConfig = farmer.isGlowing() ? plugin.getConfiguration().farmerGUI.items.get("glowingEnabled")
                : plugin.getConfiguration().farmerGUI.items.get("glowingDisabled");
        GuiItem glowingItem = ItemUtils.makeItem(itemConfig, Placeholder.builder().build())
                .asGuiItem();
        glowingItem.setAction(event -> {
            farmer.setGlowing(!farmer.isGlowing());
            NPC.Global npc = plugin.getNpcManager().getNpcMap().get(farmer);
            npc.forceUpdate();
            gui.updateItem(itemConfig.slots.get(0), getGlowingItem(player, farmer, gui));
            SFarmerPlugin.getInstance().getAdventure().player(player)
                    .playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f));

        });
        return glowingItem;
    }

    private static GuiItem getAutoSellInfo(Farmer farmer) {
        Optional<AutoSell> autoSellOptional = plugin.getDatabaseManager().getAutoSellRepository().getEntryByFarmer(farmer.getId());
        ItemConfig itemConfig = farmer.haveAutoSell() ? plugin.getConfiguration().farmerGUI.items.get("haveAutoSellInfo")
                : plugin.getConfiguration().farmerGUI.items.get("haveNotAutoSellInfo");
        Placeholder.PlaceholderBuilder placeholderBuilder = Placeholder.builder();
        if(farmer.haveAutoSell()) placeholderBuilder.apply("%elapsed_time%", StringUtils.formatDuration(autoSellOptional.get().getExpiryTime() - System.currentTimeMillis()));

        GuiItem guiItem = ItemUtils.makeItem(itemConfig, placeholderBuilder.build())
                        .asGuiItem();

        guiItem.setAction(event -> ((Player) event.getWhoClicked()).performCommand("token shop diger"));
        return guiItem;
    }

    private static GuiItem getCollectedItem(XMaterial type, Farmer farmer, CollectedItem collectedItem, Configuration.CollectedMaterial collectedMaterial, PaginatedGui gui, Player player) {

        int amount = collectedItem.getAmount();

        Configuration.Level level = collectedMaterial.levels.get(collectedItem.getLevel());
        int capacity = level.capacity;

        double unitPrice = collectedMaterial.price;

        Production production = farmer.getProductions().getOrDefault(collectedItem.getMaterial(), new Production());
        List<Placeholder> placeholders = Placeholder.builder()
                .apply("%capacity%", StringUtils.numberFormat(capacity))
                .apply("%amount%", StringUtils.numberFormat(amount))
                .apply("%unit_price%", String.valueOf(unitPrice))
                .apply("%production_rate_in_minute%", production.getProductionRatePerMinute())
                .apply("%production_rate_in_hour%", production.getProductionRatePerHour())
                .apply("%production_rate_in_daily%", production.getProductionRatePerDaily())
                .apply("%fullness%", String.valueOf((int) (((double) amount / (double) capacity) * 100)))
                .build();


        ItemConfig collectedItemConfig = collectedItem.isSellable() ? plugin.getConfiguration().farmerGUI.items.get("collectedItemSellableEnabled")
                : plugin.getConfiguration().farmerGUI.items.get("collectedItemSellableDisabled");

        collectedItemConfig.setMaterial(type);
        CollectedItem finalCollectedItem = collectedItem;
        GuiItem item = ItemUtils.makeCollectedItem(collectedItemConfig, placeholders)
                .asGuiItem();
        item.setAction(event -> {
            if (event.getClick().equals(ClickType.DROP)) {
                UpgradeGui.open(player, finalCollectedItem);
            } else if (event.getClick().equals(ClickType.MIDDLE)) {
                finalCollectedItem.toggle();
                if (finalCollectedItem.isEnabled()) {
                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.collectingEnabled,
                            Placeholder.builder());
                } else {
                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.collectingDisabled,
                            Placeholder.builder());
                }
            } else if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
                if (amount <= 0) {
                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.noProduct, Placeholder.builder());
                    return;
                }
                if (!finalCollectedItem.isSellable()) {
                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.canNotSellable, Placeholder.builder());
                    player.closeInventory();
                    return;
                }
                double price = amount * unitPrice;
                SFarmerPlugin.getInstance().getAdventure().player(player)
                        .playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1f, 1f));
                plugin.getEconomyHook().add(player, price - price / 4);
                finalCollectedItem.setAmount(0);
                StringUtils.sendMessage(player, plugin.getConfiguration().messages.youSold, Placeholder.builder()
                        .apply("%earning%", StringUtils.moneyFormat(price - price / 4)));
                gui.clearPageItems();
                FarmerGui.addContent(gui, farmer, player);
                gui.update();
            } else if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                Inventory inventory = player.getInventory();
                int remainingAmount = finalCollectedItem.getAmount();
                if (finalCollectedItem.getAmount() == 0) {
                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.noCollectableItem, Placeholder.builder());
                    return;
                }
                if (player.getInventory().firstEmpty() == -1) {
                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.inventoryIsFull, Placeholder.builder());
                    return;
                }
                for (int i = 0; i < 36; i++) {
                    ItemStack itemStack = inventory.getItem(i);
                    if (itemStack == null || itemStack.getType().equals(Material.AIR)
                            || (itemStack.getType().equals(finalCollectedItem.getMaterial().parseMaterial())
                            && itemStack.getAmount() < itemStack.getMaxStackSize())) {
                        int placedAmount = Math.min(remainingAmount, 64);
                        if (player.getInventory().firstEmpty() == -1) {
                            StringUtils.sendMessage(player, plugin.getConfiguration().messages.inventoryIsFull, Placeholder.builder());
                            break;
                        }
                        inventory.setItem(i, new ItemStack(finalCollectedItem.getMaterial().parseMaterial(), placedAmount));
                        remainingAmount -= placedAmount;
                    }
                }
                finalCollectedItem.setAmount(remainingAmount);
                StringUtils.sendMessage(player, plugin.getConfiguration().messages.collectedItems, Placeholder.builder());

            } else if (event.getClick().equals(ClickType.LEFT)) {
                finalCollectedItem.toggleSellable();
                gui.updatePageItem(event.getSlot(), getCollectedItem(type, farmer, collectedItem, collectedMaterial, gui, player));
            } else if (event.getClick().equals(ClickType.RIGHT)) {
                new AnvilGUI.Builder()
                        .onComplete((completion) -> {
                            try {
                                if (finalCollectedItem.getAmount() == 0) {
                                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.noCollectableItem, Placeholder.builder());
                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                                }
                                if (player.getInventory().firstEmpty() == -1) {
                                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.inventoryIsFull, Placeholder.builder());
                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                                }
                                int remainingAmount = Integer.parseInt(completion.getText().substring(16));
                                if (remainingAmount > finalCollectedItem.getAmount()) {
                                    StringUtils.sendMessage(player, plugin.getConfiguration().messages.notEnought, Placeholder.builder());
                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                                }
                                Inventory inventory = player.getInventory();
                                for (int i = 0; i < 36; i++) {
                                    ItemStack itemStack = inventory.getItem(i);
                                    if (itemStack == null || itemStack.getType().equals(Material.AIR)
                                            || (itemStack.getType().equals(finalCollectedItem.getMaterial().parseMaterial())
                                            && itemStack.getAmount() < itemStack.getMaxStackSize())) {
                                        if (player.getInventory().firstEmpty() == -1) {
                                            StringUtils.sendMessage(player, plugin.getConfiguration().messages.inventoryIsFull, Placeholder.builder());
                                            break;
                                        }
                                        int placedAmount = Math.min(remainingAmount, 64);
                                        inventory.addItem(new ItemStack(finalCollectedItem.getMaterial().parseMaterial(), placedAmount));

                                        remainingAmount -= placedAmount;
                                    }
                                }
                                finalCollectedItem.setAmount(finalCollectedItem.getAmount() - Integer.parseInt(completion.getText().substring(16)));
                                StringUtils.sendMessage(player, plugin.getConfiguration().messages.collectedItem, Placeholder.builder()
                                        .apply("%amount%", StringUtils.numberFormat(Integer.parseInt(completion.getText().substring(16)))));

                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                            } catch (NumberFormatException exception) {
                                StringUtils.sendMessage(player, plugin.getConfiguration().messages.shouldNumberFormat, Placeholder.builder());
                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                            }
                        })                                //allow player to take out and replace the right input item
                        .text("Adet Giriniz -> ")//sets the text the GUI should start with
                        .itemOutput(new ItemStack(Material.PAPER))
                        .itemLeft(new ItemStack(Material.PAPER))                      //use a custom item for the second slot
                        .onLeftInputClick(anvilPlayer -> anvilPlayer.sendMessage("first sword"))     //called when the left input slot is clicked
                        .onRightInputClick(anvilPlayer -> anvilPlayer.sendMessage("second sword"))   //called when the right input slot is clicked
                        .title("Enter your answer.")                                       //set the title of the GUI (only works in 1.14+)
                        .plugin(plugin)                                          //set the plugin instance
                        .open(player);
            }
            plugin.getDatabaseManager().getCollectedItemRepository().addEntry(finalCollectedItem);

        });
        return item;
    }

    public static void addContent(PaginatedGui gui, Farmer farmer, Player player) {

        ItemConfig changeStatus = farmer.isEnabled() ? plugin.getConfiguration().farmerGUI.items.get("changeStatusEnabled")
                : plugin.getConfiguration().farmerGUI.items.get("changeStatusDisabled");
        gui.setItem(changeStatus.slots, getChangeStatusItem(player, farmer, gui));

        ItemConfig glowing = farmer.isGlowing() ? plugin.getConfiguration().farmerGUI.items.get("glowingEnabled")
                : plugin.getConfiguration().farmerGUI.items.get("glowingDisabled");
        gui.setItem(glowing.slots, getGlowingItem(player, farmer, gui));

        ItemConfig autoSellInfo = farmer.haveAutoSell() ? plugin.getConfiguration().farmerGUI.items.get("haveAutoSellInfo")
                : plugin.getConfiguration().farmerGUI.items.get("haveNotAutoSellInfo");
        gui.setItem(autoSellInfo.slots, getAutoSellInfo(farmer));

        plugin.getConfiguration().collectedMaterials.forEach((type, collectedMaterial) -> {
            CollectedItem collectedItem = farmer.getCollectedItems().get(type);
            if (collectedItem == null) {
                collectedItem = new CollectedItem(farmer.getId(), type);
                farmer.getCollectedItems().put(type, collectedItem);
            }
            gui.addItem(getCollectedItem(type, farmer, collectedItem, collectedMaterial, gui, player));
        });
    }
}
