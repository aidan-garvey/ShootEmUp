package shooter;

public interface GameConstants
{
    // indexes of each component of a coordinate
    int I_X = 0, I_Y = 1, I_Z = 2;
    // controls
    char
        // player controls
        KEY_LEFT = 'a',
        KEY_RIGHT = 'd',
        KEY_UP = 'w',
        KEY_DOWN = 's',
        KEY_SHOOT = ' ',

        KEY_VIEW = 'r', // toggle camera mode

        KEY_DBG_COLL = 'c', // toggle collisions
        KEY_DBG_CHGBIOME = 'g', // force biome change for next chunk
        KEY_DBG_DIE = 'k', // kill player
        KEY_DBG_PARTICLE_REPORT = 'p', // print info about current particles

        KEY_DBG_SCRLUP = ']', // force scroll up
        KEY_DBG_SCRLDOWN = '[', // force scroll down

        KEY_DBG_RESET = ';'; // reset game

    // floating point constants
    float
            TWO_PI = (float)(Math.PI * 2.0),
            PI = (float)Math.PI,
            HALF_PI = (float)(Math.PI / 2.0),
            QUARTER_PI = (float)(Math.PI / 4.0),
            ROOT_TWO = (float)Math.sqrt(2.0);
}
