package shooter.actors;

import processing.core.PImage;
import processing.core.PMatrix3D;
import shooter.GameConstants;
import shooter.GameSketch;
import shooter.Matrices;
import shooter.particles.ParticleTracker;

import static processing.core.PConstants.TRIANGLE_STRIP;
import static shooter.Textures.xNDCToTexture;
import static shooter.Textures.yNDCToTexture;

/* Enemy
 *
 * An actor which is given a starting point at one of the
 * screen edges and parameters for the way it moves across
 * the screen (such as velocity, rotation, etc). The way
 * it attacks the player is determined by the type of
 * projectile it fires.
 */

public class Enemy extends Actor implements GameConstants
{
    private static final float
            // screen bounds for deleting enemies
            LEFT_BOUND = -2.5f,
            RIGHT_BOUND = 2.5f,
            TOP_BOUND = 2.5f,
            BOTTOM_BOUND = -2.f,
            ATTACK_WAIT_MIN = 500.f,
            ATTACK_WAIT_MAX = 1500.f;
    private static final int MAX_ATTACKS = 2;

    private final ParticleTracker particles;

    private final float[] auxVel; // needed for matrix multiplication

    private float attackWaitTimer, attackTime;
    private int shotsTaken;

    public Enemy(ParticleTracker particles, float[] size, float collisionRadius, PImage staticTexture,
                 int tint, float[] startPos, float[] startAngle, float[] vel, float[] rotation)
    {
        super(size, collisionRadius, staticTexture, tint);
        this.particles = particles;
        System.arraycopy(startPos, 0, this.pos, 0, 3);
        System.arraycopy(startAngle, 0, this.angle, 0, 3);
        System.arraycopy(vel, 0, this.vel, 0, 3);
        System.arraycopy(rotation, 0, this.rotation, 0, 3);
        this.auxVel = new float[3];
        this.attackWaitTimer = 0.f;
        this.attackTime = (float)(Math.random() * (ATTACK_WAIT_MAX - ATTACK_WAIT_MIN)) + ATTACK_WAIT_MIN;
        this.shotsTaken = 0;
    }

    // rotate enemy's angle by rotation, rotate velocity with rotation matrix
    public void update(float elapsedMillis)
    {
        PMatrix3D rotMatrix = Matrices.rotateZ(rotation[I_Z] * elapsedMillis);
        rotMatrix.mult(vel, auxVel);
        System.arraycopy(auxVel, 0, vel, 0, 3);

        for (int i = I_X; i <= I_Z; i++)
        {
            angle[i] += rotation[i] * elapsedMillis;
            pos[i] += vel[i] * elapsedMillis;
        }

        if (shotsTaken < MAX_ATTACKS && (attackWaitTimer += elapsedMillis) >= attackTime)
        {
            ++shotsTaken;
            particles.enemyShoot(this.pos);
            this.attackWaitTimer = 0.f;
            this.attackTime = (float)(Math.random() * (ATTACK_WAIT_MAX - ATTACK_WAIT_MIN)) + ATTACK_WAIT_MIN;
        }

        // determine if enemy should be deleted
        if (pos[I_X] < LEFT_BOUND || pos[I_X] > RIGHT_BOUND || pos[I_Y] > TOP_BOUND || pos[I_Y] < BOTTOM_BOUND)
            this.dead = true;

        lastMillis = elapsedMillis;
    }

    // constants for drawing the ship's vertices
    private static final float
            // COCKPIT_TIP_X = 0.f,
            // COCKPIT_TIP_Y = 1.f,
            // WINGS_FRONT_X = 5.f / 16.f,
            // WINGS_FRONT_Y = -2.f / 16.f,
            // WINGS_BACK_X = 1.f,
            // WINGS_BACK_Y = -12.f / 16.f,
            // SHIP_BACK_X = 1.f,
            // SHIP_BACK_Y = -1.f,
            COCKPIT_TIP_X = 1.f,
            COCKPIT_TIP_Y = 0.f,
            WINGS_FRONT_X = -2.f / 16.f,
            WINGS_FRONT_Y = 5.f / 16.f,
            WINGS_BACK_X = -12.f / 16.f,
            WINGS_BACK_Y = 1.f,
            SHIP_BACK_X = -1.f,
            SHIP_BACK_Y = 1.f,

