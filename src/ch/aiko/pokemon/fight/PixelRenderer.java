package ch.aiko.pokemon.fight;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import ch.aiko.engine.sprite.Sprite;

public class PixelRenderer {

	protected int[] pixels;
	protected int width, height;
	protected Sprite img;
	protected BufferedImage bi;
	protected Font f;

	public PixelRenderer(Sprite img, int w, int h) {
		this.img = img;
		this.pixels = img.getPixels();
		this.bi = img.getImage();
		this.width = w;
		this.height = h;
		f = new Font("Arial", 0, 25);
	}

	public void fillRect(int x, int y, int w, int h, int col) {
		for (int i = x; i < x + w && i <= width && i >= 0; i++) {
			for (int j = y; j < y + h && j <= height && j >= 0; j++) {
				pixels[i + j * width] = col;
			}
		}
	}
	
	public void drawString(String s, Font f, int x, int y, int col) {
		Graphics g = bi.getGraphics();
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics(f);
		g.setColor(new Color(col));
		g.drawString(s, x, y + fm.getHeight());
	}
	
	public void drawString(String s, int size, int x, int y, int col) {
		Graphics g = bi.getGraphics();
		Font f = new Font(this.f.getFontName(), 0, size);
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics(f);
		g.setColor(new Color(col));
		g.drawString(s, x, y + fm.getHeight());
	}

}
