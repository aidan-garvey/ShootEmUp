package shooter.particles;

import processing.core.PMatrix3D;
import processing.core.PVector;
import shooter.GameConstants;
import shooter.GameMath;
import shooter.GameSketch;
import shooter.Matrices;
import shooter.actors.*;
import shooter.particles.DeathEffect;
import shooter.particles.EffectFactory;
import shooter.particles.ParticleEmitter;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ParticleTracker implements GameConstants
{
    // the current player (when player loses a life and respawns, a new player object is given to this class)
    private Player player;
    // having the player in an ArrayList lets it be passed to the collision checking methods in this class
    private final List<Actor> playerInList = new LinkedList<>();
    // the bullets fired by the player, deleted once offscreen
    private final List<Actor> playerBullets = new LinkedList<>();
    // current enemies, get removed once their movement animation is finished
    private final List<Actor> enemies = new LinkedList<>();
    // the bullets fired by all enemies, deleted once offscreen
    private final List<Actor> enemyBullets = new LinkedList<>();
    // effects produced by an actor colliding, such as an explosion
    private final List<ParticleEmitter> effects = new LinkedList<>();

    private static final int MAX_ENEMIES = 4;
    private static final float
            // units travelled per frame by enemy bullets
            // units travelled per millisecond by enemy bullets
            ENEMY_BULLET_SPEED = 0.0018f,
            // each enemy that gets killed results in this value being added to difficulty
            DIFFICULTY_PER_ENEMY = 1.f / 20000.f,
            // start value for difficulty
            START_DIFFICULTY = 1.f / 4000.f;
    private static final float[] PLAYER_BULLET_SPEED = {0.006f, 0.f, 0.f};// {0.f, 0.006f, 0.f};

    private static float
            // Each millisecond, spawnCounter is increased by this value
            difficulty = START_DIFFICULTY,
            // When this value is >= 1, an enemy is spawned
            spawnCounter = 0.f;

    public void setPlayer(Player p)
    {
        player = p;
        playerInList.clear();
        playerInList.add(p);
    }

    public void update(float elapsedMillis, boolean collisionsOn, boolean gameOver)
    {
        // update tracked particles, delete if dead
        player.update(elapsedMillis);
        updateList(playerBullets, elapsedMillis);
        updateList(enemies, elapsedMillis);
        updateList(enemyBullets, elapsedMillis);

        // update particle emitters, delete if dead
        ListIterator<ParticleEmitter> fx = effects.listIterator();
        while (fx.hasNext())
        {
            ParticleEmitter e = fx.next();
            e.update(elapsedMillis);
            if (e.isExpired())
                fx.remove();
        }

        // check for collisions
        if (collisionsOn)
        {
            // player's bullets vs. enemy bullets
            checkCollisions(enemyBullets, playerBullets);
            // player's bullets vs. enemies
            checkCollisions(enemies, playerBullets);
            if (!player.isImmune())
            {
                // enemies vs. player
                checkCollisions(playerInList, enemies);
                // enemy bullets vs. player
                checkCollisions(playerInList, enemyBullets);
            }
        }

        // perform on-death tasks for killed actors, then remove
        // them from the list, effectively deleting them
        int killedEnemies = handleKilledActors(enemies, DeathEffect.DEBRIS_EXPLOSION);
        handleKilledActors(enemyBullets, DeathEffect.BULLET_SPARK);
        handleKilledActors(playerBullets, DeathEffect.BULLET_SPARK);
        // here, the player is removed from playerInList,
        // but is still referenced by the player instance variable
        handleKilledActors(playerInList, DeathEffect.DEBRIS_EXPLOSION);

        if (!gameOver)
        {
            difficulty += killedEnemies * DIFFICULTY_PER_ENEMY;
            spawnCounter += difficulty * elapsedMillis;
            while (spawnCounter >= 1.f)
            {
                if (enemies.size() < MAX_ENEMIES)
                    spawnEnemy();
                spawnCounter -= 1.f;
            }
        }
    }

    public void drawParticles(GameSketch gs)
    {
        drawList(playerBullets, gs);
        drawList(enemies, gs);
        drawList(enemyBullets, gs);
        for (ParticleEmitter pe : effects)
            pe.draw(gs);
        player.draw(gs);
    }

    public void printDebugInfo()
    {
        System.out.printf("""
                Number of players: %d
                Number of enemies: %d
                Number of player bullets: %d
                Number of enemy bullets: %d
                Number of emitters: %d
                """, playerInList.size(), enemies.size(),
                playerBullets.size(), enemyBullets.size(),
                effects.size() + playerInList.size());
    }

    public void playerShoot(float[] origin, float[] offset, float zAngle)
    {
        final float[]
                posDest = new float[3],
                velDest = new float[3],
                angleDest = new float[3];
        // rotate the bullet's offset and velocity with a rotation matrix
        PMatrix3D angleMat = Matrices.rotateZ(zAngle);
        angleMat.mult(offset, posDest); // posDest gets rotated offset
        // add origin to offset for final starting position
        for (int i = I_X; i <= I_Z; i++)
            posDest[i] += origin[i];
        angleMat.mult(PLAYER_BULLET_SPEED, velDest); // velDest gets rotated velocity
        // rotate bullet's sprite by zAngle
        angleDest[I_Z] = zAngle;

        playerBullets.add(new PlayerBullet(posDest, velDest, angleDest));
    }

    // fire a bullet from the given position towards the player
    public void enemyShoot(float[] origin)
    {
        // don't shoot if player is killed
        if (player.isKilled()) return;

        final float[]
                velToPlayer,
                angle = new float[3],
                playerPos = new float[3],
                inaccuracy = {0.3f, 0.3f, 0.f};

        player.getPos(playerPos);
        for (int i = I_X; i <= I_Z; i++)
        {
            playerPos[i] += GameMath.biRand(inaccuracy[i]);
        }
        PVector toPlayer = new PVector (
                playerPos[I_X] - origin[I_X],
                playerPos[I_Y] - origin[I_Y],
                playerPos[I_Z] - origin[I_Z]);
        toPlayer.normalize();
        velToPlayer = new float[]{toPlayer.x * ENEMY_BULLET_SPEED,
                                  toPlayer.y * ENEMY_BULLET_SPEED,
                                  toPlayer.z * ENEMY_BULLET_SPEED};
        enemyBullets.add(new EnemyBullet(origin, velToPlayer, angle));
    }

    // Call on all particles in a list to update. If any particles are dead, remove them.
    private void updateList(List<Actor> l, float elapsedMillis)
    {
        ListIterator<Actor> actors = l.listIterator();
        while (actors.hasNext())
        {
            Actor a = actors.next();
            a.update(elapsedMillis);
            if (a.isDead())
                actors.remove();
        }
    }

    private void drawList(List<Actor> actors, GameSketch gs)
    {
        for (Actor a : actors)
            a.draw(gs);
    }

    private void spawnEnemy()
    {
        enemies.add(EnemyGenerator.generateEnemy(this));
    }

    private final float[] A_POS = new float[3], B_POS = new float[3];
    private void checkCollisions(List<Actor> listA, List<Actor> listB)
    {
        for (Actor a : listA)
        {
            if (a.isKilled())
                continue;
            a.getPos(A_POS);
            for (Actor b : listB)
            {
                if (b.isKilled())
                    continue;
                b.getPos(B_POS);

                float xDist = A_POS[I_X] - B_POS[I_X], yDist = A_POS[I_Y] - B_POS[I_Y];
                float distSquared = xDist * xDist + yDist * yDist;
                float radsSquared = a.getCollisionRadius() + b.getCollisionRadius();
                radsSquared *= radsSquared;
                if (distSquared <= radsSquared)
                {
                    a.setKilled();
                    b.setKilled();
                }
            }
        }
    }

    private final float[] ACTOR_POS = new float[3], ACTOR_VEL = new float[3];
    private int handleKilledActors(List<Actor> actorList, DeathEffect effect)
    {
        ListIterator<Actor> actors = actorList.listIterator();
        int numKilled = 0;

        while (actors.hasNext())
        {
            Actor a = actors.next();
            if (a.isKilled())
            {
                ++numKilled;
                if (effect != DeathEffect.NONE)
                {
                    a.getPos(ACTOR_POS);
                    if (effect == DeathEffect.DEBRIS_EXPLOSION)
                    {
                        a.getVel(ACTOR_VEL);
                        effects.add(EffectFactory.explosion(ACTOR_POS));
                        effects.add(EffectFactory.debris(ACTOR_POS, ACTOR_VEL));
                    }
                    else if (effect == DeathEffect.BULLET_SPARK)
                    {
                        effects.add(EffectFactory.bulletSpark(ACTOR_POS));
                    }
                }
                actors.remove();
            }
        }

        return numKilled;
    }
}
