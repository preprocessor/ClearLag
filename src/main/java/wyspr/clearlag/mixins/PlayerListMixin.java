package wyspr.clearlag.mixins;

import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.packet.Packet138PlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.util.helper.PlayerList;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import wyspr.clearlag.ClearLag;
import wyspr.clearlag.utils.TPSTracker;

@Mixin(value = PlayerList.class, remap = false)
public class PlayerListMixin {
	/**
	 * @author wyspr
	 * @reason Injection was adding more overhead than necessary
	 */
	@Overwrite
	public static void updateList() {
        MinecraftServer server = MinecraftServer.getInstance();
        int playerCount = server.playerList.playerEntities.size();
		int newCount = playerCount + 1;
        String[] players = new String[newCount];
        String[] scores = new String[newCount];

		int maxPlayer = 0;
		int maxScore = 0;

        for (int i = 0; i < playerCount; ++i) {
            EntityPlayerMP player = server.playerList.playerEntities.get(i);
			String displayName = player.getDisplayName();
			String score = String.valueOf(player.getScore());

			players[i] = displayName;
			scores[i] = score;

			String strippedPlayer = displayName
				.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "");

			if (strippedPlayer.length() > maxPlayer) {
				maxPlayer = strippedPlayer.length();
			}
			if (score.length() > maxScore) {
				maxScore = score.length();
			}
        }

		if (maxPlayer >= 4) maxPlayer++;

		double tpsValue = ClearLag.tpsTracker.tps();
		TextFormatting tpsColor = TPSTracker.color(tpsValue);

		String tps = StringUtils.leftPad(tpsValue + "", (int) ( maxPlayer * 1.2 ));
		players[playerCount] = tpsColor + tps;
		scores[playerCount] = StringUtils.rightPad("TPS", (int) ( maxScore * 1.2 ));

        server.playerList.sendPacketToAllPlayers(new Packet138PlayerList(newCount, players, scores));
    }
}
