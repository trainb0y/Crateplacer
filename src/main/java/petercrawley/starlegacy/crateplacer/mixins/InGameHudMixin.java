package petercrawley.starlegacy.crateplacer.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import petercrawley.starlegacy.crateplacer.Main;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(at = @At("HEAD"), method = "render")
	private void render(CallbackInfo info) {
		// If mod active
		if (Main.keyBind.isPressed()) {
			MinecraftClient.getInstance().textRenderer.draw(new MatrixStack(), "Crate Placer Enabled", 8, 8, 0xFF55FF55); // Say the mod is active
		}
	}
}
