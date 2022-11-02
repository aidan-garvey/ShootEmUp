package shooter.actors;

import processing.core.PConstants;
import processing.core.PImage;
import shooter.*;
import shooter.particles.LifespanParticle;
import shooter.particles.ParticleEmitter;

public class Player extends FrameAnimatedActor implements GameConstants
{
    private static final int
            PLAYER_ANIM_FRAMES = 10;
    private static final float
            // length of firing animation
            PLAYER_ANIM_MILLIS = 333.3f,

            // acceleration and velocity constants
            // measured in NDC units per millisecond
            ACCEL    = 0.00036f,
            FRICTION = 0.00012f,
            MIN_VEL  = 0.000072f,
            MAX_VEL  = 0.0036f,

            // movement boundaries
            H_BOUND = 1.1f,
            BOTTOM_BOUND = -1.1f,
            TOP_BOUND = 1.1f,
            // rotation constants
            MAX_H_ANGLE = HALF_PI + QUARTER_PI,
            MIN_H_ANGLE = HALF_PI - QUARTER_PI,
            CENTRE_H_ANGLE = HALF_PI,
            MAX_V_ANGLE = QUARTER_PI / 4.f,
            // rotation per millisecond when actively turning
            H_ROTATE = QUARTER_PI * 0.006f,
            V_ROTATE = MAX_V_ANGLE * 0.012f,
            // rotation back to resting position when not turning
            H_ROT_REST = QUARTER_PI * 0.003f,
            V_ROT_REST = MAX_V_ANGLE * 0.006f,
            // player's collision radius
            PLAYER_COLLISION = 0.10f;


    private static final float[]
            PLAYER_SIZE    = {0.15f, 0.15f, 0.15f},
            // engine particle emitter settings
            ENGINE_POS_R   = {0.f, 0.f, 0.f},
            ENGINE_VEL     = {-0.003f, 0.f, -0.00006f},
            ENGINE_VEL_R   = {0.0012f, 0.0006f, 0.f},
            ENGINE_ACCEL   = {0.f, 0.f, 0.f},
            ENGINE_ACCEL_R = {0.f, 0.f, 0.f},
            ENGINE_ANGLE   = {0.f, 0.f, 0.f},
            ENGINE_ANGLE_R = {0.f, 0.f, PI},
            ENGINE_ROT     = {0.f, 0.f, 0.f},
            ENGINE_ROT_R   = {0.f, 0.f, QUARTER_PI * 0.015f},
            ENGINE_OFFSET  = {-0.1f, 0.f, 0.f},
            ENGINE_SIZE    = {0.03f, 0.03f, 0.f},
            ENGINE_SIZE_R  = {0.f, 0.f, 0.f};

    private static final ParticleEmitter.Type ENGINE_EMIT_TYPE = ParticleEmitter.Type.SUSTAINED;
    private static final LifespanParticle.Shape ENGINE_SHAPE = LifespanParticle.Shape.DIAMOND;
    private static final PImage ENGINE_TEXTURE = Textures.SPARK;

    private static final float
            ENGINE_FREQ = 120.f, // engine particles per second
            ENGINE_PART_LIFESPAN = 500.f,
            ENGINE_EMIT_LIFESPAN = -1.f;

    private static final int
            ENGINE_COLOR = 0xFF80EEEE,
            ENGINE_COLOR_R = 0xFF401111;

    private static final float
            BULLET_ANGLE_EXTRA = 1.5f,
            BULLET_ANGLE_SPREAD = 0.1f;

    private static final int BULLETS_PER_SHOT = 5;
    private static final float
            BULLET_OFFSET_X = 0.1f,
            BULLET_OFFSET_Y = 0.1f,
            BULLET_OFFSET_Z = 0.f,
            PLAYER_START_X = 0.f,
            PLAYER_START_Y = -0.8f,
            PLAYER_START_Z = 0.8f;

    private int hFlip = 1;
    private boolean immune = false;

    private final ParticleEmitter engineParticles;
    private final GameState state;

