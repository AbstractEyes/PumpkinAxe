package com.abstractphil.pumpkin.util;

import com.abstractphil.pumpkin.PumpkinMain;
import com.abstractphil.pumpkin.cfg.PumpkinConfig;
import com.abstractphil.pumpkin.cfg.PumpkinEffectData;
import com.abstractphil.pumpkin.effects.AbstractAxeEffect;
import com.google.gson.JsonObject;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.abstraction.Effect;
import com.redmancometh.reditems.mediator.AttachmentManager;
import com.redmancometh.reditems.mediator.EnchantManager;
import com.redmancometh.reditems.storage.EnchantData;
import com.redmancometh.reditems.storage.SimpleContainer;
import com.redmancometh.warcore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.util.*;

public class AbsPhilItemUtils {
    PumpkinConfig config;
    Map<String, AbstractAxeEffect> effectData;

    /*
        Todo: Needs:
            1. Create a PumpkinAxe
            2. Attach to a PumpkinAxe
            3. Remove attachments from PumpkinAxe.
            4. Replace attachments with a higher level in PumpkinAxe.
     */

    public AbsPhilItemUtils(PumpkinConfig cfgIn, Map<String, AbstractAxeEffect> effectsDataIn) {
        config = cfgIn;
        effectData = effectsDataIn;
    }

    public String prepareCommand(Player playerIn, String rawCommand, @Nullable Integer amountIn) {
        if(amountIn == null) amountIn = 0;
        String out = rawCommand;
        out = out.replace("%player%", playerIn.getName());
        out = out.replace("player", playerIn.getName());
        out = out.replace("{}", playerIn.getName());
        out = out.replace("%amount%", Integer.toString(amountIn));
        return out;
    }

    public boolean canLevelEffect(Player player, ItemStack item, String effectName, int amount) {
        return (getEffectLevel(player, item, effectName) + amount) <= getMaxEffectLevel(effectName);
    }

    public int pumpkinsBroken(ItemStack item) {
        if(getJsonStatistics(item) != null && isPumpkinAxe(item) && getJsonStatistics(item) != null)
            return AbsPhilJsonUtil.getPumpkinsBroken(getJsonStatistics(item));
        return 0;
    }

    //public int getEffectLevel(ItemStack item, String effectName) {
      //  if(eManager().isRedItem(item) && eManager().getEffectLevel(item, ))
    //}

    // Get old effect level X
    // Remove old attachment
    // Create new attachment with new effect at new level
    // Add new attachment
    public ItemStack levelPumpkinAxeAttachment(Player player, ItemStack item, String effectName) {
        return levelPumpkinAxeAttachment(player, item, effectName, 1);
    }
    public ItemStack levelPumpkinAxeAttachment(Player player, ItemStack item, String effectName, int levelAmount) {
        System.out.println("Level " + levelAmount + " pumpkin axe attachment " + effectName + " " + item);
        if(effectName.equals("paxe")) return item; // axe doesn't level
        item = levelEffectSafeWithLores(player, item, effectName, levelAmount);
        return item;
    }

    private void setInternalEffectLevel(ItemStack item, String effectName, int level) {
        System.out.println("Getting internal effect level: " + item + " " + effectName + " " + level);
        AbstractAxeEffect effect = (AbstractAxeEffect) getEffect(item, effectName);
        System.out.println("Getting effect list: " + eManager().getEffects(item));
        if(effect != null) {
            System.out.println("Setting internal effect" + effect);
            if(effect.getData() != null) {
                System.out.println("Setting level via json interface");
                AbsPhilJsonUtil.setEffectLevel(getJsonStatistics(item), effectName, level);
            }
        }
    }

    private int getMaxEffectLevel(String effectName) {
        return effectData.get(effectName).getMaxNaturalLevel();
    }


    public int getEffectLevel(Player player, ItemStack item, String effectName) {
        AbstractAxeEffect effect = (AbstractAxeEffect) getEffect(item, effectName);
        if(effect != null && AbsPhilJsonUtil.getEffectLevel(getJsonStatistics(item), effectName) > 0)
            return AbsPhilJsonUtil.getEffectLevel(getJsonStatistics(item), effectName);
        return 0;
    }

    public boolean hasEffect(ItemStack item, String effectName) {
        for (EnchantData effect : eManager().getEffects(item)) {
            if (effect.getEffect().getName().equals(effectName)) return true;
        }
        return false;
    }

