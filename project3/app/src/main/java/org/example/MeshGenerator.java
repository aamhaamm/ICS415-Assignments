package org.example;

import java.util.ArrayList;
import java.util.List;

public class MeshGenerator {
    public static Mesh generateMesh(Chunk chunk) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int indexOffset = 0;

        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int y = 0; y < Chunk.HEIGHT; y++) {
                for (int z = 0; z < Chunk.DEPTH; z++) {
                    BlockType block = chunk.getBlock(x, y, z);
                    if (block == BlockType.AIR) continue;

                    // We'll pick a U offset if the block is SPECIAL
                    float blockUOffset = (block == BlockType.SPECIAL) ? 0.5f : 0.0f;

                    // front
                    if (chunk.getBlock(x, y, z + 1) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x,     y,     z+1},
                            new float[]{x+1,   y,     z+1},
                            new float[]{x+1,   y+1,   z+1},
                            new float[]{x,     y+1,   z+1},
                            0, 0, 1, indexOffset,
                            blockUOffset
                        );
                        indexOffset += 4;
                    }
                    // back
                    if (chunk.getBlock(x, y, z - 1) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x+1, y,   z},
                            new float[]{x,   y,   z},
                            new float[]{x,   y+1, z},
                            new float[]{x+1, y+1, z},
                            0, 0, -1, indexOffset,
                            blockUOffset
                        );
                        indexOffset += 4;
                    }
                    // left
                    if (chunk.getBlock(x - 1, y, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x, y, z},
                            new float[]{x, y, z+1},
                            new float[]{x, y+1, z+1},
                            new float[]{x, y+1, z},
                            -1, 0, 0, indexOffset,
                            blockUOffset
                        );
                        indexOffset += 4;
                    }
                    // right
                    if (chunk.getBlock(x + 1, y, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x+1, y, z+1},
                            new float[]{x+1, y, z},
                            new float[]{x+1, y+1, z},
                            new float[]{x+1, y+1, z+1},
                            1, 0, 0, indexOffset,
                            blockUOffset
                        );
                        indexOffset += 4;
                    }
                    // top
                    if (chunk.getBlock(x, y + 1, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x,   y+1, z},
                            new float[]{x+1, y+1, z},
                            new float[]{x+1, y+1, z+1},
                            new float[]{x,   y+1, z+1},
                            0, 1, 0, indexOffset,
                            blockUOffset
                        );
                        indexOffset += 4;
                    }
                    // bottom
                    if (chunk.getBlock(x, y - 1, z) == BlockType.AIR) {
                        addFace(vertices, indices,
                            new float[]{x+1, y,   z},
                            new float[]{x,   y,   z},
                            new float[]{x,   y,   z+1},
                            new float[]{x+1, y,   z+1},
                            0, -1, 0, indexOffset,
                            blockUOffset
                        );
                        indexOffset += 4;
                    }
                }
            }
        }

        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }
        int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();

        return new Mesh(verticesArray, indicesArray);
    }

    /**
     * addFace with an extra parameter 'uOffset' to shift the texture in the atlas.
     */
    private static void addFace(
            List<Float> vertices, List<Integer> indices,
            float[] v0, float[] v1, float[] v2, float[] v3,
            float nx, float ny, float nz, int indexOffset,
            float uOffset
    ) {
        // We'll assume the texture atlas is split horizontally:
        // [0..0.5) for STONE, [0.5..1.0) for SPECIAL, etc.
        // (uOffset is 0.0 for stone, 0.5 for special.)
        
        // bottom-left vertex
        addVertex(vertices, v0, uOffset + 0.0f, 0f, nx, ny, nz);
        // bottom-right vertex
        addVertex(vertices, v1, uOffset + 0.5f, 0f, nx, ny, nz);
        // top-right vertex
        addVertex(vertices, v2, uOffset + 0.5f, 1f, nx, ny, nz);
        // top-left vertex
        addVertex(vertices, v3, uOffset + 0.0f, 1f, nx, ny, nz);

        indices.add(indexOffset);
        indices.add(indexOffset + 1);
        indices.add(indexOffset + 2);
        indices.add(indexOffset + 2);
        indices.add(indexOffset + 3);
        indices.add(indexOffset);
    }

    private static void addVertex(
            List<Float> vertices, float[] pos,
            float u, float v,
            float nx, float ny, float nz
    ) {
        // position (x,y,z)
        vertices.add(pos[0]);
        vertices.add(pos[1]);
        vertices.add(pos[2]);
        // texture coords (u,v)
        vertices.add(u);
        vertices.add(v);
        // normal (nx,ny,nz)
        vertices.add(nx);
        vertices.add(ny);
        vertices.add(nz);
    }
}
