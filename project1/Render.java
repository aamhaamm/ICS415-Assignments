package project1; 

import project1.HitInfo.HitRecord;
import project1.HitInfo.Interval;
import project1.HitInfo.Radiance;
import project1.HitTable.HitTable;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;

import static project1.RTUtil.*;

public class Render {
    private final int imageWidth;
    private final int imageHeight;
    private final int spp = 10000; 
    private final int depth = 64; 
    private final Camera camera;

    public Render(Camera camera) {
        this.camera = camera;
        this.imageWidth = 1280;
        this.imageHeight = 720;
    }

    public Render(Camera camera, int width, int height) {
        this.camera = camera;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    private String writeRGB(Vec3 color, int samplesPerPixel) {
        double r = color.x();
        double g = color.y();
        double b = color.z();

        double scale = 1.0 / samplesPerPixel;
        r = Math.sqrt(scale * r);
        g = Math.sqrt(scale * g);
        b = Math.sqrt(scale * b);

        int R = (int) (clamp(r, 0.0, 0.999) * 256);
        int G = (int) (clamp(g, 0.0, 0.999) * 256);
        int B = (int) (clamp(b, 0.0, 0.999) * 256);

        return R + " " + G + " " + B;
    }

    private java.awt.Color convertToAwtColor(Vec3 color, long samplesPerPixel) {
        double r = color.x();
        double g = color.y();
        double b = color.z();

        double scale = 1.0 / samplesPerPixel;
        r = Math.sqrt(scale * r);
        g = Math.sqrt(scale * g);
        b = Math.sqrt(scale * b);

        short R = (short) (clamp(r, 0.0, 0.999) * 256);
        short G = (short) (clamp(g, 0.0, 0.999) * 256);
        short B = (short) (clamp(b, 0.0, 0.999) * 256);

        return new java.awt.Color(R, G, B);
    }

    private Color skyColor(Ray r) {
        Vec3 unitDirection = Vec3.unitVector(r.dir());
        double t = 0.5 * (unitDirection.y() + 1.0);

        Color white = new Color(1.0, 1.0, 1.0);
        Color blue = new Color(0.5, 0.7, 1.0);
        return white.multiply(1.0 - t).add(blue.multiply(t));
    }

    private Color rayColor(Ray r, HitTable world, int depth) {
        if (depth == 0) {
            return new Color(0);
        }

        HitRecord rec = new HitRecord();
        Radiance radiance = new Radiance();
        Interval tRange = new Interval(0.0001, RTUtil.INFINITY);

        if (world.hit(r, tRange, rec)) {
            if (rec.mat.scatter(r, rec, radiance)) {
                return radiance.attenuation.multiply(rayColor(radiance.scattered, world, depth - 1));
            }
            return new Color(0);
        }

        return skyColor(r);
    }

    public void renderImage(HitTable scene) {
        try (FileOutputStream file = new FileOutputStream("OutputImage.ppm");
             PrintStream filePrint = new PrintStream(file)) {

            filePrint.println("P3");
            filePrint.println(imageWidth + " " + imageHeight);
            filePrint.println(255);

            for (long y = 0; y < imageHeight; ++y) {
                System.out.println("Remaining rows: " + (imageHeight - y));
                for (long x = 0; x < imageWidth; ++x) {
                    Color pixelColor = new Color(0);
                    for (int s = 0; s < spp; ++s) {
                        double u = (x + randomDouble()) / (imageWidth - 1);
                        double v = (y + randomDouble()) / (imageHeight - 1);
                        pixelColor.plusEqual(rayColor(camera.getRay(u, v), scene, depth));
                    }
                    filePrint.println(writeRGB(pixelColor, spp));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void renderWindow(HitTable scene) {
        Color[] pixelColors = new Color[imageHeight * imageWidth];
        for (int i = 0; i < imageHeight * imageWidth; ++i) {
            pixelColors[i] = new Color(0);
        }

        JFrame window = new JFrame("Ray Tracing in Java") {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                for (long s = 0; s < spp; ++s) {
                    long startTime = System.currentTimeMillis();
                    for (int y = 0; y < imageHeight; ++y) {
                        for (int x = 0; x < imageWidth; ++x) {
                            double u = (x + randomDouble()) / (imageWidth - 1);
                            double v = (y + randomDouble()) / (imageHeight - 1);
                            pixelColors[y * imageWidth + x].plusEqual(rayColor(camera.getRay(u, v), scene, depth));
                            g.setColor(convertToAwtColor(pixelColors[y * imageWidth + x], s + 1));
                            g.drawRect(x, y, 1, 1);
                        }
                    }
                    System.out.println("Samples: " + (s + 1) + " | Time elapsed: " + (double) (System.currentTimeMillis() - startTime) / 1000 + "s");
                }
            }
        };
        window.setSize(imageWidth, imageHeight);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
