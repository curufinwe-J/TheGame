package myGameV2;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Entity {
	
	public static double px; //x-position
	public static double py; //y-position
	public static double ex; // enemy x
	public static double ey; // enemy y
	public static BufferedImage texture;
	private double scale;        // Size scaling factor
	
    public int height;
	public int width;
	
	public Boolean isStatic;
	public double speed;
	public int eUp;
	
	public Color color;
	
	public Entity(double px, double py, int width, int height, Color color) {
		this.px=px;
		this.py=py;
		this.width=width;
		this.color=color;
	}
	public Entity(double ex, double ey, BufferedImage texture, double scale, int eUp, boolean isStatic, double speed) {
		this.ex=ex;
		this.ey=ey;
		this.texture = texture;
        this.scale = scale;
        this.eUp = eUp;
        this.isStatic = isStatic;
        this.speed = speed;
	}
}
