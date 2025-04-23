package myGameV2;

import java.awt.Color;
import java.awt.Graphics;

public class Enemies extends Entity{

	public Enemies(double ex, double ey, int width, int height, Color color) {
		super(px, py, width, height, color);
		this.ex=ex;
		this.ey=ey;
		this.width=width;
		this.height=height;
		this.color=color;
	}

	public void draw(Graphics g) {
		g.setColor(this.color);
		g.fillRect((int)ex, (int)ey, width, height);
	}
	
	public void followPlayer(double px, double py) {
		
		
		
	}
}
