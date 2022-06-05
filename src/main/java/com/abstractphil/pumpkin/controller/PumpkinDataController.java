package com.abstractphil.pumpkin.controller;

import com.abstractphil.pumpkin.cfg.PumpkinEffectData;
import com.abstractphil.pumpkin.effects.AbstractAxeEffect;
import com.abstractphil.pumpkin.util.AbsPhilItemUtils;
import com.redmancometh.configcore.config.ConfigManager;
import com.abstractphil.pumpkin.cfg.PumpkinConfig;

import com.redmancometh.reditems.RedItems;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PumpkinDataController {
	private ConfigManager<PumpkinConfig> cfg = new ConfigManager("pumpkinaxe.json", PumpkinConfig.class);
	private Map<String, AbstractAxeEffect> effectDataMap = new ConcurrentHashMap();
	private AbsPhilItemUtils utils;

	public void init() {
		cfg.init();
		Map<String, PumpkinEffectData> effects = cfg.getConfig().getEffects();
		prepareEffects(effects, effectDataMap);
		utils = new AbsPhilItemUtils(cfg.getConfig(), effectDataMap);
	}

	private void prepareEffects(Map<String, PumpkinEffectData> effectDefinitionMap, Map<String, AbstractAxeEffect> definedObjects) {
		effectDefinitionMap.forEach((name, attachment) -> {
			try {
				System.out.println("BUFF: " + attachment);
				System.out.println("NEW INSTANCE OF " + attachment.getClazz());
				AbstractAxeEffect attachmentInst = attachment.getClazz().newInstance();
				attachmentInst.setData(attachment);
				attachmentInst.setUtils(new AbsPhilItemUtils(cfg.getConfig(), effectDataMap));
				System.out.println("THE INSTANCE: " + attachmentInst);
				System.out.println("NAME: " + attachmentInst.getName());
				effectDataMap.put(name, attachmentInst);
				RedItems.getInstance().getEnchantManager().registerEffect(attachmentInst);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	public AbsPhilItemUtils getUtils() {
		return utils;
	}

	public boolean hasPumpkinEffects() {
		return effectDataMap.values().size() > 0;
	}

	public Map<String, AbstractAxeEffect> getPumpkinEffects() {
		return effectDataMap;
	}

	public PumpkinEffectData getAttachmentData(String attachmentName) {
		return cfg.getConfig().getEffects().get(attachmentName);
	}

	@Nullable
	public ItemStack getPumpkinAxe(Player player) {
		if(utils.isPumpkinAxe(player.getItemInHand()))
			return player.getItemInHand();
		return null;
	}

	@Nullable
	public ItemStack findPumpkinAxe(Player player) {
		if(player == null) return null;
		for( ItemStack item : player.getInventory().getContents()) {
			if(getUtils().isPumpkinAxe(item)) return item;
		}
		return null;
	}

	public AbstractAxeEffect getTemplateObject(String attachmentName) {
		return effectDataMap.get(attachmentName);
	}

	public PumpkinConfig cfg() {
		return cfg.getConfig();
	}

	public void terminate() {
		effectDataMap.values().forEach((effect) ->
				RedItems.getInstance().getEnchantManager().deregisterEffect(effect));
		utils.terminate();
	}
}
