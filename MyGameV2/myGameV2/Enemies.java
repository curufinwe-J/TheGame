package myGameV2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Enemies extends Entity{
	
	public static double ey;
	public static double ex;
	boolean isStatic;    // Whether sprite can move
    private double distance;	// how far away the player is	
    public static double px;          // player x
    public static double py; 			// player y
    public static double pa;               //player angle
	private static int width;
	private static Color color;
    private double speed;
    private static BufferedImage texture;
    private double scale;        // Size scaling factor
    private int height;       // Height offset (for flying or different height sprites)
    private double directionX;
    private double directionY;
    private int damage;
    private int health;

	public Enemies(double ex, double ey, BufferedImage texture, double scale, int eUp, boolean isStatic, double speed, int damage) {
		super(ex, ey, texture, scale, eUp, isStatic, speed);
		this.ex = ex;
		this.ex = ey;
		this.speed = speed;
		this.texture = texture;
		this.scale = scale;
		this.eUp = eUp;
		this.isStatic = isStatic;
		this.damage = damage;
    }
	
	public double distanceTo(double playerX, double playerY) {
	    return Math.sqrt((ex - playerX) * (ex - playerX) + (ey - playerY) * (ey - playerY));
	}
	
	public void setX(double ex) { 
		this.ex = ex; }
    public void setY(double ey) { 
    	this.ey = ey; }
    public void draw(Graphics g) {
		g.setColor(Color.blue);
		g.fillRect((int)ex, (int)ey, 5, 5);
	}
    public void spriteMovement(Enemies enemy, Player player) { //movement
    	
    	px = player.getPx();
    	py = player.getPy();
    	
    	double dx = px - ex;
    	double dy = py - ey;
    	
    	double distance = Math.sqrt(dx * dx + dy * dy);
    	
    	 // Normalize and move
        if (distance > 0.1) { // Prevent jitter when very close
            dx = (dx / distance) * speed;
            dy = (dy / distance) * speed;

            ex += dx;
            ey += dy;
            
            System.out.println("dx: " + dx + " dy: " + dy);
        }
    }
    
    public void collidDetect(Player player) {
    	
    	health = player.getHealth();
    			
    	px = player.getPx();
    	py = player.getPy();
    	
    	// compute distance
        double dx = ex - px;
        double dy = ey - py;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // if within 5 units apply damage
        if (distance <= 5) {
            int health = player.getHealth();
            health -= damage;
            player.setHealth(health);
        }
    }
    
    public static void draw3DEnemy(Player player, Graphics g, int winHeight, int winWidth,
    								double ex, double ey, double eUp, double[] zBuffer) {
    	px = player.getPx();
    	py = player.getPy();
    	pa = player.getAngle();
    	
    	// Vector from player to enemy
        double dx = ex - px;
        double dy = ey - py;
        
        // Calculate the angle to the enemy from player perspective
        double enemyAngle = Math.atan2(dy, dx);
        
        // Calculate the relative angle (accounting for player rotation)
        double relativeAngle = enemyAngle - pa;
        
        // Normalize the angle to [-π, π]
        while (relativeAngle > Math.PI) relativeAngle -= 2*Math.PI;
        while (relativeAngle < -Math.PI) relativeAngle += 2*Math.PI;
        
        // Calculate distance to enemy (for depth scaling)
        double distance = Math.sqrt(dx*dx + dy*dy);
        distance = Math.max(distance, 0.5);
        double correctedDistance = distance * Math.cos(relativeAngle);
        
        // Field of view in radians
        double fov = 1.5;  // About 86 degrees
        
        // Don't render if outside field of view
        if (Math.abs(relativeAngle) > fov/2) return;
        
        // Map relative angle to screen X coordinate
        double screenX = winWidth/2 * (1 + (relativeAngle / (fov/2)));
        
        // Calculate vertical position and size
        double scale = 2000 / distance;  // Adjust constant as needed
        scale = Math.min(scale, 300);
        double screenY = winHeight/2 - (eUp * scale);
        double size = Math.min(300, scale * 20);  // Adjust base size as needed
        
        // Draw sprite image
        Graphics2D g2d = (Graphics2D) g;
        
        // Save original transform for restoration later
        AffineTransform originalTransform = g2d.getTransform();
        
        // Calculate destination rectangle
        int destX = (int)(screenX - size/2);
        int destY = (int)screenY;
        int destWidth = (int)size;
        int destHeight = (int)size;
        
        // Basic z-buffer check before drawing
        boolean visible = false;
        for (int x = 0; x < destWidth; x++) {
            int sx = destX + x;
            if (sx >= 0 && sx < zBuffer.length && correctedDistance < zBuffer[sx]) {
                visible = true;
                break;
            }
        }
        if (!visible) return; 
        g.drawImage(texture, destX, destY, destWidth, destHeight, null);
        System.out.printf("Enemy at (%.2f, %.2f), distance: %.2f, angle: %.2f\n",
                ex, ey, distance, relativeAngle);
    }
    
    public double getEx() { return ex; }
    public double getEy() { return ey; }
    public double geteUp() { return eUp;}
    public double getSpeed() { return speed; }
}

