package com.abstractphil.pumpkin.cfg;

import java.util.List;
import java.util.Map;

import com.abstractphil.pumpkin.effects.AbstractAxeEffect;
import lombok.Data;
import org.bukkit.Material;

@Data
public class PumpkinEffectData {
	private Class<? extends AbstractAxeEffect> clazz;
	private String effectName, guiName, skullHost, vanillaEnchant;
	private List<String> guiLore, itemLore, commands;
	private Material material;
	private int maxLevel, cooldown, cost;
	private float chancePerLevel, multiplierPerLevel;
	private Map<String, LootCommand> loot;
}
