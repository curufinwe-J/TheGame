package myGameV2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity implements KeyListener {
    // Movement
    public double angle = 2 * Math.PI;
    public double MS = 1; // movement-speed
    public final double RS = .045; // rotation speed
    public boolean left, right, forward, back, sprint;
    private double dy;
    private double dx;
    private int stamina;
    private int health;
    private int mana;
    public int mx;
    public int my;
    
    // Collision
    public int[][] mp = new int[12][12]; // map position
    public int mapS;
    private double xn; // collision node x coordinate
    private double yn; // collision node y coordinate
    public boolean pause;

    public Player(double px, double py, int width, int height, Color color) {
        super(px, py, width, height, color);
        this.px = px;
        this.py = py;
        this.width = width;
        this.height = height;
        this.color = color;
        this.mp = GameMap.map;
        this.mapS = GameMap.mapS;
        stamina = 200;
        health = 100;
        mana = 100;
        calculateDirectionVectors();
    }
    
    private void calculateDirectionVectors() {
        dx = Math.cos(angle) * MS;
        dy = Math.sin(angle) * MS;
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
        if((e.getKeyCode() == KeyEvent.VK_CONTROL)) {
            sprint = true;
        }
        
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
        if((e.getKeyCode() == KeyEvent.VK_CONTROL)) {
            sprint = false;
            MS = 1;
        }
    }
    
    public void update() {
    	if(left) {
            angle -= RS;
            if(angle < 0) {
                angle += 2 * Math.PI;
            }
            calculateDirectionVectors();
        }
        if(right) {
            angle += RS;
            if(angle > 2 * Math.PI) {
                angle -= 2 * Math.PI;
            }
            calculateDirectionVectors();
        }
        if(forward) {
            xn = (px + dx * 7) / mapS;
            yn = (py + dy * 7) / mapS;
            
            if(mp[(int) yn][(int) px / mapS] == 0) {
                py = py + dy * MS;
            }
            if(mp[(int) py / mapS][(int) xn] == 0) {
                px = px + dx * MS;
            }
        }
        if(back) {
            xn = (px - dx * 7) / mapS;
            yn = (py - dy * 7) / mapS;
            
            if(mp[(int) yn][(int) px / mapS] == 0) {
                py = py - dy * MS;
            }
            if(mp[(int) py / mapS][(int) xn] == 0) {
                px = px - dx * MS;
            }
        }
        if(sprint) {
            if(stamina > 0) {
                MS = 2;
                stamina-=3;
            } else {
                MS = 1;
            }
        }
        if(!sprint) {
            if(stamina < 200) {
                MS = 1;
                stamina++;
            }
        }
        //System.out.printf("player at (%.2f, %.2f)\n", px, py);
    }
    public int getPlayerMapX() {
    	
    	mx=(int)px/mapS;
    	
    	return mx;
    	
    }
    
    public int getPlayerMapY() {
    	
    	my=(int)py/mapS;
    	
    	return my;
    	
    }
    
    // Getters and setters for access from Camera
    public double getPx() { return px; }
    public double getPy() { return py; }
    public double getAngle() { return angle; }
    public int getStamina() { return stamina; }
    public int getHealth() { return health; }
    public int getMana() { return mana; }
    public int[][] getMap() { return mp; }
    public int getMapS() { return mapS; }
    public void setPx(double px) { this.px = px; }
    public void setPy(double py) { this.py = py; }
    public void setHealth(int health) {this.health = health; }
    public void setMana(int mana) {this.mana = mana; }
}