package org.example;

public class Chunk {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int DEPTH = 16;
    
    private BlockType[][][] blocks = new BlockType[WIDTH][HEIGHT][DEPTH];

    public Chunk() {
        // Simple terrain: stone in the bottom half, air above.
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int z = 0; z < DEPTH; z++) {
                    blocks[x][y][z] = (y < HEIGHT / 2) ? BlockType.STONE : BlockType.AIR;
                }
            }
        }
    }

    public BlockType getBlock(int x, int y, int z) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || z < 0 || z >= DEPTH)
            return BlockType.AIR;
        return blocks[x][y][z];
    }
}
