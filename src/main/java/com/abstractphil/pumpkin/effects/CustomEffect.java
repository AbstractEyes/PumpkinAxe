package com.abstractphil.pumpkin.effects;

import com.abstractphil.pumpkin.cfg.LootCommand;
import com.redmancometh.reditems.abstraction.BlockBreakEffect;
import com.redmancometh.reditems.abstraction.HeldEffect;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CustomEffect extends AbstractAxeEffect implements HeldEffect, BlockBreakEffect {

    public boolean applicableFor(ItemStack item) {
        return getUtils().isPumpkinAxe(item);
    }

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        if(blockBreakEvent.isCancelled()) return;
        Player player = blockBreakEvent.getPlayer();
        Integer amount = null;
        if(player != null) {
            if(randomCheck(player)) {
                //player.sendMessage("Your harvested pumpkin has been upgraded!");
                for (Map.Entry<String, LootCommand> entry : this.getData().getLoot().entrySet()) {
                    String name = entry.getKey();
                    LootCommand loots = entry.getValue();
                    if (lootRandomCheck(blockBreakEvent.getPlayer(), loots)) {
                        player.playEffect(EntityEffect.FIREWORK_EXPLODE);
                        getUtils().addSafeJsonAmountInt(blockBreakEvent.getPlayer(),
                                blockBreakEvent.getPlayer().getItemInHand(), getData().getEffectName(), 1, true);
                        System.out.println("Attempting to dispatch modified command");
                        Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                getUtils().prepareCommand(
                                        blockBreakEvent.getPlayer(), loots.getCommand(),
                                        1));
                        break;
                    }
                }
            }
        }
    }


}