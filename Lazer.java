import java.awt.*;

public class Lazer {
	private double x, y, rad, speed, dx, dy;
	private int r;
	private Color color;

	public Lazer(double angle, int x, int y) {
		this.x = x;
		this.y = y;
		r = 5;

		rad = Math.toRadians(angle);
		speed = 10;
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;

		color = Color.YELLOW;
	}

	public boolean update () {
		x += dx;
		y += dy;

		if (x < -r || x > GameLogic.width + r || y > GameLogic.height + r)
			return true;
		return false;
	}
	public void draw (Graphics2D g) {
		g.setColor(color);
		g.fillOval((int) (x - r) - 10, (int) (y - r) - 15, 2 * r, 2 * r);
	}

	public double getX () {return x;}
	public double getY () {return y;}
	public double getR () {return r;}
}