package shooter.world;

import shooter.GameSketch;

public class World
{
    private static final int
        NUM_CHUNKS = 2,
        CHUNKS_PER_BIOME = 16;
    private static final float
        SCROLL_OFFSET = WorldChunk.CHUNK_H / 5.f;

    // current chunks
    private final WorldChunk[] chunks;

    private float scroll;
    private int chunkCount;
    private Biome biome;

    public World()
    {
        scroll = 0.f;
        chunkCount = 0;
        biome = Biome.HILLS;

        chunks = new WorldChunk[NUM_CHUNKS];
        chunks[0] = new WorldChunk(biome, null);
        for (int c = 1; c < NUM_CHUNKS; c++)
            chunks[c] = new WorldChunk(biome, chunks[c-1].getHeightMap());
    }

    public void drawWorld(GameSketch gs)
    {
        gs.pushMatrix();
        gs.noStroke();
        // we will work in 1x1 tile units
        gs.scale(WorldChunk.TILE_SIZE);
        // translate vertically by scroll value
        gs.translate(0, (-scroll * WorldChunk.CHUNK_H) + SCROLL_OFFSET, 0);

        // draw each chunk, lowest-to-highest
        for (int c = 0; c < NUM_CHUNKS; c++)
        {
            gs.pushMatrix();
            gs.translate(0, c * WorldChunk.CHUNK_H, 0);
            chunks[c].drawChunk(gs);
            gs.popMatrix();
        }
        gs.popMatrix();
    }

    public void scrollWorld(float delta)
    {
        this.scroll += delta;
        while (scroll >= 1.f)
        {
            // discard first chunk, shift the rest down, add new last chunk
            for (int i = 1; i < NUM_CHUNKS; i++)
                chunks[i-1] = chunks[i];
            chunks[NUM_CHUNKS - 1] = new WorldChunk(biome, chunks[NUM_CHUNKS - 2].getHeightMap());

            if (++chunkCount >= CHUNKS_PER_BIOME)
            {
                cycleBiome();
                chunkCount = 0;
            }

            scroll -= 1.f;
        }
    }

    public void cycleBiome()
    {
        switch(this.biome)
        {
            case HILLS -> biome = Biome.DESERT;
            case DESERT -> biome = Biome.ICE;
            case ICE -> biome = Biome.HILLS;
        }
    }
}
