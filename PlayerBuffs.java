import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerBuffs {
	private Color color;
	private double x, y;
	private int r, type;
	private BufferedImage imgLife, imgPower2;

	public PlayerBuffs(int type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;

		if (type == 1) {
			if (imgLife == null) imgLife = new ImageManager().loadImg("/img/features/life.png");
			r = 3;
		}
		if (type == 2) {
			color = Color.WHITE;
			r = 4;
		}
		if (type == 3) {
			if (imgPower2 == null) imgPower2 = new ImageManager().loadImg("/img/features/bullet_image.png");
			r = 6;
		}
	}

	public double getX () {return x;}
	public double getY () {return y;}
	public double getR () {return r;}
	public int getType () {return type;}

	public boolean update () {
		y += 4;
		if (y > GameLogic.height + r) return true;
		return false;
	}
	public void draw (Graphics2D g) {
		if (type == 1) {
			g.drawImage(imgLife, (int) (x - r), (int) (y - r), null);
		} else 
		if (type == 3) {
			g.drawImage(imgPower2, (int) (x - r), (int) (y - r), null);
		} else {
			g.setColor(color);
			g.fillRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);

			g.setStroke(new BasicStroke(3));
			g.setColor(color.darker());
			g.drawRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
			g.setStroke(new BasicStroke(1));
		}
	}
}