package shooter.world;

import processing.core.PImage;
import shooter.*;

import static processing.core.PConstants.TRIANGLE_FAN;
import static shooter.GameMath.biRand;

public class Pyramid extends WorldObject implements GameConstants
{
    private static final float
            EDGE_MARGIN = 4.f,
            OFFSET_RANGE = (WorldChunk.CHUNK_H - EDGE_MARGIN) / 2.f,
            BASE_ALT = 0.f,
            TOP_MIN = 2.f,
            TOP_MAX = 4.f;

    private final float
            HEIGHT,
            SIDE_LEN;

    private static final PImage PYRAMID_TEX = Textures.DESERT_PYRAMID;

    public Pyramid()
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
        this.HEIGHT = super.TOP_Z - BASE_ALT;
        this.SIDE_LEN = this.HEIGHT * ROOT_TWO;
    }

    public void draw(GameSketch gs)
    {
        gs.pushMatrix();
        gs.translate(X_OFF, Y_OFF, 0.f);
        gs.rotateZ(Z_ANGLE);

        gs.beginShape(TRIANGLE_FAN);
        gs.texture(PYRAMID_TEX);

        gs.vertex(0.f,     0.f,     TOP_Z,    HEIGHT,       0.f);
        gs.vertex(-HEIGHT, HEIGHT,  BASE_ALT, 0.f,          SIDE_LEN);
        gs.vertex(-HEIGHT, -HEIGHT, BASE_ALT, HEIGHT * 2.f, SIDE_LEN);
        gs.vertex(HEIGHT,  -HEIGHT, BASE_ALT, 0.f,          SIDE_LEN);
        gs.vertex(HEIGHT,  HEIGHT,  BASE_ALT, HEIGHT * 2.f, SIDE_LEN);
        gs.vertex(-HEIGHT, HEIGHT,  BASE_ALT, 0.f,          SIDE_LEN);

        gs.endShape();
        gs.popMatrix();
    }
}
