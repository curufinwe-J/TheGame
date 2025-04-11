package myGameV2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity implements KeyListener{
	//movement
	public double angle = 2*Math.PI;
	public final double MS = 1; //movement-speed
	public final double RS = .045; //rotation speed
	public boolean left, right, forward, back;
	private double dy;
	private double dx;
	//collision
	public int[][] mp = new int[12][12]; //map position
	public int mapS;
	private double xn; //collision node x coordinate
	private double yn; //collision node y coordinate
	public boolean pause;

	public Player(double px, double py, int width, int height, Color color) {
		super(px, py, width, height, color);
		this.px=px;
		this.py=py;
		this.width=width;
		this.height=height;
		this.color=color;
		this.mp = GameMap.map;
		this.mapS = GameMap.mapS;
		
	}
	public static double get(double get) {
		return get;
	}
	public void draw(Graphics g) {
		g.setColor(this.color);
		g.fillRect((int)px, (int)py, width, height);
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		if((e.getKeyCode() == KeyEvent.VK_LEFT))
			left = true;
		if((e.getKeyCode() == KeyEvent.VK_RIGHT))
			right = true;
		if((e.getKeyCode() == KeyEvent.VK_UP))
			forward = true;
		if((e.getKeyCode() == KeyEvent.VK_DOWN))
			back = true;
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if((e.getKeyCode() == KeyEvent.VK_LEFT))
			left = false;
		if((e.getKeyCode() == KeyEvent.VK_RIGHT))
			right = false;
		if((e.getKeyCode() == KeyEvent.VK_UP))
			forward = false;
		if((e.getKeyCode() == KeyEvent.VK_DOWN))
			back = false;
	}
	
	public void update() {
		if(left) {
			angle-=RS;
			if(angle<0) {
				angle+=2*Math.PI;
			}
			dx= (Math.cos(angle)*MS);
			dy= (Math.sin(angle)*MS);
		}
		if(right) {
			angle+=RS;
			if(angle>2*Math.PI) {
				angle-=2*Math.PI;
			}
			dx= (Math.cos(angle)*MS);
			dy= (Math.sin(angle)*MS);
		}
		if(forward) {
			xn = (px + dx*7) / mapS;
			yn = (py + dy*7) / mapS;
			
			if(mp[(int) yn][(int) px/mapS] == 0 ) {
			py = py + dy*MS;
			}
			if(mp[(int) py/mapS][(int) xn] == 0 ) {
			px = px + dx*MS;
			}
		}
		if(back) {
			xn = (px - dx*7) / mapS;
			yn = (py - dy*7) / mapS;
			
			if(mp[(int) yn][(int) px/mapS] == 0 ) {
			py = py - dy*MS;
			}
			if(mp[(int) py/mapS][(int) xn] == 0 ) {
			px = px - dx*MS;
			}
		}	
	}
	public void drawPlayerView() {//might use this if Enviorment class never works
		
	}

}
