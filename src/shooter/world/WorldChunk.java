package shooter.world;

import shooter.GameSketch;
import shooter.Textures;

import java.util.LinkedList;
import java.util.List;

import static shooter.GameMath.randRange;

public class WorldChunk
{
    public static final int
            CHUNK_W = 44,
            CHUNK_H = 36;
    public static final float TILE_SIZE = 0.2f;
    private static final int
            HILL_SIZE = 17; // length & width of a hill in tiles

    // tiles that constitute this chunk
    private final WorldTile[] tiles;
    // height map used to generate tiles
    private final float[][] tileHeights;
    // 3D scenery objects
    private final List<WorldObject> decorations;


    public WorldChunk(Biome biome, float[][] prevHeightMap)
    {
        tiles = new WorldTile[CHUNK_W * CHUNK_H];
        tileHeights = new float[CHUNK_W][CHUNK_H + HILL_SIZE];
        decorations = new LinkedList<>();

        if (prevHeightMap != null)
        {
            for (int x = 0; x < CHUNK_W; x++)
            {
                System.arraycopy(prevHeightMap[x], CHUNK_H, tileHeights[x], 0, HILL_SIZE);
            }
        }

        // generate terrain
        switch (biome) {
            case HILLS -> generateHills();
            case DESERT -> generateDesert();
            case ICE -> generateIce();
        }
    }

    private void generateHills()
    {
        final int MIN_HILLS = 8, MAX_HILLS = 12;
        final float HILL_MIN_HEIGHT = 1.f, HILL_MAX_HEIGHT = 4.f;

        int numHills = (int)(Math.random() * (MAX_HILLS - MIN_HILLS + 1)) + MIN_HILLS;
        // generate hills, place in height map
        for (int h = 0; h < numHills; h++)
        {
            int xOff = (int)randRange(-HILL_SIZE, CHUNK_W);
            int yOff = (int)(Math.random() * CHUNK_H);
            hillGenerator(xOff, yOff, HILL_MIN_HEIGHT, HILL_MAX_HEIGHT);
        }

        // using the heightmap we generated, generate the chunk's tiles
        for (int x = 0; x < CHUNK_W; x++)
            for (int y = 0; y < CHUNK_H; y++)
                tiles[x + y * CHUNK_W] = new WorldTile (
                        tileHeights[x][y],
                        Textures.HILLS_GRASS,
                        Textures.HILLS_DIRT);
    }

    private void generateDesert() {
        final int MIN_HILLS = 4, MAX_HILLS = 8, NUM_PILLARS = 2, NUM_PYRAMIDS = 2;
        final float HILL_MIN_HEIGHT = 0.f, HILL_MAX_HEIGHT = 0.5f;

        int numHills = (int) (Math.random() * (MAX_HILLS - MIN_HILLS + 1)) + MIN_HILLS;
        // generate hills, place in height map
        for (int h = 0; h < numHills; h++) {
            int xOff = (int)randRange(-HILL_SIZE, CHUNK_W);
            int yOff = (int)(Math.random() * CHUNK_H);
            hillGenerator(xOff, yOff, HILL_MIN_HEIGHT, HILL_MAX_HEIGHT);
        }

        // using the heightmap we generated, generate the chunk's tiles
        for (int x = 0; x < CHUNK_W; x++)
        {
            for (int y = 0; y < CHUNK_H; y++) {
                tiles[x + y * CHUNK_W] = new WorldTile(
                        tileHeights[x][y],
                        Textures.DESERT_SAND,
                        Textures.DESERT_SANDSTONE
                );
            }
        }

        // generate WorldObjects
        for (int i = 0; i < NUM_PILLARS; i++)
            decorations.add(new Pillar());
        for (int i = 0; i < NUM_PYRAMIDS; i++)
            decorations.add(new Pyramid());
    }

    private void generateIce()
    {
        final int MIN_HILLS = 6, MAX_HILLS = 12,
                MIN_MESAS = 1, MAX_MESAS = 2,
                NUM_SNOWMEN = 2;
        final float HILL_MIN_HEIGHT = 0.25f, HILL_MAX_HEIGHT = 1.f, MESA_HEIGHT = 3.f;

        // generate hills
        int numHills = (int) (Math.random() * (MAX_HILLS - MIN_HILLS + 1)) + MIN_HILLS;
        for (int h = 0; h < numHills; h++)
        {
            int xOff = (int)randRange(-HILL_SIZE, CHUNK_W);
            int yOff = (int)(Math.random() * CHUNK_H);
            hillGenerator(xOff, yOff, HILL_MIN_HEIGHT, HILL_MAX_HEIGHT);
        }

        // generate tiles with tilemap
        for (int x = 0; x < CHUNK_W; x++)
        {
            for (int y = 0; y < CHUNK_H; y++) {
                tiles[x + y * CHUNK_W] = new WorldTile(
                        tileHeights[x][y],
                        Textures.ICE_SNOW,
                        Textures.ICE_SNOW
                );
            }
        }

        // generate mesas with ice walls, replace existing tiles with them
        int numMesas = (int) (Math.random() * (MAX_MESAS - MIN_MESAS + 1)) + MIN_MESAS;
        for (int m = 0; m < numMesas; m++)
        {
            float[][] mesa = MesaGenerator.generateMesa(0.f, MESA_HEIGHT);
            int xOff = (int)(Math.random() * (CHUNK_W - mesa[0].length));
            int yOff = (int)(Math.random() * (CHUNK_H - mesa.length));
            for (int y = 0; y < mesa.length; y++)
            {
                for (int x = 0; x < mesa[0].length; x++)
                {
                    WorldTile t = tiles[x + xOff + (y + yOff) * CHUNK_W];
                    t.increaseHeight(mesa[y][x]);
                    t.sideTexture = Textures.ICE_WALL;
                }
            }
        }

        for (int s = 0; s < NUM_SNOWMEN; s++)
            decorations.add(new Snowman(tiles));
    }

