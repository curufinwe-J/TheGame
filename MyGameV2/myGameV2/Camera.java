package myGameV2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Camera {
    // Player reference
    private Player player;
    
    // Ray casting properties
    private final int numRays = 680;
    private final double FOV = 1.5;
    private double angleInc = FOV / (numRays - 1);
    
    // Screen properties
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private Dimension dim = toolkit.getScreenSize();
    private int windowWidth = (int) dim.getWidth();
    private int windowHeight = (int) dim.getHeight();
    
    // Buffer for rendering
    private BufferedImage viewBuffer;
    private boolean running = false;
    
    // Sprites
    private List<Sprite> sprites = new ArrayList<>();
    private double[] zBuffer; // For depth testing
    
    public Camera(Player player) {
        this.player = player;
        this.viewBuffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
        this.zBuffer = new double[windowWidth]; // One Z value per screen column
    }
    
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }
    
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }
    
    
    
    public void startRenderingThread() {
        running = true;
        
        new Thread(() -> {
            while (running) {
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
    
    public BufferedImage getViewBuffer() {
        synchronized (viewBuffer) {
            return viewBuffer;
        }
    }
    
    public void drawPlayerView(Graphics2D g2) {
        double playerX = player.getPx();
        double playerY = player.getPy();
        double playerAngle = player.getAngle();
        int[][] map = player.getMap();
        int mapS = player.getMapS();
        
        // Clear Z-buffer
        for (int i = 0; i < zBuffer.length; i++) {
            zBuffer[i] = Double.POSITIVE_INFINITY;
        }
        
        double ra = normalizeAngle(playerAngle - (FOV / 2));
        // Draw walls
        for (int r = 0; r < numRays; r++) {
            double rayAngle = normalizeAngle(ra);
            double hDist = 1e9, hx = playerX, hy = playerY;
            double vDist = 1e9, vx = playerX, vy = playerY;
            
            // HORIZONTAL INTERSECTION
            double aTan = -1 / Math.tan(rayAngle);
            double ry, rx, yo = 0, xo = 0;
            int dof = 0;
            
            if (rayAngle > Math.PI) {
                ry = Math.floor(playerY / mapS) * mapS - 0.0001;
                rx = (playerY - ry) * aTan + playerX;
                yo = -mapS;
                xo = -yo * aTan;
            } else if (rayAngle < Math.PI) {
                ry = Math.floor(playerY / mapS) * mapS + mapS;
                rx = (playerY - ry) * aTan + playerX;
                yo = mapS;
                xo = -yo * aTan;
            } else {
                ry = playerY;
                rx = playerX;
                dof = 12;
            }
            
            while (dof < 12) {
                int mx = (int)(rx / mapS);
                int my = (int)(ry / mapS);
                if (mx >= 0 && mx < map[0].length && my >= 0 && my < map.length && map[my][mx] == 1) {
                    hx = rx;
                    hy = ry;
                    hDist = distance(playerX, playerY, hx, hy);
                    break;
                } else {
                    rx += xo;
                    ry += yo;
                    dof++;
                }
            }
            
            // VERTICAL INTERSECTION
            double nTan = -Math.tan(rayAngle);
            dof = 0;
            
            if (rayAngle > Math.PI / 2 && rayAngle < 3 * Math.PI / 2) {
                rx = Math.floor(playerX / mapS) * mapS - 0.0001;
                ry = (playerX - rx) * nTan + playerY;
                xo = -mapS;
                yo = -xo * nTan;
            } else if (rayAngle < Math.PI / 2 || rayAngle > 3 * Math.PI / 2) {
                rx = Math.floor(playerX / mapS) * mapS + mapS;
                ry = (playerX - rx) * nTan + playerY;
                xo = mapS;
                yo = -xo * nTan;
            } else {
                rx = playerX;
                ry = playerY;
                dof = 12;
            }
            
            while (dof < 12) {
                int mx = (int)(rx / mapS);
                int my = (int)(ry / mapS);
                if (mx >= 0 && mx < map[0].length && my >= 0 && my < map.length && map[my][mx] == 1) {
                    vx = rx;
                    vy = ry;
                    vDist = distance(playerX, playerY, vx, vy);
                    break;
                } else {
                    rx += xo;
                    ry += yo;
                    dof++;
                }
            }
            
            // CHOOSE SHORTEST DISTANCE
            double finalDist, finalX, finalY;
            if (vDist < hDist) {
                finalDist = vDist;
                finalX = vx;
                finalY = vy;
                int shade = (int)(255 / (2 + finalDist * 0.01));
                shade = Math.max(0, Math.min(255, shade));
                g2.setColor(new Color(shade/2 - 5, shade/2 - 5, shade/2 - 5)); // vertical shade
            } else {
                finalDist = hDist;
                finalX = hx;
                finalY = hy;
                int shade = (int)(255 / (2 + finalDist * 0.01));
                shade = Math.max(0, Math.min(255, shade));
                g2.setColor(new Color(shade/2 + 5, shade/2 + 5, shade/2 + 5)); // horizontal shade
            }
            
            // Correct for fish-eye distortion
            double ca = normalizeAngle(playerAngle - rayAngle);
            finalDist *= Math.cos(ca);
            
            // Line height based on distance
            int lineH = (int) (mapS * 1080 / finalDist);
            if (lineH > windowHeight) lineH = windowHeight;
            int lineOffset = windowHeight / 2 - lineH / 2;
            
            int rayWidth = windowWidth / numRays + 1;
            g2.fillRect(r * rayWidth, lineOffset, rayWidth, lineH);
            
            int screenX = r * rayWidth;
            for (int i = 0; i < rayWidth; i++) {
                if (screenX + i < zBuffer.length) {
                    zBuffer[screenX + i] = finalDist;
                }
            }
            
            // Draw floor and ceiling
            for (int y = lineOffset + lineH; y < windowHeight; y++) {
                double rowDistance = (double)(mapS * 1080) / (2.0 * y - windowHeight);
                
                double rayDirX0 = Math.cos(playerAngle - FOV / 2);
                double rayDirY0 = Math.sin(playerAngle - FOV / 2);
                double rayDirX1 = Math.cos(playerAngle + FOV / 2);
                double rayDirY1 = Math.sin(playerAngle + FOV / 2);
                
                double stepX = rowDistance * (rayDirX1 - rayDirX0) / windowWidth;
                double stepY = rowDistance * (rayDirY1 - rayDirY0) / windowWidth;
                
                double floorX = playerX + rowDistance * rayDirX0 + r * stepX;
                double floorY = playerY + rowDistance * rayDirY0 + r * stepY;
                
                int cellX = (int)(floorX);
                int cellY = (int)(floorY);
                
                // Simulate texture sampling with color shading
                int shade = (int)(255 / (2 + rowDistance * 0.01));
                shade = Math.max(0, Math.min(255, shade));
                Color floorColor = new Color(shade/2, shade/2, shade/2);
                Color ceilingColor = new Color(shade/2, shade/2, shade/2);
                
                g2.setColor(floorColor);
                g2.fillRect(r * rayWidth, y, rayWidth, 1); // Floor
                
                g2.setColor(ceilingColor);
                g2.fillRect(r * rayWidth, windowHeight - y, rayWidth, 1); // Ceiling
            }
            
            ra += angleInc;
        }
        drawSprites(g2, playerX, playerY, playerAngle, mapS);
        drawPlayerUI(g2);
    }
    
    public void drawPlayerUI(Graphics g) {
        // Sprint bar
        g.setColor(Color.GRAY);
        g.fillRect(windowWidth - 160, 40, 120, 40);
        g.setColor(Color.GREEN);
        g.fillRect(windowWidth - 150, 50, player.getStamina(), 20);
        
        // Health bar
        g.setColor(Color.GRAY);
        g.fillRect(windowWidth - 160, 90, 120, 40);
        g.setColor(Color.RED);
        g.fillRect(windowWidth - 150, 100, player.getHealth(), 20);
        
        // Mana bar
        g.setColor(Color.GRAY);
        g.fillRect(windowWidth - 160, 140, 120, 40);
        g.setColor(Color.BLUE);
        g.fillRect(windowWidth - 150, 150, player.getMana(), 20);
    }
    
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    
    private double normalizeAngle(double a) {
        a %= (2 * Math.PI);
        if (a < 0) a += 2 * Math.PI;
        return a;
    }
    private void drawSprites(Graphics2D g2, double playerX, double playerY, double playerAngle, int mapS) {
        // Sort sprites by distance (furthest to nearest)
        sprites.sort(Comparator.comparingDouble(s -> -s.distanceTo(playerX, playerY)));
        
        // Draw each sprite
        for (Sprite sprite : sprites) {
        	// Calculate vector direction and camera plane
        	double dirX = Math.cos(playerAngle);
        	double dirY = Math.sin(playerAngle);
        	double planeX = Math.cos(playerAngle + Math.PI/2) * Math.tan(FOV/2);
        	double planeY = Math.sin(playerAngle + Math.PI/2) * Math.tan(FOV/2);

        	// Calculate sprite position relative to player
        	double spriteX = sprite.getX() - playerX;
        	double spriteY = sprite.getY() - playerY;

        	// Determinant of the inverse camera matrix
        	double invDet = 1.0 / (dirX * planeY - dirY * planeX);

        	// Transform sprite into camera space
        	double transformX = invDet * (planeY * spriteX - planeX * spriteY);
        	double transformY = invDet * (-dirY * spriteX + dirX * spriteY);
            // Skip sprite if behind camera
            if (transformY <= 0) continue;
            
            // Calculate sprite screen position
            int spriteScreenX = (int)((windowWidth / 2) * (1 + transformX / transformY));
            
            // Calculate sprite height on screen
            int spriteHeight = Math.abs((int)(windowHeight / transformY)) * (int)sprite.getScale();
            if (spriteHeight <= 0) continue;
            
            // Calculate sprite width on screen (maintain aspect ratio)
            int spriteWidth = spriteHeight;
            if (sprite.getTexture().getWidth() != sprite.getTexture().getHeight()) {
                spriteWidth = (int)(spriteHeight * ((double)sprite.getTexture().getWidth() / sprite.getTexture().getHeight()));
            }
            
            // Calculate sprite draw start and end positions
            int drawStartY = (int)(windowHeight / 2 - spriteHeight / 2 + sprite.getHeight() * windowHeight / transformY);
            int drawEndY = drawStartY + spriteHeight;
            if (drawStartY < 0) drawStartY = 0;
            if (drawEndY >= windowHeight) drawEndY = windowHeight - 1;
            
            int drawStartX = spriteScreenX - spriteWidth / 2;
            int drawEndX = drawStartX + spriteWidth;
            if (drawStartX < 0) drawStartX = 0;
            if (drawEndX >= windowWidth) drawEndX = windowWidth - 1;
            
            // Draw sprite
            g2.setColor(Color.RED);
            int rectWidth = drawEndX - drawStartX;
            int rectHeight = drawEndY - drawStartY;
            g2.fillRect(drawStartX, drawStartY, rectWidth, rectHeight);
        }
    }
}