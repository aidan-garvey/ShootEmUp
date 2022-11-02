package shooter;

public class Interpolate
{
    // linear interpolation between two floats
    public static float lerpFloat(float t, float a, float b)
    {
        return a + t * (b - a);
    }

    // exponential interpolation between two floats
    public static float expInterpFloat(float t, float a, float b)
    {
        return a + t * t * (b - a);
    }
}
