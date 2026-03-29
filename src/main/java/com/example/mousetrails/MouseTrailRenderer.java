package com.example.mousetrails;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class MouseTrailRenderer {
    private static final List<TrailPoint> trailPoints = new ArrayList<>();
    private static int trailLength = 20; // Length of the trail
    private static float red = 0.5f;
    private static float green = 0.8f;
    private static float blue = 1.0f;
    private static float alpha = 0.7f;

    public static void updateTrail(double mouseX, double mouseY) {
        // Get current screen position in world coordinates
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Convert screen coordinates to world coordinates
        double scaledWidth = client.getWindow().getScaledWidth();
        double scaledHeight = client.getWindow().getScaledHeight();

        // Calculate normalized device coordinates (-1 to 1)
        double ndcX = (mouseX / scaledWidth) * 2.0 - 1.0;
        double ndcY = 1.0 - (mouseY / scaledHeight) * 2.0; // Flip Y axis

        // Convert to world coordinates based on player's view
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        double worldX = cameraPos.x + ndcX * 5; // Scale factor to make trail visible
        double worldY = cameraPos.y - ndcY * 5; // Invert Y for proper orientation
        double worldZ = cameraPos.z;

        trailPoints.add(0, new TrailPoint(worldX, worldY, worldZ));

        // Limit the trail length
        if (trailPoints.size() > trailLength) {
            trailPoints.remove(trailPoints.size() - 1);
        }
    }

    public static void renderTrails() {
        if (trailPoints.size() < 2) return;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(2.0f);

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // Draw the trail as connected lines
        for (int i = 0; i < trailPoints.size() - 1; i++) {
            TrailPoint current = trailPoints.get(i);
            TrailPoint next = trailPoints.get(i + 1);

            // Calculate transparency based on position in trail (fade out)
            float fadeFactor = (float) i / (float) trailPoints.size();
            float a = alpha * fadeFactor;

            bufferBuilder.vertex(current.x, current.y, current.z).color(red, green, blue, a).next();
            bufferBuilder.vertex(next.x, next.y, next.z).color(red, green, blue, a).next();
        }

        tessellator.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void clearTrail() {
        trailPoints.clear();
    }

    // Inner class to represent a point in the trail
    private static class TrailPoint {
        public final double x, y, z;

        public TrailPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}