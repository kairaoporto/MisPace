import java.awt.*;
import java.awt.image.BufferedImage;

public class Destruction {
	private final double x;
	private final double y;
	private int r;
	private final int maxRadius;
	private BufferedImage ex;

	public Destruction(double x, double y, int r, int max) {
		if (ex == null) ex = new ImageManager().loadImg("/img/ship/explosion/explosionBoom.png");

		this.x = x;
		this.y = y;
		this.r = r;
		this.maxRadius = max;
	}

	public boolean update () {
		r += 1;
		return r >= maxRadius;
	}
	public void draw (Graphics2D g) {
		g.drawImage(ex, (int) (x - r), (int) (y - r), 75, 75, null);
	}
}