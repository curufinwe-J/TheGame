package myGameV2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class GamePannel extends JPanel implements Runnable{

	public Thread gameLoop;
	public GameMap map;
	public Player player;
	
	private int gameState = 0;
	
	//FPS display
	private int frameCount = 0;
	private long fpsTimer = 0;
	private int currentFps = 0;
	
	//FPS manager
	private static final int TARGET_FPS = 60;
	private static final long TARGET_FRAME_TIME = (long) (1000.0/TARGET_FPS);
	
	public GamePannel() {
		gameLoop = new Thread(this);
		this.init();
		map = new GameMap(12,12,64); 
		player = new Player(100,100,5,5,Color.red);
		this.setFocusable(true);
		this.addKeyListener(player);
		fpsTimer = System.currentTimeMillis();
	}
	
	public void run() {
		long lastFrameTime = System.currentTimeMillis();
		while(true) {
			long currentTime = System.currentTimeMillis();
			long frameTime = currentTime - lastFrameTime;
			
			//FPS clock
			frameCount++;
			if (currentTime - fpsTimer >= 1000) {
				currentFps = frameCount;
				frameCount = 0;
				fpsTimer = currentTime;
			}
			//FPS manager
			long waitTime = TARGET_FRAME_TIME - frameTime;
			if(waitTime>0) {
				this.update();
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			lastFrameTime = System.currentTimeMillis();
		}
	}
	
	public void start() {
		gameLoop.start();
	}
	
	public void init() {
		
	}
	///---PUT UPDATES HERE---
	public void update() {
		player.update();
		this.repaint();
	}
	
	public void draw(Graphics g) {
		//playing game
		if(gameState == 0) {
			this.map.drawGameMap(g);
			this.player.draw(g);
			g.setColor(Color.BLACK);
			g.drawString("FPS: " + currentFps, 10, 20);
		}
		//main menu
		if(gameState == 1) {
			g.setColor(Color.BLACK);
			g.drawString("FPS: " + currentFps, 10, 20);
		}
		//pause menu
		if(gameState == 2) {
			
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		this.draw(g);
		
		Toolkit.getDefaultToolkit().sync();
	}

}
