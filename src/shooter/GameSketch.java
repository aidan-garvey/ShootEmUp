package shooter;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;
import shooter.world.WorldChunk;

import static shooter.Interpolate.expInterpFloat;
import static shooter.Interpolate.lerpFloat;

public class GameSketch extends PApplet implements GameConstants
{
    private static final float
            ORTHO_SCREEN_TILES = 20.f, // screen will be this wide in tiles
            ORTHO_WIDTH = ORTHO_SCREEN_TILES * WorldChunk.TILE_SIZE, // width of the screen in ortho mode, in NDC
            H_FOV = radians(75.f), // horizontal fov in projected mode
            NEAR = 0.01f, // near value for ortho and projected
            FAR = 5.f, // far value for ortho and projected

            // camera position values for lerping from ortho to projected
            // start values imitate ortho, end values are the final projected camera position
            CAM_X = 0.f,
            CAM_Y_START = 0.f,
            CAM_Y_END = -1.35f,
            CAM_Z_START = ORTHO_WIDTH / (2.f * tan(H_FOV / 2.f)),
            CAM_Z_END = 2.4f,
            CAM_UP_X_START = 0.f,
            CAM_UP_Y_START = 1.f,
            CAM_UP_Z_START = 0.f,
            CAM_UP_X_END,
            CAM_UP_Y_END,
            CAM_UP_Z_END;

    static
    {
        PVector camUp, lookAt, side;
        // initial guess
        camUp = new PVector(CAM_UP_X_START, CAM_UP_Y_START, CAM_UP_Z_START);
        // looking at origin
        lookAt = new PVector(-CAM_X, -CAM_Y_END, -CAM_Z_END);
        side = lookAt.cross(camUp);
        // final camUp vector
        camUp = side.cross(lookAt);

        CAM_UP_X_END = camUp.x;
        CAM_UP_Y_END = camUp.y;
        CAM_UP_Z_END = camUp.z;
    }

    private final int sketchWidth, sketchHeight;
    private final float targetFrameRate;

    // manages game state
    private GameState gameState;
    // game's renderer
    private PGraphicsOpenGL graphics = null;
    // projections used for the different modes
    private PMatrix3D orthoProjection = null, frustumProjection = null;

    private int lastTime;
    private final boolean debugMode; // only passed to gameState
    private boolean resetFlag = false; // if true, game will be reset next frame

    public GameSketch(int w, int h, float fps, boolean debug)
    {
        super();
        this.sketchWidth = w;
        this.sketchHeight = h;
        this.targetFrameRate = fps;
        this.debugMode = debug;
        this.gameState = new GameState(this, debug);
        this.lastTime = millis();
    }

    public void reset()
    {
        resetFlag = false;
        this.gameState = new GameState(this, debugMode);
        this.lastTime = millis();
        setup();
    }

    @Override
    public void settings()
    {
        size(this.sketchWidth, this.sketchHeight, P3D);
    }

    @Override
    public void setup()
    {
        colorMode(RGB, 1.f);
        textureMode(NORMAL);
        textureWrap(REPEAT);
        frameRate(this.targetFrameRate);

        setupGraphics();
        setupProjections();
        resetMatrix();
    }

    @Override
    public void draw()
    {
        int currTime = millis();
        clear();
        gameState.update((float)(currTime - lastTime));
        gameState.draw();
        lastTime = currTime;

        if (resetFlag)
            reset();
    }

    @Override
    public void keyPressed()
    {
        // if shift/caps lock is on, change to lowercase
        if (key >= 'A' && key <= 'Z')
        {
            key += 'a' - 'A';
        }
        switch(key)
        {
            case KEY_LEFT -> gameState.leftHeld = true;
            case KEY_RIGHT -> gameState.rightHeld = true;
            case KEY_UP -> gameState.upHeld = true;
            case KEY_DOWN -> gameState.downHeld = true;
            case KEY_SHOOT -> gameState.shootHeld = true;
            case KEY_VIEW -> gameState.toggleCamera();

            case KEY_DBG_COLL -> gameState.toggleCollisions();
            case KEY_DBG_CHGBIOME -> gameState.forceBiomeChange();
            case KEY_DBG_DIE -> gameState.killPlayer();
            case KEY_DBG_PARTICLE_REPORT -> gameState.printDebugInfo();

            case KEY_DBG_SCRLUP -> gameState.scrollUp();
            case KEY_DBG_SCRLDOWN -> gameState.scrollDown();

            case KEY_DBG_RESET -> resetFlag = true;
        }
    }

