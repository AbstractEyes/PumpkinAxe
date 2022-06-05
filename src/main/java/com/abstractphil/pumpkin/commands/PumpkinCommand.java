package com.abstractphil.pumpkin.commands;

import com.abstractphil.pumpkin.PumpkinMain;
import com.abstractphil.pumpkin.effects.AbstractAxeEffect;
import com.abstractphil.pumpkin.util.AbsPhilItemUtils;
import com.redmancometh.reditems.RedItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PumpkinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp())
			return false;
		switch(args[0]) {
			case "create":
				return createPumpkinAxe(sender, args);
			case "levelup":
				return levelAttachEffect(sender, args);
			case "debug":
				return debugAxe(sender, args);
		}
		if(sender instanceof Player && sender.isOp()) {
			sender.sendMessage("Your command was invalid.");
			sender.sendMessage("pumpkin <create/levelup/debug> <player> <args>");
		}
		return false;
	}

	private boolean createPumpkinAxe(CommandSender sender, String[] args){
		Player player = Bukkit.getPlayer(args[1]);
		if(player != null)  {
			AbsPhilItemUtils utils =  PumpkinMain.getInstance().getMainController().getUtils();
			ItemStack stack = utils.createPumpkinAxe(player);
			player.getInventory().addItem(stack);
			if(sender instanceof Player && sender.isOp()){
				sender.sendMessage("OP rewarded Pumpkin Axe successfully added to " + player.getName() + "'s inventory!");
			}
			return true;
		} else {
			sender.sendMessage("Player " + args[1] + " does not exist.");
			return false;
		}
	}

	private boolean levelAttachEffect(CommandSender sender, String[] args) {
		Player player = Bukkit.getPlayer(args[1]);
		System.out.println(args[1] + " " + player);
		AbsPhilItemUtils utils = PumpkinMain.getInstance().getMainController().getUtils();
		if(player != null && utils.getPumpkinAxe(player) != null) {
			try {
				//player.setItemInHand(RedItems.getInstance().getEnchantManager().attachEffect(getPumpkinAxe(player), "psell", 1));
				ItemStack item =  utils.getPumpkinAxe(player);
				item = utils.levelPumpkinAxeAttachment(player, item, args[2], Integer.parseInt(args[3]));
				utils.commitReplaceHeldItem(player, item);
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	private int getPumpkinAxeIndexPosition(Player player) {
		if(player != null) {
			AbsPhilItemUtils utils =  PumpkinMain.getInstance().getMainController().getUtils();
			ItemStack[] list = player.getInventory().getContents();
			for (int i = 0; i < list.length; i++) {
				ItemStack item = list[i];
				if (utils.isPumpkinAxe(item)) return i;
			}
		}
		return -1;
	}

	private boolean debugAxe(CommandSender sender, String[] args) {
		Player player = Bukkit.getPlayer(args[1]);
		if (player != null) {
			try {
				AbsPhilItemUtils utils = PumpkinMain.getInstance().getMainController().getUtils();
				System.out.println(args[1] + " " + player.getName());
				player.sendMessage("Pumpkin Axe Attachments: ");
				if(utils.getPumpkinAxe(player) != null) {
					System.out.println(RedItems.getInstance().getEnchantManager().getData( utils.getPumpkinAxe(player)));
					RedItems.getInstance().getEnchantManager().getEffects( utils.getPumpkinAxe(player)).forEach( effect -> {
						System.out.println((AbstractAxeEffect)effect.getEffect());
					});
					System.out.println("Dumping internal statistics: " );
					System.out.println(utils.getJsonStatistics(utils.getPumpkinAxe(player)));
				}
				return true;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

}
