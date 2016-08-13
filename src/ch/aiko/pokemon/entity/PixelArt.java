package ch.aiko.pokemon.entity;

import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.sprite.Sprite;

public class PixelArt implements Renderable {

	/**
	 * To Pokemon.java (after Level leve = new Level();)
	 * 
	 * PixelArt art = new PixelArt(16, 0xFF000000, 0xFF00AA00);
	 * 
	 * for (int i = 0; i < 4; i++) { for (int j = 0; j < 4; j++) { art.enable(2 + i, 2 + j); art.enable(10 + i, 2 + j); } } for (int i = 0; i < 4; i++) { for (int j = 0; j < 6; j++) { art.enable(6 + i, 6 + j); } } for(int i = 0; i < 6; i++) { art.enable(4, 8 + i); art.enable(5, 8 + i); art.enable(10, 8 + i); art.enable(11, 8 + i); }
	 * 
	 * level.addLayer(new LayerBuilder().setRenderable(art).toLayer());
	 */

	private Sprite spon;
	private Sprite spoff;

	private boolean[] onoff;
	private int size;

	public PixelArt(int size) {
		this.size = size;
		spon = new Sprite(0xFFFF00FF, 16, 16);
		spoff = new Sprite(0xFF000000, 16, 16);
		onoff = new boolean[size * size];
	}

	public PixelArt(int size, int col1, int col2) {
		this.size = size;
		spon = new Sprite(col1, 16, 16);
		spoff = new Sprite(col2, 16, 16);
		onoff = new boolean[size * size];
	}

	public void enable(int x, int y) {
		onoff[x + y * size] = true;
	}

	public void disable(int x, int y) {
		onoff[x + y * size] = false;
	}

	public void set(int x, int y, boolean b) {
		onoff[x + y * size] = b;
	}

	@Override
	public void render(Renderer renderer) {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				renderer.drawSprite(onoff[x + y * size] ? spon : spoff, x * 16, y * 16);
			}
		}
	}

}
