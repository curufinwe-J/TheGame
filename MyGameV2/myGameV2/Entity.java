package myGameV2;

import java.awt.Color;

public class Entity {
	
	public static double px; //x-position
	public static double py; //y-position
	
	public int width;
	public int height;
	
	public Color color;
	
	public Entity(double px, double py, int width, int height, Color color) {
		this.px=px;
		this.py=py;
		this.width=width;
		this.color=color;
	}
}