    public Player(GameState state)
    {
        super(PLAYER_SIZE, PLAYER_COLLISION, Textures.PLAYER_BASE, 0,
                PLAYER_ANIM_FRAMES, PLAYER_ANIM_MILLIS, Textures.PLAYER_SHOOT);
        this.pos[I_X] = PLAYER_START_X;
        this.pos[I_Y] = PLAYER_START_Y;
        this.pos[I_Z] = PLAYER_START_Z;
        this.angle[I_Z] = CENTRE_H_ANGLE;
        this.state = state;
        engineParticles = new ParticleEmitter (
                ENGINE_EMIT_TYPE, ENGINE_SHAPE, ENGINE_PART_LIFESPAN, ENGINE_EMIT_LIFESPAN,
                pos, ENGINE_POS_R, ENGINE_VEL, ENGINE_VEL_R, ENGINE_ACCEL, ENGINE_ACCEL_R,
                ENGINE_ANGLE, ENGINE_ANGLE_R, ENGINE_ROT, ENGINE_ROT_R, ENGINE_SIZE, ENGINE_SIZE_R,
                ENGINE_OFFSET, ENGINE_TEXTURE, ENGINE_COLOR, ENGINE_COLOR_R, ENGINE_FREQ);
    }

    public void setImmune(boolean i) {immune = i;}
    public boolean isImmune() {return immune;}

    public void draw(GameSketch gs)
    {
        if (killed || invisible) return;

        engineParticles.draw(gs);

        gs.pushMatrix();
        gs.translate(pos[I_X], pos[I_Y], pos[I_Z]);
        gs.scale(size[I_X], size[I_Y], size[I_Z]);
        gs.rotateZ(angle[I_Z]);
        gs.rotateY(angle[I_Y]);

        gs.beginShape(PConstants.TRIANGLE_STRIP);
        gs.texture(this.getCurrTexture());

        gs.vertex(1.f, 1.f, 0.f, 1.f, 0.f);
        gs.vertex(-1.f, 1.f, 0.f, 0.f, 0.f);
        gs.vertex(1.f, -1.f, 0.f, 1.f, 1.f * hFlip);
        gs.vertex(-1.f, -1.f, 0.f, 0.f, 1.f * hFlip);

        gs.endShape();
        gs.popMatrix();
    }

    public void update(float elapsedMillis)
    {
        if (killed) return;

        applyFriction(elapsedMillis);
        updateVelocity(elapsedMillis);
        updatePosition();
        engineParticles.setPosition(pos);
        engineParticles.setZAngle(angle[I_Z]);
        engineParticles.setXAngle(angle[I_X]);
        updateShooting(elapsedMillis);
        engineParticles.update(elapsedMillis);

        lastMillis = elapsedMillis;
    }

    private void applyFriction(float elapsedMillis)
    {
        // horizontal speed
        if (vel[I_X] > MIN_VEL * elapsedMillis)
            vel[I_X] -= FRICTION * elapsedMillis;
        else if (vel[I_X] < -MIN_VEL * elapsedMillis)
            vel[I_X] += FRICTION * elapsedMillis;
        else
            vel[I_X] = 0.f;

        // vertical
        if (vel[I_Y] > MIN_VEL * elapsedMillis)
            vel[I_Y] -= FRICTION * elapsedMillis;
        else if (vel[I_Y] < -MIN_VEL * elapsedMillis)
            vel[I_Y] += FRICTION * elapsedMillis;
        else
            vel[I_Y] = 0.f;

        // horizontal angle
        if (angle[I_Z] - CENTRE_H_ANGLE > H_ROT_REST * elapsedMillis)
            angle[I_Z] -= H_ROT_REST * elapsedMillis;
        else if (angle[I_Z] - CENTRE_H_ANGLE < -H_ROT_REST * elapsedMillis)
            angle[I_Z] += H_ROT_REST * elapsedMillis;
        else
            angle[I_Z] = CENTRE_H_ANGLE;

        // vertical angle
        if (angle[I_Y] > V_ROT_REST * elapsedMillis)
            angle[I_Y] -= V_ROT_REST * elapsedMillis;
        else if (angle[I_Y] < V_ROT_REST * elapsedMillis)
            angle[I_Y] += V_ROT_REST * elapsedMillis;
        else
            angle[I_Y] = 0.f;
    }