    public float[][] getHeightMap()
    {
        return tileHeights;
    }

    private void hillGenerator(int xOffset, int yOffset, float minHeight, float maxHeight)
    {
        final float HEIGHT_VARIATION = 0.5f;
        final float[][] heightMap = new float[HILL_SIZE][HILL_SIZE];

        // initialize four corners to zero and centre to random height
        heightMap[HILL_SIZE / 2][HILL_SIZE / 2] = (float)(Math.random() * (maxHeight - minHeight) + minHeight);

        // perform diamond-square algorithm
        int currSize = HILL_SIZE / 2;
        float currRand = HEIGHT_VARIATION;
        while (currSize > 1)
        {
            int halfSize = currSize / 2;

            // diamond step
            for (int x = 0; x < HILL_SIZE - 1; x += currSize)
            {
                for (int y = 0; y < HILL_SIZE - 1; y += currSize)
                {
                    float currHeight = heightMap[x][y]
                            + heightMap[x + currSize][y]
                            + heightMap[x][y + currSize]
                            + heightMap[x + currSize][y + currSize];
                    currHeight = currHeight / 4.f + (float)(Math.random() * currRand * 2) - currRand;
                    heightMap[x + halfSize][y + halfSize] = currHeight;
                }
            }

            // square step
            for (int x = 0; x < HILL_SIZE - 1; x += halfSize)
            {
                for (int y = (x + halfSize) % currSize; y < HILL_SIZE - 1; y += currSize)
                {
                    float currHeight;
                    // left neighbor
                    currHeight = x < halfSize ? 0 : heightMap[x - halfSize][y];
                    // right neighbor
                    currHeight += x + halfSize >= HILL_SIZE ? 0 : heightMap[x + halfSize][y];
                    // top neighbor
                    currHeight += y < halfSize ? 0 : heightMap[x][y - halfSize];
                    // bottom neighbor
                    currHeight += y + halfSize >= HILL_SIZE ? 0 : heightMap[x][y + halfSize];

                    currHeight = currHeight / 4.f + (float)(Math.random() * currRand * 2) - currRand;
                    heightMap[x][y] = currHeight;
                }
            }

            // decrease size of area and variation
            currRand /= 2.f;
            currSize /= 2;
        }

        // re-generate height for centre tile with heights of its neighbors
        // otherwise, the centre tile looks far too tall
        int mid = HILL_SIZE / 2;
        float centre = heightMap[mid-1][mid]
                + heightMap[mid+1][mid]
                + heightMap[mid][mid-1]
                + heightMap[mid][mid+1];
        centre = centre / 4.f + (float)Math.random() * HEIGHT_VARIATION;
        heightMap[mid][mid] = centre;

        // dump heightMap to this chunk's tile map
        // clamp x and y indexes to start of dest
        // break if x or y go beyond end of dest
        for (int x = Math.max(0, -xOffset); x < HILL_SIZE; x++)
        {
            int destX = x + xOffset;
            if (destX >= CHUNK_W)
                break;

            for (int y = Math.max(0, -yOffset); y < HILL_SIZE; y++)
            {
                int destY = y + yOffset;
                if (destY >= CHUNK_H + HILL_SIZE)
                    break;

                float newHeight = heightMap[x][y];
                if (newHeight > this.tileHeights[destX][destY])
                    this.tileHeights[destX][destY] = newHeight;
            }
        }
    }

    // Draw the objects and tiles of this chunk.
    // The tops of each tile are drawn before the
    // sides to minimize how many times the current
    // texture needs to be switched.
    // Precondition: The current matrix is scaled to
    // the size of one WorldTile.
    public void drawChunk(GameSketch gs)
    {
        drawWorldObjects(gs);

        // draw the tops of each tile
        gs.pushMatrix();
        // begin drawing in the lower left (-ve x, -ve y)
        gs.translate(-CHUNK_W/2.f + 0.5f, -CHUNK_H/2.f, 0.f);
        // draw each row, bottom-to-top
        for (int y = 0; y < CHUNK_H; y++)
        {
            gs.pushMatrix();
            for (int x = 0; x < CHUNK_W; x++)
            {
                tiles[y * CHUNK_W + x].drawTop(gs);
                gs.translate(1.f, 0.f, 0.f);
            }
            // carriage return
            gs.popMatrix();
            // line feed
            gs.translate(0.f, 1.f, 0.f);
        }
        gs.popMatrix();

        // do same algorithm as above, but for the sides of each tile
        gs.pushMatrix();
        gs.translate(-CHUNK_W/2.f + 0.5f, -CHUNK_H/2.f, 0.f);
        for (int y = 0; y < CHUNK_H; y++)
        {
            gs.pushMatrix();
            for (int x = 0; x < CHUNK_W; x++)
            {
                tiles[y * CHUNK_W + x].drawSides(gs);
                gs.translate(1.f, 0.f, 0.f);
            }
            gs.popMatrix();
            gs.translate(0.f, 1.f, 0.f);
        }
        gs.popMatrix();
    }

    private void drawWorldObjects(GameSketch gs)
    {
        for (WorldObject o : decorations)
            o.draw(gs);
    }
}
