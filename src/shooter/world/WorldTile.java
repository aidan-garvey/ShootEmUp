package shooter.world;

import processing.core.PImage;
import shooter.GameConstants;
import shooter.GameSketch;

import static processing.core.PConstants.TRIANGLE_STRIP;

import static shooter.StaticColor.color;

public class WorldTile implements GameConstants
{
    // indexes into surface and base for vertices (when viewed top-down)
    private static final int
        TOP_L = 0,
        BOT_L = 1,
        BOT_R = 2,
        TOP_R = 3;

    private int topColor, sideColor;
    public PImage topTexture, sideTexture;

    private final float[][] surface =
            {{-0.5f, 0.5f, 0.f}, // top left
            {-0.5f, -0.5f, 0.f}, // bottom left
            {0.5f, -0.5f, 0.f}, // bottom right
            {0.5f, 0.5f, 0.f}}; // top right
    private final float[][] base =
            {{-0.5f, 0.5f, -2.f}, // top left
            {-0.5f, -0.5f, -2.f}, // bottom left
            {0.5f, -0.5f, -2.f}, // bottom right
            {0.5f, 0.5f, -2.f}}; // top right

    // generate a flat tile with random height and colours
    public WorldTile()
    {
        topColor = color((float)Math.random(), (float)Math.random(), (float)Math.random());
        sideColor = color((float)Math.random(), (float)Math.random(), (float)Math.random());
        topTexture = null;
        sideTexture = null;

        float height = (float)Math.random() * 2.f;
        for (float[] v : surface)
            v[I_Z] = height;
    }

    // generate a flat tile with given height and textures
    public WorldTile(float z, PImage topTex, PImage sideTex)
    {
        topColor = 0;
        sideColor = 0;
        topTexture = topTex;
        sideTexture = sideTex;

        for (float[] v : surface)
            v[I_Z] = z;
    }

    public void setHeight(float z)
    {
        for (float[] v : surface)
            v[I_Z] = z;
    }

    public void increaseHeight(float z)
    {
        for (float[] v : surface)
            v[I_Z] = Math.max(v[I_Z], z);
    }

    public float getHeight()
    {
        float sum = 0.f;
        for (float[] v : surface)
            sum += v[I_Z];
        return sum / 4.f;
    }

    public void drawTop(GameSketch gs)
    {
        gs.fill(topColor);
        gs.beginShape(TRIANGLE_STRIP);
        gs.texture(topTexture);

        gs.vertex(surface[TOP_L][I_X], surface[TOP_L][I_Y], surface[TOP_L][I_Z], 0.f, 0.f);
        gs.vertex(surface[BOT_L][I_X], surface[BOT_L][I_Y], surface[BOT_L][I_Z], 0.f, 1.f);
        gs.vertex(surface[TOP_R][I_X], surface[TOP_R][I_Y], surface[TOP_R][I_Z], 1.f, 0.f);
        gs.vertex(surface[BOT_R][I_X], surface[BOT_R][I_Y], surface[BOT_R][I_Z], 1.f, 1.f);

        gs.endShape();
    }

    public void drawSides(GameSketch gs)
    {
        gs.fill(sideColor);
        gs.beginShape(TRIANGLE_STRIP);
        gs.texture(sideTexture);

        float height = surface[0][I_Z] - base[0][I_Z];

        // front face
        gs.vertex(surface[BOT_L][I_X], surface[BOT_L][I_Y], surface[BOT_L][I_Z], 0.f, 0.f);
        gs.vertex(base[BOT_L][I_X], base[BOT_L][I_Y], base[BOT_L][I_Z],          0.f, height);
        gs.vertex(surface[BOT_R][I_X], surface[BOT_R][I_Y], surface[BOT_R][I_Z], 1.f, 0.f);
        gs.vertex(base[BOT_R][I_X], base[BOT_R][I_Y], base[BOT_R][I_Z],          1.f, height);

        // right face
        gs.vertex(surface[TOP_R][I_X], surface[TOP_R][I_Y], surface[TOP_R][I_Z], 0.f, 0.f);
        gs.vertex(base[TOP_R][I_X], base[TOP_R][I_Y], base[TOP_R][I_Z],          0.f, height);

        // back face
        gs.vertex(surface[TOP_L][I_X], surface[TOP_L][I_Y], surface[TOP_L][I_Z], 1.f, 0.f);
        gs.vertex(base[TOP_L][I_X], base[TOP_L][I_Y], base[TOP_L][I_Z],          1.f, height);

        // left face
        gs.vertex(surface[BOT_L][I_X], surface[BOT_L][I_Y], surface[BOT_L][I_Z], 0.f, 0.f);
        gs.vertex(base[BOT_L][I_X], base[BOT_L][I_Y], base[BOT_L][I_Z],          0.f, height);

        gs.endShape();
    }
}
