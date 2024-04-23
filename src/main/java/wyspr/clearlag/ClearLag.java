package wyspr.clearlag;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.vehicle.EntityBoat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import wyspr.clearlag.utils.TPSTracker;

public class ClearLag implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "ClearLag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static TPSTracker tpsTracker = new TPSTracker();
    @Override
    public void onInitialize() {
        log("ClearLag initialized.");
    }

	public static void log(String s) {
		LOGGER.info(s);
		System.out.println(s);
	}

	@Override
	public void beforeGameStart() {}
	@Override
	public void afterGameStart() {}
	@Override
	public void onRecipesReady() {}

	public enum LagSource {
		ITEMS,
		MOBS,
		BOAT,
		ALL {
			@Override
			public String toString() { return "entities"; }
		};

		public Class<? extends Entity> getMCClass() {
			Class<? extends Entity> MCClass = null;
			switch (this) {
                case ITEMS:
                    MCClass = EntityItem.class;
					break;
                case MOBS:
                    MCClass = EntityLiving.class;
					break;
				case BOAT:
					MCClass = EntityBoat.class;
					break;
				case ALL:
                    MCClass = Entity.class;
					break;
            }
			return MCClass;
        }
	}
}
