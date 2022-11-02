package shooter.actors;

import processing.core.PImage;

public abstract class FrameAnimatedActor extends Actor
{
    final protected int numAnimFrames;
    final protected float animLength;
    protected float animTimer;
    final protected PImage[] animFrames;

    public FrameAnimatedActor (
            float[] size, float collisionRadius,
            PImage staticTexture, int tint,
            int numAnimFrames, float animLength,
            PImage[] animFrames)
    {
        super(size, collisionRadius, staticTexture, tint);
        this.numAnimFrames = numAnimFrames;
        this.animLength = animLength;
        this.animTimer = -1.f;
        this.animFrames = animFrames;
    }

    protected PImage getCurrTexture()
    {
        if (animTimer < 0.f)
        {
            return staticTexture;
        }
        else
        {
            int index = (int)(animTimer * (float)numAnimFrames / animLength);
            index = Math.min(index, numAnimFrames - 1);
            return animFrames[index];
        }
    }
}
