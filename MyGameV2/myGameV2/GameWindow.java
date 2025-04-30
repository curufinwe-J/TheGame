package myGameV2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame{
	public Dimension dim;
	public int width;
	public int height;
	public GamePannel game;
	
	public GameWindow() {}
	
	public GameWindow(String str) {
		this.setTitle(str);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		this.dim = toolkit.getScreenSize();
		width = (int) dim.getWidth();
		height = (int) dim.getHeight();
		this.setExtendedState(this.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setMinimumSize(dim);
		
		game = new GamePannel();
		game.requestFocusInWindow();
		game.setBackground(new Color(75,75,75));
		this.add(game);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}
	

	public static void main(String[] args) {
		GameWindow win = new GameWindow("GameV2");
		win.game.start();
		
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
}
