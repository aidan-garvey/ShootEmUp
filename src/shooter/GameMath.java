package shooter;

public class GameMath
{
    // return random float in range [-bound, bound)
    public static float biRand(float bound)
    {
        return (float)Math.random() * 2.f * bound - bound;
    }
    public static int biRand(int bound)
    {
        return (int)(Math.random() * 2.0 * bound) - bound;
    }

    public static float randRange(float low, float high)
    {
        return (float)Math.random() * (high - low) + low;
    }

    public static int constrain(int val, int lower, int upper)
    {
        return val <= lower ? lower : Math.min(val, upper);
    }
}
