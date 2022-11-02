package shooter.particles;

import processing.core.PConstants;
import processing.core.PImage;
import shooter.GameSketch;

import static shooter.Textures.xNDCToTexture;
import static shooter.Textures.yNDCToTexture;

public class LifespanParticle extends Particle
{
    public enum Shape { DIAMOND, TRIANGLE }

    private final Shape shape;
    private final float lifespan;
    private float lifeCounter;

    public LifespanParticle(Shape sh, float life,
                            float[] pos, float[] vel, float[] accel,
                            float[] angle, float[] rot, float[] size,
                            PImage tex, int tint)
    {
        super(pos, vel, accel, angle, rot, size, 0.f, tex, tint);
        this.shape = sh;
        this.lifespan = life;
        this.lifeCounter = 0.f;
    }

    public void update(float elapsedMillis)
    {
        for (int i = I_X; i <= I_Z; i++)
        {
            vel[i] += accel[i] * elapsedMillis;
            pos[i] += vel[i] * elapsedMillis;
            angle[i] += rotation[i] * elapsedMillis;
        }
        if ((lifeCounter += elapsedMillis) >= lifespan)
        {
            dead = true;
        }

        lastMillis = elapsedMillis;
    }

    public void draw(GameSketch gs)
    {
        gs.pushMatrix();

        gs.translate(pos[I_X], pos[I_Y], pos[I_Z]);
        gs.scale(size[I_X], size[I_Y], size[I_Z]);
        gs.rotateZ(angle[I_Z]);
        gs.rotateY(angle[I_Y]);
        gs.rotateX(angle[I_X]);

        gs.tint(this.tint);
        gs.fill(this.tint);

        switch (shape)
        {
            case DIAMOND:
                drawDiamond(gs);
            case TRIANGLE:
                drawTriangle(gs);
        }

        gs.noTint();
        gs.popMatrix();
    }

    private static final float
            D_LEFT_X = -1.f,
            D_MIDLEFT_X = -0.5f,
            D_MID_X = 0.f,
            D_MIDRIGHT_X = 0.5f,
            D_RIGHT_X = 1.f,
            D_TOP_Y = 1.f,
            D_TOPMID_Y = 0.5f,
            D_MID_Y = 0.f,
            D_BOTMID_Y = -0.5f,
            D_BOT_Y = -1.f,

            D_TEX_LEFT_X = xNDCToTexture(D_LEFT_X),
            D_TEX_MIDLEFT_X = xNDCToTexture(D_MIDLEFT_X),
            D_TEX_MID_X = xNDCToTexture(D_MID_X),
            D_TEX_MIDRIGHT_X = xNDCToTexture(D_MIDRIGHT_X),
            D_TEX_RIGHT_X = xNDCToTexture(D_RIGHT_X),
            D_TEX_TOP_Y = yNDCToTexture(D_TOP_Y),
            D_TEX_TOPMID_Y = yNDCToTexture(D_TOPMID_Y),
            D_TEX_MID_Y = yNDCToTexture(D_MID_Y),
            D_TEX_BOTMID_Y = yNDCToTexture(D_BOTMID_Y),
            D_TEX_BOT_Y = yNDCToTexture(D_BOT_Y);

    private void drawDiamond(GameSketch gs)
    {
        // top and bottom tips
        gs.beginShape(PConstants.TRIANGLE_STRIP);
        gs.texture(this.staticTexture);
        gs.vertex(D_MID_X, D_TOP_Y, 0.f, D_TEX_MID_X, D_TEX_TOP_Y);
        gs.vertex(D_MIDLEFT_X, D_MID_Y, 0.f, D_TEX_MIDLEFT_X, D_TEX_MID_Y);
        gs.vertex(D_MIDRIGHT_X, D_MID_Y, 0.f, D_TEX_MIDRIGHT_X, D_TEX_MID_Y);
        gs.vertex(D_MID_X, D_BOT_Y, 0.f, D_TEX_MID_X, D_TEX_BOT_Y);
        gs.endShape();

        // left and right tips
        gs.beginShape(PConstants.TRIANGLE_STRIP);
        gs.texture(this.staticTexture);
        gs.vertex(D_LEFT_X, D_MID_Y, 0.f, D_TEX_LEFT_X, D_TEX_MID_Y);
        gs.vertex(D_MID_X, D_BOTMID_Y, 0.f, D_TEX_MID_X, D_TEX_BOTMID_Y);
        gs.vertex(D_MID_X, D_TOPMID_Y, 0.f, D_TEX_MID_X, D_TEX_TOPMID_Y);
        gs.vertex(D_RIGHT_X, D_MID_Y, 0.f, D_TEX_RIGHT_X, D_TEX_MID_Y);
        gs.endShape();
    }

    private static final float
            TRIANGLE_HEIGHT = (float)Math.sin(Math.toRadians(60.f)),
            T_TOP_X = 0.f,
            T_TOP_Y = TRIANGLE_HEIGHT / 2.f,
            T_LEFT_X = -1.f,
            T_LEFT_Y = -TRIANGLE_HEIGHT / 2.f,
            T_RIGHT_X = 1.f,
            T_RIGHT_Y = -TRIANGLE_HEIGHT / 2.f,

            T_TEX_TOP_X = xNDCToTexture(T_TOP_X),
            T_TEX_TOP_Y = yNDCToTexture(T_TOP_Y),
            T_TEX_LEFT_X = xNDCToTexture(T_LEFT_X),
            T_TEX_LEFT_Y = yNDCToTexture(T_LEFT_Y),
            T_TEX_RIGHT_X = xNDCToTexture(T_RIGHT_X),
            T_TEX_RIGHT_Y = yNDCToTexture(T_RIGHT_Y);

    private void drawTriangle(GameSketch gs)
    {
        gs.beginShape(PConstants.TRIANGLES);
        gs.texture(this.staticTexture);

        gs.vertex(T_TOP_X, T_TOP_Y, 0.f, T_TEX_TOP_X, T_TEX_TOP_Y);
        gs.vertex(T_LEFT_X, T_LEFT_Y, 0.f, T_TEX_LEFT_X, T_TEX_LEFT_Y);
        gs.vertex(T_RIGHT_X, T_RIGHT_Y, 0.f, T_TEX_RIGHT_X, T_TEX_RIGHT_Y);

        gs.endShape();
    }
}
