package wyspr.clearlag.mixins;

import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Season;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = WeatherManager.class, remap = false)
public class WeatherManagerMixin {

	@Shadow
	private World world;
	@Shadow
	private Weather currentWeather;
	@Shadow
	private Weather nextWeather;
	@Shadow
	private float weatherIntensity;
	@Shadow
	private float weatherPower;
	@Shadow
	private long weatherDuration;

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public boolean tick() {
		WorldType worldType = this.world.worldType;

		if (worldType.hasCeiling()
		|| this.world.seasonManager.getCurrentSeason() == null) {
			return false;
		}
		if (this.currentWeather == null) {
			this.currentWeather = worldType.getDefaultWeather();
		}
		if (this.nextWeather == null) {
			if (this.weatherIntensity <= 1.0f) {
				this.weatherIntensity = Math.min(this.weatherIntensity + 0.002f, 1.0f);
			}
		} else {
			if (this.weatherIntensity >= 0.0f) {
				this.weatherIntensity -= 0.002f;
				if (this.weatherIntensity == 0.0f) {
					this.currentWeather = this.nextWeather;
					this.weatherPower = this.getRandomWeatherPower();
					this.nextWeather = null;
				}
			}
		}

		if (--this.weatherDuration <= 0L) {
			if (this.currentWeather != worldType.getDefaultWeather()) {
				this.nextWeather = worldType.getDefaultWeather();
			} else {
				Season season = this.world.seasonManager.getCurrentSeason();
				float acc = 0.0f;
				float val = this.world.rand.nextFloat();
				for (Weather weather : season.allowedWeathers) {
					acc += season.weatherProbability.get(weather);
					if (val < acc) {
						this.nextWeather = weather;
						break;
					}
				}
				if (this.nextWeather == null) {
					this.nextWeather = worldType.getDefaultWeather();
				}
			}
			this.weatherDuration = this.getRandomWeatherDuration();
		}
		this.world.dimensionData.setCurrentWeather(this.currentWeather);
		this.world.dimensionData.setNextWeather(this.nextWeather);
		this.world.dimensionData.setWeatherDuration(this.weatherDuration);
		this.world.dimensionData.setWeatherIntensity(this.weatherIntensity);
		this.world.dimensionData.setWeatherPower(this.weatherPower);
		return true;
	}

	@Shadow
	private long getRandomWeatherDuration() {return 0;}
	@Shadow
	private float getRandomWeatherPower() {return 0;}
}
