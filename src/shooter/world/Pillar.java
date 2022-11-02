package shooter.world;

import processing.core.PImage;
import shooter.*;

import static processing.core.PConstants.TRIANGLE_STRIP;
import static shooter.GameMath.biRand;

public class Pillar extends WorldObject implements GameConstants
{
    private static final float
            EDGE_MARGIN = 6.f,
            OFFSET_RANGE = (WorldChunk.CHUNK_H - EDGE_MARGIN) / 2.f,
            BASE_ALT = 0.f,
            TOP_MIN = 1.5f,
            TOP_MAX = 3.f,
            H_SCALE = 0.5f;

    private final float HEIGHT;

    private static final PImage
            PILLAR_TOP_TEX = Textures.DESERT_TILE,
            PILLAR_SIDE_TEX = Textures.DESERT_GLYPHS;

    public Pillar()
    {
        super (
                biRand(OFFSET_RANGE),
                biRand(OFFSET_RANGE),
                // BASE_ALT,
                // (float)(Math.random() * (TOP_MAX - TOP_MIN)) + TOP_MIN,
                (float)Math.random() * TWO_PI
        );
        this.BASE_Z = BASE_ALT;
        this.TOP_Z = (float)(Math.random() * (TOP_MAX - TOP_MIN)) + TOP_MIN;
        this.HEIGHT = super.TOP_Z - super.BASE_Z;
    }

    public void draw(GameSketch gs)
    {
        gs.pushMatrix();
        gs.translate(X_OFF, Y_OFF, 0.f);
        gs.scale(H_SCALE, H_SCALE, 1.f);
        gs.rotateZ(Z_ANGLE);

        gs.beginShape(TRIANGLE_STRIP);
        gs.texture(PILLAR_TOP_TEX);

        gs.vertex(-1.f, 1.f, TOP_Z, 0.f, 0.f);
        gs.vertex(-1.f, -1.f, TOP_Z, 0.f, 1.f);
        gs.vertex(1.f, 1.f, TOP_Z, 1.f, 0.f);
        gs.vertex(1.f, -1.f, TOP_Z, 1.f, 1.f);

        gs.endShape();

        gs.beginShape(TRIANGLE_STRIP);
        gs.texture(PILLAR_SIDE_TEX);

        gs.vertex(-1.f, 1.f, TOP_Z, 0.f, 0.f);
        gs.vertex(-1.f, 1.f, BASE_ALT, 0.f, HEIGHT);

        gs.vertex(-1.f, -1.f, TOP_Z, 1.f, 0.f);
        gs.vertex(-1.f, -1.f, BASE_ALT, 1.f, HEIGHT);

        gs.vertex(1.f, -1.f, TOP_Z, 0.f, 0.f);
        gs.vertex(1.f, -1.f, BASE_ALT, 0.f, HEIGHT);

        gs.vertex(1.f, 1.f, TOP_Z, 1.f, 0.f);
        gs.vertex(1.f, 1.f, BASE_ALT, 1.f, HEIGHT);

        gs.vertex(-1.f, 1.f, TOP_Z, 0.f, 0.f);
        gs.vertex(-1.f, 1.f, BASE_ALT, 0.f, HEIGHT);

        gs.endShape();
        gs.popMatrix();
    }
}
