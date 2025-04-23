package myGameV2;

import java.awt.image.BufferedImage;

public class Sprite {
    private double x, y;         // Position in the world
    private BufferedImage texture;
    private double scale;        // Size scaling factor
    private double height;       // Height offset (for flying or different height sprites)
    private boolean isStatic;    // Whether sprite can move
    
    public Sprite(double x, double y, BufferedImage texture, double scale, double height, boolean isStatic) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.scale = scale;
        this.height = height;
        this.isStatic = isStatic;
    }
    
    // Getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public BufferedImage getTexture() { return texture; }
    public double getScale() { return scale; }
    public double getHeight() { return height; }
    public boolean isStatic() { return isStatic; }
    
    // Calculate distance to player (for sorting)
    public double distanceTo(double playerX, double playerY) {
        return Math.sqrt((x - playerX) * (x - playerX) + (y - playerY) * (y - playerY));
    }
}