    public static boolean hasActiveEffect(Player player, String effectName) {
        for (EnchantData effect : RedItems.getInstance().getEnchantManager().getActiveEffects(player)) {
            if(effectName.equals(effect.getEffect().getName())) return true;
        }
        return false;
    }

    @Nullable
    public static Effect getEffect(ItemStack item, String effectName) {
        for (EnchantData attachment : RedItems.getInstance().getEnchantManager().getEffects(item)) {
            if (effectName.equals(attachment.getEffect().getName())) {
                return attachment.getEffect();
            }
        }
        return null;
    }

    public AbstractAxeEffect getAxeEffect(String effectName) {
        return effectData.get(effectName);
    }

    public EnchantManager eManager() {
        return RedItems.getInstance().getEnchantManager();
    }
    public AttachmentManager aManager() {
        return RedItems.getInstance().getAttachManager();
    }

    // Creates a simple red item.
    private ItemStack makeSimpleRedItem(String effectName, int level) {
        return makeSimpleRedItem(effectData.get(effectName), level);
    }
    private ItemStack makeSimpleRedItem(AbstractAxeEffect effectIn, int level){
        ItemStack item;
        PumpkinEffectData dataIn = effectIn.getData();
        if(dataIn.getMaterial().equals(Material.SKULL_ITEM)) {
            item = new ItemStack(dataIn.getMaterial(), 1, (short)3);
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwner(dataIn.getSkullHost());
            meta.setDisplayName(dataIn.getGuiName());
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(dataIn.getMaterial(), 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(dataIn.getGuiName());
            item.setItemMeta(meta);
        }
        System.out.println("Debug output for the levelup check.");
        System.out.println(item + " " + effectIn + " " + level);
        item = eManager().attachEffect(item, effectIn, level);
        eManager().getData(item).get().setData(new JsonObject());
        //eManager().getData(item).get().setData(new JsonObject());
        return item;
    }

    /*
    // Creates a simple red item attachment.
    private ItemStack makeSimpleRedItemAttachment(String effectName, int level){
        return makeSimpleRedItemAttachment(effectData.get(effectName).getData(), level);
    }
    private ItemStack makeSimpleRedItemAttachment(PumpkinEffectData dataIn, int level){
        ItemStack item = makeSimpleRedItem(dataIn, level);
        aManager().makeAttachment(item);
        return item;
    }*/
    private ItemStack levelEffectSafeWithLores(Player player, ItemStack item, String effectName, int levelAmount) {
        try {
            if(!canLevelEffect(player, item, effectName, levelAmount)){
                //player.sendMessage("Blessing level " + passiveName + " is too high to level up.");
                return item;
            }
            //System.out.println("Passive name: " + passiveName);
            int level = getEffectLevel(player, item, effectName);
            //System.out.println("Level: " + level);
            if(level < 0) level = 0;
            System.out.println("Raw Effect: " + getAxeEffect(effectName));
            Effect effect = getAxeEffect(effectName);
            int finalLevel = Math.max(1, level + levelAmount);
            if(finalLevel <= effect.getMaxNaturalLevel()) {
                System.out.println("Level before: " + getEffectLevel(player, item, effectName));
                // Get simple container.
                Optional<SimpleContainer> data = RedItems.getInstance().getEnchantManager().getData(item);
                // Get enchantment list.
                System.out.println("Level Data: " + data);
                @Nullable ArrayList<EnchantData> eData;
                if(data.isPresent()) {
                    eData = (ArrayList<EnchantData>)data.get().getEnchants();
                    if(eData != null){
                        System.out.println("eData " + eData);
                        ArrayList<EnchantData> newData = removeEnchant(eData, effectData.get(effectName));
                        System.out.println("newData " + newData);
                        data.get().setEnchants(newData);
                        System.out.println("newData " + newData);
                    }
                    AbsPhilJsonUtil.setEffectLevel(data.get().getData(), effectName, finalLevel);
                    eManager().attachEffect(item, effectData.get(effectName), finalLevel);

                    String vanillaEnchant = getAxeEffect(effectName).getData().getVanillaEnchant();
                    if(vanillaEnchant != null && !vanillaEnchant.equals("")) {
                        if(item.containsEnchantment(Enchantment.getByName(vanillaEnchant))){
                            item.removeEnchantment(Enchantment.getByName(vanillaEnchant));
                        }
                        item.addEnchantment(Enchantment.getByName(vanillaEnchant), finalLevel);
                    }
                    System.out.println("readyitem " + item);
                    item = bakePumpkinAxeLores(player, item);
                    data.get().commit(item);
                    System.out.println("Final item: " + item);
                } else {
                    System.out.println("You've attempted to level an item that cannot level");

                }
                return item;
            } else {
                return item;
            }
        } catch (Exception ex) {
            System.out.println("failed to add a level");
            ex.printStackTrace();
        }
        return item;
    }

    private ArrayList<EnchantData> removeEnchant(ArrayList<EnchantData> data, Effect effectToRemove) {
        if(data.size() == 0) return data;
        int oi = -1;
        for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
            EnchantData ench = data.get(i);
            if (ench.getEffect().getName().equals(effectToRemove.getName())) {
                oi = i;
                break;
            }
        }
        if(oi >= 0) data.remove(oi);
        return data;
    }

