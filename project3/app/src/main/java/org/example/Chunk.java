package org.example;

import java.util.Random;

public class Chunk {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int DEPTH = 16;
    
    private BlockType[][][] blocks = new BlockType[WIDTH][HEIGHT][DEPTH];

    public Chunk() {
        Random rand = new Random(12345); // fixed seed for consistency
        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < DEPTH; z++) {
                int groundHeight = rand.nextInt(9) + 4; // between 4 and 12
                for (int y = 0; y < HEIGHT; y++) {
                    if (y < groundHeight) {
                        blocks[x][y][z] = BlockType.STONE;
                    } else {
                        blocks[x][y][z] = BlockType.AIR;
                    }
                }
            }
        }
    }

    public BlockType getBlock(int x, int y, int z) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || z < 0 || z >= DEPTH)
            return BlockType.AIR;
        return blocks[x][y][z];
    }
    
    // New method: set a block to AIR (destroy it).
    public void destroyBlock(int x, int y, int z) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && z >= 0 && z < DEPTH) {
            blocks[x][y][z] = BlockType.AIR;
        }
    }
}
