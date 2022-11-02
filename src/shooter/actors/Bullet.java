package shooter.actors;

import processing.core.PImage;
import shooter.GameSketch;

import static processing.core.PConstants.TRIANGLE_STRIP;

public abstract class Bullet extends FrameAnimatedActor
{
    private static final float
            // screen bounds for deleting bullets
            LEFT_BOUND = -2.5f,
            RIGHT_BOUND = 2.5f,
            TOP_BOUND = 2.5f,
            BOTTOM_BOUND = -2.f,
            // collision radius
            BULLET_COLLISION = 0.04f;

    public Bullet(float[] pos, float[] vel, float[] angle,
                  int numAnimFrames, float animLength,
                  PImage[] animFrames, float[] size)
    {
        super(size, BULLET_COLLISION, null,
                0, numAnimFrames, animLength, animFrames);
        this.animTimer = 0.f;
        for (int i = I_X; i <= I_Z; i++)
        {
            this.pos[i] = pos[i];
            this.vel[i] = vel[i];
            this.angle[i] = angle[i];
        }
    }

    public void update(float elapsedMillis)
    {
        animTimer = (animTimer + elapsedMillis) % animLength;
        for (int i = I_X; i <= I_Z; i++)
            pos[i] += vel[i] * elapsedMillis;
        if (pos[I_X] < LEFT_BOUND || pos[I_X] > RIGHT_BOUND || pos[I_Y] > TOP_BOUND || pos[I_Y] < BOTTOM_BOUND)
            this.dead = true;
        lastMillis = elapsedMillis;
    }

    public void draw(GameSketch gs)
    {
        gs.pushMatrix();
        gs.translate(pos[I_X], pos[I_Y], pos[I_Z]);
        gs.rotateZ(angle[I_Z]);
        gs.scale(size[I_X], size[I_Y], size[I_Z]);

        gs.beginShape(TRIANGLE_STRIP);
        gs.texture(getCurrTexture());

        gs.vertex(1.f, 1.f, 0.f, 1.f, 0.f);
        gs.vertex(1.f, -1.f, 0.f, 1.f, 1.f);
        gs.vertex(-1.f, 1.f, 0.f, 0.f, 0.f);
        gs.vertex(-1.f, -1.f, 0.f, 0.f, 1.f);

        gs.endShape();
        gs.popMatrix();
    }
}
