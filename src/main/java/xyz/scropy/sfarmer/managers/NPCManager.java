package xyz.scropy.sfarmer.managers;

import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.gui.FarmerGui;
import xyz.scropy.sfarmer.model.Farmer;

import java.util.*;

public class NPCManager implements Listener {

    private final SFarmerPlugin plugin;

    @Getter
    private final Map<Farmer, NPC.Global> npcMap = new HashMap<>();

    public NPCManager(SFarmerPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        for (Farmer farmer : plugin.getDatabaseManager().getFarmerRepository().getEntries()) {
            createNPC(farmer);
        }
    }

    public void createNPC(Farmer farmer) {
        NPC.Global npc = NPCLib.getInstance().generateGlobalNPC(plugin, farmer.getId().toString(), farmer.getLocation());
        npc.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTY4Njc0Mjc5NDA0NSwKICAicHJvZmlsZUlkIiA6ICIxMGY2NjQ4ZGNlYzE0ODY2ODJkY2E4Zjc5MmQ4YzZhOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4R3VjaW9sIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I4ZjJmMzI0MzZhZmFhYzJhZThjMDc2M2VkOTBiMjgyMWUwZmJlNzYzMjZjNmEzOTdkMjhmNzYyMTQ0MWUwMyIKICAgIH0KICB9Cn0=",
                "rNIL6ro2qvhlhqaDCUOOtxVOxJOuaHZUKA2WANAUpskKTveSJ8qfGfm87QdFMjjmM+E3N2wLOrxPWbddsdJR80bfTy2NirPexZ9WwWichwRoo421CY6n6lgPqM1TFz3bCi3JCEHSwUJ02wxk+kc/Mz+ew5cVFXPgHTCXSEENcrfOVn5HEnfyW+pEBUNdD/U1zH5A8Ephti+KNpa0+KAtipkUYYuVvHbmvbsMIAl19vUwyMUQs/GH0PSot1s+fa6Bjuv5VNkKVnNkUb2DfxHAkeL8rCOr2MpoId5Wr8MBdJ3Y0V5gtl6WZdKMzNlPMXSbi+fWbYIarL9NEzQkOVTku5v5uaq62adRvaiOQp2/nZLTTF+gnfEx6/WxvENZ9IhTFGZPv7B7OPnbKlQqF+rfLQubIEmCPpe0k9rRy5G6v0JSnHX57dPVB9cq3JJb6fsdMnTjn7V0UaFidqflDQ2W0m8oI6vXfJ90XiCLvQgvhlwJVT4enRwEWoHNK/jf0S9nrn36iYpLoSjaAV5XJzCK4KQirEdK9FO2z95qKq7caYBiSuZ/FBZtwwNDhnobpYSVx0gcvi234FvUp9VbGQOcnUeVfVlF9Lig/VjUxjSvMoQR4/G3SCcDt9EsL/DOSwz1GDLdath60nesScNBtRbsZijUYbqMymA3JEP1feCRULY=");
        npc.setText(ChatColor.translateAlternateColorCodes('&', "&6&lÇİFTÇİ"));
        npc.addCustomClickAction((clickedNPC, player) -> {
            if (farmer != this.plugin.getRegionHook().getFarmer(player).orElse(null)) {
                return;
            }
            FarmerGui.open(player);
        });
        npc.setGlowing(farmer.isGlowing());
        npc.forceUpdate();
        this.npcMap.put(farmer, npc);
    }

}
