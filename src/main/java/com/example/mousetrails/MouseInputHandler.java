package com.example.mousetrails;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class MouseInputHandler {
    private static double lastMouseX = -1;
    private static double lastMouseY = -1;
    private static boolean mouseMoved = false;

    public static void initialize() {
        // Register tick event to track mouse movement
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                // Get current mouse position
                long windowHandle = client.getWindow().getHandle();
                double[] xpos = new double[1];
                double[] ypos = new double[1];

                org.lwjgl.glfw.GLFW.glfwGetCursorPos(windowHandle, xpos, ypos);

                double currentX = xpos[0];
                double currentY = ypos[0];

                // Check if mouse has moved
                if (lastMouseX != currentX || lastMouseY != currentY) {
                    mouseMoved = true;
                    lastMouseX = currentX;
                    lastMouseY = currentY;

                    // Update trail with new mouse position
                    MouseTrailRenderer.updateTrail(currentX, currentY);
                } else {
                    mouseMoved = false;
                }
            }
        });

        // Register HUD render callback to draw the trail
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (MinecraftClient.getInstance().world != null) {
                MouseTrailRenderer.renderTrails();
            }
        });
    }
}