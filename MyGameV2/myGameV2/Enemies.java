package myGameV2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Enemies extends Entity{
	
	public static double ey;
	public static double ex;
	boolean isStatic;    // Whether sprite can move
    private double distance;	// how far away the player is	
    public static double px;          // player x
    public static double py; 			// player y
	private static int width;
	private static Color color;
    private double speed;
    private BufferedImage texture;
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
    	
    	directionX = px - ex;
    	directionY = py - ey;
    	
    	ex += directionX / speed;
    	ey += directionY / speed;
    }
    
    public void collidDetect(Player player) {
    	
    	health = player.getHealth();
    			
    	px = player.getPx();
    	py = player.getPy();
    	
    	if ((int)ex == (int)px && (int)ey == (int)py) {
    		
    		health = health - damage;
    		
    		player.setHealth(health);
    		
    	}
    	
    }
    
    public double getEx() { return ex; }
    public double getEy() { return ey; }
    public double getSpeed() { return speed; }
}

