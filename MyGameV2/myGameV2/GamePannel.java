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
	public JButton exit;
	
	
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
		buttonDetails();
	}
	
	public void buttonDetails() { //Handles the details of various buttons, i.e width/ height/position etc
		
		start = new JButton("Start");
		load = new JButton("Load");
		exit = new JButton("Exit");
		
		start.addActionListener(this);
		load.addActionListener(this);
		exit.addActionListener(this);
		
		//start.setFont(new Font("Arial", Font.BOLD, 24));
		//start.setBackground(Color.red);
		
		start.setLayout(null);
		load.setLayout(null);
		exit.setLayout(null);
		
		this.setLayout(null);
		
		start.setBounds(580,390,100,50);
		load.setBounds(580,440,100,50);
		exit.setBounds(580,490,100,50);
		
		start.setActionCommand("start");
		load.setActionCommand("load");
		exit.setActionCommand("exit");
		
		add(start);
		add(load);
		add(exit);
		
		
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
			g.setColor(Color.gray);
			g.drawString("FPS: " + currentFps, 10, 20);
			
			System.out.println("test");
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		this.draw(g);
		
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void clearButtons() { //clears the start menu when you click start
		
		start.setEnabled(false);
		start.setVisible(false);
		
		load.setEnabled(false);
		load.setVisible(false);
		
		exit.setEnabled(false);
		exit.setVisible(false);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) { // performs actions when button is pressed
		String pressed = e.getActionCommand();
		
		if(pressed.equals("start")) {
			
			gameState = 0;
			clearButtons();
			
		}
		
		if(pressed.equals("load")) {
			
			System.out.println("test2");
			
		}
		
		if(pressed.equals("exit")) {
			
			System.exit(0);
			
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameState == 0) { //detect the esc key, and changes gameState to pause menu
			
			gameState = 2;
			
			System.out.println("ye");
			
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {

		
		
	}

}
