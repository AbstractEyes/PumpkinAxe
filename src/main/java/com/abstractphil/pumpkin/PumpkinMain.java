package com.abstractphil.pumpkin;

import com.abstractphil.pumpkin.controller.PumpkinDataController;
import org.bukkit.plugin.java.JavaPlugin;

import com.abstractphil.pumpkin.commands.PumpkinCommand;

import lombok.Getter;

@Getter
public class PumpkinMain extends JavaPlugin {
	private PumpkinDataController mainController;

	@Override
	public void onEnable() {
		super.onEnable();
		this.mainController = new PumpkinDataController();
		this.mainController.init();
		System.out.println("Initialized pumpkin axe plugin.");
		getCommand("pumpkin").setExecutor(new PumpkinCommand());
	}

	@Override
	public void onDisable() {
		mainController.terminate();
		super.onDisable();
	}

	public static PumpkinMain getInstance() {
		return JavaPlugin.getPlugin(PumpkinMain.class);
	}
}
