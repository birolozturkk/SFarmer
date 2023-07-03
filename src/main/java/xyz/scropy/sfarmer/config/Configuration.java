package xyz.scropy.sfarmer.config;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.*;

public class Configuration {

    public double tax = 0.04;
    public String maxLevel = "&cSon seviyeye ulaştın";
    public double farmerPrice = 10000;
    public Messages messages = new Messages();
    public Map<XMaterial, CollectedMaterial> collectedMaterials = ImmutableMap.<XMaterial, CollectedMaterial>builder()
            .put(XMaterial.WHEAT, new CollectedMaterial()).build();
    public Gui farmerGUI = new Gui("Farmer", 6, 36,  XMaterial.GRAY_STAINED_GLASS_PANE, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 51, 52, 53),
            ImmutableMap.<String, ItemConfig>builder().put("collectedItem", new ItemConfig(XMaterial.CHEST, 1, "", true, null, null,
                    Arrays.asList("", "&7Mevcut stok: &a%amount%&8/&c%capacity%", "&7Birim fiyat: %unit_price%", ""), Collections.singletonList(6))).build());
    public Gui shopGUI = new Gui("Farmer Shop", 6, 36, XMaterial.GRAY_STAINED_GLASS_PANE, Arrays.asList(1, 2, 3),
            ImmutableMap.<String, ItemConfig>builder().put("buy", new ItemConfig(XMaterial.WHEAT, 1, "BUY", true, null, null,
                    Arrays.asList("", "buy farmer"), Collections.singletonList(6))).build());
    public Gui upgradeGUI = new Gui("Upgrade", 6, 36,  XMaterial.GRAY_STAINED_GLASS_PANE, Arrays.asList(1, 2, 3),
            ImmutableMap.<String, ItemConfig>builder().put("upgrade", new ItemConfig(XMaterial.WHEAT, 1, "Upgrade", true, null, null,
                    Arrays.asList("", "%current_capacity%", "%next_capacity%", "%price%", ""), Collections.singletonList(6))).build());

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gui {
        public String title;
        public int rows;
        public int pageSize;
        public XMaterial fillerMaterial;
        public List<Integer> fillerSlots;
        public Map<String, ItemConfig> items;
    }

    public static class Messages {

        public String prefix = "&3CQuest &8>>";
        public String reloaded = "%prefix% config dosyaları güncellendi";
        public String invalidArgument = "&cInvalid Argument";
        public String noPermission = "%prefix% &cYou do not have permission to run this command.";
        public String tooManyArguments = "%prefix% &cToo many arguments.";
        public String notEnoughArguments = "%prefix% &cNot enough arguments.";
        public String unknownCommand = "%prefix% &cUnknown command.";
        public String notHaveAnIsland = "%prefix% &cYou don't have an island";
        public String alreadyHaveFarmer = "%prefix% &cYou already have the farmer";
        public String successfullyBought = "%prefix% &asuccessfully bought";
        public String maxLevel = "%prefix% &calready max level";
        public String youSold = "%prefix% &aYou have successfully sold the storage and earned %earning%";
        public String noProduct = "%prefix% &7Satılacak bir &#e30036eşya yok!";
        public String collectingEnabled = "%prefix% &7Başarıyla &aaktif &7edildi";
        public String collectingDisabled = "%prefix% &7Başarıyla &ade-aktif &7edildi";
        public String canNotMove = "%prefix% &Çiftçinin lokasyonunu değiştirmek için &#e300363 dakika &7beklemen &#e30036gerek.";
        public String collectedItems = "%prefix% &7Başarıyla tüm ürünleri &#05e300aldın.";
        public String noCollectableItem = "%prefix% &7Toplanacak bir &#e30036eşya yok!";
        public List<String> helpMessage = Arrays.asList("", "&#e2cc00&lÇiftçi Komutları",
                "&7/çiftçi &#d0e300satınal &o&7(çiftçi satın alma komutu.)",
                "&7/çiftçi &#d0e300menü &o&7(çiftçi satın alma komutu.)",
                "&7/çiftçi &#d0e300getir &o&7(çiftçi satın alma komutu.)");
        public String notHaveAFarmer = "%prefix% &cÇiftçin yok.";
        public String inventoryIsFull = "%prefix% &cEnvanterin dolu";
        public String canNotSellable = "%prefix% &7Bu ürün satışa kapalı, sol tık ile açabilirsin!";
        public String thisIslandDoesNotBelongToYou = "%prefix% &cBu ada sana ait değil";
        public String shouldNumberFormat = "%prefix% &7Geçerli bir sayı &#e30036girmedin.";
        public String collectedItem = "%prefix% &#54fb55Başarıyla envanterine &#05e200%amount% &#54fb55ürün aldın.";
        public String notEnought = "%prefix% &7Yeteri kadar &#e30036ürün yok.";
        public String successfullyUpgraded = "%prefix% &7Başarıyla yükseltme satın alındı.";
        public String notEnoughtMoney = "%prefix% &7Bakiyen &#e30036yetersiz!";
        public String addedAutoSell = "%prefix% &7%day% günlük otomatik satış çiftçine eklendi";
    }

    public static class CollectedMaterial {
        public double price = 50;
        public Map<Integer, Level> levels = ImmutableMap.<Integer, Level>builder().put(0, new Level(1000, 5000)).build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Level {
        public int price;
        public int capacity;
    }

}
