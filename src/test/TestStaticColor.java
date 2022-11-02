package test;

import org.junit.jupiter.api.Test;
import shooter.StaticColor;

import static org.junit.jupiter.api.Assertions.*;

public class TestStaticColor
{
    @Test
    public void TestColorBounds()
    {
        // lower bound - all zeroes should return full alpha, everything else zeroes
        assertEquals(0xFF000000, StaticColor.color(0.f, 0.f, 0.f));
        // upper bound - largest float less than 1 should return max colour values
        float maxVal = Math.nextDown(1.f);
        assertEquals(0xFFFFFFFF, StaticColor.color(maxVal, maxVal, maxVal));
        // invalid upper bound - using exactly 1 should not work correctly
        // this literal is the result of 0xFF00 0000 | 0x100 << 16 | 0x100 << 8 | 0x100
        assertEquals(0xFF010100, StaticColor.color(1.f, 1.f, 1.f));
    }
}
