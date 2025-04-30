package myGameV2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GamePannel extends JPanel implements Runnable, ActionListener, KeyListener{
	
	public Thread gameLoop;
	public GameMap map;
	public static Player player;
	public JButton start,load,exit1,resume,save,exit2,exit3,newB,newBut,exit4;
	public static Enemies enemy;
	public Camera camera;
	public int mapX;
	public int mapY;
	public double playerX;
	public double playerY;
	public double enemyX;
	public double enemyY;
	private int health;
	private boolean attack = false;
    private int attackX;
    private int attackY;
    public Label startL, victory, pause, death;
	
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
        TextureManager.loadAllTextures();
        map = new GameMap(12, 12, 64);
        player = new Player(100, 100, 5, 5, Color.red);
        enemy = new Enemies(400, 400, TextureManager.getTexture("ghost"), 3.0, 5, true, 1, 1);
        
        setPos();
        
        camera = new Camera(player);
        
        fpsTimer = System.currentTimeMillis();
        
        startDetails();
        pauseDetails();
        victoryDetails();
        deathDetails();
        clearPause();
        clearVictory();
        clearDeath();
        
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
        
        gameLoop = new Thread(this);
	}
	
	public void startDetails() { //Handles the details for the start screen
		start = new JButton("Start");
		load = new JButton("Load");
		exit1 = new JButton("Exit");
		startL = new Label("GAUNTLET");
		
		start.addActionListener(this);
		load.addActionListener(this);
		exit1.addActionListener(this);
		
		startL.setAlignment(Label.CENTER);

		start.setLayout(null);
		load.setLayout(null);
		exit1.setLayout(null);
		
		this.setLayout(null);
		
		start.setBounds(910,390,100,50);
		load.setBounds(910,440,100,50);
		exit1.setBounds(910,490,100,50);
		startL.setBounds(860, 350, 200, 50);
		
		start.setActionCommand("start");
		load.setActionCommand("load");
		exit1.setActionCommand("exit");
		
		add(start);
		add(load);
		add(exit1);	
		add(startL);
	}
	
	public void setPos() {
		enemy.setX(400);
		enemy.setY(400);
		
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
	
	public void pauseDetails() { //handles the details for the pause screen
		resume = new JButton("Resume");
		save = new JButton("Save");
		exit2 = new JButton("Exit");
		pause = new Label("Game Paused");
		
		resume.addActionListener(this);
		save.addActionListener(this);
		exit2.addActionListener(this);
		
		pause.setAlignment(Label.CENTER);
		
		resume.setLayout(null);
		save.setLayout(null);
		exit2.setLayout(null);
		
		this.setLayout(null);
		
		resume.setBounds(910,390,100,50);
		save.setBounds(910,440,100,50);
		exit2.setBounds(910,490,100,50);
		pause.setBounds(860, 350, 200, 50);
		
		resume.setActionCommand("resume");
		save.setActionCommand("save");
		exit2.setActionCommand("exit");
		
		add(resume);
		add(save);
		add(exit2);
		add(pause);
	}
	
	public void victoryDetails() { //handles the details for the victory screen
		newB = new JButton("New");
		exit3 = new JButton("Exit");
		victory = new Label("You Won!");
		
		newB.addActionListener(this);
		exit3.addActionListener(this);
		
		victory.setAlignment(Label.CENTER);
		
		newB.setLayout(null);
		exit3.setLayout(null);
		
		this.setLayout(null);
		
		newB.setBounds(910,390,100,50);
		exit3.setBounds(910,440,100,50);
		victory.setBounds(860, 350, 200, 50);
		
		newB.setActionCommand("new");
		exit3.setActionCommand("exit");
		
		add(newB);
		add(exit3);
		add(victory);
	}
	
	public void deathDetails() { // handles the details of the death menu
		newBut = new JButton("New");
		exit4 = new JButton("Exit");
		death = new Label("You Died!");
		
		newBut.addActionListener(this);
		exit4.addActionListener(this);
		
		death.setAlignment(Label.CENTER);
		
		newBut.setLayout(null);
		exit4.setLayout(null);
		
		this.setLayout(null);
		
		newBut.setBounds(910,390,100,50);
		exit4.setBounds(910,440,100,50);
		death.setBounds(860, 350, 200, 50);
		
		newBut.setActionCommand("new");
		exit4.setActionCommand("exit");
		
		add(newBut);
		add(exit4);
		add(death);
	}
	
	public void start() {
		gameLoop.start();
		camera.startRenderingThread();
		//requestFocusInWindow();
	}
	
	public void init() {}
	
	///---PUT UPDATES HERE---
	public void update() {
		player.update();
    	if (gameState == 0) {
    		
    		enemy.spriteMovement(enemy, player);
    		
    	}
		playerMapPosition();
		enemy.collidDetect(player);
		detectDeath();
	}
	
	public void draw(Graphics2D g) {
		//playing game
		if(gameState == 0) {
			this.map.drawGameMap(g);
			this.player.draw(g);
			this.enemy.draw(g);
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
		//win screen
		if(gameState == 3) {
			showFPS(g);
			revealVictory();
		}
		//death screen
		if(gameState == 4) {
			showFPS(g);
			revealDeath();
		}
		
	}
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;

	    if (gameState == 0 && camera.getViewBuffer() != null) {
	        synchronized (camera.getViewBuffer()) {
	            g2.drawImage(camera.getViewBuffer(), 0, 0, null);
	        }
	    }
	    drawUI(g2);
	    Toolkit.getDefaultToolkit().sync();
	    
	    attack = false;
	    
	    if (attack = true) {
	    	
	    	System.out.println(attack);
	    	
	    	drawAttack(g);
	    	
	    	//System.out.println("test");
	    	
	    }
	    
	}

	private void drawAttack(Graphics g) {

			attackX = (int) player.getPx();
			attackY = (int) player.getPy();
			
			g.fillOval(attackX + 1, attackY + 1, 5, 5);
			
			//System.out.println(attackX + " " + attackY);
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
            case 3 -> revealVictory();
            case 4 -> revealDeath();
        }
    }

	public void detectDeath() { //detects when player health hits zero
		
		health = player.getHealth();
		
		if (health == 0) {
			
			gameState = 4;
			
		}
		
	}
	
	public void clearStart() { //clears the start menu when you click start
		start.setEnabled(false);
		start.setVisible(false);
		
		startL.setEnabled(false);
		startL.setVisible(false);
		
		load.setEnabled(false);
		load.setVisible(false);
		
		exit1.setEnabled(false);
		exit1.setVisible(false);
	}
	
	public void clearDeath() { //clears the death menu
		newBut.setVisible(false);
		newBut.setEnabled(false);
		
		death.setVisible(false);
		death.setEnabled(false);
		
		exit4.setVisible(false);
		exit4.setEnabled(false);
	}
	
	public void clearVictory() { //clears the victory menu
		newB.setEnabled(false);
		newB.setVisible(false);
		
		victory.setEnabled(false);
		victory.setVisible(false);
		
		exit3.setEnabled(false);
		exit3.setVisible(false);
	}
	
	public void clearPause() { //clears the pause menu
		resume.setEnabled(false);
		resume.setVisible(false);
		
		save.setEnabled(false);
		save.setVisible(false);
		
		pause.setEnabled(false);
		pause.setVisible(false);
		
		exit2.setEnabled(false);
		exit2.setVisible(false);	
	}
	
	public void revealPause() { //reveals the pause menu
		resume.setEnabled(true);
		resume.setVisible(true);
		
		pause.setEnabled(true);
		pause.setVisible(true);
		
		save.setEnabled(true);
		save.setVisible(true);
		
		exit2.setEnabled(true);
		exit2.setVisible(true);	
	}
	
	public void revealVictory() { //reveals the victory menu	
		newB.setVisible(true);
		newB.setEnabled(true);
		
		victory.setVisible(true);
		victory.setEnabled(true);
		
		exit3.setVisible(true);
		exit3.setEnabled(true);
	}
	
	public void revealDeath() { //reveals the death menu
		newBut.setVisible(true);
		newBut.setEnabled(true);
		
		death.setVisible(true);
		death.setVisible(true);
		
		exit4.setVisible(true);
		exit4.setEnabled(true);
	}
	
	private void showFPS(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("FPS: " + currentFps, 10, 20);
    }
	
	public void playerReset() {
		playerX = 100;
		playerY = 100;
		
		player.setPx(playerX);
		player.setPy(playerY);
	}
	
	public void enemyReset() {
		enemyX = 400;
		enemyY = 400;
		
		enemy.setX(enemyX);
		enemy.setY(enemyY);
	}
	
	public void playerMapPosition() { //detects player position on the map grid in x and y coords
		
		mapX = player.getPlayerMapX();
		mapY = player.getPlayerMapY();
		
		if (mapX == 11 && mapY == 10) {
			gameState = 3;
		}	
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
		if(pressed.equals("new")) {
			gameState = 0;
			clearVictory();
			playerReset();
			enemyReset();
			clearDeath();
			player.setHealth(100);
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
	        
	        if (code == KeyEvent.VK_Q) {
	        	
	        	attack = true;
	        	
	        }
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
	    
	    if (code == KeyEvent.VK_Q) {
        	
        	attack = false;
        	
        }
	}
}
