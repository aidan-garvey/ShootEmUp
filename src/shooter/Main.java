package shooter;

import processing.core.PApplet;

public class Main
{
    public static void main(String[] args)
    {
        String[] pArgs = {"Test Sketch"};
        GameSketch gameSketch = new GameSketch(900, 900, 144, true);
        PApplet.runSketch(pArgs, gameSketch);
    }
}
