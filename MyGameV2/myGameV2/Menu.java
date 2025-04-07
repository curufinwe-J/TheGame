package myGameV2;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JButton;

public class Menu {

	public JButton button1;
	
	public void drawMenu(Graphics g) {
		GamePannel gamePannel = new GamePannel();
		
		button1 = new JButton("Test");
		
		button1.setBounds(1920 / 2 - 50, 1080 /2 - 100, 100, 50);
		
		button1.setBackground(Color.red);
		
		button1.addActionListener(null);

		gamePannel.add(button1);
		
		g.fillRect(1920 / 2 - 50, 1080 /2 - 100 + 100, 100, 50);
		g.fillRect(1920 / 2 - 50, 1080 /2 - 100 - 100, 100, 50);
		
	}
	
}
