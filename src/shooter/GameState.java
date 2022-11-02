package shooter;

import processing.core.PConstants;
import shooter.actors.Player;
import shooter.particles.ParticleTracker;
import shooter.world.World;

import static shooter.Interpolate.lerpFloat;

public class GameState {
    private final boolean DEBUG;

    private static final float
            SCROLL_SPEED = 0.00072f, // amount scrolled per millisecond
            SCROLL_BUMP = SCROLL_SPEED * 320.f, // scroll amount when '[' or ']' is pressed
            IMMUNE_MILLIS = 2500.f, // invincibility length after respawning
            IMMUNE_FLICKER_PERIOD = 150.f, // length of flicker animation when immune to damage
            RESPAWN_WAIT = 1000.f, // time between dying and respawning
            GAME_OVER_MILLIS = 1000.f, // length of game over animation
            // values for drawing lives
            LIVES_ORTHO_X = -1.822f,
            LIVES_ORTHO_Y = 1.822f,
            LIVES_ORTHO_Z = -1.f,
            LIVES_ORTHO_SIZE = 0.1176f,
            LIVES_PERSPECTIVE_X = -1.55f,
            LIVES_PERSPECTIVE_Y = 1.55f,
            LIVES_PERSPECTIVE_Z = -2.215f,
            LIVES_PERSPECTIVE_SIZE = 0.1f,
            LIVES_SPACING = 2.f;

    private static final int
            STARTING_LIVES = 3;

    private final ParticleTracker particles;
    private final GameSketch sketch;
    private final Camera camera;
    private final World gameWorld;
    // private final ParticleTracker gameParticles;
    private Player player; // not final, as a new player is created on respawn

    private int playerLives = STARTING_LIVES;

    private boolean
            // player's state
            immune = false,
            respawning = false,
            // game state
            gameIsOver = false,
            collisionsOn = true;
    // controls
    public boolean
            leftHeld = false,
            rightHeld = false,
            upHeld = false,
            downHeld = false,
            shootHeld = false;

    private float
            immuneTimer = 0.f,
            respawnTimer = 0.f,
            gameOverTimer = 0.f,
            currScrollSpeed = SCROLL_SPEED; // slows to a halt when game ends

    public GameState(GameSketch sketch, boolean debug) {
        this.sketch = sketch;
        this.DEBUG = debug;
        this.camera = new Camera(sketch);
        this.gameWorld = new World();
        this.player = new Player(this);
        // ParticleTracker.setPlayer(this.player);
        this.particles = new ParticleTracker();
        particles.setPlayer(player);
    }

    public void update(float elapsedMillis) {
        // ParticleTracker.update(elapsedMillis, collisionsOn, gameIsOver);
        particles.update(elapsedMillis, collisionsOn, gameIsOver);
        gameWorld.scrollWorld(currScrollSpeed * elapsedMillis);

        // if the game is over, slow down the map scrolling
        if (gameIsOver)
        {
            if (gameOverTimer < GAME_OVER_MILLIS)
            {
                currScrollSpeed = lerpFloat (
                        1.f - (gameOverTimer / GAME_OVER_MILLIS),
                        0.f, SCROLL_SPEED);
                gameOverTimer += elapsedMillis;
            }
            else
                currScrollSpeed = 0.f;
        }
        // the player has died and is waiting to respawn
        else if (respawning)
        {
            // once timer is up, respawn the player, make immune to damage
            if ((respawnTimer += elapsedMillis) >= RESPAWN_WAIT)
            {
                respawnTimer = 0.f;
                player = new Player(this);
                // ParticleTracker.setPlayer(player);
                particles.setPlayer(player);
                respawning = false;
                immune = true;
                player.setImmune(true);
            }
        }
        // if player is immune after respawning, flicker sprite
        else if (immune)
        {
            if ((immuneTimer += elapsedMillis) >= IMMUNE_MILLIS)
            {
                immuneTimer = 0.f;
                immune = false;
                player.setImmune(false);
                player.setInvisible(false);
            }
            else
            {
                player.setInvisible((immuneTimer % IMMUNE_FLICKER_PERIOD)
                        / (IMMUNE_FLICKER_PERIOD / 2.f) >= 1.f);
            }
        }
        // if player was killed this frame, decrease lives and respawn or end the game
        else if (player.isKilled())
        {
            if (--playerLives >= 0)
                respawning = true;
            else
                gameIsOver = true;
        }

        camera.setupCamera(elapsedMillis);
    }

    public void draw() {
        gameWorld.drawWorld(sketch);
        // ParticleTracker.drawParticles(sketch);
        particles.drawParticles(sketch);
        drawLives();
    }

    public void playerShoot(float[] pos, float[] offset, float angle)
    {
        particles.playerShoot(pos, offset, angle);
    }

    public void toggleCamera()
    {
        camera.toggleCameraMode();
    }

    public void toggleCollisions()
    {
        if (DEBUG)
            collisionsOn = !collisionsOn;
    }

    public void forceBiomeChange()
    {
        if (DEBUG)
            gameWorld.cycleBiome();
    }

    public void killPlayer()
    {
        if (DEBUG)
            player.setKilled();
    }

    public void printDebugInfo()
    {
        if (DEBUG)
            particles.printDebugInfo();
    }

    public void scrollUp()
    {
        if (DEBUG)
            gameWorld.scrollWorld(SCROLL_BUMP);
    }

    public void scrollDown()
    {
        if (DEBUG)
            gameWorld.scrollWorld(-SCROLL_BUMP);
    }

    private void drawLives() {
        sketch.pushMatrix();

        // draw lives to screen in same spot, regardless of camera position
        sketch.resetMatrix();
        // if camera is not in perspective mode, and not interpolating between modes, use LIVES_ORTHO_Z
        if (camera.isUsingOrtho()) {
            sketch.translate(LIVES_ORTHO_X, LIVES_ORTHO_Y, LIVES_ORTHO_Z);
            sketch.scale(LIVES_ORTHO_SIZE, LIVES_ORTHO_SIZE, 1.f);
        }
        // otherwise, a perspective projection is being used, use LIVES_PERSPECTIVE_Z
        else
        {
            sketch.translate(LIVES_PERSPECTIVE_X, LIVES_PERSPECTIVE_Y, LIVES_PERSPECTIVE_Z);
            sketch.scale(LIVES_PERSPECTIVE_SIZE, LIVES_PERSPECTIVE_SIZE, 1.f);
        }

        for (int i = 0; i < playerLives; i++)
        {
            sketch.pushMatrix();

            sketch.rotateZ(GameConstants.HALF_PI);
            sketch.beginShape(PConstants.TRIANGLE_STRIP);
            sketch.texture(Textures.PLAYER_BASE);

            sketch.vertex(-1.f, 1.f, 0.f,   0.f, 0.f);
            sketch.vertex(-1.f, -1.f, 0.f,  0.f, 1.f);
            sketch.vertex(1.f, 1.f, 0.f,    1.f, 0.f);
            sketch.vertex(1.f, -1.f, 0.f,   1.f, 1.f);

            sketch.endShape();
            sketch.popMatrix();

            // draw next life to the right of the previous one
            sketch.translate(LIVES_SPACING, 0.f, 0.f);
        }

        sketch.popMatrix();
    }
}
