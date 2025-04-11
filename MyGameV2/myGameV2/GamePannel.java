package myGameV2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GamePannel extends JPanel implements Runnable, ActionListener, KeyListener{
	
	public Thread gameLoop;
	public GameMap map;
	public Player player;
	public JButton start;
	public JButton load;
	public JButton exit1;
	public JButton resume;
	public JButton save;
	public JButton exit2;
	
	public static int gameState = 1;
	
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
		startDetails();
		pauseDetails();
		addKeyListener(this);
	}
	
	public void startDetails() { //Handles the details of buttons for the start screen
		
		start = new JButton("Start");
		load = new JButton("Load");
		exit1 = new JButton("Exit");
		
		start.addActionListener(this);
		load.addActionListener(this);
		exit1.addActionListener(this);
		
		//start.setFont(new Font("Arial", Font.BOLD, 24));
		//start.setBackground(Color.red);
		
		start.setLayout(null);
		load.setLayout(null);
		exit1.setLayout(null);
		
		this.setLayout(null);
		
		start.setBounds(580,390,100,50);
		load.setBounds(580,440,100,50);
		exit1.setBounds(580,490,100,50);
		
		start.setActionCommand("start");
		load.setActionCommand("load");
		exit1.setActionCommand("exit");
		
		add(start);
		add(load);
		add(exit1);
		
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
	
	public void stopPlayer() { //stops the player from moving
		
		this.player.left = false;
		this.player.right = false;
		this.player.forward = false;
		this.player.back = false;
		
	}
	
	public void pauseDetails() { //handles the details of buttons for the pause screen
		
		resume = new JButton("Resume");
		save = new JButton("Save");
		exit2 = new JButton("Exit");
		
		resume.addActionListener(this);
		save.addActionListener(this);
		exit2.addActionListener(this);
		
		resume.setLayout(null);
		save.setLayout(null);
		exit2.setLayout(null);
		
		this.setLayout(null);
		
		resume.setBounds(580,390,100,50);
		save.setBounds(580,440,100,50);
		exit2.setBounds(580,490,100,50);
		
		resume.setActionCommand("resume");
		save.setActionCommand("save");
		exit2.setActionCommand("exit");
		
		add(resume);
		add(save);
		add(exit2);
		
	}
	
	public void start() {
		gameLoop.start();
	}
	
	public void init() {
		
	}
	///---PUT UPDATES HERE---
	public void update() {
		player.update();
		//enviorment.update();//testing
		this.repaint();
	}
	
	public void draw(Graphics g) {
		//playing game
		if(gameState == 0) {
			//this.map.drawGameMap(g);
			//this.player.draw(g);
			this.player.drawPlayerView(g);
			g.setColor(Color.BLACK);
			g.drawString("FPS: " + currentFps, 10, 20);
			clearPause();
		}
		//main menu
		if(gameState == 1) {
			g.setColor(Color.BLACK);
			g.drawString("FPS: " + currentFps, 10, 20);
			stopPlayer();
		}
		//pause menu
		if(gameState == 2) {
			g.setColor(Color.gray);
			g.drawString("FPS: " + currentFps, 10, 20);
			revealPause();
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.draw(g);
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void clearStart() { //clears the start menu when you click start
		
		start.setEnabled(false);
		start.setVisible(false);
		
		load.setEnabled(false);
		load.setVisible(false);
		
		exit1.setEnabled(false);
		exit1.setVisible(false);
		
	}
	
	public void clearPause() { //clears the pause menu
		
		resume.setEnabled(false);
		resume.setVisible(false);
		
		save.setEnabled(false);
		save.setVisible(false);
		
		exit2.setEnabled(false);
		exit2.setVisible(false);
		
	}
	
	public void revealPause() { //reveals the pause menu
		
		resume.setEnabled(true);
		resume.setVisible(true);
		
		save.setEnabled(true);
		save.setVisible(true);
		
		exit2.setEnabled(true);
		exit2.setVisible(true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) { // performs actions when button is pressed
		String pressed = e.getActionCommand();
		
		if(pressed.equals("start")) {
			gameState = 0;
			clearStart();
		}
		if(pressed.equals("load")) {	
			System.out.println("test2");
		}
		if(pressed.equals("exit")) {	
			System.exit(0);
		}
		if(pressed.equals("resume")) {	
			gameState = 0;
			clearPause();
		}
		if(pressed.equals("save")) {	
			System.out.println("test2");
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameState == 0) {	
			gameState = 2;	
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {}
}
