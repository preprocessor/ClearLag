package wyspr.clearlag.commands;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.TextFormatting;
import wyspr.clearlag.ClearLag;
import wyspr.clearlag.utils.TPSTracker;

public class TPSCommand extends Command {
	public TPSCommand() {
		super("tps");
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] strings) {
		double tps = ClearLag.tpsTracker.tps();
		TextFormatting tpsColor = TPSTracker.color(tps);

		if (sender.isConsole()) {
			String formattedTPS = String.format("Current TPS: %.1f", tps);
			System.out.println(formattedTPS);
		} else {
			String formattedTPS = String.format("%sCurrent TPS: %s%.1f", TextFormatting.LIGHT_GRAY, tpsColor, tps);
			sender.sendMessage(formattedTPS);
		}
		return true;
	}

	@Override
	public boolean opRequired(String[] args) {
		return false;
	}

	@Override
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/tps");
	}
}
