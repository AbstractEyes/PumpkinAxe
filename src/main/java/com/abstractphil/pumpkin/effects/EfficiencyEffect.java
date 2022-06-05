package com.abstractphil.pumpkin.effects;

import com.redmancometh.reditems.abstraction.HeldEffect;
import com.redmancometh.reditems.abstraction.TickingWeaponEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EfficiencyEffect extends AbstractAxeEffect implements TickingWeaponEffect, HeldEffect {

    @Override
    public void onTick(Player player, int i) {
        /*
        try {
            if(player != null && !player.getWorld().getName().equalsIgnoreCase("zone-1"))
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, getLevel(player)-1), true);
        } catch (Exception ex ){
            ex.printStackTrace();
        }*/
    }

}