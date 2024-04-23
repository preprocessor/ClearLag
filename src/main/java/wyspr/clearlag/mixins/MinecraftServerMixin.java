package wyspr.clearlag.mixins;

import net.minecraft.core.net.IUpdatePlayerListBox;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.core.net.packet.Packet4UpdateTime;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.EntityTracker;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.NetworkListenThread;
import net.minecraft.server.util.helper.PlayerList;
import net.minecraft.server.world.WorldServer;
import org.apache.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wyspr.clearlag.ClearLag;

import java.util.*;

@Mixin(value = MinecraftServer.class, remap = false)
public abstract class MinecraftServerMixin  {
	@Shadow
	public static HashMap field_6037_b;
	@Shadow
	int deathTime;
	@Shadow
	public WorldServer[] dimensionWorlds;
	@Shadow
	public PropertyManager propertyManager;
	@Shadow
	public net.minecraft.server.net.PlayerList playerList;
	@Shadow
	public EntityTracker[] entityTracker;
	@Shadow
	public NetworkListenThread networkServer;
	@Shadow
	private List field_9010_p; // IUpdatePlayerListBox
	@Shadow
	public static Logger logger;
	@Shadow
	private boolean serverRunning;
	@Shadow
	public boolean serverStopped;

	/**
	 * @author wyspr
	 * @reason
	 */
	@Overwrite
	public void run() {
		try {
			if (this.startServer()) {
				long startTime = System.currentTimeMillis();
				long timeSinceLastTick = 0L;
				int i = 0;
				while (this.serverRunning) {
					long currentTime = System.currentTimeMillis();
					long timeChange = currentTime - startTime;
					if (timeChange > 2000L) {
						logger.warn("Can't keep up! Did the system time change, or is the server overloaded?");
						timeChange = 2000L;
					}
					if (timeChange < 0L) {
						logger.warn("Time ran backwards! Did the system time change?");
						timeChange = 0L;
					}
					timeSinceLastTick += timeChange;
					startTime = currentTime;
					if (this.dimensionWorlds[0].areEnoughPlayersFullyAsleep()) {
						this.doTick();
						timeSinceLastTick = 0L;
					} else {
						while (timeSinceLastTick > 10L) {
							timeSinceLastTick -= 10L;
							++i;
							for (EntityPlayerMP player : this.playerList.playerEntities) {
								player.tickSendChunks();
							}
							if (i % 5 != 0) continue;
							this.doTick();
							i = 0;
						}
					}
					Thread.sleep(1L);
				}
				return;
			} else {
				while (this.serverRunning) {
					this.commandLineParser();
					try {
						Thread.sleep(10L);
					} catch (InterruptedException interruptedexception) {
						interruptedexception.printStackTrace();
					}
				}
			}
			return;
		} catch (Throwable throwable1) {
			throwable1.printStackTrace();
			logger.error("Unexpected exception", throwable1);
			while (this.serverRunning) {
				this.commandLineParser();
				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedexception1) {
					interruptedexception1.printStackTrace();
				}
			}
			return;
		} finally {
			try {
				this.stopServer();
				this.serverStopped = true;
			} catch (Throwable throwable2) {
				throwable2.printStackTrace();
			} finally {
				System.exit(0);
			}
		}
	}



	@Unique
	private int playerListUpdate = 0;

	/**
	 * @author wyspr
	 * @reason speed
	 */
	@Overwrite
	private void doTick() {
		int i;
		Iterator<Map.Entry<String, Integer>> it = field_6037_b.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			int count = entry.getValue();
			if (count > 0) {
				entry.setValue(count - 1);
			} else {
				it.remove();
			}
		}
		AABB.initializePool();
		Vec3d.initializePool();
		++this.deathTime;
		for (i = 0; i < this.dimensionWorlds.length; ++i) {
			if (i == Dimension.nether.id && !this.propertyManager.getBooleanProperty("allow-nether", true) || i == Dimension.paradise.id && !this.propertyManager.getBooleanProperty("allow-paradise", false)) continue;
			WorldServer worldserver = this.dimensionWorlds[i];
			if (this.deathTime % 20 == 0) {
				this.playerList.sendPacketToAllPlayersInDimension(new Packet4UpdateTime(worldserver.getWorldTime()), worldserver.dimension.id);
			}
			worldserver.tick();
			while (worldserver.updatingLighting()) {}
			worldserver.updateEntities();
		}
		this.networkServer.handleNetworkListenThread();
		this.playerList.onTick();
		for (EntityTracker tracker : this.entityTracker) {
			tracker.tick();
		}
		for (Object o : this.field_9010_p) {
			((IUpdatePlayerListBox) o).update();
		}
		try {
			this.commandLineParser();
		} catch (Exception exception) {
			logger.warn("Unexpected exception while parsing console command", exception);
		}

		ClearLag.tpsTracker.tick();
		playerListUpdate++;
		if (playerListUpdate == 40) {
			playerListUpdate = 0;
			PlayerList.updateList();
		}
	}

	@Shadow
	public abstract void commandLineParser();
	@Shadow
	protected abstract boolean startServer();
	@Shadow
	protected abstract void stopServer();
}
