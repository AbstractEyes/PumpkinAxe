package com.abstractphil.pumpkin.effects;

import com.redmancometh.reditems.abstraction.BlockBreakEffect;
import com.redmancometh.reditems.abstraction.HeldEffect;
import com.redmancometh.reditems.abstraction.TickingWeaponEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class FortuneEffect extends AbstractAxeEffect implements HeldEffect, TickingWeaponEffect, BlockBreakEffect {

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        if(blockBreakEvent.getPlayer() != null) {
            if(blockBreakEvent.isCancelled()) return;
            if(!isPumpkinBroke(blockBreakEvent.getBlock(), blockBreakEvent.getPlayer())) return;
            int fortuneValue = (int)Math.ceil(getData().getMultiplierPerLevel() * getLevel(blockBreakEvent.getPlayer()));
            if(randomCheck(blockBreakEvent.getPlayer())) {
                if(getUtils().isPumpkinMask(blockBreakEvent.getPlayer().getInventory().getHelmet())) fortuneValue += 3;
                for (ItemStack drop : blockBreakEvent.getBlock().getDrops()) {
                    int outCount = ThreadLocalRandom.current().nextInt(1, (int) Math.ceil(fortuneValue * 2));
                    blockBreakEvent.getPlayer().getInventory().addItem(new ItemStack(drop.getType(), outCount));
                    ItemStack item = blockBreakEvent.getPlayer().getItemInHand();
                    getUtils().addSafeJsonAmountInt(blockBreakEvent.getPlayer(), item, getData().getEffectName(), outCount, true);
                }
                blockBreakEvent.setDoesDrop(false);
            }
        }
    }

    @Override
    public void onTick(Player player, int i) {

    }
}
