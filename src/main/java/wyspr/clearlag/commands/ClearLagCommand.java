package wyspr.clearlag.commands;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
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
		if (sender.isConsole()) return false;

		EntityPlayer p = sender.getPlayer();
		LagSource target = LagSource.ALL;
		int range = 0;

		switch (args.length) {
			case 0:
				break;
			case 1:
				try {
					range = Integer.parseInt(args[0]);
				} catch (NumberFormatException ignored) {
					// Ignore because we have default value
				}
				break;
			case 2:
				try {
					target = LagSource.valueOf(args[0].toUpperCase());
				} catch (IllegalArgumentException ignored) {
					// Ignore because we have default value
				}
				try {
					range = Integer.parseInt(args[1]);
				} catch (NumberFormatException ignored) {
					// Ignore because we have default value
				}
				break;
			default:
				EntityPlayer targetPlayer = handler.getPlayer(args[0]);
				if (targetPlayer == null) {
					throw new CommandError("Player not found!");
				}
				p = targetPlayer;
				try {
					target = LagSource.valueOf(args[1].toUpperCase());
				} catch (IllegalArgumentException ignored) {
					// Ignore because we have default value
				}
				try {
					range = Integer.parseInt(args[2]);
				} catch (NumberFormatException ignored) {
					// Ignore because we have default value
				}
		}

		int centerChunkX = p.chunkCoordX;
		int centerChunkZ = p.chunkCoordZ;
		AABB area = new AABB(
			16.0 * (centerChunkX - range),
			0.0,
			16.0 * (centerChunkZ - range),
			16.0 * (1 + centerChunkX + range),
			255.0,
			16.0 * (1 + centerChunkZ + range)
		);
		World world = handler.getWorld(p);

		List<Entity> entities = null;

		switch (target) {
			case ITEMS:
				entities = world.getEntitiesWithinAABB(EntityItem.class, area);
				break;
			case MOBS:
				entities = world.getEntitiesWithinAABB(EntityLiving.class, area);
				break;
			case ALL:
				entities = world.getEntitiesWithinAABB(EntityItem.class, area);
				entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, area));
				break;
		}

		entities = entities.stream()
			.filter(e -> !( e instanceof EntityPlayer))
			.filter(e -> !( e instanceof EntityLiving && !((EntityLiving) e).nickname.isEmpty() ))
			.filter(e -> !( e instanceof EntityWolf && ((EntityWolf) e).isWolfTamed() ))
			.filter(e -> !( e instanceof EntityPig && ((EntityPig) e).getSaddled() ))
			.collect(Collectors.toList());

		boolean debugMode = false;
		int entityAmount = entities.size();
		for (Entity e : entities) {
			if (debugMode) {
				String entityName = e.getClass()
					.getSimpleName()
					.replace("Entity", "");

				String info = String.format("Removed %-8s at: %.1f, %.1f, %.1f", entityName, e.x, e.y, e.z);
				ClearLag.log(info);
			}
			e.remove();
		}

		String targetString = target.toString().toLowerCase();
		sender.sendMessage("§eRemoved §4" + entityAmount + " " + targetString + "§e in a §4" + range + "§e chunk radius.");

		return true;
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
