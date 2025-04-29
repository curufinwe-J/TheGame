package myGameV2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class TextureManager {
    private static Map<String, BufferedImage> textures = new HashMap<>();
    
    public static void loadTexture(String name, String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            textures.put(name, img);
            System.out.println("Loaded texture: " + name);
        } catch (IOException e) {
            System.err.println("Failed to load texture: " + path);
            e.printStackTrace();
        }
    }
    
    public static BufferedImage getTexture(String name) {
        return textures.get(name);
    }
    
    public static void loadAllTextures() {
    	File file = new File("res/Ghost3.png");
    	System.out.println("Looking for file at: " + file.getAbsolutePath());
    	
        loadTexture("ghost", "res/Ghost3.png");
    }
}
