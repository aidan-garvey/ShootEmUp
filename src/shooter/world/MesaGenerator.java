package shooter.world;

import java.util.Arrays;

public class MesaGenerator
{
    // choose random mesa, mirror horizontally and/or vertically, convert to doubles
    // false -> minHeight, true -> maxHeight
    public static float[][] generateMesa(float minHeight, float maxHeight)
    {
        int patIndex = (int)(Math.random() * MESA_PATTERNS.length);
        boolean[][] pattern = MESA_PATTERNS[patIndex];
        boolean hFlip = Math.random() < 0.5;
        boolean vFlip = Math.random() < 0.5;
        int width = pattern[0].length;

        float[][] result = new float[pattern.length][width];
        // convert pattern to result, flip x/y if h/vFlip
        for (int y = 0; y < result.length; y++)
        {
            int tY = vFlip ? result.length - y - 1 : y;
            for (int x = 0; x < width; x++)
            {
                int tX = hFlip ? width - x - 1 : x;
                result[y][x] = pattern[tY][tX] ? maxHeight : minHeight;
            }
        }

        // smooth out mesa
        float[][] clone = result.clone();
        for (int y = 0; y < result.length; y++)
        {
            for (int x = 0; x < width; x++)
            {
                float sum = y == 0 ? minHeight : clone[y-1][x];
                sum += y == result.length - 1 ? minHeight : clone[y+1][x];
                sum += x == 0 ? minHeight : clone[y][x-1];
                sum += x == width - 1 ? minHeight : clone[y][x+1];
                result[y][x] = sum / 4.f;
            }
        }

        return result;
    }

    private static boolean[][] expandMesa(int[] bits)
    {
        int width = bits[0];
        boolean[][] result = new boolean[bits.length - 1][width];
        for (int row = 0; row < result.length; row++)
        {
            int curr = bits[row + 1];
            for (int col = width - 1; col >= 0; col--)
            {
                result[row][col] = (curr & 1) == 1;
                curr >>= 1;
            }
        }
        return result;
    }

    // bitmaps for mesas - first integer is width
    private static final int[][] MESA_COMPRESSED =
    {
        {
            12,
            0b0001_1110_0000,
            0b0011_1111_0000,
            0b0011_1111_1100,
            0b0111_1111_1110,
            0b1111_1111_1110,
            0b1111_1111_1110,
            0b1111_1111_1111,
            0b1111_1111_1111,
            0b1111_1111_1111,
            0b1111_1111_1111,
            0b1111_1111_1111,
            0b1111_1111_1111,
            0b1111_1111_1110,
            0b0011_1111_1110,
            0b0000_1111_1110,
        },
        {
            14,
            0b00_0001_1100_0000,
            0b00_0011_1110_0000,
            0b00_0111_1111_1000,
            0b00_0111_1111_1110,
            0b01_1111_1111_1111,
            0b01_1111_1111_1111,
            0b11_1111_1111_1111,
            0b11_1111_1111_1111,
            0b11_1111_1111_1110,
            0b01_1111_1111_1100,
            0b00_1111_1111_1100,
            0b00_0111_1111_1000,
            0b00_0011_1111_1000
        },
        {
            17,
            0b0_0000_0111_0000_0000,
            0b0_0000_1111_1110_0000,
            0b0_0001_1111_1111_1100,
            0b0_0001_1111_1111_1100,
            0b0_0011_1111_1111_1110,
            0b0_1111_1111_1111_1110,
            0b1_1111_1111_1111_1111,
            0b1_1111_1111_1111_1111,
            0b0_1111_1111_1111_1110,
            0b0_1111_1111_1111_1110,
            0b0_1111_1111_1111_1100,
            0b0_0111_1111_1111_1100,
            0b0_0000_1111_1111_0000,
            0b0_0000_0000_1100_0000
        }
    };

    private static final boolean[][][] MESA_PATTERNS;
    static
    {
        MESA_PATTERNS = new boolean[MESA_COMPRESSED.length][][];
        for (int i = 0; i < MESA_COMPRESSED.length; i++)
        {
            MESA_PATTERNS[i] = expandMesa(MESA_COMPRESSED[i]);
        }
    }
}
