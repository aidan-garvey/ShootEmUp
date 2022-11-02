package shooter.actors;

import processing.core.PImage;
import shooter.Textures;

public class PlayerBullet extends Bullet
{
    private static final int
            PB_FRAMES = 1;
    private static final float
            PB_ANIM_LENGTH = 1.f;

    private static final float[] PB_SIZE = {0.04f, 0.025f, 0.f};

    public PlayerBullet(float[] p, float[] v, float[] a)
    {
        super(p, v, a, PB_FRAMES, PB_ANIM_LENGTH,
                new PImage[]{Textures.PLAYER_BULLET},
                PB_SIZE);
    }
}
