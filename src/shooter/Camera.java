package shooter;

import static shooter.Interpolate.*;

public class Camera
{
    // length of camera switch animation, in milliseconds
    private static final float CAM_SWITCH_LENGTH = 1000.f;

    private final GameSketch sketch; // change to a parameter for setupCamera?
    private boolean orthoMode;
    private boolean switchingToFrustum;
    private boolean switchingToOrtho;
    private float cameraTimer;

    public Camera(GameSketch sketch)
    {
        this.sketch = sketch;
        orthoMode = true;
        switchingToFrustum = false;
        switchingToOrtho = false;
        // cameraSteps = -1;
        cameraTimer = -1.f;
    }

    public boolean isUsingOrtho()
    {
        return orthoMode && !switchingToOrtho && !switchingToFrustum && cameraTimer >= 0.f;
    }

    public void setupCamera(float elapsedMillis)
    {
        if (switchingToFrustum)
        {
            sketch.interpolateCamera(cameraTimer / CAM_SWITCH_LENGTH);
            if ((cameraTimer += elapsedMillis) >= CAM_SWITCH_LENGTH)
            {
                cameraTimer = -1.f;
                switchingToFrustum = false;
                orthoMode = false;
            }
        }
        else if (switchingToOrtho)
        {
            sketch.interpolateCamera(1.f - (cameraTimer / CAM_SWITCH_LENGTH));
            if ((cameraTimer += elapsedMillis) >= CAM_SWITCH_LENGTH)
            {
                cameraTimer = -1.f;
                switchingToOrtho = false;
                orthoMode = true;
            }
        }
        else if (cameraTimer < 0.f)
        {
            cameraTimer = 0.f;
            updateCameraMode();
        }
    }

    public void toggleCameraMode()
    {
        // only switch if not already transitioning
        if (!switchingToOrtho && !switchingToFrustum)
        {
            orthoMode = !orthoMode;
            cameraTimer = 0.f;
            if (orthoMode)
                switchingToOrtho = true;
            else
                switchingToFrustum = true;
        }
    }

    private void updateCameraMode()
    {
        sketch.toggleProjection(orthoMode);
    }
}
