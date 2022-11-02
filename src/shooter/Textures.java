package shooter;

import processing.awt.PImageAWT;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Textures
{
    public static final PImageAWT
        // terrain textures
        HILLS_GRASS = loadTexture("res/grass.png"),
        HILLS_DIRT = loadTexture("res/dirt.png"),
        DESERT_SAND = loadTexture("res/sand.png"),
        DESERT_SANDSTONE = loadTexture("res/sandstone.png"),
        ICE_SNOW = loadTexture("res/snow.png"),
        ICE_WALL = loadTexture("res/ice.png"),

        // decoration textures
        DESERT_GLYPHS = loadTexture("res/glyphs.png"),
        DESERT_PYRAMID = loadTexture("res/pyramid.png"),
        DESERT_TILE = loadTexture("res/desert_tile.png"),
        SNOWMAN_BODY = loadTexture("res/snowman_body.png"),

        // spark particle
        SPARK = loadTexture("res/spark.png"),

        // enemy sprite
        ENEMY_BASE = loadTexture("res/enemy_1.png"),

        // enemy bullet sprites
        ENEMY_BULLET_1 = loadTexture("res/enemy_bullet_1.png"),
        ENEMY_BULLET_2 = loadTexture("res/enemy_bullet_2.png"),
        ENEMY_BULLET_3 = loadTexture("res/enemy_bullet_3.png"),
        ENEMY_BULLET_4 = loadTexture("res/enemy_bullet_4.png"),

        // player bullet sprite
        PLAYER_BULLET = loadTexture("res/player_bullet.png"),

        // player sprites
        PLAYER_BASE = loadTexture("res/player.png"),
        PLAYER_SHOOT_1 = loadTexture("res/player_shoot1.png"),
        PLAYER_SHOOT_2 = loadTexture("res/player_shoot2.png"),
        PLAYER_SHOOT_3 = loadTexture("res/player_shoot3.png"),
        PLAYER_SHOOT_4 = loadTexture("res/player_shoot4.png"),
        PLAYER_SHOOT_5 = loadTexture("res/player_shoot5.png"),
        PLAYER_SHOOT_6 = loadTexture("res/player_shoot6.png"),
        PLAYER_SHOOT_7 = loadTexture("res/player_shoot7.png"),
        PLAYER_SHOOT_8 = loadTexture("res/player_shoot8.png"),
        PLAYER_SHOOT_9 = loadTexture("res/player_shoot9.png"),
        PLAYER_SHOOT_10 = loadTexture("res/player_shoot10.png");

    // arrays for frame animation
    public static final PImageAWT[]
        PLAYER_SHOOT = {PLAYER_SHOOT_1, PLAYER_SHOOT_2, PLAYER_SHOOT_3,
            PLAYER_SHOOT_4, PLAYER_SHOOT_5, PLAYER_SHOOT_6, PLAYER_SHOOT_7,
            PLAYER_SHOOT_8, PLAYER_SHOOT_9, PLAYER_SHOOT_10},
        ENEMY_BULLET = {ENEMY_BULLET_1, ENEMY_BULLET_2,
                ENEMY_BULLET_3, ENEMY_BULLET_4};

    public static PImageAWT loadTexture(String path)
    {
        PImageAWT result = null;
        try
        {
            result = new PImageAWT(ImageIO.read(new File(path)));
        }
        catch (IOException ioe)
        {
            System.err.println("Image " + path + " could not be loaded!");
            ioe.printStackTrace();
        }
        return result;
    }

    // convert NDC x value in range [-1, 1] to normalized texture coordinate
    public static float xNDCToTexture(float x)
    {
        return (x + 1.f) / 2.f;
    }

    // convert y-up NDC in range [-1, 1] to y-down normalized texture coordinate
    public static float yNDCToTexture(float y)
    {
        return 1.f - (y + 1.f) / 2.f;
    }
}
