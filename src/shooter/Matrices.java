package shooter;

import processing.core.PMatrix3D;

public class Matrices
{
    public static PMatrix3D identity()
    {
        return new PMatrix3D (
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        );
    }

    public static PMatrix3D rotateX(float angle)
    {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        return new PMatrix3D (
            1,  0,    0,  0,
            0, cos, -sin, 0,
            0, sin,  cos, 0,
            0,  0,    0,  1
        );
    }

    public static PMatrix3D rotateY(float angle)
    {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        return new PMatrix3D (
             cos, 0, sin, 0,
              0,  1,  0,  0,
            -sin, 0, cos, 0,
              0,  0,  0,  1
        );
    }

    public static PMatrix3D rotateZ(float angle)
    {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        return new PMatrix3D (
            cos, -sin, 0, 0,
            sin,  cos, 0, 0,
             0,    0,  1, 0,
             0,    0,  0, 1
        );
    }
}
