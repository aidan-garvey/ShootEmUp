package shooter.world;

import shooter.GameSketch;

public abstract class WorldObject
{
    final protected float
            X_OFF,
            Y_OFF,
            // BASE_Z,
            // TOP_Z,
            Z_ANGLE;

    protected float
            BASE_Z,
            TOP_Z;

    public WorldObject(float x, float y, float ang)
    {
        X_OFF = x;
        Y_OFF = y;
        // BASE_Z = zB;
        // TOP_Z = zT;
        Z_ANGLE = ang;
    }

    public abstract void draw(GameSketch gs);
}
