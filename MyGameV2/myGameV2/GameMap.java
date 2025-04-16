package myGameV2;

import java.awt.Color;
import java.awt.Graphics;

public class GameMap {
	
	public static int mapX;		//Number of columns
	public static int mapY; 	//Number of Rows
	public static int mapS; 	//Width and height of each square in the array
	
	public static int[][] map =			
		{
			{1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,1,1,0,1,1,0,1,1,0,1},
			{1,0,1,0,0,0,0,0,0,1,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,1,0,0,1,1,0,0,1,0,1},
			{1,0,1,0,0,1,1,0,0,1,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,1,0,0,0,0,0,0,1,0,1},
			{1,0,1,1,0,1,1,0,1,1,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,1},
			{1,1,1,1,1,1,1,1,1,1,1,1},
		};
	
	public GameMap(int mapX, int mapY, int mapS) {
		this.mapX=mapX;
		this.mapY=mapY;
		this.mapS=mapS;
	}
	public void drawGameMap(Graphics g) {
		int x;
		int y;
		int xo;
		int yo;
		for(x=0;x<mapX;x++) {
			for(y=0;y<mapY;y++) {
				if(map[y][x]==1) {
					g.setColor(Color.white);
				}else {
					g.setColor(Color.black);
				}
				xo=x*mapS;
				yo=y*mapS;
				g.fillRect(xo+1, yo+1, mapS-1, mapS-1);
			}
		}
	}
}