            TEX_COCKPIT_X = xNDCToTexture(COCKPIT_TIP_X),
            TEX_COCKPIT_Y = yNDCToTexture(COCKPIT_TIP_Y),
            // TEX_WFRONT_X_L = xNDCToTexture(-WINGS_FRONT_X),
            // TEX_WFRONT_X_R = xNDCToTexture(WINGS_FRONT_X),
            // TEX_WFRONT_Y = yNDCToTexture(WINGS_FRONT_Y),
            TEX_WFRONT_X = xNDCToTexture(WINGS_FRONT_X),
            TEX_WFRONT_Y_T = yNDCToTexture(WINGS_FRONT_Y),
            TEX_WFRONT_Y_B = yNDCToTexture(-WINGS_FRONT_Y),
            // TEX_WBACK_X_L = xNDCToTexture(-WINGS_BACK_X),
            // TEX_WBACK_X_R = xNDCToTexture(WINGS_BACK_X),
            // TEX_WBACK_Y = yNDCToTexture(WINGS_BACK_Y),
            TEX_WBACK_X = xNDCToTexture(WINGS_BACK_X),
            TEX_WBACK_Y_T = yNDCToTexture(WINGS_BACK_Y),
            TEX_WBACK_Y_B = yNDCToTexture(-WINGS_BACK_Y),
            // TEX_SBACK_X_L = xNDCToTexture(-SHIP_BACK_X),
            // TEX_SBACK_X_R = xNDCToTexture(SHIP_BACK_X),
            // TEX_SBACK_Y = yNDCToTexture(SHIP_BACK_Y);
            TEX_SBACK_X = xNDCToTexture(SHIP_BACK_X),
            TEX_SBACK_Y_T = yNDCToTexture(SHIP_BACK_Y),
            TEX_SBACK_Y_B = yNDCToTexture(-SHIP_BACK_Y);

    public void draw(GameSketch gs)
    {
        gs.pushMatrix();

        gs.translate(pos[I_X], pos[I_Y], pos[I_Z]);
        gs.scale(size[I_X], size[I_Y], size[I_Z]);
        gs.rotateZ(angle[I_Z]);
        gs.rotateY(angle[I_Y]);

        gs.beginShape(TRIANGLE_STRIP);
        gs.texture(this.staticTexture);

        // cockpit - 1 triangle
        gs.vertex(COCKPIT_TIP_X, COCKPIT_TIP_Y, 0, TEX_COCKPIT_X, TEX_COCKPIT_Y);
        gs.vertex(WINGS_FRONT_X, WINGS_FRONT_Y, 0, TEX_WFRONT_X, TEX_WFRONT_Y_T);
        gs.vertex(WINGS_FRONT_X, -WINGS_FRONT_Y, 0, TEX_WFRONT_X, TEX_WFRONT_Y_B);
        // gs.vertex(-WINGS_FRONT_X, WINGS_FRONT_Y, 0, TEX_WFRONT_X_L, TEX_WFRONT_Y);
        // gs.vertex(WINGS_FRONT_X, WINGS_FRONT_Y, 0, TEX_WFRONT_X_R, TEX_WFRONT_Y);
        // wings - 2 triangles
        // gs.vertex(-WINGS_BACK_X, WINGS_BACK_Y, 0, TEX_WBACK_X_L, TEX_WBACK_Y);
        // gs.vertex(WINGS_BACK_X, WINGS_BACK_Y, 0, TEX_WBACK_X_R, TEX_WBACK_Y);
        gs.vertex(WINGS_BACK_X, WINGS_BACK_Y, 0, TEX_WBACK_X, TEX_WBACK_Y_T);
        gs.vertex(WINGS_BACK_X, -WINGS_BACK_Y, 0, TEX_WBACK_X, TEX_WBACK_Y_B);
        // back - 2 triangles
        // gs.vertex(-SHIP_BACK_X, SHIP_BACK_Y, 0, TEX_SBACK_X_L, TEX_SBACK_Y);
        // gs.vertex(SHIP_BACK_X, SHIP_BACK_Y, 0, TEX_SBACK_X_R, TEX_SBACK_Y);
        gs.vertex(SHIP_BACK_X, SHIP_BACK_Y, 0, TEX_SBACK_X, TEX_SBACK_Y_T);
        gs.vertex(SHIP_BACK_X, -SHIP_BACK_Y, 0, TEX_SBACK_X, TEX_SBACK_Y_B);

        gs.endShape();
        gs.popMatrix();
    }
}

