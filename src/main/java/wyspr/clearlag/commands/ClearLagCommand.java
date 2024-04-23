package wyspr.clearlag.commands;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.animal.EntityPig;
import net.minecraft.core.entity.animal.EntityWolf;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandError;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.WorldServer;
import wyspr.clearlag.ClearLag;
import wyspr.clearlag.ClearLag.LagSource;

import java.util.List;
import java.util.stream.Collectors;

public class ClearLagCommand extends Command {
	public ClearLagCommand() {
		super("clearlag", "cl", "lag");
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.isConsole() && args.length < 3)
			throw new CommandError("You must target a player");

		EntityPlayer p = sender.getPlayer();
		LagSource lagSource = LagSource.ALL;
		int range = 0;

		switch (args.length) {
			case 0:
				break;
			case 1:
				range = getRange(args[0], range);
				break;
			case 2:
				lagSource = getLagSource(args[0], lagSource);
				range = getRange(args[1], range);
				break;
			default:
				EntityPlayer targetPlayer = handler.getPlayer(args[0]);
				if (targetPlayer == null) {
					throw new CommandError("Player not found!");
				}
				p = targetPlayer;
				lagSource = getLagSource(args[1], lagSource);
				range = getRange(args[2], range);
		}


		AABB targetArea = getTargetArea(p, range);
		Class<? extends Entity> targetClass = lagSource.getMCClass();
//		MinecraftServer mc = MinecraftServer.getInstance();
//		WorldServer world = mc.getDimensionWorld(p.dimension);
		World world = p.world;

		List<Entity> entities = world.getEntitiesWithinAABB(targetClass, targetArea).stream()
			.filter(e -> !( e instanceof EntityPlayer))
			.filter(e -> !( e instanceof EntityLiving && !((EntityLiving) e).nickname.isEmpty() ))
			.filter(e -> !( e instanceof EntityWolf && ((EntityWolf) e).isWolfTamed() ))
			.filter(e -> !( e instanceof EntityPig && ((EntityPig) e).getSaddled() ))
			.collect(Collectors.toList());

        for (Entity e: entities) world.removePlayer(e);

		String targetString = lagSource.toString().toLowerCase();
		sender.sendMessage("§eRemoved §4" + entities.size() + " " + targetString + "§e in a §4" + range + "§e chunk radius.");

		return true;
	}

	private static AABB getTargetArea(EntityPlayer p, int range) {
		int centerChunkX = p.chunkCoordX;
		int centerChunkZ = p.chunkCoordZ;
		return new AABB(
			16.0 * (centerChunkX - range),
			0.0,
			16.0 * (centerChunkZ - range),
			16.0 * (1 + centerChunkX + range),
			255.0,
			16.0 * (1 + centerChunkZ + range)
		);
	}

	private static LagSource getLagSource(String s, LagSource lagSource) {
		try {
			lagSource = LagSource.valueOf(s.toUpperCase());
		} catch (IllegalArgumentException ignored) {
			// Ignore because we have default value
		}
		return lagSource;
	}

	private static int getRange(String s, int range) {
		try {
			range = Integer.parseInt(s);
		} catch (NumberFormatException ignored) {
			// Ignore because we have default value
		}
		return range;
	}

	@Override
	public boolean opRequired(String[] strings) {
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {
		sender.sendMessage("/lag [chunkRange]");
		sender.sendMessage("/lag [lagType] [chunkRange]");
		sender.sendMessage("/lag [player] [lagType] [chunkRange]");
		sender.sendMessage("Lag types:");
		sender.sendMessage("  mobs, items, all");
		sender.sendMessage("Default values:");
		sender.sendMessage("  Range = 0");
		sender.sendMessage("  Type = all");
	}
}
