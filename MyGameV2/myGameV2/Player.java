package myGameV2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class Player extends Entity implements KeyListener{
	//movement
	public double angle = 2*Math.PI;
	public double MS = 1; //movement-speed
	public final double RS = .045; //rotation speed
	public boolean left, right, forward, back, sprint;
	private double dy;
	private double dx;
	private int stamina;
	private int health;
	private int mana;
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
	//
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Dimension dim = toolkit.getScreenSize();
	private int windowWidth = (int) dim.getWidth();
	private int windowHeight = (int) dim.getHeight();
	BufferedImage viewBuffer;
	private boolean running = false;


	public Player(double px, double py, int width, int height, Color color) {
		super(px, py, width, height, color);
		this.px=px;
		this.py=py;
		this.width=width;
		this.height=height;
		this.color=color;
		this.mp = GameMap.map;
		this.mapS = GameMap.mapS;
		stamina = 100;
		health = 100;
		mana = 100;
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
		if((e.getKeyCode() == KeyEvent.VK_CONTROL)) {
			sprint = true;
		}
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
		if((e.getKeyCode() == KeyEvent.VK_CONTROL))
			sprint = false;
			MS=1;
	}
	
	public void update() {
		System.out.println("player updated");
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
		if(sprint) {
			if(stamina>0) {
				MS=2;
				stamina--;
			}else {
				MS=1;
			}
		}
		if(!sprint) {
			if(stamina < 100) {
				MS=1;
				stamina++;
			}
		}
		System.out.println("x: " + px + ", y: " + py);
	}
	public void startRenderingThread() {
	    viewBuffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
	    running = true;

	    new Thread(() -> {
	        while (true) {
	            synchronized (viewBuffer) {
	                Graphics2D g2 = (Graphics2D) viewBuffer.getGraphics();
	                g2.setColor(Color.BLACK);
	                g2.fillRect(0, 0, viewBuffer.getWidth(), viewBuffer.getHeight());
	                drawPlayerView(g2);
	                g2.dispose();
	            }

	            try {
	                Thread.sleep(16); // ~60 FPS
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }).start();

	}
	public void stopRenderingThread() {
	    running = false;
	}
	
	public void drawPlayerView(Graphics2D g2) {
	    double ra = normalizeAngle(angle - (FOV / 2));
	    //draw walls
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
	            int shade = (int)(255 / (2 + finalDist * 0.01));
	            shade = Math.max(0, Math.min(255, shade));
	            g2.setColor(new Color(shade/2 -5, shade/2 -5, shade/2 -5)); // vertical shade
	        } else {
	            finalDist = hDist;
	            finalX = hx;
	            finalY = hy;
	            int shade = (int)(255 / (2 + finalDist * 0.01));
	            shade = Math.max(0, Math.min(255, shade));
	            g2.setColor(new Color(shade/2 +5, shade/2 +5, shade/2 +5)); // horizontal shade
	        }

	        //draw rays in 2D map view ===
	        //g.setColor(Color.BLUE);
	        // g.drawLine((int) px, (int) py, (int) finalX, (int) finalY);

	        //3D wall slice
	        //correct for fish-eye distortion
	        double ca = normalizeAngle(angle - rayAngle);
	        finalDist *= Math.cos(ca);

	        // Line height based on distance
	        int lineH = (int) (mapS * 1080 / finalDist);
	        if (lineH > windowHeight) lineH = windowHeight;//resolution ymax
	        int lineOffset = windowHeight/2 - lineH / 2;
	        
	        
	        int rayWidth = windowWidth / numRays + 1;
	        g2.fillRect(r * rayWidth, lineOffset, rayWidth, lineH);//resolution xmax / numrays
	        
	        //draw floor and ceiling
	        for (int y = lineOffset + lineH; y < windowHeight; y++) {
	        	double rowDistance = (double)(mapS * 1080) / (2.0 * y - windowHeight);
	        	
	        	double rayDirX0 = Math.cos(angle - FOV / 2);
	            double rayDirY0 = Math.sin(angle - FOV / 2);
	            double rayDirX1 = Math.cos(angle + FOV / 2);
	            double rayDirY1 = Math.sin(angle + FOV / 2);
	            
	            double stepX = rowDistance * (rayDirX1 - rayDirX0) / windowWidth;
	            double stepY = rowDistance * (rayDirY1 - rayDirY0) / windowWidth;
	            
	            double floorX = px + rowDistance * rayDirX0 + r * stepX;
	            double floorY = py + rowDistance * rayDirY0 + r * stepY;
	            
	            int cellX = (int)(floorX);
	            int cellY = (int)(floorY);
	            
	            //Simulate texture sampling with color shading (replace with texture later)
	            int shade = (int)(255 / (2 + rowDistance * 0.01));
	            shade = Math.max(0, Math.min(255, shade));
	            Color floorColor = new Color(shade/2, shade/2, shade/2);
	            Color ceilingColor = new Color(shade/2, shade/2, shade/2 );
	            
	            g2.setColor(floorColor);
	            g2.fillRect(r * rayWidth, y, rayWidth, 1); // Floor
	            
	            g2.setColor(ceilingColor);
	            g2.fillRect(r * rayWidth, windowHeight - y, rayWidth, 1); // Ceiling
	        }
	        ra += angleInc; 
	    }
	    drawPlayerUI(g2);
	}

	private double distance(double x1, double y1, double x2, double y2) {
	    return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	private double normalizeAngle(double a) {
	    a %= (2 * Math.PI);
	    if (a < 0) a += 2 * Math.PI;
	    return a;
	}
	public void drawPlayerUI(Graphics g) {
		//sprint bar
		g.setColor(Color.GRAY);
		g.fillRect(windowWidth-160, 40, 120, 40);
		g.setColor(Color.GREEN);
		g.fillRect(windowWidth-150, 50, stamina, 20);
		//health bar
		g.setColor(Color.GRAY);
		g.fillRect(windowWidth-160, 90, 120, 40);
		g.setColor(Color.RED);
		g.fillRect(windowWidth-150, 100, health, 20);
		//mana bar
		g.setColor(Color.GRAY);
		g.fillRect(windowWidth-160, 140, 120, 40);
		g.setColor(Color.BLUE);
		g.fillRect(windowWidth-150, 150, mana, 20);
	}
}