    public boolean isRedItem(ItemStack item) {
        return eManager().isRedItem(item);
    }

    // Prototype, unlikely to be used.
    @Nullable
    private JsonObject getInternalEffects(ItemStack item) {
        if(getSimpleRedItemData(item).isPresent())
            return getSimpleRedItemData(item).get().getData().getAsJsonObject();
        return null;
    }

    private Optional<SimpleContainer> getSimpleRedItemData(ItemStack item) {
        return eManager().getData(item);
    }

    public ItemStack commitReplaceHeldItem(Player player, ItemStack item) {
        if(eManager().getData(item).isPresent())
            item = eManager().getData(item).get().commit(item);
        player.setItemInHand(item);
        return item;
    }

    // Pumpkin Axe Specific Functions ----------------------------------------------
    // Pumpkin Axe Specific Functions ----------------------------------------------
    // Pumpkin Axe Specific Functions ----------------------------------------------
    public boolean isPumpkinAxe(ItemStack item) {
        if(isRedItem(item)){
            for (EnchantData effect : eManager().getEffects(item)) {
                if (effect.getEffect().getName().equals("paxe")) return true;
            }
        }
        return false;
    }
    public boolean isPumpkinMask(ItemStack item) {
        if(isRedItem(item)){
            for (EnchantData effect : eManager().getEffects(item)) {
                if (effect.getEffect().getName().equals("pmask")) return true;
            }
        }
        return false;
    }
    public ItemStack createPumpkinAxe(Player player) {
        ItemStack item = makeSimpleRedItem(effectData.get("paxe"), 1);
        AbsPhilJsonUtil.setEffectLevel(getJsonStatistics(item), "paxe", 1);
        item = bakePumpkinAxeLores(player, item);
        item = getSimpleRedItemData(item).get().commit(item);
        return item;
    }

    public boolean isPumpkinBroke(Block block, Player player) {
        return block.getType() == Material.PUMPKIN &&
                (!block.hasMetadata("PLACEDBLOCK") || player.isOp());
    }

    public void addSafeJsonAmountInt(Player player, ItemStack item, String effectName, int amount, boolean replaceItem) {
        if(isPumpkinAxe(item) && getJsonStatistics(item) != null) {
            AbsPhilJsonUtil.addInternalIntAmountWithKey(getJsonStatistics(item), effectName, amount);
            item = bakePumpkinAxeLores(player, item);
            if(replaceItem) player.setItemInHand(getSimpleRedItemData(player.getItemInHand()).get().commit(item));
        }
    }

    public void setSafeJsonAmountInt(Player player, ItemStack item, String effectName, int amount, boolean replaceItem) {
        if(isPumpkinAxe(item) && getJsonStatistics(item) != null) {
            AbsPhilJsonUtil.setInternalIntAmountWithKey(getJsonStatistics(item), effectName, amount);
            item = bakePumpkinAxeLores(player, item);
            if(replaceItem) player.setItemInHand(getSimpleRedItemData(player.getItemInHand()).get().commit(item));
        }
    }


    public int getUnsafeJsonAmountInt(ItemStack item, String effectName) {
        return AbsPhilJsonUtil.getInternalIntAmountWithKey(getJsonStatistics(item), effectName);
    }

