package myGameV2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	public JButton start,load,exit1,resume,save,exit2;
	public Enemies enemy;
	
	public static int gameState = 1;
	
	//FPS display
	private int frameCount = 0;
	private long fpsTimer = 0;
	private int currentFps = 0;
	
	//FPS manager
	private static final int TARGET_FPS = 60;
	private static final long TARGET_FRAME_TIME = (long) (1000.0/TARGET_FPS);
	
	public GamePannel() {
		setFocusable(true);
        setDoubleBuffered(true);
        setLayout(null);
        addKeyListener(this);
        
        init();
        map = new GameMap(12, 12, 64);
        player = new Player(100, 100, 5, 5, Color.red);
        enemy = new Enemies(100, 100, 5, 5, Color.blue);
        fpsTimer = System.currentTimeMillis();
        
        startDetails();
        pauseDetails();
        clearPause();
        
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
        
        gameLoop = new Thread(this);
	}
	
	public void startDetails() { //Handles the details of buttons for the start screen
		start = new JButton("Start");
		load = new JButton("Load");
		exit1 = new JButton("Exit");
		
		start.addActionListener(this);
		load.addActionListener(this);
		exit1.addActionListener(this);
		
		
		start.setLayout(null);
		load.setLayout(null);
		exit1.setLayout(null);
		
		this.setLayout(null);
		
		start.setBounds(910,390,100,50);
		load.setBounds(910,440,100,50);
		exit1.setBounds(910,490,100,50);
		
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
			//game update
			update();
            repaint();
			//FPS manager
			long waitTime = TARGET_FRAME_TIME - frameTime;
			if(waitTime>0) {
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
		
		resume.setBounds(910,390,100,50);
		save.setBounds(910,440,100,50);
		exit2.setBounds(910,490,100,50);
		
		resume.setActionCommand("resume");
		save.setActionCommand("save");
		exit2.setActionCommand("exit");
		
		add(resume);
		add(save);
		add(exit2);
	}
	
	public void start() {
		gameLoop.start();
		player.startRenderingThread();
		//requestFocusInWindow();
	}
	
	public void init() {}
	
	///---PUT UPDATES HERE---
	public void update() {
		player.update();
		//System.out.println("pannel updated");
	}
	
	public void draw(Graphics2D g) {
		//playing game
		if(gameState == 0) {
			//this.map.drawGameMap(g);
			//this.player.draw(g);
			showFPS(g);
			clearPause();
			
		}
		//main menu
		if(gameState == 1) {
			showFPS(g);
			stopPlayer();
		}
		//pause menu
		if(gameState == 2) {
			showFPS(g);
			revealPause();
		}
	}
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;

	    if (gameState == 0 && player.viewBuffer != null) {
	        synchronized (player.viewBuffer) {
	            g2.drawImage(player.viewBuffer, 0, 0, null);
	        }
	    }
	    enemy.draw(g2);
	    drawUI(g2);
	    Toolkit.getDefaultToolkit().sync();
	}

	private void drawUI(Graphics2D g) {
        showFPS(g);

        switch (gameState) {
            case 0 -> clearPause();
            case 1 -> {
                stopPlayer();
                showFPS(g);
            }
            case 2 -> revealPause();
        }
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
	
	private void showFPS(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("FPS: " + currentFps, 10, 20);
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
	    int code = e.getKeyCode();

	    if (gameState == 0) {
	        if (code == KeyEvent.VK_W) player.forward = true;
	        if (code == KeyEvent.VK_S) player.back = true;
	        if (code == KeyEvent.VK_A) player.left = true;
	        if (code == KeyEvent.VK_D) player.right = true;
	        if (code == KeyEvent.VK_CONTROL) player.sprint = true;
	    }

	    if (code == KeyEvent.VK_ESCAPE && gameState == 0) {
	        gameState = 2;
	    }
	}

	@Override
	public void keyReleased(KeyEvent e) {
	    int code = e.getKeyCode();

	    if (code == KeyEvent.VK_W) player.forward = false;
	    if (code == KeyEvent.VK_S) player.back = false;
	    if (code == KeyEvent.VK_A) player.left = false;
	    if (code == KeyEvent.VK_D) player.right = false;
	    if (code == KeyEvent.VK_CONTROL) player.sprint = false;
	}
}
