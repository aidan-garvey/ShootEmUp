package shooter.particles;

import processing.core.PImage;
import shooter.GameConstants;
import shooter.GameSketch;

public abstract class Particle implements GameConstants
{
    final protected float[]
        pos,
        vel,
        accel,
        angle,
        rotation,
        size;
    final protected float
        collisionRadius;
    protected float lastMillis;
    protected boolean
        dead,
        invisible;
    final protected PImage staticTexture;
    final protected int tint;

    public Particle(float[] size, float collisionRadius, PImage staticTexture, int tint)
    {
        this.pos = new float[3];
        this.vel = new float[3];
        this.accel = new float[3];
        this.angle = new float[3];
        this.rotation = new float[3];
        this.size = size.clone();
        this.collisionRadius = collisionRadius;
        this.dead = false;
        this.invisible = false;
        this.staticTexture = staticTexture;
        this.tint = tint;
        this.lastMillis = 1.f;
    }

    public Particle(float[] pos, float[] vel, float[] accel,
                    float[] angle, float[] rotation, float[] size,
                    float collisionRadius, PImage staticTexture, int tint)
    {
        this.pos = pos.clone();
        this.vel = vel.clone();
        this.accel = accel.clone();
        this.angle = angle.clone();
        this.rotation = rotation.clone();
        this.size = size.clone();
        this.collisionRadius = collisionRadius;
        this.staticTexture = staticTexture;
        this.tint = tint;
        this.dead = false;
        this.invisible = false;
        this.lastMillis = 1.f;
    }

    public abstract void update(float elapsedMillis);
    public abstract void draw(GameSketch gs);

    public void setInvisible(boolean i) {this.invisible = i;}

    public boolean isDead() {return dead;}
    public boolean isInvisible() {return invisible;}
    public void getPos(float[] dest)
    {
        dest[I_X] = pos[I_X];
        dest[I_Y] = pos[I_Y];
        dest[I_Z] = pos[I_Z];
    }
    public void getVel(float[] dest)
    {
        dest[I_X] = vel[I_X] / lastMillis;
        dest[I_Y] = vel[I_Y] / lastMillis;
        dest[I_Z] = vel[I_Z] / lastMillis;
    }
    public float getCollisionRadius() {return collisionRadius;}
}
