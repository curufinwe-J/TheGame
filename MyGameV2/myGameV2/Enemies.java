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

	public Enemies(double ex, double ey, BufferedImage texture, double scale, int eUp, boolean isStatic, double speed) {
		super(ex, ey, texture, scale, eUp, isStatic, speed);
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
		//System.out.println("DRAWN!!!");
		//System.out.println("exy" + ex + ey);
	}
    public void spriteMovement(Enemies enemy, Player player) { //movement
    	
    	//px = player.getPx();
    	//py = player.getPy();
    	
    	double px = player.px;
    	double py = player.py;
    	double ex = enemy.ex;
    	double ey = enemy.ey;
    	double dex = 0.0;
    	double dey = 0.0;
    	
    	if(!enemy.isStatic) {
    		ex=px-10;
    		ey=py-10;
    	}
    }
    public double getEx() { return ex; }
    public double getEy() { return ey; }
}

