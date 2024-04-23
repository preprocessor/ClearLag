package wyspr.clearlag.mixins;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.LightUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin {
	@Shadow
	private int lightingUpdatesCounter;
	@Shadow
	private List<LightUpdate> lightingToUpdate;


}
