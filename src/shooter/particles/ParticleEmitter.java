package shooter.particles;

import processing.core.PImage;
import processing.core.PMatrix3D;
import shooter.*;
import shooter.particles.LifespanParticle;
import shooter.particles.Particle;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static shooter.GameMath.biRand;

public class ParticleEmitter implements GameConstants
{
    public enum Type
    {
        BURST, // immediately emits number of particles equal to particlesPerSec then switches off
        SUSTAINED // emits particles at consistent rate then switches off
    }
    private final Type type;
    private final LifespanParticle.Shape shape;

    private final float[]
            position, posRange,
            velocity, velRange,
            acceleration, accelRange,
            angle, angleRange,
            rotation, rotRange,
            size, sizeRange,
            posOffset;
    private final PImage texture;
    private final int[] tint, tintRange;
    private float particlesPerSec; // todo: increase rate when player accelerates, decrease when player brakes
    private float spawnCounter;
    private final List<LifespanParticle> particles;
    private PMatrix3D xAngle, yAngle, zAngle;
    private final float myLifespan;
    private final float particleLifespan;
    private float lifespanCounter;
    private boolean switchedOn;

    public ParticleEmitter (Type type, LifespanParticle.Shape shape,
                            float particleLife, float emitterLife,
                            float[] pos, float[] posR, float[] vel, float[] velR,
                            float[] acc, float[] accR, float[] ang, float[] angR,
                            float[] rot, float[] rotR, float[] sz, float[] szR,
                            float[] off, PImage tex, int col, int colR, float perSec)
    {
        this.type = type;
        this.shape = shape;
        this.particleLifespan = particleLife;
        this.myLifespan = emitterLife;
        this.position = pos.clone();
        this.posRange = posR.clone();
        this.velocity = vel.clone();
        this.velRange = velR.clone();
        this.acceleration = acc.clone();
        this.accelRange = accR.clone();
        this.angle = ang.clone();
        this.angleRange = angR.clone();
        this.rotation = rot.clone();
        this.rotRange = rotR.clone();
        this.size = sz.clone();
        this.sizeRange = szR.clone();
        this.posOffset = off.clone();
        this.texture = tex;
        this.tint = new int[] {
                (col >> 16) & 0xFF,
                (col >> 8) & 0xFF,
                col & 0xFF
        };
        this.tintRange = new int[] {
                (colR >> 16) & 0xFF,
                (colR >> 8) & 0xFF,
                colR & 0xFF
        };
        this.particlesPerSec = perSec;
        this.lifespanCounter = 0.f;
        this.switchedOn = true;
        this.particles = new LinkedList<>();
        this.xAngle = Matrices.identity();
        this.yAngle = Matrices.identity();
        this.zAngle = Matrices.identity();
    }

    // 1) Spawn new particles
    // 2) Call on all particles to update
    // 3) Delete dead particles
    public void update(float elapsedMillis)
    {
        // spawn new particles, switch off if past lifespan
        if (switchedOn)
        {
            if (type == Type.BURST)
                spawnCounter += particlesPerSec;
            else if (type == Type.SUSTAINED)
                spawnCounter += particlesPerSec * elapsedMillis / 1000.f;
            while (spawnCounter >= 1.f)
            {
                spawnParticle();
                spawnCounter -= 1.f;
            }
            if (type == Type.BURST || (myLifespan >= 0.f && (lifespanCounter += elapsedMillis) >= myLifespan))
                switchedOn = false;
        }

        // update all particles, remove dead ones
        ListIterator<LifespanParticle> iterator = particles.listIterator();
        while (iterator.hasNext())
        {
            Particle p = iterator.next();
            p.update(elapsedMillis);
            if (p.isDead())
                iterator.remove();
        }
    }

    public void draw(GameSketch gs)
    {
        for (Particle p : particles)
            p.draw(gs);
    }

    public boolean isExpired()
    {
        return !switchedOn && particles.isEmpty();
    }

    public void setXAngle(float angle)
    {
        xAngle = Matrices.rotateX(angle);
    }

    // public void setYAngle(float angle)
    // {
    //     yAngle = Matrices.rotateY(angle);
    // }

    public void setZAngle(float angle)
    {
        zAngle = Matrices.rotateZ(angle);
    }

    public void setPosition(float[] p)
    {
        System.arraycopy(p, 0, position, 0, 3);
    }

    // reused for every particle, particles deep-copy
    // the arrays passed to their constructors
    private final float[]
            currPos = new float[3],
            currVel = new float[3],
            finalVel = new float[3],
            currAccel = new float[3],
            currAngle = new float[3],
            currRot = new float[3],
            currSize = new float[3],
            currOff = new float[3],
            finalOff = new float[3];
    private final int[] currTint = new int[3];
    private void spawnParticle()
    {
        // multiply offset by direction angle
        // xAngle.mult(posOffset, currOff);
        yAngle.mult(posOffset, currOff);
        zAngle.mult(currOff, finalOff);
        // generate other parameters
        for (int i = I_X; i <= I_Z; i++)
        {
            currPos[i] = position[i] + biRand(posRange[i]);
            currPos[i] += finalOff[i];
            currVel[i] = velocity[i] + biRand(velRange[i]);
            currAccel[i] = acceleration[i] + biRand(accelRange[i]);
            currAngle[i] = angle[i] + biRand(angleRange[i]);
            currRot[i] = rotation[i] + biRand(rotRange[i]);
            currSize[i] = size[i] + biRand(sizeRange[i]);
            currTint[i] = tint[i] + biRand(tintRange[i]);
            currTint[i] = GameMath.constrain(currTint[i], 0, 0xFF);
        }
        zAngle.mult(currVel, finalVel);
        int t = StaticColor.color(currTint);

        particles.add(new LifespanParticle(shape, particleLifespan,
                currPos, finalVel, currAccel, currAngle, currRot,
                currSize, texture, t));
    }
}
