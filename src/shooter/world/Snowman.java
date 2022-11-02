package shooter.world;

import processing.core.PConstants;
import shooter.GameConstants;
import shooter.GameSketch;
import shooter.StaticColor;

import static shooter.GameMath.biRand;

public class Snowman extends WorldObject implements GameConstants
{
    private static final float
            EDGE_MARGIN = 6.f,
            OFFSET_RANGE = (WorldChunk.CHUNK_H - EDGE_MARGIN) / 2.f,
            SCALE = 0.5f;

    // private static final PImage
    //         BODY_TEXTURE = Textures.SNOWMAN_BODY;

    private static final int
            BODY_FILL = StaticColor.color(215.f / 256.f, 240.f / 256.f, 238.f / 256.f),
            HAT_FILL = 0x101010;

    public Snowman(WorldTile[] chunkTiles)
    {
        super (
                biRand(OFFSET_RANGE),
                biRand(OFFSET_RANGE),
                (float)Math.random() * TWO_PI
        );
        int tileX = (int)(WorldChunk.CHUNK_W / 2.f + this.X_OFF);
        int tileY = (int)(WorldChunk.CHUNK_H / 2.f + this.Y_OFF);
        this.BASE_Z = chunkTiles[tileX + tileY * WorldChunk.CHUNK_W].getHeight();
    }

    public void draw(GameSketch gs)
    {
        gs.pushMatrix();
        gs.translate(X_OFF, Y_OFF, BASE_Z);
        gs.scale(SCALE, SCALE, SCALE);
        gs.rotateZ(Z_ANGLE);
        // bottom ball
        gs.pushMatrix();
        gs.translate(0.f, 0.f, 0.5f);
        gs.scale(0.5f, 0.5f, 0.5f);
        drawBall(gs);
        gs.popMatrix();
        // top ball
        gs.pushMatrix();
        gs.translate(0.f, 0.f, 4.f / 3.f);
        gs.scale(1.f / 3.f, 1.f / 3.f, 1.f / 3.f);
        drawBall(gs);
        gs.popMatrix();
        // hat
        gs.pushMatrix();
        gs.translate(0.f, 0.f, 11.f / 6.f);
        gs.scale(1.f / 6.f, 1.f / 6.f, 1.f / 6.f);
        drawHat(gs);
        gs.popMatrix();

        gs.popMatrix();
    }

    private void drawHat(GameSketch gs)
    {
        gs.fill(HAT_FILL);

        // draw sides of hat
        drawVerticalStrip(gs, 1.f);

        // draw top of hat
        gs.beginShape(PConstants.TRIANGLE_FAN);
        gs.vertex(0.f, 0.f, 1.f);

        gs.vertex(-1.f, 0.5f, 1.f);
        gs.vertex(-1.f, -0.5f, 1.f);
        gs.vertex(-0.5f, -1.f, 1.f);
        gs.vertex(0.5f, -1.f, 1.f);
        gs.vertex(1.f, -0.5f, 1.f);
        gs.vertex(1.f, 0.5f, 1.f);
        gs.vertex(0.5f, 1.f, 1.f);
        gs.vertex(-0.5f, 1.f, 1.f);

        gs.endShape();
        gs.noFill();
    }

