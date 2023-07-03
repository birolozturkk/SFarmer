package xyz.scropy.sfarmer.config;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemConfig {

    public XMaterial material;
    public int modelData;
    public int amount;
    public String displayName;
    public boolean glowing;
    public String headData;
    public String headOwner;
    public List<String> lore;
    public List<Integer> slots;

    public ItemConfig(XMaterial material, int amount, String displayName, boolean glowing, String headData, String headOwner, List<String> lore, List<Integer> slots) {
        this.material = material;
        this.amount = amount;
        this.displayName = displayName;
        this.glowing = glowing;
        this.headData = headData;
        this.headOwner = headOwner;
        this.lore = lore;
        this.slots = slots;
    }
}