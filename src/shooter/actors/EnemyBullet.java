package shooter.actors;

import shooter.Textures;

public class EnemyBullet extends Bullet
{
    private static final int ANIM_FRAMES = 4;
    private static final float ANIM_LEN = 800.f;
    private static final float[] SIZE = {0.03f, 0.03f, 0.f};

    public EnemyBullet(float[] pos, float[] vel, float[] angle)
    {
        super(pos, vel, angle, ANIM_FRAMES, ANIM_LEN, Textures.ENEMY_BULLET, SIZE);
    }
}
