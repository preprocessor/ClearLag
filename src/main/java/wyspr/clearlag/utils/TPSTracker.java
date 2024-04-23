package wyspr.clearlag.utils;

import net.minecraft.core.net.command.TextFormatting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Queue;

public class TPSTracker {
	private static final long MAX_DURATION = 60000; // 60 seconds
	private final Queue<Long> tickTimes = new LinkedList<>();
	private long lastTickTime = 0;

	public void tick() {
		long now = System.currentTimeMillis();
		if (lastTickTime > 0) {
			tickTimes.add(now);
			// Remove old ticks beyond the time window
			while (!tickTimes.isEmpty() && (now - tickTimes.peek() > MAX_DURATION)) {
				tickTimes.poll();
			}
		}
		lastTickTime = now;
	}

	public double tps() {
		if (tickTimes.size() <= 1) {
			return 0.0;
		} else {
			long earliestTick = tickTimes.peek();
			long latestTick = lastTickTime;
			long duration = latestTick - earliestTick;
			if (duration == 0) return 0.0;
			double tps = Math.min(tickTimes.size() * 1000.0 / duration, 20);
			BigDecimal bd = new BigDecimal(Double.toString(tps));
			bd = bd.setScale(1, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}

	public static TextFormatting color(double tps) {
		int tpsColorRange = ( (int) tps - 1 ) / 4;
		TextFormatting tpsColor = TextFormatting.LIME;
		switch (tpsColorRange) {
			case 0:
				tpsColor = TextFormatting.RED;
				break;
			case 1:
				tpsColor = TextFormatting.ORANGE;
				break;
			case 2:
				tpsColor = TextFormatting.YELLOW;
				break;
			case 3:
				tpsColor = TextFormatting.GREEN;
				break;
			case 4:
				tpsColor = TextFormatting.LIME;
				break;
			default: break;
		}
		return tpsColor;
	}
}




