import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ImageManager {

	public BufferedImage loadImg (String urlName) {
		try {
			URL url = getClass().getResource(urlName);
			BufferedImage img = ImageIO.read(url);
			return img;
		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			return null;
		}
	}
}