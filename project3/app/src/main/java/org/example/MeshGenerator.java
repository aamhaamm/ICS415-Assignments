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
                    
                    // Front face (positive z)
                    if (chunk.getBlock(x, y, z + 1) == BlockType.AIR) {
                        addFace(vertices, indices, 
                            new float[]{x,     y,     z + 1},
                            new float[]{x + 1, y,     z + 1},
                            new float[]{x + 1, y + 1, z + 1},
                            new float[]{x,     y + 1, z + 1},
                            0, 0, 1, indexOffset);
                        indexOffset += 4;
                    }
                    
                    // Back face (negative z)
                    if (chunk.getBlock(x, y, z - 1) == BlockType.AIR) {
                        addFace(vertices, indices, 
                            new float[]{x + 1, y,     z},
                            new float[]{x,     y,     z},
                            new float[]{x,     y + 1, z},
                            new float[]{x + 1, y + 1, z},
                            0, 0, -1, indexOffset);
                        indexOffset += 4;
                    }
                    
                    // Left face (negative x)
                    if (chunk.getBlock(x - 1, y, z) == BlockType.AIR) {
                        addFace(vertices, indices, 
                            new float[]{x, y, z + 1},
                            new float[]{x, y, z},
                            new float[]{x, y + 1, z},
                            new float[]{x, y + 1, z + 1},
                            -1, 0, 0, indexOffset);
                        indexOffset += 4;
                    }
                    
                    // Right face (positive x)
                    if (chunk.getBlock(x + 1, y, z) == BlockType.AIR) {
                        addFace(vertices, indices, 
                            new float[]{x + 1, y, z},
                            new float[]{x + 1, y, z + 1},
                            new float[]{x + 1, y + 1, z + 1},
                            new float[]{x + 1, y + 1, z},
                            1, 0, 0, indexOffset);
                        indexOffset += 4;
                    }
                    
                    // Top face (positive y)
                    if (chunk.getBlock(x, y + 1, z) == BlockType.AIR) {
                        addFace(vertices, indices, 
                            new float[]{x,     y + 1, z},
                            new float[]{x + 1, y + 1, z},
                            new float[]{x + 1, y + 1, z + 1},
                            new float[]{x,     y + 1, z + 1},
                            0, 1, 0, indexOffset);
                        indexOffset += 4;
                    }
                    
                    // Bottom face (negative y)
                    if (chunk.getBlock(x, y - 1, z) == BlockType.AIR) {
                        addFace(vertices, indices, 
                            new float[]{x + 1, y, z},
                            new float[]{x,     y, z},
                            new float[]{x,     y, z + 1},
                            new float[]{x + 1, y, z + 1},
                            0, -1, 0, indexOffset);
                        indexOffset += 4;
                    }
                }
            }
        }
        
        // Convert lists to arrays.
        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }
        int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();
        
        return new Mesh(verticesArray, indicesArray);
    }
    
    // Helper method to add one face to the mesh.
    private static void addFace(List<Float> vertices, List<Integer> indices,
                                float[] v0, float[] v1, float[] v2, float[] v3,
                                float nx, float ny, float nz, int indexOffset) {
        // Add vertices (with hardcoded UVs: bottom-left (0,0), bottom-right (1,0), top-right (1,1), top-left (0,1))
        addVertex(vertices, v0, 0f, 0f, nx, ny, nz);
        addVertex(vertices, v1, 1f, 0f, nx, ny, nz);
        addVertex(vertices, v2, 1f, 1f, nx, ny, nz);
        addVertex(vertices, v3, 0f, 1f, nx, ny, nz);
        
        // Create two triangles.
        indices.add(indexOffset);
        indices.add(indexOffset + 1);
        indices.add(indexOffset + 2);
        indices.add(indexOffset + 2);
        indices.add(indexOffset + 3);
        indices.add(indexOffset);
    }
    
    private static void addVertex(List<Float> vertices, float[] pos,
                                  float u, float v, float nx, float ny, float nz) {
        vertices.add(pos[0]);
        vertices.add(pos[1]);
        vertices.add(pos[2]);
        vertices.add(u);
        vertices.add(v);
        vertices.add(nx);
        vertices.add(ny);
        vertices.add(nz);
    }
}
