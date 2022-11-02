package shooter.particles;

import processing.core.PImage;
import shooter.GameConstants;
import shooter.Textures;

import static shooter.particles.ParticleEmitter.Type;
import static shooter.particles.LifespanParticle.Shape;

public class EffectFactory implements GameConstants
{
    /* * * * * * * * * * *
     * EXPLOSION  EFFECT *
     * * * * * * * * * * */
    // Large explosion consisting of red, orange and yellow spark particles.
    private static final float[]
            // position (pos parameter used for base)
            EXP_RAND_POS = {0.f, 0.f, 0.f},
            // velocity
            EXP_BASE_VEL = {0.f, 0.f, 0.f},
            EXP_RAND_VEL = {0.0036f, 0.0036f, 0.0036f},
            // acceleration
            EXP_BASE_ACC = {0.f, 0.f, 0.f},
            EXP_RAND_ACC = {0.f, 0.f, 0.f},
            // initial rotation
            EXP_BASE_ANGLE = {0.f, 0.f, 0.f},
            EXP_RAND_ANGLE = {PI, PI, PI},
            // rotation applied per frame
            EXP_BASE_ROT = {0.f, 0.f, 0.f},
            EXP_RAND_ROT = {0.f, 0.f, 0.f},
            // offset from base position
            EXP_POS_OFF = {0.f, 0.f, 0.f},
            EXP_BASE_SIZE = {0.02f, 0.02f, 0.02f},
            EXP_RAND_SIZE = {0.01f, 0.01f, 0.01f};

    private static final float
            // number of particles the emitter will spawn for the explosion
            EXP_NUM_PARTICLES = 150.f,
            // number of milliseconds before the explosion's particles (and the emitter) are deleted
            EXP_LIFETIME = 400.f;
    private static final int
            // base colour for particles
            EXP_BASE_COL = 0xE0A000,
            // each component of this value is used to randomize the components of the base colour
            EXP_RAND_COL = 0x1F5F00;
    private static final PImage EXP_TEX = Textures.SPARK;

    public static ParticleEmitter explosion(float[] pos)
    {
        return new ParticleEmitter (
                Type.BURST, Shape.DIAMOND, EXP_LIFETIME, EXP_LIFETIME,
                pos, EXP_RAND_POS, EXP_BASE_VEL, EXP_RAND_VEL,
                EXP_BASE_ACC, EXP_RAND_ACC, EXP_BASE_ANGLE, EXP_RAND_ANGLE,
                EXP_BASE_ROT, EXP_RAND_ROT, EXP_BASE_SIZE, EXP_RAND_SIZE,
                EXP_POS_OFF, EXP_TEX, EXP_BASE_COL, EXP_RAND_COL, EXP_NUM_PARTICLES);
    }

    /* * * * * * * * *
     * DEBRIS EFFECT *
     * * * * * * * * */
    // Cluster of grey triangles which have a constant downward
    // acceleration (gravity) and begin with the same base
    // velocity as the object which the debris replaces.
    private static final float[]
            // position (pos parameter used as the base)
            DEB_RAND_POS = {0.1f, 0.1f, 0.1f},
            // velocity (vel parameter used as the base)
            DEB_RAND_VEL = {0.0018f, 0.0018f, 0.0018f},
            // acceleration
            DEB_BASE_ACC = {0.f, 0.f, -0.00001f},
            DEB_RAND_ACC = {0.f, 0.f, 0.f},
            // initial rotation
            DEB_BASE_ANGLE = {0.f, 0.f, 0.f},
            DEB_RAND_ANGLE = {PI, PI, PI},
            // rotation applied per frame
            DEB_BASE_ROT = {0.f, 0.f, 0.f},
            DEB_RAND_ROT = {0.006f, 0.006f, 0.006f},
            // offset from base position
            DEB_POS_OFF = {0.f, 0.f, 0.f},
            // particle size
            DEB_BASE_SIZE = {0.025f, 0.025f, 0.025f},
            DEB_RAND_SIZE = {0.01f, 0.01f, 0.01f};

    private static final float
            // number of particles used for the effect
            DEB_NUM_PARTICLES = 200.f,
            // millis before the particles and emitter are deleted
            DEB_LIFETIME = 3000.f;
    private static final int
            // base colour for particles
            DEB_BASE_COL = 0x808080,
            // each component of this value is used to randomize the components of the base colour
            DEB_RAND_COL = 0x202020;
    // debris does not use a texture, only triangles of varying shades of grey
    private static final PImage DEB_TEX = null;

    public static ParticleEmitter debris(float[] pos, float[] vel)
    {
        return new ParticleEmitter(
                Type.BURST, Shape.TRIANGLE, DEB_LIFETIME, DEB_LIFETIME,
                pos, DEB_RAND_POS, vel, DEB_RAND_VEL,
                DEB_BASE_ACC, DEB_RAND_ACC, DEB_BASE_ANGLE, DEB_RAND_ANGLE,
                DEB_BASE_ROT, DEB_RAND_ROT, DEB_BASE_SIZE, DEB_RAND_SIZE,
                DEB_POS_OFF, DEB_TEX, DEB_BASE_COL, DEB_RAND_COL, DEB_NUM_PARTICLES);
    }

    /* * * * * * * * * * * *
     * BULLET SPARK EFFECT *
     * * * * * * * * * * * */
    // Small effect similar to an explosion but with fewer
    // particles, blue in colour. Used when bullets collide.
    private static final float[]
            // position (pos parameter used for the base)
            BSP_RAND_POS = {0.f, 0.f, 0.f},
            // velocity
            BSP_BASE_VEL = {0.f, 0.f, 0.f},
            BSP_RAND_VEL = {0.0018f, 0.0018f, 0.0018f},
            // acceleration
            BSP_BASE_ACC = {0.f, 0.f, 0.f},
            BSP_RAND_ACC = {0.f, 0.f, 0.f},
            // initial rotation
            BSP_BASE_ANGLE = {0.f, 0.f, 0.f},
            BSP_RAND_ANGLE = {PI, PI, PI},
            // rotation applied per frame
            BSP_BASE_ROT = {0.f, 0.f, 0.f},
            BSP_RAND_ROT = {0.f, 0.f, 0.f},
            // offset from base position
            BSP_POS_OFF = {0.f, 0.f, 0.f},
            // particle size
            BSP_BASE_SIZE = {0.015f, 0.015f, 0.015f},
            BSP_RAND_SIZE = {0.f, 0.f, 0.f};
    private static final float
            // number of particles for the effect
            BSP_NUM_PARTICLES = 25.f,
            // millis before the particles and emitter are deleted
            BSP_LIFETIME = 350.f;
    private static final int
            // base colour
            BSP_BASE_COL = 0x20C0C0,
            // r, g and b variation allowed for each particle's colour
            BSP_RAND_COL = 0x203F3F;
    // texture for the particles
    private static final PImage BSP_TEX = Textures.SPARK;

    public static ParticleEmitter bulletSpark(float[] pos)
    {
        return new ParticleEmitter (
                Type.BURST, Shape.TRIANGLE, BSP_LIFETIME, BSP_LIFETIME,
                pos, BSP_RAND_POS, BSP_BASE_VEL, BSP_RAND_VEL,
                BSP_BASE_ACC, BSP_RAND_ACC, BSP_BASE_ANGLE, BSP_RAND_ANGLE,
                BSP_BASE_ROT, BSP_RAND_ROT, BSP_BASE_SIZE, BSP_RAND_SIZE,
                BSP_POS_OFF, BSP_TEX, BSP_BASE_COL, BSP_RAND_COL, BSP_NUM_PARTICLES);
    }
}
