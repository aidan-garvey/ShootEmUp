package shooter.actors;

import shooter.GameConstants;
import shooter.particles.ParticleTracker;
import shooter.Textures;

public class EnemyGenerator implements GameConstants
{
    private static final float
            COLLISION = 0.12f,
            LEFT_X = -1.1f,
            RIGHT_X = 1.1f,
            BOT_Y = -2.f,
            TOP_Y = 2.5f,
            ENEMY_Z = 0.8f,

            DEG_15 = (float)Math.toRadians(15.0),
            DEG_30 = (float)Math.toRadians(30.0);
    private static final float[]
            SIZE = {0.12f, 0.12f, 0.12f},
            POS_Q0 = {LEFT_X, BOT_Y, ENEMY_Z},
            POS_Q1 = {RIGHT_X, BOT_Y, ENEMY_Z},
            POS_Q2 = {LEFT_X, TOP_Y, ENEMY_Z},
            POS_Q3 = {RIGHT_X, TOP_Y, ENEMY_Z},
            VEL_BASE = {0.f, 0.002f, 0.f},
            VEL_BOT = {0.f, 0.002f, 0.f},
            VEL_TOP = {0.f, -0.002f, 0.f},
            ANGLE_Q0 = {0.f, 0.f, HALF_PI},
            ANGLE_Q1 = {0.f, 0.f, HALF_PI},
            ANGLE_Q2 = {0.f, 0.f, -HALF_PI},
            ANGLE_Q3 = {0.f, 0.f, -HALF_PI},
            ROT_CW = {0.f, 0.f, -0.0002f},
            ROT_CCW = {0.f, 0.f, 0.0002f};

    private static int lastQuad = -1;

    public static Enemy generateEnemy(ParticleTracker particles)
    {
        // spawn in a random quadrant
        int quad = lastQuad;
        int attempts = 0;
        while (quad == lastQuad && attempts++ < 4)
            quad = (int)(Math.random() * 4);
        lastQuad = quad;

        Enemy e;
        switch (quad)
        {
            // lower left
            case 0 -> {
                e = new Enemy(particles, SIZE, COLLISION, Textures.ENEMY_BASE, 0, POS_Q0, ANGLE_Q0, VEL_BOT, ROT_CW);
            }
            // lower right
            case 1 -> {
                e = new Enemy(particles, SIZE, COLLISION, Textures.ENEMY_BASE, 0, POS_Q1, ANGLE_Q1, VEL_BOT, ROT_CCW);
            }
            // upper left
            case 2 -> {
                e = new Enemy(particles, SIZE, COLLISION, Textures.ENEMY_BASE, 0, POS_Q2, ANGLE_Q2, VEL_TOP, ROT_CCW);
            }
            // upper right
            case 3 -> {
                e = new Enemy(particles, SIZE, COLLISION, Textures.ENEMY_BASE, 0, POS_Q3, ANGLE_Q3, VEL_TOP, ROT_CW);
            }
            default -> throw new IllegalStateException("Unexpected value: " + quad);
        }
        return e;
    }
}
