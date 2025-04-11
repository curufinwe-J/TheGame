package myGameV2;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame{
	public Dimension dim;
	public GamePannel game;
	
	public GameWindow() {}
	
	public GameWindow(String str) {
		this.setTitle(str);
		this.dim = new Dimension(1920,1080);
		this.setExtendedState(this.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setMinimumSize(dim);
		
		game = new GamePannel();
		game.setBackground(Color.gray);
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
}
