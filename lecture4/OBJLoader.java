package lecture4;

import lecture1.Vector;
import java.io.*;
import java.util.*;

public class OBJLoader {
    public static List<Triangle> loadOBJ(String filename) throws IOException {
        List<Vector> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length < 4) continue;

            if (parts[0].equals("v")) {
                // Scale larger & move forward
                double x = Double.parseDouble(parts[1]) * 10; 
                double y = Double.parseDouble(parts[2]) * 10;
                double z = Double.parseDouble(parts[3]) * 10 + 1; // Moves forward
                vertices.add(new Vector(x, y, z));
            } else if (parts[0].equals("f")) {
                int v0 = Integer.parseInt(parts[1]) - 1;
                int v1 = Integer.parseInt(parts[2]) - 1;
                int v2 = Integer.parseInt(parts[3]) - 1;
                triangles.add(new Triangle(vertices.get(v0), vertices.get(v1), vertices.get(v2), 0xCC0000)); // Red
            }
        }
        reader.close();

        System.out.println("Loaded " + vertices.size() + " vertices and " + triangles.size() + " triangles.");
        return triangles;
    }
}
