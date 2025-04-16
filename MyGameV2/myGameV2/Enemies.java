package myGameV2;

import java.awt.Color;
import java.awt.Graphics;

public class Enemies extends Entity{

	public Enemies(double px, double py, int width, int height, Color color) {
		super(px, py, width, height, color);
		this.px=px;
		this.py=py;
		this.width=width;
		this.height=height;
		this.color=color;
	}

	public void draw(Graphics g) {
		g.setColor(this.color);
		g.fillRect((int)px, (int)py, width, height);
	}
}
