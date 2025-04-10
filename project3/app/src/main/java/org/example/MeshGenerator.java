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
                    
                    // Check the front face (positive z). Generate if neighbor is AIR.
                    if (chunk.getBlock(x, y, z + 1) == BlockType.AIR) {
                        addVertex(vertices, x, y, z + 1, 0, 0,  0, 0, 1);
                        addVertex(vertices, x + 1, y, z + 1, 1, 0,  0, 0, 1);
                        addVertex(vertices, x + 1, y + 1, z + 1, 1, 1,  0, 0, 1);
                        addVertex(vertices, x, y + 1, z + 1, 0, 1,  0, 0, 1);
                        
                        indices.add(indexOffset);
                        indices.add(indexOffset + 1);
                        indices.add(indexOffset + 2);
                        indices.add(indexOffset + 2);
                        indices.add(indexOffset + 3);
                        indices.add(indexOffset);
                        indexOffset += 4;
                    }
                    // TODO: Generate other faces (back, left, right, top, bottom) similarly.
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
    
    private static void addVertex(List<Float> vertices, float x, float y, float z,
                                  float u, float v, float nx, float ny, float nz) {
        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
        vertices.add(u);
        vertices.add(v);
        vertices.add(nx);
        vertices.add(ny);
        vertices.add(nz);
    }
}