    // draw one of the snowman's snowballs
    private void drawBall(GameSketch gs)
    {
        gs.fill(BODY_FILL);

        // draw top square
        gs.beginShape(PConstants.TRIANGLE_STRIP);
        gs.vertex(-0.5f, 0.5f, 1.f, 0.f, 0.f);
        gs.vertex(-0.5f, -0.5f, 1.f, 0.f, 1.f);
        gs.vertex(0.5f, 0.5f, 1.f, 1.f, 0.f);
        gs.vertex(0.5f, -0.5f, 1.f, 1.f, 1.f);
        gs.endShape();

        // draw upper strip
        drawDiagonalStrip(gs, 1.f);

        // draw middle strip
        drawVerticalStrip(gs, 0.5f);
        /*
        gs.beginShape(PConstants.TRIANGLE_STRIP);
        vertStack(gs, -1.f, 0.5f, 0.5f, 0.f);
        vertStack(gs, -1.f, -0.5f, 0.5f, 1.f);

        vertStack(gs, -0.5f, -1.f, 0.5f, 0.f);
        vertStack(gs, 0.5f, -1.f, 0.5f, 1.f);

        vertStack(gs, 1.f, -0.5f, 0.5f, 0.f);
        vertStack(gs, 1.f, 0.5f, 0.5f, 1.f);

        vertStack(gs, 0.5f, 1.f, 0.5f, 0.f);
        vertStack(gs, -0.5f, 1.f, 0.5f, 1.f);
        gs.vertex(-1.f, 0.5f, 0.5f, 0.f, 0.f);
        gs.endShape();
         */

        // draw lower strip
        drawDiagonalStrip(gs, -1.f);

        // draw bottom square
        gs.beginShape(PConstants.TRIANGLE_STRIP);
        gs.vertex(-0.5f, 0.5f, -1.f, 0.f, 0.f);
        gs.vertex(-0.5f, -0.5f, -1.f, 0.f, 1.f);
        gs.vertex(0.5f, 0.5f, -1.f, 1.f, 0.f);
        gs.vertex(0.5f, -0.5f, -1.f, 1.f, 1.f);
        gs.endShape();

        gs.noFill();
    }

    private void drawDiagonalStrip(GameSketch gs, float zOrientation)
    {
        gs.beginShape(PConstants.TRIANGLE_STRIP);
        // left rectangle
        gs.vertex(-1.f, 0.5f, 0.5f * zOrientation);
        gs.vertex(-0.5f, 0.5f, 1.f * zOrientation);
        gs.vertex(-1.f, -0.5f, 0.5f * zOrientation);
        gs.vertex(-0.5f, -0.5f, 1.f * zOrientation);
        // bottom-left triangle
        gs.vertex(-0.5f, -1.f, 0.5f * zOrientation);
        // bottom rectangle
        gs.vertex(0.5f, -0.5f, 1.f * zOrientation);
        gs.vertex(0.5f, -1.f, 0.5f * zOrientation);
        // bottom-right triangle
        gs.vertex(1.f, -0.5f, 0.5f * zOrientation);
        gs.vertex(0.5f, -0.5f, 1.f * zOrientation);
        // right rectangle
        gs.vertex(1.f, 0.5f, 0.5f * zOrientation);
        gs.vertex(0.5f, 0.5f, 1.f * zOrientation);
        // upper-right triangle
        gs.vertex(0.5f, 1.f, 0.5f * zOrientation);
        // upper rectangle
        gs.vertex(-0.5f, 0.5f, 1.f * zOrientation);
        gs.vertex(-0.5f, 1.f, 0.5f * zOrientation);
        // upper-left triangle
        gs.vertex(-1.f, 0.5f, 0.5f * zOrientation);
        gs.endShape();
    }

    private void drawVerticalStrip(GameSketch gs, float z)
    {
        gs.beginShape(PConstants.TRIANGLE_STRIP);
        vertStack(gs, -1.f, 0.5f, z, 0.f);
        vertStack(gs, -1.f, -0.5f, z, 1.f);

        vertStack(gs, -0.5f, -1.f, z, 0.f);
        vertStack(gs, 0.5f, -1.f, z, 1.f);

        vertStack(gs, 1.f, -0.5f, z, 0.f);
        vertStack(gs, 1.f, 0.5f, z, 1.f);

        vertStack(gs, 0.5f, 1.f, z, 0.f);
        vertStack(gs, -0.5f, 1.f, z, 1.f);
        gs.vertex(-1.f, 0.5f, z, 0.f, 0.f);
        gs.endShape();
    }

    private void vertStack(GameSketch gs, float x, float y, float z, float u)
    {
        gs.vertex(x, y, z, u, 0.f);
        gs.vertex(x, y, -z, u, 1.f);
    }
}
