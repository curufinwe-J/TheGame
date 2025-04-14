package myGameV2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity implements KeyListener{
	//movement
	public double angle = 2*Math.PI;
	public final double MS = 1; //movement-speed
	public final double RS = .045; //rotation speed
	public boolean left, right, forward, back;
	private double dy;
	private double dx;
	//collision
	public int[][] mp = new int[12][12]; //map position
	public int mapS;
	private double xn; //collision node x coordinate
	private double yn; //collision node y coordinate
	public boolean pause;
	//rays
	private int dof; //make checks equal 12
	private final int numRays = 680;
	private final double FOV = 1.5;
	private double angleInc = FOV / (numRays-1);
	public double vDist;
	public double hDist;

	public Player(double px, double py, int width, int height, Color color) {
		super(px, py, width, height, color);
		this.px=px;
		this.py=py;
		this.width=width;
		this.height=height;
		this.color=color;
		this.mp = GameMap.map;
		this.mapS = GameMap.mapS;
	}
	public static double get(double get) {
		return get;
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
	}
	
	public void update() {
		if(left) {
			angle-=RS;
			if(angle<0) {
				angle+=2*Math.PI;
			}
			dx= (Math.cos(angle)*MS);
			dy= (Math.sin(angle)*MS);
		}
		if(right) {
			angle+=RS;
			if(angle>2*Math.PI) {
				angle-=2*Math.PI;
			}
			dx= (Math.cos(angle)*MS);
			dy= (Math.sin(angle)*MS);
		}
		if(forward) {
			xn = (px + dx*7) / mapS;
			yn = (py + dy*7) / mapS;
			
			if(mp[(int) yn][(int) px/mapS] == 0 ) {
			py = py + dy*MS;
			}
			if(mp[(int) py/mapS][(int) xn] == 0 ) {
			px = px + dx*MS;
			}
		}
		if(back) {
			xn = (px - dx*7) / mapS;
			yn = (py - dy*7) / mapS;
			
			if(mp[(int) yn][(int) px/mapS] == 0 ) {
			py = py - dy*MS;
			}
			if(mp[(int) py/mapS][(int) xn] == 0 ) {
			px = px - dx*MS;
			}
		}	
	}
	
	public void drawPlayerView(Graphics g) {
	    double ra = normalizeAngle(angle - (FOV / 2));

	    for (int r = 0; r < numRays; r++) {
	        double rayAngle = normalizeAngle(ra);
	        double hDist = 1e9, hx = px, hy = py;
	        double vDist = 1e9, vx = px, vy = py;

	        //HORIZONTAL INTERSECTION
	        double aTan = -1 / Math.tan(rayAngle);
	        double ry, rx, yo = 0, xo = 0;
	        int dof = 0;

	        if (rayAngle > Math.PI) {
	            ry = Math.floor(py / mapS) * mapS - 0.0001;
	            rx = (py - ry) * aTan + px;
	            yo = -mapS;
	            xo = -yo * aTan;
	        } else if (rayAngle < Math.PI) {
	            ry = Math.floor(py / mapS) * mapS + mapS;
	            rx = (py - ry) * aTan + px;
	            yo = mapS;
	            xo = -yo * aTan;
	        } else {
	            ry = py;
	            rx = px;
	            dof = 12;
	        }
	        while (dof < 12) {
	            int mx = (int)(rx / mapS);
	            int my = (int)(ry / mapS);
	            if (mx >= 0 && mx < mp[0].length && my >= 0 && my < mp.length && mp[my][mx] == 1) {
	                hx = rx;
	                hy = ry;
	                hDist = distance(px, py, hx, hy);
	                break;
	            } else {
	                rx += xo;
	                ry += yo;
	                dof++;
	            }
	        }
	        //VERTICAL INTERSECTION
	        double nTan = -Math.tan(rayAngle);
	        dof = 0;

	        if (rayAngle > Math.PI / 2 && rayAngle < 3 * Math.PI / 2) {
	            rx = Math.floor(px / mapS) * mapS - 0.0001;
	            ry = (px - rx) * nTan + py;
	            xo = -mapS;
	            yo = -xo * nTan;
	        } else if (rayAngle < Math.PI / 2 || rayAngle > 3 * Math.PI / 2) {
	            rx = Math.floor(px / mapS) * mapS + mapS;
	            ry = (px - rx) * nTan + py;
	            xo = mapS;
	            yo = -xo * nTan;
	        } else {
	            rx = px;
	            ry = py;
	            dof = 12;
	        }

	        while (dof < 12) {
	            int mx = (int)(rx / mapS);
	            int my = (int)(ry / mapS);
	            if (mx >= 0 && mx < mp[0].length && my >= 0 && my < mp.length && mp[my][mx] == 1) {
	                vx = rx;
	                vy = ry;
	                vDist = distance(px, py, vx, vy);
	                break;
	            } else {
	                rx += xo;
	                ry += yo;
	                dof++;
	            }
	        }
	        //CHOOSE SHORTEST DISTANCE
	        double finalDist, finalX, finalY;
	        if (vDist < hDist) {
	            finalDist = vDist;
	            finalX = vx;
	            finalY = vy;
	            g.setColor(new Color(50, 50, 50)); // vertical shade
	        } else {
	            finalDist = hDist;
	            finalX = hx;
	            finalY = hy;
	            g.setColor(new Color(70, 70, 70)); // horizontal shade
	        }

	        //draw rays in 2D map view ===
	        //g.setColor(Color.BLUE);
	        // g.drawLine((int) px, (int) py, (int) finalX, (int) finalY);

	        //3D wall slice
	        //correct for fish-eye distortion
	        double ca = normalizeAngle(angle - rayAngle);
	        finalDist *= Math.cos(ca);

	        // Line height based on distance
	        int lineH = (int)(mapS * 1080 / finalDist);
	        if (lineH > 1080) lineH = 1080;//resolution ymax
	        int lineOffset = 540 - lineH / 2;

	        g.fillRect(r * 3, lineOffset, 3, lineH);//resolution xmax / numrays
	        
	        ra += angleInc;
	    }
	}

	private double distance(double x1, double y1, double x2, double y2) {
	    return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	private double normalizeAngle(double a) {
	    a %= (2 * Math.PI);
	    if (a < 0) a += 2 * Math.PI;
	    return a;
	}
	
}
