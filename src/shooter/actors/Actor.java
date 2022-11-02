package shooter.actors;

import processing.core.PImage;
import shooter.particles.Particle;

public abstract class Actor extends Particle
{
    protected boolean killed;

    public Actor(float[] size, float collisionRadius, PImage staticTexture, int tint)
    {
        super(size, collisionRadius, staticTexture, tint);
        this.killed = false;
    }

    public boolean isKilled() {return killed;}
    public void setKilled() {killed = true;}
}