    public int getSafeJsonAmountInt(ItemStack item, String effectName) {
        if(isPumpkinAxe(item) && getJsonStatistics(item) != null) {
            return AbsPhilJsonUtil.getInternalIntAmountWithKey(getJsonStatistics(item), effectName);
        }
        return 0;
    }

    private EnchantData getPumpkinAxeEffect(ItemStack item) {
        for (EnchantData enchantData : eManager().getEffects(item)) {
            if(enchantData.getEffect().getName().equals("paxe")) return enchantData;
        }
        return null;
    }

    private ItemStack bakePumpkinAxeLores(Player player, ItemStack item) {
        if(isPumpkinAxe(item)) {
            ArrayList<String> lores = new ArrayList<>();
            //lores.add(colorize("**&6Chopped: &c" + pumpkinsBroken(item)) );
            for (EnchantData enchantData : eManager().getEffects(item)) {
                if(enchantData.getEffect().getName().equals("paxe")) continue; // exclude paxe until the end.
                if(enchantData.getEffect() instanceof AbstractAxeEffect) {
                    AbstractAxeEffect effect = (AbstractAxeEffect) enchantData.getEffect();
                    if(effect.getData().getVanillaEnchant() != null && !effect.getData().getVanillaEnchant().equals("")) continue;
                    ArrayList<String> cLores = effect.getBakeLore(player);
                    lores.addAll(colorize(cLores));
                    lores = replaceAmounts(item, lores);
                } else if(enchantData.getEffect() instanceof com.redmancometh.enchants.enchants.Enchantment) {
                    // Do nothing for now, check to see results later if there's problems.
                }
            }
            // Prepare paxe specific lores last.
            EnchantData enchantData = getPumpkinAxeEffect(item);
            if(enchantData != null && enchantData.getEffect() instanceof AbstractAxeEffect) {
                AbstractAxeEffect effect = (AbstractAxeEffect) enchantData.getEffect();
                ArrayList<String> cLores = effect.getBakeLore(player);
                lores.addAll(colorize(cLores));
                lores = replaceAmounts(item, lores);
                // ---------------------------------
                item = ItemUtil.setLore(item, lores);
            }
            return item;
        }
        return item;
    }

    private int getSafeInternalJsonAmountInt(ItemStack item, String key) {
        if(isRedItem(item) && getJsonStatistics(item).has(key)) {
            return AbsPhilJsonUtil.getInternalIntAmountWithKey(getJsonStatistics(item), key);
        }
        return 0;
    }

    @Nullable
    public ItemStack getPumpkinAxe(Player player){
        if(isPumpkinAxe(player.getItemInHand())) return player.getItemInHand();
        return null;
    }

    private ItemStack replaceEnchantment(ItemStack item, String enchantName, int level) {
        if(item.containsEnchantment(Enchantment.getByName(enchantName))) {
            item.removeEnchantment(Enchantment.getByName(enchantName));
            // Todo: check if level matches correctly.
            item.addEnchantment(Enchantment.getByName(enchantName), level);
        }
        return item;
    }

    public JsonObject getJsonStatistics(ItemStack item) {
        if(isPumpkinAxe(item)) {
            return eManager().getData(item).get().getData();
        }
        return null;
    }

    // Pumpkin Axe Specific Functions ----------------------------------------------
    // Pumpkin Axe Specific Functions ----------------------------------------------
    // Pumpkin Axe Specific Functions ----------------------------------------------

    public String colorize(String stringIn) {
        return ChatColor.translateAlternateColorCodes('&', stringIn);
    }

    public List<String> colorize(List<String> listIn) {
        List<String> list = new ArrayList<>();
        for (String str : listIn) {
            list.add(colorize(str));
        }
        return list;
    }

    public ArrayList<String> replaceAmounts(ItemStack item, List<String> listIn) {
        ArrayList<String> list = new ArrayList<>();
        for (String str : listIn) {
            String preppedString = str;
            for (Map.Entry<String, AbstractAxeEffect> entry : effectData.entrySet()) {
                String key = entry.getKey();
                AbstractAxeEffect eff = entry.getValue();
                int internalAmount = getSafeInternalJsonAmountInt(item, eff.getData().getEffectName());
                if (str.contains("%" + eff.getName()))
                    preppedString = str.replace("%" + eff.getName(), String.valueOf(internalAmount));
            }
            list.add(preppedString);
        }
        return list;
    }

    public void terminate() {
    }

}
