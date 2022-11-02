package shooter;

// This class' purpose is to allow creating colours
// without a reference to an instance of PApplet.

public class StaticColor
{
    private static final int
            COL_RANGE = 0x100, // given floats must be strictly less than 1
            COL_BITS = 8,
            FULL_ALPHA = 0xFF000000;
    public static int color(float r, float g, float b)
    {
        return FULL_ALPHA
                | (int)(r * COL_RANGE) << (COL_BITS * 2)
                | (int)(g * COL_RANGE) << COL_BITS
                | (int)(b * COL_RANGE);
    }

    public static int color(int[] col)
    {
        return FULL_ALPHA
                | col[0] << (COL_BITS * 2)
                | col[1] << (COL_BITS)
                | col[2];
    }
}