    @Override
    public void keyReleased()
    {
        // if shift/caps lock is on, change to lowercase
        if (key >= 'A' && key <= 'Z')
        {
            key += 'a' - 'A';
        }
        switch(key)
        {
            case KEY_LEFT -> gameState.leftHeld = false;
            case KEY_RIGHT -> gameState.rightHeld = false;
            case KEY_UP -> gameState.upHeld = false;
            case KEY_DOWN -> gameState.downHeld = false;
            case KEY_SHOOT -> gameState.shootHeld = false;
        }
    }

    public void toggleProjection(boolean ortho)
    {
        resetMatrix();
        if (ortho)
        {
            setProjection(orthoProjection);
            camera(CAM_X, CAM_Y_START, CAM_Z_START,
                    0, 0, 0,
                    CAM_UP_X_START, CAM_UP_Y_START, CAM_UP_Z_START);
        }
        else
        {
            setProjection(frustumProjection);
            camera(CAM_X, CAM_Y_END, CAM_Z_END,
                    0, 0, 0,
                    CAM_UP_X_END, CAM_UP_Y_END, CAM_UP_Z_END);
        }
    }

    public void interpolateCamera(float t)
    {
        float
                camY = expInterpFloat(t, CAM_Y_START, CAM_Y_END),
                camZ = expInterpFloat(1.f - t, CAM_Z_END, CAM_Z_START),
                camUpX = lerpFloat(t, CAM_UP_X_START, CAM_UP_X_END),
                camUpY = expInterpFloat(t, CAM_UP_Y_START, CAM_UP_Y_END),
                camUpZ = expInterpFloat(1.f - t, CAM_UP_Z_END, CAM_UP_Z_START);

        resetMatrix();
        setProjection(frustumProjection);
        camera(CAM_X, camY, camZ,
                0, 0, 0,
                camUpX, camUpY, camUpZ);
        // crush z axis as we transition to/from ortho
        scale(1.f, 1.f, t * t);
    }

    private void setupGraphics()
    {
        this.graphics = (PGraphicsOpenGL) g;
    }

    private void setupProjections()
    {
        // create orthographic projection
        float halfW = ORTHO_WIDTH / 2.f;
        float halfH = halfW * (float)this.height / (float)this.width;
        ortho(-halfW, halfW, halfH, -halfH, NEAR, FAR); // y-up coordinate system
        this.orthoProjection = getProjection();

        // create perspective projection
        float aspect = (float)this.width / (float)this.height;
        float vertFOV = (float)(2.0 * Math.atan((0.5 * this.height) / (0.5 * this.width / Math.tan(H_FOV / 2.0))));
        perspective(vertFOV, aspect, NEAR, FAR);
        fixFrustumYFlip(); // y-up coordinate system
        frustumProjection = getProjection();
    }

    private void setProjection(PMatrix3D projMatrix)
    {
        assert this.graphics != null : "Fatal error: no PGraphicsOpenGL context!";
        graphics.projection.set(projMatrix);
        graphics.updateProjmodelview();
    }

    private PMatrix3D getProjection()
    {
        assert this.graphics != null : "Fatal error: no PGraphicsOpenGL context!";
        return this.graphics.projection.get();
    }

    private void fixFrustumYFlip()
    {
        PMatrix3D proj = getProjection();
        proj.preApply(new PMatrix3D(
                1, 0, 0, 0,
                0, -1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        ));
        setProjection(proj);
    }
}
