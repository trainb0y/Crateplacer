package petercrawley.starlegacy.crateplacer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.StickyKeyBinding;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.glfw.GLFW;

public class Main implements ModInitializer {

	public static KeyBinding keyBind;

	@Override public void onInitialize() {
		// Register Keybind to toggle mod
		keyBind = KeyBindingHelper.registerKeyBinding(new StickyKeyBinding(
				"key.crateplacer.toggle",
				GLFW.GLFW_KEY_RIGHT_BRACKET,
				"category.crateplacer",
				() -> true
		));

		// Register Tick Action
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// If there is no world we will crash if we try to do anything, check if there is a world or not.
			if (MinecraftClient.getInstance().world == null) {
				keyBind.setPressed(false);
				return;
			}

			// If enabled
			if (keyBind.isPressed()) {
				// Determine the size of the area that is in player reach
				int xmin = (int) MinecraftClient.getInstance().player.getX() - 5;
				int xmax = (int) MinecraftClient.getInstance().player.getX() + 5;
				int ymin = (int) MinecraftClient.getInstance().player.getY() - 5;
				int ymax = (int) MinecraftClient.getInstance().player.getY() + 5;
				int zmin = (int) MinecraftClient.getInstance().player.getZ() - 5;
				int zmax = (int) MinecraftClient.getInstance().player.getZ() + 5;

				// Loop though every block in player reach
				for (int x = xmin; x <= xmax; x++) {
					for (int y = ymin; y <= ymax; y++) {
						for (int z = zmin; z <= zmax; z++) {

							// If the block is a sticky piston
							if (MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).getBlock().is(Blocks.STICKY_PISTON)) {
								// Move to 9th Hotbar Slot
								MinecraftClient.getInstance().player.inventory.selectedSlot = 8;

								// If we are not holding a crate
								if (!MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND).getItem().getTranslationKey().endsWith("shulker_box")) {
									for (int invSlot = 0; invSlot <= 40; invSlot++) {
										if (MinecraftClient.getInstance().player.inventory.getStack(invSlot).getItem().getTranslationKey().endsWith("shulker_box")) {
											MinecraftClient.getInstance().interactionManager.pickFromInventory(invSlot);
										}
									}
								}

								// Figure out the block we need to place the shulker relative to the piston
								Vec3i addPos = MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).get(Properties.FACING).getVector();

								// Determine crate position
								Vec3d pos = new Vec3d(x + addPos.getX(), y + addPos.getY(), z + addPos.getZ());

								// If there is not already a shulker there
								if (!MinecraftClient.getInstance().world.getBlockState(new BlockPos(pos)).getBlock().isIn(BlockTags.SHULKER_BOXES)) {
									// Place the block
									MinecraftClient.getInstance().interactionManager.interactBlock(
											MinecraftClient.getInstance().player,
											MinecraftClient.getInstance().world,
											Hand.MAIN_HAND,
											new BlockHitResult(pos, MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).get(Properties.FACING), new BlockPos(pos), true)
									);

									return; // Don't do more then 1 per tick, its laggy and the server will hate you
								}
							}
						}
					}
				}
			}
		});

		// Finish
		System.out.println("Crateplacer Started");
	}

	// Alias for printing to chat, for debugging only
	public static void print(Object str) {
		str = String.valueOf(str);
		if (str == "") return;
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(String.valueOf(str)));
	}
}