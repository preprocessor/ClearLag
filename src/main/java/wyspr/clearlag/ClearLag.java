package wyspr.clearlag;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.Objects;


public class ClearLag implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "ClearLag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        log("ClearLag initialized.");
    }

	public static void log(String s) {
		LOGGER.info(s);
		System.out.println(s);
	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}

	public enum LagSource {
		ITEMS,
		MOBS,
//		REDSTONE,
		ALL {
			@Override
			public String toString() { return "entities"; }
		}
	}
}
