package wyspr.clearlag.mixins;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wyspr.clearlag.commands.ClearLagCommand;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Commands.class, remap = false)
public final class CommandsMixin {
	@Shadow
	public static final List<Command> commands = new ArrayList<>();
	@Inject(method = "initCommands", at = @At("TAIL"))
	private static void initCommands(CallbackInfo ci) {
		commands.add(new ClearLagCommand());
	}
}