    // apply acceleration to the ship depending on which directional inputs are being given by the player
    private void updateVelocity(float elapsedMillis)
    {
        // adjust velocity based on inputs
        if (state.leftHeld)
        {
            vel[I_X] -= ACCEL * elapsedMillis;
            angle[I_Z] += H_ROTATE * elapsedMillis;
        }
        if (state.rightHeld)
        {
            vel[I_X] += ACCEL * elapsedMillis;
            angle[I_Z] -= H_ROTATE * elapsedMillis;
        }
        if (state.upHeld)
        {
            vel[I_Y] += ACCEL * elapsedMillis;
            angle[I_Y] += V_ROTATE * elapsedMillis;
        }
        if (state.downHeld)
        {
            vel[I_Y] -= ACCEL * elapsedMillis;
            angle[I_Y] -= V_ROTATE * elapsedMillis;
        }

        // cap velocity at +/- max
        if (vel[I_X] > MAX_VEL * elapsedMillis)
            vel[I_X] = MAX_VEL * elapsedMillis;
        else if (vel[I_X] < -MAX_VEL * elapsedMillis)
            vel[I_X] = -MAX_VEL * elapsedMillis;

        if (vel[I_Y] > MAX_VEL * elapsedMillis)
            vel[I_Y] = MAX_VEL * elapsedMillis;
        else if (vel[I_Y] < -MAX_VEL * elapsedMillis)
            vel[I_Y] = -MAX_VEL * elapsedMillis;

        // cap angle at +/- max
        if (angle[I_Z] > MAX_H_ANGLE)
            angle[I_Z] = MAX_H_ANGLE;
        else if (angle[I_Z] < MIN_H_ANGLE)
            angle[I_Z] = MIN_H_ANGLE;

        if (angle[I_Y] > MAX_V_ANGLE)
            angle[I_Y] = MAX_V_ANGLE;
        else if (angle[I_Y] < -MAX_V_ANGLE)
            angle[I_Y] = -MAX_V_ANGLE;
    }

    private void updatePosition()
    {
        pos[I_X] += vel[I_X];
        pos[I_Y] += vel[I_Y];

        if (pos[I_X] > H_BOUND)
            pos[I_X] = H_BOUND;
        else if (pos[I_X] < -H_BOUND)
            pos[I_X] = -H_BOUND;

        if (pos[I_Y] > TOP_BOUND)
            pos[I_Y] = TOP_BOUND;
        else if (pos[I_Y] < BOTTOM_BOUND)
            pos[I_Y] = BOTTOM_BOUND;
    }

    private void updateShooting(float elapsedMillis)
    {
        if (state.shootHeld || animTimer >= 0.f)
        {
            if (animTimer >= PLAYER_ANIM_MILLIS)
            {
                animTimer = -1.f;
                hFlip *= -1;
            }
            // if this is the first frame, spawn bullets
            else
            {
                if (animTimer < 0.f)
                {
                    for (int i = 0; i < BULLETS_PER_SHOT; i++)
                    {
                        float bulletAngle = (angle[I_Z] - CENTRE_H_ANGLE) * BULLET_ANGLE_EXTRA + CENTRE_H_ANGLE;
                        if (bulletAngle < MIN_H_ANGLE)
                            bulletAngle = MIN_H_ANGLE;
                        else if (bulletAngle > MAX_H_ANGLE)
                            bulletAngle = MAX_H_ANGLE;
                        bulletAngle += GameMath.biRand(BULLET_ANGLE_SPREAD);

                        float[] bulletOffset = {
                                BULLET_OFFSET_X,
                                BULLET_OFFSET_Y * hFlip,
                                BULLET_OFFSET_Z
                        };
                        state.playerShoot(this.pos, bulletOffset, bulletAngle);
                    }
                }
                animTimer += elapsedMillis;
            }
        }
    }
}
